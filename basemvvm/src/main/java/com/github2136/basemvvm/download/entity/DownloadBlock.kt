package com.github2136.basemvvm.download.entity

/**
 * Created by YB on 2019/6/11
 * 多线程文件下载记录块
 */
data class DownloadBlock(
    var id: Long,//主键
    var fileId: Long,//文件记录id
    var start: Long,//文件下载开始位置
    var end: Long,//文件下载结束位置
    var fileUrl: String,//文件下载路径
    var filePath: String,//文件本地路径
    var fileSize: Long,//已下载文件大小
    var complete: Boolean//下载已完成
)