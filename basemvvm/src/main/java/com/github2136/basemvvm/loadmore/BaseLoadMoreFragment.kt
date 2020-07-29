package com.github2136.basemvvm.loadmore

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github2136.basemvvm.BaseFragment
import com.github2136.basemvvm.R

/**
 * Created by YB on 2020/7/28
 */
abstract class BaseLoadMoreFragment<V : BaseLoadMoreVM<T>, B : ViewDataBinding, T> : BaseFragment<V, B>() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        bind.root.findViewById<SwipeRefreshLayout>(R.id.srlList)?.setColorSchemeResources(R.color.colorPrimary)
        vm.baseInitData()
    }

    open fun onRefreshListener() {
        vm.baseInitData()
    }
}