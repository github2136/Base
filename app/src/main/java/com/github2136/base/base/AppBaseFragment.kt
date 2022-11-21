package com.github2136.base.base

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import com.github2136.basemvvm.BaseFragment
import com.github2136.basemvvm.BaseVM

/**
 * 基础类
 */
abstract class AppBaseFragment<V : BaseVM, B : ViewDataBinding> : BaseFragment<V, B>() {
    override fun preInitData(savedInstanceState: Bundle?) {
        super.preInitData(savedInstanceState)
    }
}