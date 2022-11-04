package com.github2136.basemvvm.node

/**
 * Created by YB on 2022/11/1
 * 折叠节点对象接口
 */
interface BaseNodeItem {
    /**
     * 是否展开
     */
    var isExpanded: Boolean

    /**
     * 子节点，如果为null则表示叶节点
     */
    fun getChildNode(): Collection<BaseNodeItem>?
}