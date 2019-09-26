package com.github2136.basemvvm.list

import android.app.Application
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.MutableLiveData
import com.github2136.basemvvm.BaseVM

/**
 * Created by YB on 2019/8/29
 */
abstract class BaseListVM<T>(app: Application) : BaseVM(app) {
    var adapter: BaseLoadMoreAdapter<T, *> = initAdapter()

    /**
     * 设置首页数据
     */
    fun setData(list: MutableList<T>) {
        adapter.pageCount = adapter.pageCount
        handle.post {
            adapter.pageIndex = adapter.pageIndex + 1
            adapter.refreshing.value = false
            adapter.result.value = true
            if (list.size != adapter.pageCount) {
                //加载完成
                adapter.complete = true
            }
            adapter.setData(list)

        }
    }

    /**
     * 加载更多数据
     */
    fun appendData(list: MutableList<T>) {
        handle.post {
            adapter.pageIndex = adapter.pageIndex + 1
            adapter.loading.value = false
            adapter.result.value = true
            if (list.size != adapter.pageCount) {
                //加载完成
                adapter.complete = true
            }
            adapter.appendData(list)
        }
    }

    /**
     * 数据获取失败
     */
    fun failedData() {
        adapter.refreshing.postValue(false)
        adapter.loading.postValue(false)
        adapter.result.postValue(false)
        handle.post {
            adapter.notifyDataSetChanged()
        }
    }

    /**
     * 首页加载数据
     */
    abstract fun initData()

    /**
     * 加载更多数据
     */
    abstract fun loadMoreData()

    /**
     * 初始化adapter
     */
    abstract fun initAdapter(): BaseLoadMoreAdapter<T, *>
}