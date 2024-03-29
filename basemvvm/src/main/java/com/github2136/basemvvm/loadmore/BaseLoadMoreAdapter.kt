package com.github2136.basemvvm.loadmore

import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.MutableLiveData
import com.github2136.base.ViewHolderRecyclerView
import com.github2136.basemvvm.BaseRecyclerVMAdapter
import com.github2136.basemvvm.R

/**
 * Created by YB on 2019/9/23
 * viewType -1,-2,-3不能使用
 */
abstract class BaseLoadMoreAdapter<T, B : ViewDataBinding> : BaseRecyclerVMAdapter<T, B>() {
    var pageIndex = 1 //页码
    var pageCount = 20 //每页数量
    var refreshing = MutableLiveData<Boolean>() //刷新状态 true 刷新中 false 刷新完成
    var loading = MutableLiveData<Boolean>() //加载更多 true 获取更多中 false 获取完成
    var result = MutableLiveData<Boolean>() //数据获取结果 true 数据获取成功 false 数据获取失败
    var complete = false //加载所有数据
    var showCompleteItem = true //加载完成显示总数
    lateinit var retryCallback: () -> Unit //首页重试
    lateinit var loadMore: () -> Unit //加载更多

    /**
     * 通过类型获取布局id
     */
    abstract fun getLayoutIdByList(viewType: Int): Int

    override fun getLayoutId(viewType: Int): Int {
        return when (viewType) {
            TYPE_EMPTY -> R.layout.item_util_empty
            TYPE_ERROR -> R.layout.item_util_error
            TYPE_LOAD_ITEM -> R.layout.item_util_load_more
            else -> getLayoutIdByList(viewType)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderRecyclerView {
        return when (viewType) {
            TYPE_EMPTY -> BastLoadMoreViewHolder.create(parent, R.layout.item_util_empty, null)
            TYPE_ERROR -> BastLoadMoreViewHolder.create(parent, R.layout.item_util_error, retryCallback)
            TYPE_LOAD_ITEM -> BastLoadMoreViewHolder.create(parent, R.layout.item_util_load_more, this::adapterLoadMore)
            else -> super.onCreateViewHolder(parent, viewType)
        }
    }

    override fun onBindViewHolder(holder: ViewHolderRecyclerView, position: Int) {
        if (!complete && refreshing.value != true && loading.value != true && result.value == true) {
            //提前半页触发加载更多
            val trigger = itemCount - pageCount / 2
            if (trigger in 1 until position) {
                loadMore.invoke()
            }
        }
        when (getItemViewType(position)) {
            TYPE_EMPTY -> (holder as BastLoadMoreViewHolder)
            TYPE_ERROR -> (holder as BastLoadMoreViewHolder)
            TYPE_LOAD_ITEM -> {
                if (complete) {
                    (holder as BastLoadMoreViewHolder).bindTo(1)
                } else {
                    if (loading.value == true) {
                        (holder as BastLoadMoreViewHolder).bindTo(2)
                    } else if (result.value == false) {
                        (holder as BastLoadMoreViewHolder).bindTo(0)
                    }
                }
            }
            else -> getItem(position)?.let {
                onBindView(it, DataBindingUtil.getBinding<B>(holder.itemView)!!, holder, position)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (itemCount == 1) {
            when (result.value) {
                true -> TYPE_EMPTY
                false -> TYPE_ERROR
                else -> 0
            }
        } else {
            val size =
                if (complete)
                    if (showCompleteItem) 1
                    else 0
                else 1
            val isLoadMoreIndex = position + size == itemCount
            if (isLoadMoreIndex) {
                TYPE_LOAD_ITEM
            } else {
                0
            }
        }
    }

    override fun getItem(position: Int): T? {
        val size =
            if (complete)
                if (showCompleteItem) 1
                else 0
            else 1
        val isLoadMoreIndex = position + size == itemCount
        return if (isLoadMoreIndex) {
            null
        } else {
            super.getItem(position)
        }
    }

    override fun getItemCount(): Int {
        val size =
            if (result.value != null)
                if (complete)
                    if (showCompleteItem) 1
                    else 0
                else 1
            else 0
        return super.getItemCount() + size
    }

    private fun adapterLoadMore() {
        notifyDataSetChanged()
        loadMore.invoke()
    }

    fun refresh() {
        refreshing.value = true
        complete = false
    }

    companion object {
        const val TYPE_EMPTY = -1 //空数据
        const val TYPE_ERROR = -2 //第一页获取错误
        const val TYPE_LOAD_ITEM = -3 //加载更多
    }
}