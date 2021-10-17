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
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by yb on 2020/4/10
 * 多文件下载
 */
class DownloadMultipleTask(
    private val context: Context,
    private val urlAndPath: Map<String, String>,
    private val replay: Boolean
) {
    var callback: ((state: Int, progress: Int, url: String, path: String, error: String?) -> Unit)? = null
    private val downLoadFileDao by lazy { DownloadFileDao(context) }
    private val downLoadBlockDao by lazy { DownloadBlockDao(context) }
    private val downloadChannel by lazy { Channel<DownloadTask>() }
    private val fileCount = urlAndPath.size

    private val successCount = AtomicInteger() //下载完成数量
    private val failCount = AtomicInteger() //下载失败数量
    private val taskSize = 5 //同时下载任务数
    private val progressTask = Array<DownloadTask?>(taskSize) { null }
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
                        downloadChannel.send(DownloadTask(context, entry.key, entry.value, true, ::callback))
                    }
                } else {
                    successCount.incrementAndGet()
                    val p = getProgress()
                    if (DownloadUtil.LOG_ENABLE) {
                        Log.d(DownloadUtil.TAG, "多文件已存在:${entry.key}")
                    }
                    //已下载
                    callback?.invoke(DownloadUtil.STATE_BLOCK_SUCCESS, p, entry.key, path, null)
                    if (successCount.get() == fileCount) {
                        if (DownloadUtil.LOG_ENABLE) {
                            Log.d(DownloadUtil.TAG, "多文件全部已存在")
                        }
                        callback?.invoke(DownloadUtil.STATE_SUCCESS, p, entry.key, path, null)
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
            Log.d(DownloadUtil.TAG, "文件停止 ${downloadTask?.url}")
            downloadTask?.stop()
        }
        callback?.invoke(DownloadUtil.STATE_STOP, getProgress(), "", "", null)
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

    fun callback(state: Int, progress: Int, path: String, url: String, error: String?) {
        when (state) {
            DownloadUtil.STATE_SUCCESS -> {
                successCount.incrementAndGet()
                val p = getProgress()
                callback?.invoke(DownloadUtil.STATE_BLOCK_SUCCESS, p, url, path, error)
                callback?.invoke(DownloadUtil.STATE_BLOCK_PROGRESS, p, url, path, error)
                if (DownloadUtil.LOG_ENABLE) {
                    Log.d(DownloadUtil.TAG, "多文件下载完成:${url}")
                }
                if (successCount.get() + failCount.get() == fileCount) {
                    //全部下载完成
                    callback?.invoke(DownloadUtil.STATE_SUCCESS, p, "", "", "")
                }
            }
            DownloadUtil.STATE_FAIL -> {
                failCount.incrementAndGet()
                val p = getProgress()
                callback?.invoke(DownloadUtil.STATE_BLOCK_FAIL, p, url, path, error)
                if (DownloadUtil.LOG_ENABLE) {
                    Log.d(DownloadUtil.TAG, "多文件下载失败:${url}")
                }
                if (successCount.get() + failCount.get() == fileCount) {
                    //全部下载完成，部分失败
                    callback?.invoke(DownloadUtil.STATE_FAIL, p, "", "", "")
                }
            }
        }
    }

    fun getProgress(): Int {
        return ((successCount.get() / fileCount.toDouble()) * 100).toInt()
    }
}