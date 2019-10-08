package com.github2136.basemvvm

import android.app.Application
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.github2136.util.JsonUtil
import com.github2136.util.SPUtil
import retrofit2.Call

/**
 * Created by YB on 2019/8/29
 */
abstract class BaseVM(app: Application) : AndroidViewModel(app) {

    val calls = mutableListOf<Call<*>>()

    val loadingStr = "请稍后……"
    val failedStr = "无法连接服务器"
    var mJsonUtil: JsonUtil = JsonUtil.instance
    var mSpUtil: SharedPreferences = SPUtil.getSharedPreferences(app)
    //显示dialog
    val ldDialog = MutableLiveData<String>()
    val handle = Handler(Looper.getMainLooper())

    @Synchronized
    fun addCall(call: Call<*>) {
        val iterable = calls.iterator()
        while (iterable.hasNext()) {
            val c = iterable.next()
            if (c.isExecuted || c.isCanceled) {
                //把已经执行或取消的对象移除
                iterable.remove()
            }
        }
        calls.add(call)
    }

    //取消请求
    open fun cancelRequest() {
        val iterable = calls.iterator()
        while (iterable.hasNext()) {
            val c = iterable.next()
            c.cancel()
            iterable.remove()
        }
    }
}