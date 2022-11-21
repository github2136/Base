package com.github2136.base.base

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import com.github2136.basemvvm.loadmore.BaseLoadMoreActivity
import com.github2136.basemvvm.loadmore.BaseLoadMoreFragment
import com.github2136.basemvvm.loadmore.BaseLoadMoreVM

/**
 * 基础类
 */
abstract class AppBaseLoadMoreFragment<V : BaseLoadMoreVM<T>, B : ViewDataBinding, T> : BaseLoadMoreFragment<V, B, T>() {
    override fun preInitData(savedInstanceState: Bundle?) {
        super.preInitData(savedInstanceState)
    }
}