package com.github2136.base.vm

import android.app.Application
import com.github2136.base.adapter.LoadMoreAdapter
import com.github2136.base.entity.User
import com.github2136.base.executor
import com.github2136.basemvvm.list.BaseListVM
import java.util.*

/**
 * Created by YB on 2019/9/20
 */
class LoadMoreVM(app: Application) : BaseListVM<User>(app) {
    override fun initAdapter() = LoadMoreAdapter(this::initData, this::loadMoreData)

    override fun initData() {
        adapter.pageIndex = 1
        adapter.refresh()

        executor.submit {
            val data = mutableListOf<User>()
            val r = Random().nextInt()
            for (i in 0 until adapter.pageCount) {
                data.add(User("pageIndex ${adapter.pageIndex} i $i $r", ""))
            }
            Thread.sleep(500)
            if (Random().nextBoolean()) {
                setData(data)
            } else {
                failedData()
            }
        }
    }

    override fun loadMoreData() {
        adapter.loading.value = true
        executor.submit {
            val data = mutableListOf<User>()
            val r = Random().nextInt()
            val count = if (adapter.pageIndex > 3) {
                10
            } else {
                adapter.pageCount
            }
            for (i in 0 until count) {
                data.add(User("pageIndex ${adapter.pageIndex} i $i $r", ""))
            }
            Thread.sleep(500)
            if (Random().nextBoolean()) {
                appendData(data)
            } else {
                failedData()
            }
        }
    }

    override fun cancelRequest() {

    }
}