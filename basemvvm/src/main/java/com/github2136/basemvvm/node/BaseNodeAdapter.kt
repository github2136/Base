package com.github2136.basemvvm.node

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.github2136.base.ViewHolderRecyclerView

/**
 * Created by YB on 2022/11/1
 * 折叠节点Adapter
 */
abstract class BaseNodeAdapter(var list: MutableList<BaseNodeItem>? = null) : RecyclerView.Adapter<ViewHolderRecyclerView>() {
    protected lateinit var mLayoutInflater: LayoutInflater

    /**
     * 通过下标获取类型
     */
    protected abstract fun getViewType(position: Int): Int

    /**
     * 通过类型获得布局ID
     */
    @androidx.annotation.LayoutRes
    abstract fun getLayoutId(viewType: Int): Int

    /**
     * 绑定数据
     */
    protected abstract fun onBindView(t: BaseNodeItem, bind: ViewDataBinding, holder: ViewHolderRecyclerView, position: Int)

    override fun getItemViewType(position: Int): Int {
        return getViewType(position)
    }

    /**
     * 获得对象
     */
    open fun getItem(position: Int): BaseNodeItem? {
        return list?.get(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderRecyclerView {
        if (!::mLayoutInflater.isInitialized) {
            mLayoutInflater = LayoutInflater.from(parent.context)
        }
        val bind = DataBindingUtil.inflate<ViewDataBinding>(mLayoutInflater, getLayoutId(viewType), parent, false)
        return ViewHolderRecyclerView(bind.root, ::itemClickListener, ::itemLongClickListener)
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

    override fun onBindViewHolder(holder: ViewHolderRecyclerView, position: Int) {
        getItem(position)?.let {
            onBindView(it, DataBindingUtil.getBinding(holder.itemView)!!, holder, position)
        }
    }

    private fun itemClickListener(position: Int) {
        getItem(position)?.apply {
            if (getChildNode() == null) {
                itemLeafClickListener?.invoke(position)
            } else {
                if (isExpanded) {
                    collapse(position, true)
                } else {
                    expand(position, true)
                }
            }
        }
    }

    fun itemLongClickListener(position: Int) {
        getItem(position)?.apply {
            if (getChildNode() == null) {
                itemLeafLongClickListener?.invoke(position)
            }
        }
    }

    protected var itemLeafClickListener: ((Int) -> Unit)? = null
    protected var itemLeafLongClickListener: ((Int) -> Unit)? = null
    protected var viewClickListener: ((position: Int, id: Int, view: View) -> Unit)? = null

    /**
     * 叶节点点击
     */
    fun setOnItemLeafClickListener(itemLeafClickListener: (position: Int) -> Unit) {
        this.itemLeafClickListener = itemLeafClickListener
    }

    /**
     * 叶节点长按
     */
    fun setOnItemLeafLongClickListener(itemLeafLongClickListener: (position: Int) -> Unit) {
        this.itemLeafLongClickListener = itemLeafLongClickListener
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

    /**
     * 展开子项
     */
    private fun expand(position: Int, animate: Boolean = true) {
        val node = getItem(position)
        node?.apply {
            node.isExpanded = true
            if (node.getChildNode().isNullOrEmpty()) {
                notifyItemChanged(position)
            } else {
                val items = flatData(getChildNode()!!, false)
                val size = items.size
                list?.addAll(position + 1, items)
                if (animate) {
                    notifyItemChanged(position)
                    notifyItemRangeInserted(position + 1, size)
                } else {
                    notifyDataSetChanged()
                }
            }
        }
    }

    /**
     * 折叠子项
     */
    private fun collapse(position: Int, animate: Boolean = true) {
        val node = getItem(position)
        node?.apply {
            node.isExpanded = false
            if (node.getChildNode().isNullOrEmpty()) {
                notifyItemChanged(position)
            } else {
                val items = flatData(getChildNode()!!, false)
                val size = items.size
                list?.removeAll(items)
                if (animate) {
                    notifyItemChanged(position)
                    notifyItemRangeRemoved(position + 1, size)
                } else {
                    notifyDataSetChanged()
                }
            }
        }
    }

    /**
     * 将输入的嵌套类型数组循环递归，在扁平化数据的同时，设置展开状态
     * @param list Collection<BaseNodeItem>
     * @param isExpanded Boolean? 如果不需要改变状态，设置为null。true 为展开，false 为收起
     */
    private fun flatData(list: Collection<BaseNodeItem>, isExpanded: Boolean? = null): MutableList<BaseNodeItem> {
        val newList = ArrayList<BaseNodeItem>()
        for (element in list) {
            newList.add(element)
            // 如果是展开状态 或者需要设置为展开状态
            if (isExpanded == true || element.isExpanded) {
                val childNode = element.getChildNode()
                if (!childNode.isNullOrEmpty()) {
                    val items = flatData(childNode, isExpanded)
                    newList.addAll(items)
                }
            }
            isExpanded?.let {
                element.isExpanded = it
            }
        }
        return newList
    }

    fun setData(list: MutableList<BaseNodeItem>) {
        this.list = flatData(list)
        notifyDataSetChanged()
    }
}