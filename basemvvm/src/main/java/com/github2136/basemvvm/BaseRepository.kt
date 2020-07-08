package com.github2136.basemvvm

import android.app.Application
import android.content.SharedPreferences
import com.github2136.util.JsonUtil
import com.github2136.util.NetworkUtil
import com.github2136.util.SPUtil
import retrofit2.Call

/**
 * Created by YB on 2020/7/3
 *
 */
abstract class BaseRepository(app: Application) {
    val failedStr = "无法连接服务器"
    val calls = mutableListOf<Call<*>>()

    val networkUtil by lazy { NetworkUtil.getInstance(app) }
    val mJsonUtil: JsonUtil = JsonUtil.instance
    val mSpUtil: SharedPreferences = SPUtil.getSharedPreferences(app)

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

interface RepositoryCallback<T> {
    fun onSuccess(t: T)
    fun onFail(errorCode: Int, msg: String)
}