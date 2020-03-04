package com.github2136.basemvvm.download

import android.app.Application
import android.util.Log
import com.github2136.basemvvm.download.dao.DownloadBlockDao
import com.github2136.basemvvm.download.dao.DownloadFileDao
import java.io.File

/**
 * Created by YB on 2019/6/6
 * 断点续传
 * getPathExists获取本地存在的文件路径
 * download下载文件并存储在指定位置，如果正在下载则不做任何操作，如果存在记录则会删除记录重新下载
 */
class DownloadUtil private constructor(val app: Application) {
    private val downLoadFileDao by lazy { DownloadFileDao(app) }
    private val downLoadBlockDao by lazy { DownloadBlockDao(app) }
    private val downloadTask = mutableMapOf<String, DownloadTask>()

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

    fun download(url: String, filePath: String, callback: (state: Int, progress: Int, path: String, error: String?) -> Unit) {
        if (!downloadTask.containsKey(url)) {
            fun callback(state: Int, progress: Int, path: String, url: String, error: String?) {
                if (state != STATE_DOWNLOAD) {
                    downloadTask.remove(url)
                }
                callback(state, progress, path, error)
            }
            val task = DownloadTask(app, url, filePath, ::callback)
            task.start()
            downloadTask[url] = task
        } else {
            val task = downloadTask[url]
            task?.apply {
                if (state != STATE_DOWNLOAD) {
                    //非下载中则下载
                    start()
                }
            }
        }
    }


    fun stop(url: String) {
        if (downloadTask.containsKey(url)) {
            val task = downloadTask[url]
            task?.stop()
            downloadTask.remove(url)
        }
    }

    fun release() {
        downLoadFileDao.close()
        downLoadBlockDao.close()
    }


    companion object {
        const val STATE_DOWNLOAD = 1//下载中
        const val STATE_FAIL = 2//下载失败
        const val STATE_SUCCESS = 3//下载成功
        const val STATE_STOP = 4//下载停止

        private var instance: DownloadUtil? = null
        fun getInstance(app: Application): DownloadUtil {
            if (instance == null) {
                synchronized(DownloadUtil::class) {
                    if (instance == null) {
                        instance = DownloadUtil(app)
                    }
                }
            }
            return instance!!
        }
    }
}