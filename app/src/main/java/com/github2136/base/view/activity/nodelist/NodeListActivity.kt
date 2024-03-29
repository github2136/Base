package com.github2136.base.view.activity.nodelist

import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.github2136.base.R
import com.github2136.base.base.AppBaseActivity
import com.github2136.base.databinding.ActivityNodeListBinding
import com.github2136.base.model.entity.LeafData

class NodeListActivity : AppBaseActivity<NodeListVM, ActivityNodeListBinding>() {

    override fun initData(savedInstanceState: Bundle?) {
        bind.vm = vm
        bind.view = this

        findViewById<RecyclerView>(com.github2136.basemvvm.R.id.rvList).addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        vm.setData()
        vm.adapter.setOnItemLeafClickListener { position ->
            val item = vm.adapter.getItem(position)
            if (item is LeafData) {
                showToast(item.str)
            }
        }
    }
}
