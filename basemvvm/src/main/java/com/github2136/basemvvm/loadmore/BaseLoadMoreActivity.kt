package com.github2136.basemvvm.loadmore

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github2136.basemvvm.BaseActivity
import com.github2136.basemvvm.R

/**
 * Created by YB on 2019/9/20
 */
abstract class BaseLoadMoreActivity<V : BaseLoadMoreVM<T>, B : ViewDataBinding, T> : BaseActivity<V, B>() {
    open var autoInit = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        findViewById<SwipeRefreshLayout>(R.id.srlList).setColorSchemeResources(R.color.colorPrimary)
        findViewById<RecyclerView>(R.id.rvList).let {
            val lm = it.layoutManager
            if (lm is GridLayoutManager) {
                lm.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return if (position == it.adapter!!.itemCount - 1) {
                            lm.spanCount
                        } else {
                            1
                        }
                    }
                }
            } else {
                it.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
            }
        }
        if (autoInit) {
            vm.baseInitData()
        }
    }

    open fun onRefreshListener() {
        vm.baseInitData()
    }
}