package com.github2136.basemvvm.list

import android.os.Bundle
import android.util.Log
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.github2136.basemvvm.BaseActivity

/**
 * Created by YB on 2019/9/20
 */
abstract class BaseListActivity<V : BaseListVM<T>, B : ViewDataBinding, T> : BaseActivity<V, B>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm.initData()
    }
}