package com.github2136.base

import android.content.Intent
import android.os.Bundle
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
//                if (downloadUtil.getPathExists(url) == null) {
                    downloadUtil.download(url, File(FileUtil.getExternalStorageRootPath(), "163.exe").absolutePath) { state, progress, path, error ->
                        when (state) {
                            DownloadUtil.STATE_DOWNLOAD -> {
                                vm.downloadLD.postValue(progress)
                            }
                            DownloadUtil.STATE_SUCCESS  -> {
                                showToast("下载完成$path")
                            }
                            DownloadUtil.STATE_FAIL     -> {
                                showToast("下载失败$error")
                            }
                            DownloadUtil.STATE_STOP     -> {
                                showToast("下载停止")
                            }
                        }

                    }
//                } else {
//                    showToast("下载已完成")
//                }
            }
            R.id.btnDownload2 -> {
                val url = "https://qd.myapp.com/myapp/qqteam/AndroidQQ/Android_8.2.7.4395.apk"
//                if (downloadUtil.getPathExists(url) == null) {
                    downloadUtil.download(url, File(FileUtil.getExternalStorageRootPath(), "163.exe").absolutePath) { state, progress, path, error ->
                        when (state) {
                            DownloadUtil.STATE_DOWNLOAD -> {
                                vm.downloadLD.postValue(progress)
                            }
                            DownloadUtil.STATE_SUCCESS  -> {
                                showToast("下载完成$path")
                            }
                            DownloadUtil.STATE_FAIL     -> {
                                showToast("下载失败$error")
                            }
                            DownloadUtil.STATE_STOP     -> {
                                showToast("下载停止")
                            }
                        }

                    }
//                } else {
//                    showToast("下载已完成")
//                }
            }
        }
    }

    override fun initObserve() {

    }
}