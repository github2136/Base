package com.github2136.base.vm

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.github2136.basemvvm.BaseVM

/**
 * Created by YB on 2019/9/20
 */
class MainVM(app: Application) : BaseVM(app) {
    val downloadLD = MutableLiveData<Int>()
    override fun cancelRequest() {

    }
}