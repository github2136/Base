package com.github2136.base.vm

import android.app.Application
import com.github2136.base.adapter.LoadMoreAdapter
import com.github2136.base.entity.User
import com.github2136.base.executor
import com.github2136.basemvvm.loadmore.BaseLoadMoreVM
import java.util.*

/**
 * Created by YB on 2019/9/20
 */
class LoadMoreVM(app: Application) : BaseLoadMoreVM<User>(app) {
    override fun initAdapter() = LoadMoreAdapter()

    override fun initData() {
        adapter.pageIndex = 1
        adapter.pageCount = 5
        executor.submit {
            val data = mutableListOf<User>()
            val r = Random().nextInt()
            for (i in 0 until adapter.pageCount) {
                data.add(User("pageIndex ${adapter.pageIndex} i $i $r", "", "中文中"))
            }
            Thread.sleep(500)
//            if (Random().nextBoolean()) {
            setData(data)
//            } else {
//                failedData()
//            }
        }
    }

    override fun loadMoreData() {
        executor.submit {
            val data = mutableListOf<User>()
            val r = Random().nextInt()
            val count = if (adapter.pageIndex > 10) {
                10
            } else {
                adapter.pageCount
            }
            for (i in 0 until count) {
                data.add(User("pageIndex ${adapter.pageIndex} i $i $r", "", r.toString() + "5555555"))
            }
            Thread.sleep(500)
//            if (Random().nextBoolean()) {
            appendData(data)
//            } else {
//                failedData()
//            }
        }
    }

    override fun cancelRequest() {

    }
}