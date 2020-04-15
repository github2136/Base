package com.github2136.base

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import com.github2136.base.databinding.ActivityMainBinding
import com.github2136.base.vm.MainVM
import com.github2136.basemvvm.BaseActivity
import com.github2136.basemvvm.download.DownloadUtil
import com.github2136.util.FileUtil
import java.io.File

class MainActivity : BaseActivity<MainVM, ActivityMainBinding>() {
    override fun getLayoutId() = R.layout.activity_main
    val downloadUtil by lazy { DownloadUtil.getInstance(application) }

    override fun initData(savedInstanceState: Bundle?) {
        bind.view = this
        bind.vm = vm
    }

    var i = 0
    fun onClick(view: View) {
        when (view.id) {
            R.id.btnClick1    -> {
                startActivity(Intent(this, ListActivity::class.java))
            }
            R.id.btnClick2    -> {
                startActivity(Intent(this, LoadMoreActivity::class.java))
            }
            R.id.btnClick3    -> {
                showProgressDialog("aaaa")
                dismissProgressDialog()
                showProgressDialog("ccc")
            }
            R.id.btnDownload  -> {
                val url = "https://qd.myapp.com/myapp/qqteam/AndroidQQ/Android_8.2.7.4395.apk"
                val start = System.currentTimeMillis()
                Log.e("download", "$start")
                if (downloadUtil.getPathExists(url) != null) {
                    showToast("本地已存在")
                }
                downloadUtil.download(url, File(FileUtil.getExternalStorageRootPath(), "qq.apk").absolutePath) { state, progress, path, error ->
                    when (state) {
                        DownloadUtil.STATE_DOWNLOAD -> {
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
            R.id.btnDownload2 -> {
            }
        }
    }

    override fun initObserve() {

    }
}