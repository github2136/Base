package com.github2136.base.base

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import com.github2136.basemvvm.loadmore.BaseLoadMoreActivity
import com.github2136.basemvvm.loadmore.BaseLoadMoreVM

/**
 * Created by yb on 2022/11/19
 *
 */
abstract class AppBaseLoadMoreActivity<V : BaseLoadMoreVM<T>, B : ViewDataBinding, T> : BaseLoadMoreActivity<V, B, T>() {
    override fun preInitData(savedInstanceState: Bundle?) {
        super.preInitData(savedInstanceState)
    }
}