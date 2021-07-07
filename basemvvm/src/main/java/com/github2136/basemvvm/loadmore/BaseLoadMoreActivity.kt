package com.github2136.basemvvm.loadmore

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.github2136.basemvvm.BaseActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github2136.basemvvm.R

/**
 * Created by YB on 2019/9/20
 */
abstract class BaseLoadMoreActivity<V : BaseLoadMoreVM<T>, B : ViewDataBinding, T> : BaseActivity<V, B>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        findViewById<SwipeRefreshLayout>(R.id.srlList).setColorSchemeResources(R.color.colorPrimary)
        findViewById<RecyclerView>(R.id.rvList).addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        vm.baseInitData()
    }

    open fun onRefreshListener() {
        vm.baseInitData()
    }
}