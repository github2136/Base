package com.github2136.base.model.entity

import com.github2136.basemvvm.node.BaseNodeItem

/**
 * Created by YB on 2022/11/2
 * 叶节点
 */
data class LeafData(val id: Int, val str: String) : BaseNodeItem {
    override var isExpanded = false
    override fun getChildNode() = null

}