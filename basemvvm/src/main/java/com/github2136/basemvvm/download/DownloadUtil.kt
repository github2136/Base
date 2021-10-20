package com.github2136.basemvvm.download

import android.content.Context
import android.util.Log
import com.github2136.basemvvm.download.dao.DownloadBlockDao
import com.github2136.basemvvm.download.dao.DownloadFileDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Created by YB on 2019/6/6
 * 断点续传
 * getPathExists获取本地存在的文件路径
 * download下载文件并存储在指定位置，如果正在下载则不做任何操作，如果存在记录则会删除记录重新下载
 * 只有STATE_PROGRESS、STATE_BLOCK_PROGRESS或STATE_BLOCK_SUCCESS进度(progress)才可信其他状态下进度(progress)不一定准确
 */
class DownloadUtil private constructor(val context: Context) {
    private val downLoadFileDao by lazy { DownloadFileDao(context) }
    private val downLoadBlockDao by lazy { DownloadBlockDao(context) }
    private val downloadTask = mutableMapOf<String, DownloadTask>()
    private val downloadMultipleTask = mutableMapOf<String, DownloadMultipleTask>()

    /**
     * 根据下载地址获取本地存储地址且文件必须存在，如果为null则表示没有下载或下载未完成
     */
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

    /**
     * 单文件下载
     */
    fun download(url: String, filePath: String, replay: Boolean = false, callback: (state: Int, progress: Int, size: Long, contentLength: Long, path: String, url: String, error: String?) -> Unit) {
        if (LOG_ENABLE) {
            Log.d(TAG, "开始单文件下载，URL:$url path:$filePath")
        }
        GlobalScope.launch(Dispatchers.IO) {
            if (!downloadTask.containsKey(url)) {
                if (LOG_ENABLE) {
                    Log.d(TAG, "新建下载对象，URL:$url path:$filePath")
                }
                val task = DownloadTask(context, url, filePath, replay) { state, progress, size, contentLength, path, url, error ->
                    if (state != STATE_PROGRESS) {
                        downloadTask.remove(url)
                    }
                    callback(state, progress, size, contentLength, path, url, error)
                }
                downloadTask[url] = task
                task.start()
            } else {
                if (LOG_ENABLE) {
                    Log.d(TAG, "继续下载对象，URL:$url path:$filePath")
                }
                val task = downloadTask[url]
                task?.apply {
                    this.callback = { state, progress, size, contentLength, path, url, error ->
                        if (state != STATE_PROGRESS) {
                            downloadTask.remove(url)
                        }
                        callback(state, progress, size, contentLength, path, url, error)
                    }
                    if (state != STATE_PROGRESS) {
                        //非下载中则下载
                        start()
                    }
                }
            }
        }
    }

    /**
     * 多文件下载
     */
    fun downloadMultiple(urlAndPath: Map<String, String>, id: String? = null, replay: Boolean = false, callback: (state: Int, progress: Int, successCount: Int, fileCount: Int, url: String, path: String, error: String?) -> Unit): String {
        if (LOG_ENABLE) {
            Log.d(TAG, "开始多单文件下载，共${urlAndPath.size}个")
        }
        val multipleTask = DownloadMultipleTask(context, urlAndPath, replay)
        val taskId = id ?: multipleTask.hashCode().toString()
        if (LOG_ENABLE) {
            Log.d(TAG, "开始多单文件下载，taskId:$taskId")
        }
        multipleTask.callback = { state, progress, successCount, fileCount, url, path, error ->
            if (state == STATE_SUCCESS || state == STATE_FAIL) {
                downloadMultipleTask.remove(taskId)
            }
            callback(state, progress, successCount, fileCount, url, path, error)
        }
        downloadMultipleTask[taskId] = multipleTask.apply { start() }
        return taskId
    }

    fun stop(url: String) {
        if (downloadTask.containsKey(url)) {
            val task = downloadTask[url]
            task?.stop()
            downloadTask.remove(url)
        }
    }

    fun stopMultiple(id: String) {
        downloadMultipleTask[id]?.stop()
        downloadMultipleTask.remove(id)
    }

    fun release() {
        downLoadFileDao.close()
        downLoadBlockDao.close()
    }

    private suspend fun <T> execute(block: () -> T): T {
        return withContext(Dispatchers.IO) {
            block()
        }
    }

    companion object {
        val LOG_ENABLE = true
        val TAG = "DOWNLOAD_UTIL"
        const val STATE_PROGRESS = 1 //下载中
        const val STATE_FAIL = 2 //下载失败
        const val STATE_SUCCESS = 3 //下载成功
        const val STATE_STOP = 4 //下载停止
        const val STATE_BLOCK_SUCCESS = 5 //多文件下载，其中一块成功
        const val STATE_BLOCK_FAIL = 6 //多文件下载，其中一块失败
        const val STATE_BLOCK_PROGRESS = 7 //多文件下载，下载进度，跳过已下载只在实际文件下载时调用，所以调用时会有延迟
        private var instance: DownloadUtil? = null
        fun getInstance(context: Context): DownloadUtil {
            if (instance == null) {
                synchronized(DownloadUtil::class) {
                    if (instance == null) {
                        instance = DownloadUtil(context)
                    }
                }
            }
            return instance!!
        }
    }
}