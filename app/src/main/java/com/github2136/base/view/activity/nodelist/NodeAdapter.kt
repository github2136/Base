package com.github2136.base.view.activity.nodelist

import androidx.databinding.ViewDataBinding
import com.github2136.base.R
import com.github2136.base.ViewHolderRecyclerView
import com.github2136.base.databinding.*
import com.github2136.base.model.entity.FirstNodeData
import com.github2136.base.model.entity.LeafData
import com.github2136.base.model.entity.SecondNodeData
import com.github2136.basemvvm.node.BaseNodeAdapter
import com.github2136.basemvvm.node.BaseNodeItem

/**
 * Created by YB on 2022/11/1
 * 折叠布局
 */
class NodeAdapter(list: MutableList<BaseNodeItem>) : BaseNodeAdapter(list) {
    override fun getViewType(position: Int): Int {
        return when (getItem(position)) {
            is FirstNodeData -> 0
            is SecondNodeData -> 1
            else -> 2
        }
    }

    override fun getLayoutId(viewType: Int): Int {
        return when (viewType) {
            0 -> R.layout.item_node_first
            1 -> R.layout.item_node_second
            else -> R.layout.item_node_leaf
        }
    }

    override fun onBindView(t: BaseNodeItem, bind: ViewDataBinding, holder: ViewHolderRecyclerView, position: Int) {
        when (bind) {
            is ItemNodeFirstBinding -> {
                bind.user = t as FirstNodeData
                bind.executePendingBindings()
            }
            is ItemNodeSecondBinding -> {
                bind.user = t as SecondNodeData
                bind.executePendingBindings()
            }
            is ItemNodeLeafBinding -> {
                bind.user = t as LeafData
                bind.executePendingBindings()
            }
        }
    }
}