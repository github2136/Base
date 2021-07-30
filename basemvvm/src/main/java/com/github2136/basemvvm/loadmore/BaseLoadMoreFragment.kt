package com.github2136.basemvvm.loadmore

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github2136.basemvvm.BaseFragment
import com.github2136.basemvvm.R

/**
 * Created by YB on 2020/7/28
 */
abstract class BaseLoadMoreFragment<V : BaseLoadMoreVM<T>, B : ViewDataBinding, T> : BaseFragment<V, B>() {
    open var autoInit = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind.root.findViewById<SwipeRefreshLayout>(R.id.srlList)?.setColorSchemeResources(R.color.colorPrimary)
        bind.root.findViewById<RecyclerView>(R.id.rvList).addItemDecoration(DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL))
        if (autoInit) {
            vm.baseInitData()
        }
    }

    open fun onRefreshListener() {
        vm.baseInitData()
    }
}