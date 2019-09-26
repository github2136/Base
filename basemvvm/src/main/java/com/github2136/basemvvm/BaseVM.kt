package com.github2136.basemvvm

import android.app.Application
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.github2136.util.JsonUtil
import com.github2136.util.SPUtil

/**
 * Created by YB on 2019/8/29
 */
abstract class BaseVM(app: Application) : AndroidViewModel(app) {
    protected lateinit var mTag: String
    val loadingStr = "请稍后……"
    val failedStr = "无法连接服务器"
    var mJsonUtil: JsonUtil = JsonUtil.instance
    var mSpUtil: SharedPreferences = SPUtil.getSharedPreferences(app)
    //显示dialog
    val ldDialog = MutableLiveData<String>()
    val handle = Handler(Looper.getMainLooper())

    open fun init(tag: String) {
        mTag = tag
    }

    //取消请求
    abstract fun cancelRequest()
}