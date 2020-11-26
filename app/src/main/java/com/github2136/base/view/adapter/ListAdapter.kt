package com.github2136.base.view.adapter

import androidx.databinding.ViewDataBinding
import com.github2136.base.R
import com.github2136.base.ViewHolderRecyclerView
import com.github2136.base.databinding.ItemList2Binding
import com.github2136.base.databinding.ItemListBinding
import com.github2136.base.model.entity.User
import com.github2136.basemvvm.BaseRecyclerVMAdapter

/**
 * Created by YB on 2019/9/23
 * 单一类型布局
 */
class ListAdapter(list: MutableList<User>) : BaseRecyclerVMAdapter<User, ItemListBinding>(list) {
    override fun getLayoutId(viewType: Int) = R.layout.item_list

    override fun onBindView(t: User, bind: ItemListBinding, holder: ViewHolderRecyclerView, position: Int) {
        bind.user = t
        bind.executePendingBindings()
    }
}

/**
 * Created by YB on 2019/9/24
 * 多种类型布局
 */
class ListMultipleAdapter(list: MutableList<User>) : BaseRecyclerVMAdapter<User, ViewDataBinding>(list) {
    override fun getItemViewType(position: Int): Int {
        return when (position % 2) {
            0 -> 0
            else -> 1
        }
    }

    override fun getLayoutId(viewType: Int) =
        when (viewType) {
            0 -> R.layout.item_list
            else -> R.layout.item_list2
        }


    override fun onBindView(t: User, bind: ViewDataBinding, holder: ViewHolderRecyclerView, position: Int) {
        if (bind is ItemListBinding) {
            bind.user = t
            bind.executePendingBindings()
        } else if (bind is ItemList2Binding) {
            bind.user = t
            bind.executePendingBindings()
        }
    }
}