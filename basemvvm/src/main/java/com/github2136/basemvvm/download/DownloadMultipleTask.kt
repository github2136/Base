package com.github2136.basemvvm.download

import android.content.Context
import android.util.Log
import com.github2136.basemvvm.download.dao.DownloadBlockDao
import com.github2136.basemvvm.download.dao.DownloadFileDao
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by yb on 2020/4/10
 * 多文件下载，下载进度按文件已下载数量及单文件下载进度来计算总进度，不是按照已下载文件大小显示总进度
 */
class DownloadMultipleTask(
    private val context: Context,
    private val urlAndPath: Map<String, String>,
    private val replay: Boolean,
    private val header: Map<String, String>?
) {
    /**
     * 下载状态，下载进度百分比，已下载文件数量，总文件大小，本地路径，网络路径，错误信息
     */
    var callback: ((state: Int, progress: Int, successCount: Int, fileCount: Int, url: String, path: String, error: String?) -> Unit)? = null
    private val downLoadFileDao by lazy { DownloadFileDao(context) }
    private val downLoadBlockDao by lazy { DownloadBlockDao(context) }
    private val downloadChannel by lazy { Channel<DownloadTask>() }
    private val fileCount = urlAndPath.size
    private val singleProgress = 1 / fileCount.toDouble() * 100 //单个文件占比

    private val successCount = AtomicInteger() //下载完成数量
    private val failCount = AtomicInteger() //下载失败数量
    private val taskSize = 5 //同时下载任务数
    private val progressTask = arrayOfNulls<DownloadTask>(taskSize)
    private val progresss = mutableMapOf<String, Int>() //正在下载的下载进度
    private var stop = false

    fun start() {
        GlobalScope.launch {
            stop = false
            launch {
                repeat(taskSize) { i ->
                    async {
                        for (downloadTask in downloadChannel) {
                            if (DownloadUtil.LOG_ENABLE) {
                                Log.d(DownloadUtil.TAG, "多文件${i}开始 ${downloadTask.url}")
                            }
                            progressTask[i] = downloadTask
                            downloadTask.start()
                        }
                    }
                }
            }

            for (entry in urlAndPath) {
                if (stop) {
                    break
                }
                val path = getPathExists(entry.key)
                if (path == null || replay) {
                    //下载文件
                    if (!downloadChannel.isClosedForSend) {
                        downloadChannel.send(DownloadTask(context, entry.key, entry.value, header, true, ::callback))
                    }
                } else {
                    successCount.incrementAndGet()
                    progresss.remove(entry.key)
                    val p = getProgress()
                    if (DownloadUtil.LOG_ENABLE) {
                        Log.d(DownloadUtil.TAG, "多文件已存在:${entry.key}")
                    }
                    //已下载
                    callback?.invoke(DownloadUtil.STATE_BLOCK_SUCCESS, p, successCount.get(), fileCount, entry.key, path, null)
                    if (successCount.get() + failCount.get() == fileCount) {
                        if (failCount.get() == 0) {
                            if (DownloadUtil.LOG_ENABLE) {
                                Log.d(DownloadUtil.TAG, "多文件下载结束")
                            }
                            callback?.invoke(DownloadUtil.STATE_SUCCESS, p, successCount.get(), fileCount, entry.key, path, null)
                        } else {
                            if (DownloadUtil.LOG_ENABLE) {
                                Log.d(DownloadUtil.TAG, "多文件下载结束，部分失败")
                            }
                            callback?.invoke(DownloadUtil.STATE_FAIL, p, successCount.get(), fileCount, entry.key, path, null)
                        }
                    }
                }
            }
            downloadChannel.close()
        }
    }

    fun stop() {
        stop = true
        downloadChannel.close()
        for (downloadTask in progressTask) {
            if (DownloadUtil.LOG_ENABLE) {
                Log.d(DownloadUtil.TAG, "多文件下载停止 ${downloadTask?.url}")
            }
            downloadTask?.stop()
        }
        callback?.invoke(DownloadUtil.STATE_STOP, getProgress(), successCount.get(), fileCount, "", "", null)
        callback = null
    }

    fun getPathExists(url: String): String? {
        downLoadFileDao.get(url)?.apply {
            if (complete) {
                //记录存在并且已经下载完成
                if (File(filePath).exists()) {
                    return filePath
                } else {
                    //如果记录文件下载完成但实际文件不存在则删除下载记录
                    downLoadFileDao.delete(url)
                    downLoadBlockDao.delete(url)
                }
            }
        }
        return null
    }

    fun callback(state: Int, progress: Int, size: Long, contentLength: Long, url: String, path: String, error: String?) {
        when (state) {
            DownloadUtil.STATE_PROGRESS -> {
                progresss[url] = progress
                val p = getProgress()
                callback?.invoke(DownloadUtil.STATE_PROGRESS, p, successCount.get(), fileCount, url, path, error)
            }
            DownloadUtil.STATE_SUCCESS -> {
                progresss.remove(url)
                successCount.incrementAndGet()
                val p = getProgress()
                callback?.invoke(DownloadUtil.STATE_BLOCK_SUCCESS, p, successCount.get(), fileCount, url, path, error)
                callback?.invoke(DownloadUtil.STATE_BLOCK_PROGRESS, p, successCount.get(), fileCount, url, path, error)
                if (successCount.get() + failCount.get() == fileCount) {
                    if (failCount.get() == 0) {
                        //全部下载完成
                        if (DownloadUtil.LOG_ENABLE) {
                            Log.d(DownloadUtil.TAG, "多文件下载结束")
                        }
                        callback?.invoke(DownloadUtil.STATE_SUCCESS, p, successCount.get(), fileCount, "", "", "")
                    } else {
                        //全部下载完成，部分失败
                        if (DownloadUtil.LOG_ENABLE) {
                            Log.d(DownloadUtil.TAG, "多文件下载结束，部分失败")
                        }
                        callback?.invoke(DownloadUtil.STATE_FAIL, p, successCount.get(), fileCount, "", "", "")
                    }
                }
            }
            DownloadUtil.STATE_FAIL -> {
                failCount.incrementAndGet()
                val p = getProgress()
                callback?.invoke(DownloadUtil.STATE_BLOCK_FAIL, p, successCount.get(), fileCount, url, path, error)
                if (successCount.get() + failCount.get() == fileCount) {
                    //全部下载完成，部分失败
                    if (DownloadUtil.LOG_ENABLE) {
                        Log.d(DownloadUtil.TAG, "多文件下载结束，部分失败")
                    }
                    callback?.invoke(DownloadUtil.STATE_FAIL, p, successCount.get(), fileCount, "", "", "")
                }
            }
        }
    }

    fun getProgress(): Int {
        var blockProgress = 0
        for (p in progresss) {
            blockProgress += p.value
        }
        return (singleProgress * blockProgress / 100 + successCount.get() / fileCount.toDouble() * 100).toInt()
    }
}