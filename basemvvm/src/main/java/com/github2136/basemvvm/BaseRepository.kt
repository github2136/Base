package com.github2136.basemvvm

import android.app.Application
import android.content.SharedPreferences
import com.github2136.util.JsonUtil
import com.github2136.util.NetworkUtil
import com.github2136.util.SPUtil

/**
 * Created by YB on 2020/7/3
 *
 */
abstract class BaseRepository(app: Application) {
    val failedStr = "无法连接服务器"

    val networkUtil by lazy { NetworkUtil.getInstance(app) }
    val mJsonUtil: JsonUtil = JsonUtil.instance
    val mSpUtil: SharedPreferences = SPUtil.getSharedPreferences(app)

    //取消请求
    open fun cancelRequest() {}
}

