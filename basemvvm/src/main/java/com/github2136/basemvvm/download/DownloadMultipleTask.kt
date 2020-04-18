package com.github2136.basemvvm.download

import android.app.Application
import com.github2136.basemvvm.download.dao.DownloadBlockDao
import com.github2136.basemvvm.download.dao.DownloadFileDao
import java.io.File
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by yb on 2020/4/10
 * 多文件下载
 */
class DownloadMultipleTask(
    val app: Application,
    private val urlAndPath: Map<String, String>
) {
    var callback: ((state: Int, progress: Int, path: String, url: String, error: String?) -> Unit)? = null
    private val downLoadFileDao by lazy { DownloadFileDao(app) }
    private val downLoadBlockDao by lazy { DownloadBlockDao(app) }
    private val fileCount = urlAndPath.size
    private val downloadTask = ArrayBlockingQueue<DownloadTask>(5)
    //下载完成数量
    private val successCount = AtomicInteger()
    //下载失败数量
    private val failCount = AtomicInteger()
    private var run = true
    fun start() {
        run = true
        DownloadUtil.executors.execute {
            var url: String
            for (entry in urlAndPath) {
                if (!run) {
                    break
                }
                url = entry.key
                val path = getPathExists(url)
                if (path == null) {
                    //下载文件
                    downloadTask.put(DownloadTask(app, url, entry.value, ::callback, true).apply { start() })
                } else {
                    //已下载
                    callback?.invoke(DownloadUtil.STATE_BLOCK_SUCCESS, getProgress(), path, url, null)
                    if (successCount.incrementAndGet() == fileCount) {
                        callback?.invoke(DownloadUtil.STATE_SUCCESS, getProgress(), path, url, null)
                    }
                }
            }
        }
    }

    fun stop() {
        run = false
        while (downloadTask.poll()?.apply { stop() } != null) {
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
                downloadTask.take()
                successCount.incrementAndGet()
                callback?.invoke(DownloadUtil.STATE_BLOCK_SUCCESS, getProgress(), path, url, error)
                if (successCount.get() + failCount.get() == fileCount) {
                    //全部下载完成
                    callback?.invoke(DownloadUtil.STATE_SUCCESS, getProgress(), "", "", "")
                    downloadTask.clear()
                }
            }
            DownloadUtil.STATE_FAIL    -> {
                downloadTask.take()
                failCount.incrementAndGet()
                callback?.invoke(DownloadUtil.STATE_BLOCK_FAIL, getProgress(), path, url, error)
                if (successCount.get() + failCount.get() == fileCount) {
                    //全部下载完成，部分失败
                    callback?.invoke(DownloadUtil.STATE_FAIL, getProgress(), "", "", "")
                    downloadTask.clear()
                }
            }
        }
    }

    fun getProgress(): Int {
        return ((successCount.get() / fileCount.toDouble()) * 100).toInt()
    }
}