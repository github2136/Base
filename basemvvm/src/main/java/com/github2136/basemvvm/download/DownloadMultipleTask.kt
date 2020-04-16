package com.github2136.basemvvm.download

import android.app.Application
import com.github2136.basemvvm.download.dao.DownloadBlockDao
import com.github2136.basemvvm.download.dao.DownloadFileDao
import java.io.Closeable
import java.io.File
import java.io.IOException


/**
 * Created by yb on 2020/4/10
 * 多文件下载
 */
class DownloadMultipleTask(
    val app: Application,
    private val urlAndPath: Map<String, String>,
    val callback: (state: Int, path: String, url: String, error: String?) -> Unit
) {
    private val downLoadFileDao by lazy { DownloadFileDao(app) }
    private val downLoadBlockDao by lazy { DownloadBlockDao(app) }

    private val downloadTask = mutableMapOf<String, DownloadTask>()

    fun start() {
        DownloadUtil.executors.execute {
            var url: String
            for (entry in urlAndPath) {
                url = entry.key
                val path = getPathExists(url)
                if (path == null) {
                    //下载文件
                    downloadTask[url] = DownloadTask(app, url, entry.value, ::callback).apply { start() }
                } else {
                    //已下载
                    callback.invoke(DownloadUtil.STATE_SUCCESS, path, url, null)
                }
            }
        }
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
            DownloadUtil.STATE_SUCCESS, DownloadUtil.STATE_FAIL -> {
                callback.invoke(state, path, url, error)
            }
        }
    }
//    private val downLoadFileDao by lazy { DownloadFileDao(app) }
//    private val downLoadBlockDao by lazy { DownloadBlockDao(app) }
//    private val okHttpManager = OkHttpManager.instance
//
//
//    //    //每块的下载进度
////    private var mProgress: LongArray? = null
////    //下载的文件
////    private lateinit var file: File
//    //文件总长度
//    private var length: Long = 0
////    //下载完成的块
////    @Volatile
////    private var childFinishCount: Int = 0
//    //当前状态
//    var state: Int = 0
//    //是否停止
//    private var stop = false
//
//    private var downloadFiles = HashMap<String, DownloadFile?>(urlAndPath.size)

    /**
     * 开始下载
     */
//    fun start() {
//        DownloadUtil.downLoadExecutor.execute {
//            stop = false
//            state = DownloadUtil.STATE_DOWNLOAD
//            var df: DownloadFile?
//            var url: String? = null
//            for (entrie in urlAndPath.entries) {
//                url = entrie.key
//                df = downLoadFileDao.get(url)
//                downloadFiles[url] = df
//                df?.apply {
//                    if (complete) {
//                        //下载完成如果再次点击下载则删除记录重新下载
//                        downLoadFileDao.delete(url)
//                        downLoadBlockDao.delete(url)
//                    }
//                }
//
//                val call = okHttpManager.call(url)
//                call.execute().use { response: Response ->
//                    if (response.isSuccessful) {
//
//                    }
//
//                }
////                BufferedReader(FileReader(path)).use({ br -> return br.readLine() })
////                try (val response =call.execute()) {
//
////                }
////val response= call.execute()
//
//            }
//
//
//            val call = okHttpManager.call(url)
//            call.enqueue(object : Callback {
//                override fun onFailure(call: Call, e: IOException) {
//                    //下载失败
//                    fail("start onFailure ${e.stackTrace}")
//                }
//
//                override fun onResponse(call: Call, response: Response) {
//                    try {
//                        if (response.isSuccessful) {
//                            length = response.body?.contentLength() ?: 0
//                            response.body?.apply {
//                                close(this)
//                            }
//                            if (length > -1) {
//                                if (response.header("accept-ranges") != "bytes") {
//                                    //不允许断点续传，删除之前的下载记录
//                                    downLoadFileDao.delete(url)
//                                    downLoadBlockDao.delete(url)
//                                }
//                                if (downloadFile == null) {
//                                    //未下载过
//                                    downloadFile = DownloadFile(0, url, filePath, 0, length, false)
//                                    val fileId = downLoadFileDao.install(downloadFile!!)
//                                    downloadFile?.id = fileId
//                                } else {
//                                    if (downloadFile?.fileTotal != length) {
//                                        //文件大小不一致，删除记录重新下载
//                                        downLoadFileDao.delete(url)
//                                        downLoadBlockDao.delete(url)
//                                        downloadFile = DownloadFile(0, url, filePath, 0, length, false)
//                                        val fileId = downLoadFileDao.install(downloadFile!!)
//                                        downloadFile?.id = fileId
//                                    }
//                                }
//                                file = File(filePath)
//                                if (!file.parentFile.exists()) file.parentFile.mkdirs()
//                                val randomFile = RandomAccessFile(file, "rw")
//                                randomFile.setLength(length)
//                                //分块下载
//                                if (length > 1048576 && response.header("accept-ranges") == "bytes") {
//                                    //文件超过1M分块下载并且支持断点续传
//                                    download(downloadFile!!.id, 5, url, length)
//                                } else {
//                                    download(downloadFile!!.id, 1, url, length)
//                                }
//                            } else {
//                                //下载失败
//                                fail("file size < 0")
//                            }
//                        } else {
//                            fail("request code ${response.code}")
//                            //下载失败
//                            response.body?.apply {
//                                close(this)
//                            }
//                        }
//                    } catch (e: Exception) {
//                        //下载失败
//                        fail(e.stackTrace.toString())
//                    }
//                }
//            })
//
//        }
//    }

