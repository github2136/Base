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
        vm.rightBtnLD.value="xxx"

        urlAndPath = mutableMapOf(
            "http://mt0.google.cn/vt/lyrs=y&gl=cn&scale=2&x=3&y=1&z=2" to "$filePath/Offlinemap/google/2/3,1",
            "http://mt0.google.cn/vt/lyrs=y&gl=cn&scale=2&x=6&y=3&z=3" to "$filePath/Offlinemap/google/3/6,3",
            "http://mt0.google.cn/vt/lyrs=y&gl=cn&scale=2&x=13&y=6&z=4" to "$filePath/Offlinemap/google/4/13,6",
            "http://mt0.google.cn/vt/lyrs=y&gl=cn&scale=2&x=26&y=13&z=5" to "$filePath/Offlinemap/google/5/26,13",
            "http://mt0.google.cn/vt/lyrs=y&gl=cn&scale=2&x=52&y=26&z=6" to "$filePath/Offlinemap/google/6/52,26",
            "http://mt0.google.cn/vt/lyrs=y&gl=cn&scale=2&x=105&y=53&z=7" to "$filePath/Offlinemap/google/7/105,53",
            "http://mt0.google.cn/vt/lyrs=y&gl=cn&scale=2&x=210&y=106&z=8" to "$filePath/Offlinemap/google/8/210,106",
            "http://mt0.google.cn/vt/lyrs=y&gl=cn&scale=2&x=420&y=213&z=9" to "$filePath/Offlinemap/google/9/420,213",
            "http://mt0.google.cn/vt/lyrs=y&gl=cn&scale=2&x=841&y=426&z=10" to "$filePath/Offlinemap/google/10/841,426",
            "http://mt0.google.cn/vt/lyrs=y&gl=cn&scale=2&x=1683&y=853&z=11" to "$filePath/Offlinemap/google/11/1683,853",
            "http://mt0.google.cn/vt/lyrs=y&gl=cn&scale=2&x=3366&y=1706&z=12" to "$filePath/Offlinemap/google/12/3366,1706",
            "http://mt0.google.cn/vt/lyrs=y&gl=cn&scale=2&x=3366&y=1707&z=12" to "$filePath/Offlinemap/google/12/3366,1707",
            "http://mt0.google.cn/vt/lyrs=y&gl=cn&scale=2&x=6732&y=3413&z=13" to "$filePath/Offlinemap/google/13/6732,3413",
            "http://mt0.google.cn/vt/lyrs=y&gl=cn&scale=2&x=6732&y=3414&z=13" to "$filePath/Offlinemap/google/13/6732,3414",
            "http://mt0.google.cn/vt/lyrs=y&gl=cn&scale=2&x=6733&y=3413&z=13" to "$filePath/Offlinemap/google/13/6733,3413",
            "http://mt0.google.cn/vt/lyrs=y&gl=cn&scale=2&x=6733&y=3414&z=13" to "$filePath/Offlinemap/google/13/6733,3414"
        )
    }

    val url = "http://mt0.google.cn/vt/lyrs=y&gl=cn&scale=2&x=53868&y=27313&z=16"
    //    val url = "https://qd.myapp.com/myapp/qqteam/AndroidQQ/Android_8.2.7.4395.apk"
    var multipleId = ""

    fun onClick(view: View) {
        when (view.id) {
            R.id.btnClick1        -> {
                startActivity(Intent(this, ListActivity::class.java))
            }
            R.id.btnClick2        -> {
                startActivity(Intent(this, LoadMoreActivity::class.java))
            }
            R.id.btnClick3        -> {
                showProgressDialog("aaaa", true)
                dismissProgressDialog()
                showProgressDialog("ccc", true)
            }
            R.id.btnDownload      -> {
                val start = System.currentTimeMillis()
                Log.e("download", "$start")
                if (downloadUtil.getPathExists(url) != null) {
                    showToast("本地已存在")
                }
                downloadUtil.download(
                    url,
                    File(FileUtil.getExternalStorageProjectPath(this), "xx.jpg").absolutePath
                ) { state, progress, path, error ->
                    when (state) {
                        DownloadUtil.STATE_PROGRESS -> {
                            Log.e("download", "$progress")
                            vm.downloadLD.postValue(progress)
                        }
                        DownloadUtil.STATE_SUCCESS  -> {
                            val end = System.currentTimeMillis()
                            Log.e("download", "${end - start}")
                        }
                        DownloadUtil.STATE_FAIL     -> {
                            Log.e("download", "下载失败$error")
                        }
                        DownloadUtil.STATE_STOP     -> {
                            Log.e("download", "下载停止")
                        }
                    }

                }
            }
            R.id.btnDownloadStop  -> {
                downloadUtil.stop(url)
            }
            R.id.btnDownload2     -> {
//                val m = mutableMapOf(
//                    "https://dldir1.qq.com/dlomg/sports/QQSports_1741.apk" to filePath + "/1.txt",
//                    "https://qd.myapp.com/myapp/qqteam/AndroidQQ/Android_8.2.7.4395.apk" to filePath + "/2.exe",
//                    "https://dldir1.qq.com/weixin/android/weixin7013android1640.apk" to filePath + "/3.exe",
//                    "https://dldir1.qq.com/weixin/Windows/WeChatSetup.exe" to filePath + "/4.exe",
//                    "https://9c80a9f7765f4da5bb5baa78fdc41def.dd.cdntips.com/dlied6.qq.com/invc/xfspeed/qqpcmgr/download/QQPCDownload1671.exe?mkey=5e99e43231d3b95d&f=24c3&cip=49.211.159.168&proto=https" to filePath + "/5.exe",
//                    "https://d1.music.126.net/dmusic/cloudmusicsetup2.7.1.198242.exe" to filePath + "/6.exe"
//                )
                var i = 0
                multipleId = downloadUtil.downloadMultiple(urlAndPath, "xx") { state, progress, path, url, error ->
                    when (state) {
                        DownloadUtil.STATE_BLOCK_SUCCESS -> {
                            Log.e("multipleDownload", "blockSuccess ${i++}")
                        }
                        DownloadUtil.STATE_BLOCK_FAIL    -> {
                            Log.e("multipleDownload", "blockFail")
                        }
                        DownloadUtil.STATE_SUCCESS       -> {
                            Log.e("multipleDownload", "Success")
                        }
                        DownloadUtil.STATE_FAIL          -> {
                            Log.e("multipleDownload", "Fail")
                        }
                        DownloadUtil.STATE_STOP          -> {
                            Log.e("multipleDownload", "Stop")
                        }
                    }
                    vm.downloadMultipleLD.postValue(progress)
                }
            }
            R.id.btnDownload2Stop -> {
                downloadUtil.stopMultiple(multipleId)
            }
        }
    }

    override fun initObserve() {

    }

}