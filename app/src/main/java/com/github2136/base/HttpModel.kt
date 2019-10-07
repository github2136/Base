package com.github2136.base

import android.app.Application
import com.github2136.basemvvm.BaseModel

/**
 * Created by yb on 2019/10/7
 */
class HttpModel(app: Application, tag: String) : BaseModel(app, tag) {
    val api by lazy { retrofit.create(HttpService::class.java) }

}