package com.github2136.basemvvm

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

/**
 * Created by YB on 2019/8/29
 */
abstract class BaseVM(app: Application) : AndroidViewModel(app) {
    protected val TAG = this.javaClass.name
    val loadingStr = "请稍后……"
    //显示dialog
    val dialogLD = MutableLiveData<String>()
    val toastLD = MutableLiveData<String>()
    val titleTextLD = MutableLiveData<String>()
    val rightBtnLD = MutableLiveData<String>()
    val handle = Handler(Looper.getMainLooper())

    //取消请求
    open fun cancelRequest() {}
}