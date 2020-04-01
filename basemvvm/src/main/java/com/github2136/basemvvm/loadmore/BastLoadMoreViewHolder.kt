package com.github2136.basemvvm.loadmore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import com.github2136.base.ViewHolderRecyclerView
import com.github2136.basemvvm.R


/**
 * Created by YB on 2019/9/23
 */
class BastLoadMoreViewHolder(view: View, private val retryCallback: (() -> Unit)?) : ViewHolderRecyclerView(view) {
    var type = 0
    var progress: ProgressBar? = view.findViewById(R.id.pb_load_more)
    var loadMore: TextView? = view.findViewById(R.id.tv_load_more)

    init {
        retryCallback?.let {
            view.setOnClickListener {
                if (type == 0) {
                    retryCallback.invoke()
                }
            }
        }
    }

    /**
     * 0:加载失败，1:加载完成，2:加载中
     */
    fun bindTo(type: Int) {
        this.type = type
        when (type) {
            0 -> {
                progress?.visibility = View.INVISIBLE
                loadMore?.text = "加载失败，点击重试"
            }
            1 -> {
                progress?.visibility = View.INVISIBLE
                loadMore?.text = "已加载所有数据"
            }
            2 -> {
                progress?.visibility = View.VISIBLE
                loadMore?.text = "加载中……"
            }
        }
    }

    companion object {
        fun create(parent: ViewGroup, layoutId: Int, retryCallback: (() -> Unit)?): BastLoadMoreViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
            return BastLoadMoreViewHolder(view, retryCallback)
        }
    }
}