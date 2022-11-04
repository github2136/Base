package com.github2136.basemvvm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.github2136.base.ViewHolderRecyclerView

/**
 * Created by YB on 2019/9/23
 * 一种类型的Adapter
 */
abstract class BaseRecyclerVMAdapter<T, B : ViewDataBinding>(var list: MutableList<T>? = null) : RecyclerView.Adapter<ViewHolderRecyclerView>() {
    protected lateinit var mLayoutInflater: LayoutInflater

    /**
     * 通过类型获得布局ID
     */
    @androidx.annotation.LayoutRes
    abstract fun getLayoutId(viewType: Int): Int

    /**
     * 数据绑定到界面
     */
    protected abstract fun onBindView(t: T, bind: B, holder: ViewHolderRecyclerView, position: Int)

    /**
     * 获得对象
     */
    open fun getItem(position: Int): T? {
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
    protected var viewClickListener: ((position: Int, id: Int, view: View) -> Unit)? = null

    /**
     * 项点击
     */
    fun setOnItemClickListener(itemClickListener: (position: Int) -> Unit) {
        this.itemClickListener = itemClickListener
    }

    /**
     * 项长按
     */
    fun setOnItemLongClickListener(itemLongClickListener: (position: Int) -> Unit) {
        this.itemLongClickListener = itemLongClickListener
    }

    /**
     * 项子控件点击
     * @param position 项下标
     * @param id 控件id
     * @param view 控件对象
     */
    fun setOnViewClickListener(viewClickListener: (position: Int, id: Int, view: View) -> Unit) {
        this.viewClickListener = viewClickListener
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