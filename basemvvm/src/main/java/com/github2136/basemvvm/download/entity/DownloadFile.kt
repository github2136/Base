package com.github2136.basemvvm.download.entity

/**
 * Created by YB on 2019/6/6
 * 文件下载记录
 */
data class DownloadFile(
    var id: Long,//主键
    var fileUrl: String,//文件下载路径
    var fileHeader: String,//请求头
    var filePath: String,//文件本地路径
    var fileSize: Long,//已下载文件大小
    var fileTotal: Long,//文件总大小
    var complete: Boolean//下载已完成
)