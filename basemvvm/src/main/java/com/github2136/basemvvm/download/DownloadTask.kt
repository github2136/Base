package com.github2136.basemvvm.download

import android.content.Context
import com.github2136.basemvvm.download.dao.DownloadBlockDao
import com.github2136.basemvvm.download.dao.DownloadFileDao
import com.github2136.basemvvm.download.entity.DownloadBlock
import com.github2136.basemvvm.download.entity.DownloadFile
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.*

/**
 * Created by YB on 2019/6/11
 */
class DownloadTask(
    val context: Context,
    val url: String,
    var filePath: String,
    var callback: (state: Int, progress: Int, path: String, url: String, error: String?) -> Unit,
    val replay: Boolean
) {
    //下载时临时文件名下载完成后需要修改文件名
    private val downloadPath by lazy { "$filePath.basetemp" }
    private val downLoadFileDao by lazy { DownloadFileDao(context) }
    private val downLoadBlockDao by lazy { DownloadBlockDao(context) }
    private val okHttpManager = OkHttpManager.instance
    //每块的下载进度
    private lateinit var progressArray: LongArray
    //下载的文件
    private lateinit var file: File
    //文件下载总进度
    private var totalProgress: Int = -1
    //文件总长度
    private var length: Long = 0
    //下载完成的块
    @Volatile
    private var childFinishCount: Int = 0
    //当前状态
    var state: Int = 0
    //是否停止
    private var stop = false
    private var downloadFile: DownloadFile? = null

    /**
     * 开始下载
     */
    /**
     * 开始下载
     */
    fun start() {
        stop = false
        childFinishCount = 0
        state = DownloadUtil.STATE_PROGRESS
        downloadFile = downLoadFileDao.get(url)
        downloadFile?.apply {
            if (complete) {
                //下载完成如果再次点击下载则删除记录重新下载
                downLoadFileDao.delete(url)
                downLoadBlockDao.delete(url)
                downloadFile = null
            }
        }

        val call = okHttpManager.call(url)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                //下载失败
                fail("start onFailure ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    if (response.isSuccessful) {
                        length = response.body?.contentLength() ?: 0
                        response.body?.apply {
                            close(this)
                        }
                        if (length == -1L) {
                            length = 0
                        }
                        if (response.header("accept-ranges") != "bytes") {
                            //不允许断点续传，删除之前的下载记录
                            downLoadFileDao.delete(url)
                            downLoadBlockDao.delete(url)
                        }
                        if (downloadFile == null) {
                            //未下载过
                            downloadFile = DownloadFile(0, url, downloadPath, 0, length, false)
                            val fileId = downLoadFileDao.install(downloadFile!!)
                            downloadFile?.id = fileId
                        } else {
                            if (downloadFile?.fileTotal != length) {
                                //文件大小不一致，删除记录重新下载
                                downLoadFileDao.delete(url)
                                downLoadBlockDao.delete(url)
                                downloadFile = DownloadFile(0, url, downloadPath, 0, length, false)
                                val fileId = downLoadFileDao.install(downloadFile!!)
                                downloadFile?.id = fileId
                            }
                        }
                        file = File(downloadPath)
                        if (!file.parentFile.exists()) file.parentFile.mkdirs()
                        val randomFile = RandomAccessFile(file, "rw")
                        randomFile.setLength(length)
                        //分块下载
                        if (response.header("accept-ranges") == "bytes") {
                            //分块下载并且支持断点续传
                            val threadSize: Int = when {
                                //1m
                                length < 1048576L -> 1
                                //5M
                                length < 5242880L -> 2
                                //50M
                                length < 52428800L -> 3
                                //100M
                                length < 104857600L -> 4
                                else -> 5
                            }
                            download(downloadFile!!.id, threadSize, url, length)
                        } else {
                            download(downloadFile!!.id, 1, url, length)
                        }
                    } else {
                        fail("request code ${response.code}")
                        //下载失败
                        response.body?.apply {
                            close(this)
                        }
                    }
                } catch (e: Exception) {
                    //下载失败
                    fail(e.stackTrace.toString())
                }
            }
        })
    }
    /**
     * fileId 文件id
     */
    private fun download(fileId: Long, threadSize: Int, url: String, contentLength: Long) {
        progressArray = LongArray(threadSize) { 0L }
        val blockSize: Long = contentLength / threadSize
        var fileBlocks = downLoadBlockDao.get(fileId)
        if (fileBlocks.size != threadSize) {
            //记录与线程数量不一致，删除记录
            downLoadBlockDao.delete(url)
            fileBlocks = mutableListOf()
        }
        for (i in 0 until threadSize) {
            //开始位置
            val start = i * blockSize
            //结束位置
            var end = (i + 1) * blockSize - 1
            if (i == threadSize - 1) {
                //最后一块
                end = contentLength - 1
            }
            val fileBlock: DownloadBlock
            if (fileBlocks.isEmpty()) {
                //没有记录
                fileBlock = DownloadBlock(0, fileId, start, end, url, downloadPath, 0, false)
                //插入记录
                fileBlock.id = downLoadBlockDao.install(fileBlock)
            } else {
                //获取记录
                fileBlock = fileBlocks[i]
            }
            if (fileBlock.complete) {
                //表示该块下载完成
                childFinish()
                progressArray[i] = fileBlock.fileSize
                continue
            }

            val call = okHttpManager.call(url, fileBlock.start + fileBlock.fileSize, fileBlock.end)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    //下载失败
                    fail("download onFailure ${e.stackTrace}")
                }

                override fun onResponse(call: Call, response: Response) {
                    var inputStream: InputStream? = null
                    var randomFile: RandomAccessFile? = null
                    try {
                        if (response.isSuccessful) {
                            val buf = ByteArray(2048)
                            var len = 0
                            response.body?.apply {
                                var current = fileBlock.fileSize
                                inputStream = byteStream()
                                var time1 = System.currentTimeMillis()
                                var time2: Long
                                randomFile = RandomAccessFile(file, "rw")
                                //跳过已下载的内容
                                randomFile!!.seek(start + current)
                                while ({ len = inputStream!!.read(buf);len }() != -1) {
                                    current += len
                                    randomFile!!.write(buf, 0, len)

                                    fileBlock.fileSize = current
                                    if (current == fileBlock.end - fileBlock.start + 1) {
                                        fileBlock.complete = true
                                        downLoadBlockDao.update(fileBlock)
                                    }
                                    progressArray[i] = current
                                    time2 = System.currentTimeMillis()
                                    if (time2 - time1 > 200) {
                                        downLoadBlockDao.update(fileBlock)
                                        time1 = time2
                                        progress()
                                    }
                                    if (stop) {
                                        //停止下载
                                        downLoadBlockDao.update(fileBlock)
                                        break
                                    }
                                }
                                progressArray[i] = current
                                progress()
                                if (childFinish() == threadSize && state == DownloadUtil.STATE_PROGRESS) {
                                    if (stop) {
                                        //停止
                                        state = DownloadUtil.STATE_STOP
                                        callback.invoke(DownloadUtil.STATE_STOP, 0, "", url, null)
                                    } else {
                                        //下载完成
                                        state = DownloadUtil.STATE_SUCCESS
                                        var newFile = File(filePath)
                                        val index = newFile.name.lastIndexOf(".")
                                        val name = StringBuilder(newFile.name)
                                        if (index == -1) {
                                            name.append("%s")
                                        } else {
                                            name.insert(index, "%s")
                                        }
                                        if (file.exists()) {
                                            if (replay) {
                                                if (newFile.exists()) {
                                                    newFile.delete()
                                                }
                                            } else {
                                                var i = 1
                                                while (newFile.exists()) {
                                                    newFile = File(newFile.parent, name.toString().format("-$i"))
                                                    i++
                                                }
                                            }
                                            file.renameTo(newFile)
                                            downloadFile?.run {
                                                filePath = newFile.absolutePath
                                                downLoadFileDao.update(this)
                                            }
                                            callback.invoke(DownloadUtil.STATE_SUCCESS, 100, newFile.absolutePath, url, null)
                                        } else {
                                            callback.invoke(DownloadUtil.STATE_FAIL, 100, "", "", "File miss")
                                        }
                                    }
                                }

                            }
                        } else {
                            //下载失败
                            fail("request code ${response.code}")
                        }
                    } catch (e: Exception) {
                        //下载失败
                        val w = StringWriter()
                        e.printStackTrace(PrintWriter(w))
                        fail(w.toString())
                    } finally {
                        inputStream?.close()
                        randomFile?.close()
                    }
                }
            })
        }
    }

    @Synchronized
    fun childFinish(): Int {
        childFinishCount++
        return childFinishCount
    }

    fun progress() {
        var progress = 0L
        progressArray.forEach { p ->
            progress += p
        }
        downloadFile?.apply {
            fileSize = progress
            if (fileSize == fileTotal) {
                complete = true
            }
            downLoadFileDao.update(this)
        }
        val tempProgress = (progress.toFloat() / length * 100).toInt()
        if (totalProgress != tempProgress) {
            callback.invoke(state, tempProgress, "", url, null)
            totalProgress = tempProgress
        }
    }

    fun stop() {
        stop = true
    }

    private fun fail(error: String) {
        if (state != DownloadUtil.STATE_FAIL) {
            state = DownloadUtil.STATE_FAIL
            stop = true
            callback.invoke(DownloadUtil.STATE_FAIL, 0, "", url, error)
        }
    }

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