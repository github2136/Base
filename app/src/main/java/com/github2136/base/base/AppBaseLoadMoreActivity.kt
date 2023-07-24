package com.github2136.base.base

import androidx.databinding.ViewDataBinding
import com.github2136.basemvvm.loadmore.BaseLoadMoreActivity
import com.github2136.basemvvm.loadmore.BaseLoadMoreVM

/**
 * Created by 44569 on 2023/7/24
 */
abstract class AppBaseLoadMoreActivity<V : BaseLoadMoreVM<*>, B : ViewDataBinding> : BaseLoadMoreActivity<V, B>() {
}