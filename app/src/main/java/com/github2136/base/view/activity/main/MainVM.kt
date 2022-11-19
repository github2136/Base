package com.github2136.base.view.activity.main

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.github2136.basemvvm.BaseVM

/**
 * Created by YB on 2019/9/20
 */
class MainVM(app: Application) : BaseVM(app) {
    val downloadLD = MutableLiveData<Int>()
    val downloadMultipleLD = MutableLiveData<Int>()

    val byteLD = MutableLiveData<Byte>()
    val shortLD = MutableLiveData<Short>()
    val intLD = MutableLiveData<Int>()
    val longLD = MutableLiveData<Long>()
    val floatLD = MutableLiveData<Float>()
    val doubleLD = MutableLiveData<Double>()
}