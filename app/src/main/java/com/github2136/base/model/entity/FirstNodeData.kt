package com.github2136.base.model.entity

import com.github2136.basemvvm.node.BaseNodeItem

/**
 * Created by YB on 2022/11/1
 */
data class FirstNodeData(val id: Int, val str: String, val child: MutableList<SecondNodeData>) : BaseNodeItem {
    override var isExpanded: Boolean = false
    override fun getChildNode(): List<BaseNodeItem>? {
        return child
    }
}