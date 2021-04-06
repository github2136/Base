package com.github2136.basemvvm

import android.content.Context
import android.content.SharedPreferences
import com.github2136.util.JsonUtil
import com.github2136.util.NetworkUtil
import com.github2136.util.SPUtil

/**
 * Created by YB on 2020/7/3
 *
 */
abstract class BaseRepository(context: Context) {
    val failedStr = "无法连接服务器"

    val networkUtil by lazy { NetworkUtil.getInstance(context) }
    val mJsonUtil by lazy { JsonUtil.instance }
    val mSpUtil: SharedPreferences = SPUtil.getSharedPreferences(context)

    //取消请求
    open fun cancelRequest() {}
}