//    /**
//     * fileId 文件id
//     */
//    private fun download(fileId: Long, threadSize: Int, url: String, contentLength: Long) {
//        mProgress = LongArray(threadSize) { 0L }
//        val blockSize: Long = contentLength / threadSize
//        var fileBlocks = downLoadBlockDao.get(fileId)
//        if (fileBlocks.size != threadSize) {
//            //记录与线程数量不一致，删除记录
//            downLoadBlockDao.delete(url)
//            fileBlocks = mutableListOf()
//        }
//        for (i in 0 until threadSize) {
//            //开始位置
//            val start = i * blockSize
//            //结束位置
//            var end = (i + 1) * blockSize - 1
//            if (i == threadSize - 1) {
//                //最后一块
//                end = contentLength - 1
//            }
//            val fileBlock: DownloadBlock
//            if (fileBlocks.isEmpty()) {
//                //没有记录
//                fileBlock = DownloadBlock(0, fileId, start, end, url, filePath, 0, false)
//                //插入记录
//                fileBlock.id = downLoadBlockDao.install(fileBlock)
//            } else {
//                //获取记录
//                fileBlock = fileBlocks[i]
//            }
//            if (fileBlock.complete) {
//                //表示该块下载完成
//                childFinish()
//                mProgress!![i] = fileBlock.fileSize
//                continue
//            }
//
//            val call = okHttpManager.call(url, fileBlock.start + fileBlock.fileSize, fileBlock.end)
//            call.enqueue(object : Callback {
//                override fun onFailure(call: Call, e: IOException) {
//                    //下载失败
//                    fail("download onFailure ${e.stackTrace}")
//                }
//
//                override fun onResponse(call: Call, response: Response) {
//                    var inputStream: InputStream? = null
//                    var randomFile: RandomAccessFile? = null
//                    try {
//                        if (response.isSuccessful) {
//                            val buf = ByteArray(2048)
//                            var len = 0
//                            response.body?.apply {
//                                var current = fileBlock.fileSize
//                                inputStream = byteStream()
//                                var time1 = System.currentTimeMillis()
//                                var time2: Long
//                                randomFile = RandomAccessFile(file, "rw")
//                                //跳过已下载的内容
//                                randomFile!!.seek(start + current)
//                                while ({ len = inputStream!!.read(buf);len }() != -1) {
//                                    current += len
//                                    randomFile!!.write(buf, 0, len)
//
//                                    fileBlock.fileSize = current
//                                    if (current == fileBlock.end - fileBlock.start + 1) {
//                                        fileBlock.complete = true
//                                        downLoadBlockDao.update(fileBlock)
//                                    }
//                                    mProgress!![i] = current
//                                    time2 = System.currentTimeMillis()
//                                    if (time2 - time1 > 200) {
//                                        downLoadBlockDao.update(fileBlock)
//                                        time1 = time2
//                                        progress()
//                                    }
//                                    if (stop) {
//                                        //停止下载
//                                        downLoadBlockDao.update(fileBlock)
//                                        break
//                                    }
//                                }
//                                mProgress!![i] = current
//                                progress()
//                                if (childFinish() == threadSize && state == DownloadUtil.STATE_DOWNLOAD) {
//                                    if (stop) {
//                                        //停止
//                                        state = DownloadUtil.STATE_STOP
//                                        callback.invoke(DownloadUtil.STATE_STOP, 0, "", url, null)
//                                    } else {
//                                        //下载完成
//                                        state = DownloadUtil.STATE_SUCCESS
//                                        callback.invoke(DownloadUtil.STATE_SUCCESS, 100, file.absolutePath, url, null)
//                                    }
//                                }
//
//                            }
//                        } else {
//                            //下载失败
//                            fail("request code ${response.code}")
//                        }
//                    } catch (e: Exception) {
//                        //下载失败
//                        val w = StringWriter()
//                        e.printStackTrace(PrintWriter(w))
//                        fail(w.toString())
//                    } finally {
//                        inputStream?.close()
//                        randomFile?.close()
//                    }
//                }
//            })
//        }
//    }
//
//    @Synchronized
//    fun childFinish(): Int {
//        childFinishCount++
//        return childFinishCount
//    }
//
//    fun progress() {
//        var progress = 0L
//        mProgress?.forEach { p ->
//            progress += p
//        }
//        downloadFile?.apply {
//            fileSize = progress
//            if (fileSize == fileTotal) {
//                complete = true
//            }
//            downLoadFileDao.update(this)
//        }
//        callback.invoke(state, (progress.toFloat() / length * 100).toInt(), "", url, null)
//    }
//
//    fun stop() {
//        stop = true
//    }
//
//    private fun fail(error: String) {
//        if (state != DownloadUtil.STATE_FAIL) {
//            state = DownloadUtil.STATE_FAIL
//            stop = true
//            callback.invoke(DownloadUtil.STATE_FAIL, 0, "", url, error)
//        }
//    }

    /**
     * 关闭资源
     */
    private fun close(vararg closeables: Closeable) {
        try {
            for (close in closeables) {
                close.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}