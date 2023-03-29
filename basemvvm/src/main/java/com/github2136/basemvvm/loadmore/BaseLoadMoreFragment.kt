package com.github2136.basemvvm.loadmore

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github2136.basemvvm.BaseFragment
import com.github2136.basemvvm.IBaseFragment
import com.github2136.basemvvm.R

/**
 * Created by YB on 2020/7/28
 */
abstract class BaseLoadMoreFragment<V : BaseLoadMoreVM<*>, B : ViewDataBinding>(iBaseFragment: IBaseFragment? = null) : BaseFragment<V, B>(iBaseFragment) {
    open var autoInit = true
    protected val rvList by lazy { bind.root.findViewById<RecyclerView>(R.id.rvList) }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind.root.findViewById<SwipeRefreshLayout>(R.id.srlList)?.setColorSchemeResources(R.color.colorPrimary)
        rvList.let {
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
                it.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            }
        }
        if (autoInit) {
            vm.baseInitData()
        }
    }


    /**
     * 刷新数据，直接使用baseInitData()会出现不滚动顶部情况
     */
    fun refreshData() {
        rvList.adapter = vm.adapter
        vm.baseInitData()
    }

    open fun onRefreshListener() {
        vm.baseInitData()
    }
}