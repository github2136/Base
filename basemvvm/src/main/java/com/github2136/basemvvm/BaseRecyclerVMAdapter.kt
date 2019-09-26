package com.github2136.basemvvm

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.github2136.base.ViewHolderRecyclerView

/**
 * Created by YB on 2019/9/23
 * 一种类型的Adapter
 */
abstract class BaseRecyclerVMAdapter<T, B : ViewDataBinding>(protected var list: MutableList<T>? = null) :
    RecyclerView.Adapter<ViewHolderRecyclerView>() {
    protected lateinit var mLayoutInflater: LayoutInflater
    /**
     * 通过类型获得布局ID
     *
     * @param viewType
     * @return
     */
    @androidx.annotation.LayoutRes
    abstract fun getLayoutId(viewType: Int): Int

    protected abstract fun onBindView(t: T, bind: B, holder: ViewHolderRecyclerView, position: Int)

    /**
     * 获得对象
     */
    fun getItem(position: Int): T? {
        return list?.get(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderRecyclerView {
        if (!::mLayoutInflater.isInitialized) {
            mLayoutInflater = LayoutInflater.from(parent.context)
        }
        val bind = DataBindingUtil.inflate<B>(mLayoutInflater, getLayoutId(viewType), parent, false)
        return ViewHolderRecyclerView(bind.root, itemClickListener, itemLongClickListener)
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolderRecyclerView, position: Int) {
        getItem(position)?.let {
            onBindView(it, DataBindingUtil.getBinding<B>(holder.itemView)!!, holder, position)
        }
    }

    protected var itemClickListener: ((Int) -> Unit)? = null
    protected var itemLongClickListener: ((Int) -> Unit)? = null

    fun setOnItemClickListener(itemClickListener: (position: Int) -> Unit) {
        this.itemClickListener = itemClickListener
    }

    fun setOnItemLongClickListener(itemLongClickListener: (position: Int) -> Unit) {
        this.itemLongClickListener = itemLongClickListener
    }

    fun setData(list: MutableList<T>) {
        this.list = list
        notifyDataSetChanged()
    }

    fun appendData(list: List<T>) {
        this.list?.let {
            it.addAll(list)
            notifyDataSetChanged()
        }
    }
}