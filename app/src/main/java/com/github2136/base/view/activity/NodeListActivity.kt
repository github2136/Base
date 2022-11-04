package com.github2136.base.view.activity

import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github2136.base.R
import com.github2136.base.databinding.ActivityNodeListBinding
import com.github2136.base.model.entity.LeafData
import com.github2136.base.vm.activity.NodeListVM
import com.github2136.basemvvm.BaseActivity

class NodeListActivity : BaseActivity<NodeListVM, ActivityNodeListBinding>() {
    override fun getLayoutId() = R.layout.activity_node_list

    override fun initData(savedInstanceState: Bundle?) {
        bind.vm = vm
        bind.view = this

        findViewById<RecyclerView>(com.github2136.basemvvm.R.id.rvList).let {
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
        vm.setData()
        vm.adapter.setOnItemLeafClickListener { position ->
            val item = vm.adapter.getItem(position)
            if (item is LeafData) {
                showToast(item.str)
            }
        }
    }
}
