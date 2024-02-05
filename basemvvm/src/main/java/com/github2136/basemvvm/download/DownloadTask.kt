package com.github2136.basemvvm.download

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.github2136.basemvvm.download.dao.DownloadBlockDao
import com.github2136.basemvvm.download.dao.DownloadFileDao
import com.github2136.basemvvm.download.entity.DownloadBlock
import com.github2136.basemvvm.download.entity.DownloadFile
import com.github2136.util.JsonUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import okhttp3.internal.headersContentLength
import java.io.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by YB on 2019/6/11
 * 单文件下载，如果没有获取到文件大小那么下载时进度永远都是-1
 */
class DownloadTask(
    private val context: Context,
    val url: String,
    private var filePath: String,
    header: Map<String, String>?,
    private val replay: Boolean,
    /**
     * 下载状态，下载进度百分比，已下载大小，总大小，本地路径，网络路径，错误信息
     */
    var callback: (state: Int, progress: Int, size: Long, contentLength: Long, path: String, url: String, error: String?) -> Unit
) {
    private val jsonutil by lazy { JsonUtil.newInstance() }
    //下载时临时文件名下载完成后需要修改文件名
    private val downloadPath by lazy { "$filePath.basetemp" }
    private val downLoadFileDao by lazy { DownloadFileDao(context) }
    private val downLoadBlockDao by lazy { DownloadBlockDao(context) }
    private val okHttpManager by lazy { OkHttpManager.instance }
    private var progressArray: LongArray = LongArray(1) //每块已下载大小
    private lateinit var file: File //下载的文件
    private var totalProgress: Int = -1 //文件下载总进度
    private var length: Long = 0 //文件总长度

    private var childFinishCount = AtomicInteger()  //下载完成的块
    var state: Int = 0 //当前状态

    //是否停止
    private var stop = false
    private var downloadFile: DownloadFile? = null
    private var _header: Map<String, String>? = null

    init {
        _header = header
    }
    /**
     * 开始下载
     */
    suspend fun start() = withContext(Dispatchers.IO) {
        stop = false
        childFinishCount.set(0)
        state = DownloadUtil.STATE_PROGRESS
        downloadFile = downLoadFileDao.get(url)
        downloadFile?.apply {
            if (complete) {
                //下载完成如果再次点击下载则删除记录重新下载
                downLoadFileDao.delete(url)
                downLoadBlockDao.delete(url)
                downloadFile = null
                _header = null
            } else {
                fileHeader = jsonutil.toJson(_header)
                downLoadFileDao.update(this)
            }
        }

        try {
            val call = okHttpManager.call(url, _header)
            val response = call.execute()
            try {
                if (response.isSuccessful) {
                    length = response.body?.contentLength() ?: response.headersContentLength()
                    response.body?.apply {
                        close(this)
                    }
                    var allowBlock = false //是否允许断点续传

                    if (TextUtils.equals(response.header("Accept-Range")?.toLowerCase(), "bytes")) {
                        allowBlock = true
                    } else {
                        for (header in response.headers) {
                            if (header.first.toLowerCase() == "accept-ranges") {
                                if (response.header(header.first)?.toLowerCase() == "bytes") {
                                    allowBlock = true
                                    break
                                }
                            }
                        }
                        if (!allowBlock) {
                            //不允许断点续传，删除之前的下载记录
                            downLoadFileDao.delete(url)
                            downLoadBlockDao.delete(url)
                        }
                    }
                    if (downloadFile == null) {
                        //未下载过
                        downloadFile = DownloadFile(0, url, jsonutil.toJson(_header), downloadPath, 0, length, false)
                        val fileId = downLoadFileDao.install(downloadFile!!)
                        downloadFile?.id = fileId
                    } else {
                        if (downloadFile?.fileTotal != length) {
                            //文件大小不一致，删除记录重新下载
                            downLoadFileDao.delete(url)
                            downLoadBlockDao.delete(url)
                            downloadFile = DownloadFile(0, url, jsonutil.toJson(_header), downloadPath, 0, length, false)
                            val fileId = downLoadFileDao.install(downloadFile!!)
                            downloadFile?.id = fileId
                        }
                    }

                    file = File(downloadPath)
                    if (!file.parentFile.exists()) file.parentFile.mkdirs()
                    val randomFile = RandomAccessFile(file, "rw")
                    randomFile.setLength(length.let { if (it == -1L) 0 else it }) //长度不能为-1
                    //分块下载
                    if (allowBlock) {
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
                        if (DownloadUtil.LOG_ENABLE) {
                            Log.d(DownloadUtil.TAG, "URL:$url 大小:$length 块数:${threadSize}")
                        }
                        download(downloadFile!!.id, threadSize, url, length)
                    } else {
                        if (DownloadUtil.LOG_ENABLE) {
                            Log.d(DownloadUtil.TAG, "URL:$url 大小:$length")
                        }
                        download(downloadFile!!.id, 1, url, length)
                    }
                } else {
                    if (DownloadUtil.LOG_ENABLE) {
                        Log.e(DownloadUtil.TAG, "URL:$url request code ${response.code}")
                    }
                    fail("request code ${response.code}")
                    //下载失败
                    response.body?.apply {
                        close(this)
                    }
                }
            } catch (e: Exception) {
                //下载失败
                if (DownloadUtil.LOG_ENABLE) {
                    Log.e(DownloadUtil.TAG, "URL:$url $e")
                }
                val sw = StringWriter()
                e.printStackTrace(PrintWriter(sw))
                fail(sw.toString())
            }
        } catch (e: Exception) {
            //下载失败
            if (DownloadUtil.LOG_ENABLE) {
                Log.e(DownloadUtil.TAG, "URL:$url $e")
            }
            val sw = StringWriter()
            e.printStackTrace(PrintWriter(sw))
            fail(sw.toString())
        }
    }

    /**
     * fileId 文件id
     */
    private suspend fun download(fileId: Long, threadSize: Int, url: String, contentLength: Long) =
        withContext(Dispatchers.IO) {
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
                    childFinishCount.incrementAndGet()
                    progressArray[i] = fileBlock.fileSize
                    continue
                }
                async {
                    if (DownloadUtil.LOG_ENABLE) {
                        Log.d(DownloadUtil.TAG, "URL:$url 第${i}块开始下载")
                    }
                    val call = okHttpManager.call(url, fileBlock.start + fileBlock.fileSize, fileBlock.end, _header)
                    var inputStream: InputStream? = null
                    var randomFile: RandomAccessFile? = null
                    try {
                        val response = call.execute()
                        if (response.isSuccessful) {
                            val buf = ByteArray(2048)
                            var len: Int
                            response.body?.apply {
                                var current = fileBlock.fileSize
                                inputStream = byteStream()
                                var time1 = System.currentTimeMillis()
                                var time2: Long
                                randomFile = RandomAccessFile(file, "rw")
                                //跳过已下载的内容
                                randomFile!!.seek(start + current)

                                while (inputStream!!.read(buf).apply { len = this } != -1) {
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
                                        if (DownloadUtil.LOG_ENABLE) {
                                            Log.d(DownloadUtil.TAG, "URL:$url 第${i}块停止下载")
                                        }
                                        //停止下载
                                        downLoadBlockDao.update(fileBlock)
                                        break
                                    }
                                }
                                progressArray[i] = current
                                if (DownloadUtil.LOG_ENABLE) {
                                    Log.d(DownloadUtil.TAG, "URL:$url 第${i}块下载完成")
                                }
                                progress()
                                if (childFinishCount.incrementAndGet() == threadSize && state == DownloadUtil.STATE_PROGRESS) {
                                    if (stop) {
                                        if (DownloadUtil.LOG_ENABLE) {
                                            Log.d(DownloadUtil.TAG, "URL:$url 停止下载")
                                        }
                                        //停止
                                        state = DownloadUtil.STATE_STOP
                                        withContext(Dispatchers.Main) {
                                            callback.invoke(DownloadUtil.STATE_STOP, 0, progressArray.reduce { acc, l -> acc + l }, length, "", url, null)
                                        }
                                    } else {
                                        //下载完成
                                        state = DownloadUtil.STATE_SUCCESS
                                        if (DownloadUtil.LOG_ENABLE) {
                                            Log.d(DownloadUtil.TAG, "URL:$url 下载完成")
                                        }
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
                                                    if (DownloadUtil.LOG_ENABLE) {
                                                        Log.d(DownloadUtil.TAG, "URL:$url 替换同名旧文件")
                                                    }
                                                }
                                            } else {
                                                var i = 1
                                                while (newFile.exists()) {
                                                    newFile = File(
                                                        newFile.parent,
                                                        name.toString().format("-$i")
                                                    )
                                                    i++
                                                }
                                                if (DownloadUtil.LOG_ENABLE) {
                                                    Log.d(DownloadUtil.TAG, "URL:$url 存在同名文件，文件名加一")
                                                }
                                            }
                                            file.renameTo(newFile)
                                            downloadFile?.run {
                                                filePath = newFile.absolutePath
                                                downLoadFileDao.update(this)
                                            }
                                            withContext(Dispatchers.Main) {
                                                callback.invoke(DownloadUtil.STATE_SUCCESS, 100, progressArray.reduce { acc, l -> acc + l }, length, newFile.absolutePath, url, null)
                                            }
                                        } else {
                                            if (DownloadUtil.LOG_ENABLE) {
                                                Log.e(DownloadUtil.TAG, "URL:$url 文件被删除")
                                            }
                                            fail("文件被删除")
                                        }
                                    }
                                }
                            }
                        } else {
                            //下载失败
                            if (DownloadUtil.LOG_ENABLE) {
                                Log.e(DownloadUtil.TAG, "URL:$url request code ${response.code}")
                            }
                            fail("request code ${response.code}")
                        }
                    } catch (e: Exception) {
                        //下载失败
                        if (DownloadUtil.LOG_ENABLE) {
                            Log.e(DownloadUtil.TAG, "URL:$url $e")
                        }
                        val sw = StringWriter()
                        e.printStackTrace(PrintWriter(sw))
                        fail(sw.toString())
                    } finally {
                        inputStream?.close()
                        randomFile?.close()
                    }
                }
            }
        }

    suspend private fun progress() {
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
            withContext(Dispatchers.Main) {
                if (length == -1L) {
                    //未获取到文件大小
                    callback.invoke(state, -1, progressArray.reduce { acc, l -> acc + l }, length, "", url, null)
                } else {
                    callback.invoke(state, tempProgress, progressArray.reduce { acc, l -> acc + l }, length, "", url, null)
                }
            }
            totalProgress = tempProgress
        }
    }

    fun stop() {
        stop = true
    }

    private suspend fun fail(error: String) {
        if (state != DownloadUtil.STATE_FAIL) {
            state = DownloadUtil.STATE_FAIL
            stop = true
            withContext(Dispatchers.Main) {
                callback.invoke(DownloadUtil.STATE_FAIL, 0, progressArray.reduce { acc, l -> acc + l }, length, "", url, error)
            }
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