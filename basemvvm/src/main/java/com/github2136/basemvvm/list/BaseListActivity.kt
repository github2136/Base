package com.github2136.basemvvm.list

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import com.github2136.basemvvm.BaseActivity

/**
 * Created by YB on 2019/9/20
 */
abstract class BaseListActivity<V : BaseListVM<T>, B : ViewDataBinding, T> : BaseActivity<V, B>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm.baseInitData()
    }

    open fun onRefreshListener() {
        vm.baseInitData()
    }
}