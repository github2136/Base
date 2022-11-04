package com.github2136.base.vm.activity

import android.app.Application
import com.github2136.base.model.entity.FirstNodeData
import com.github2136.base.model.entity.LeafData
import com.github2136.base.model.entity.SecondNodeData
import com.github2136.base.view.adapter.NodeAdapter
import com.github2136.basemvvm.BaseVM
import com.github2136.basemvvm.node.BaseNodeItem

/**
 * Created by YB on 2019/9/23
 */
class NodeListVM(app: Application) : BaseVM(app) {

    val adapter = NodeAdapter(mutableListOf())

    fun setData() {
        launch {
            val first = mutableListOf<BaseNodeItem>()
            repeat(5) { i ->
                val second = mutableListOf<SecondNodeData>()
                repeat(4) { j ->
                    val leaf = mutableListOf<LeafData>()
                    repeat(3) { k ->
                        leaf.add(LeafData(k, "str$i$j$k"))
                    }
                    second.add(SecondNodeData(i, "str$i$j", leaf))
                }
                first.add(FirstNodeData(i, "str$i", second))
            }
            adapter.setData(first)
        }
    }
}