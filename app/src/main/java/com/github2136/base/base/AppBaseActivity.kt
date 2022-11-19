package com.github2136.base.base

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import com.github2136.basemvvm.BaseActivity
import com.github2136.basemvvm.BaseVM

/**
 *
 */
abstract class AppBaseActivity<V : BaseVM, B : ViewDataBinding> : BaseActivity<V, B>() {
    override fun preInitData(savedInstanceState: Bundle?) {
        super.preInitData(savedInstanceState)
    }
}