package com.github2136.base.view.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.github2136.base.R
import com.github2136.base.databinding.ActivityMainBinding
import com.github2136.base.vm.activity.MainVM
import com.github2136.basemvvm.BaseActivity
import com.github2136.basemvvm.download.DownloadUtil
import com.github2136.util.FileUtil
import java.io.File

class MainActivity : BaseActivity<MainVM, ActivityMainBinding>() {
    override fun getLayoutId() = R.layout.activity_main
    val downloadUtil by lazy { DownloadUtil.getInstance(application) }
    val filePath by lazy { FileUtil.getExternalStorageProjectPath(this) }
    lateinit var urlAndPath: Map<String, String>
    override fun initData(savedInstanceState: Bundle?) {
        bind.view = this
        bind.vm = vm
        vm.titleTextLD.value = "主页"
        vm.rightBtnLD.value = "xxx"
    }

    // val url = "http://223.83.128.251:48092/UploadFiles/System/app/20211018/259dc425-045f-42c6-865c-34f8efa6a50f.apk"
    val url = "http://t6.tianditu.gov.cn/img_c/wmts?service=wmts&request=gettile&version=1.0.0&layer=img&format=tiles&STYLE=default&tilematrixset=c&tilecol=6729&tilerow=1400&tilematrix=13&tk=b77baf477f61aff4eb65003969b17809"

    // val url = "https://qd.myapp.com/myapp/qqteam/AndroidQQ/Android_8.2.7.4395.apk"
    var multipleId = ""

    fun onClick(view: View) {
        when (view.id) {
            R.id.btnClick1 -> {
                startActivity(Intent(this, ListActivity::class.java))
            }
            R.id.btnClick2 -> {
                startActivity(Intent(this, LoadMoreActivity::class.java))
            }
            R.id.btnClick3 -> {
                showProgressDialog("aaaa", true)
                dismissProgressDialog()
                showProgressDialog("ccc", true)
            }
            R.id.btnDownload -> {
                val start = System.currentTimeMillis()
                Log.e("download", "开始时间 $start")
                if (downloadUtil.getPathExists(url) != null) {
                    showToast("本地已存在")
                }
                downloadUtil.download(
                    url,
                    File(FileUtil.getExternalStorageProjectPath(this), "xx.exe").absolutePath
                ) { state, progress, size, contentLength, path, url, error ->
                    when (state) {
                        DownloadUtil.STATE_PROGRESS -> {
                            if (contentLength == -1L) {
                                Log.e("download", "进度${FileUtil.getAutoFileSizeStr(size)}")
                            } else {
                                Log.e("download", "进度${FileUtil.getAutoFileSizeStr(size)}/${FileUtil.getAutoFileSizeStr(contentLength)}($progress%)")
                            }
                            vm.downloadLD.postValue(progress)
                        }
                        DownloadUtil.STATE_SUCCESS -> {
                            val end = System.currentTimeMillis()
                            Log.e("download", "耗时${end - start}")
                        }
                        DownloadUtil.STATE_FAIL -> {
                            Log.e("download", "下载失败$error")
                        }
                        DownloadUtil.STATE_STOP -> {
                            Log.e("download", "下载停止")
                        }
                    }

                }
            }
            R.id.btnDownloadStop -> {
                downloadUtil.stop(url)
            }
            R.id.btnDownload2 -> {
                val m = mutableMapOf(
                    "https://d1.music.126.net/dmusic/cloudmusicsetup2.9.5.199424.exe" to "$filePath/xxx.exe",
                    "https://dldir1.qq.com/dlomg/sports/QQSports_1741.apk" to "$filePath/1.apk",
                    "https://qd.myapp.com/myapp/qqteam/AndroidQQ/Android_8.2.7.4395.apk" to "$filePath/2.exe",
                    "https://dldir1.qq.com/weixin/android/weixin7013android1640.apk" to "$filePath/3.exe",
                    "https://dldir1.qq.com/weixin/Windows/WeChatSetup.exe" to "$filePath/4.exe",
                    "https://d1.music.126.net/dmusic/cloudmusicsetup2.7.1.198242.exe" to "$filePath/6.exe",
                    "http://t6.tianditu.gov.cn/img_c/wmts?service=wmts&request=gettile&version=1.0.0&layer=img&format=tiles&STYLE=default&tilematrixset=c&tilecol=6729&tilerow=1400&tilematrix=13&tk=b77baf477f61aff4eb65003969b17809" to "$filePath/x.png"
                )
                multipleId = downloadUtil.downloadMultiple(m, "xx") { state, progress, successCount, fileCount, url, path, error ->
                    when (state) {
                        DownloadUtil.STATE_PROGRESS -> {
                            Log.e("multipleDownload", "progress $successCount/$fileCount($progress)")
                            vm.downloadMultipleLD.postValue(progress)
                        }
                        DownloadUtil.STATE_BLOCK_SUCCESS -> {
                            Log.e("multipleDownload", "blockSuccess $successCount/$fileCount($progress)")
                            vm.downloadMultipleLD.postValue(progress)
                        }
                        DownloadUtil.STATE_BLOCK_FAIL -> {
                            Log.e("multipleDownload", "blockFail")
                        }
                        DownloadUtil.STATE_SUCCESS -> {
                            Log.e("multipleDownload", "Success")
                        }
                        DownloadUtil.STATE_FAIL -> {
                            Log.e("multipleDownload", "Fail")
                        }
                        DownloadUtil.STATE_STOP -> {
                            Log.e("multipleDownload", "Stop")
                        }
                    }

                }
            }
            R.id.btnDownload2Stop -> {
                downloadUtil.stopMultiple(multipleId)
            }
            R.id.btnLiveData -> {
                try {
                    vm.byteLD.value = Byte.MAX_VALUE
                    vm.shortLD.value = Short.MAX_VALUE
                    vm.intLD.value = Int.MAX_VALUE
                    vm.longLD.value = Long.MAX_VALUE
                    vm.floatLD.value = Float.MAX_VALUE
                    vm.doubleLD.value = Double.MAX_VALUE
                } catch (e: Exception) {
                }
            }
        }
    }
}