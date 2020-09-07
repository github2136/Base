package com.github2136.base.vm

import android.app.Application
import com.github2136.base.adapter.LoadMoreAdapter
import com.github2136.base.entity.User
import com.github2136.base.repository.UserRepository
import com.github2136.basemvvm.loadmore.BaseLoadMoreVM

/**
 * Created by YB on 2019/9/20
 */
class LoadMoreVM(app: Application) : BaseLoadMoreVM<User>(app) {
    val userRepository by lazy { UserRepository(app) }

    override fun initAdapter() = LoadMoreAdapter()

    override fun initData() {
        adapter.pageIndex = 1
        adapter.pageCount = 5
        userRepository.getUser(adapter.pageIndex, adapter.pageCount) {
            onSuccess { setData(it) }
            onFail { errorCode, msg -> failedData() }
        }
    }

    override fun loadMoreData() {
        userRepository.getUser(adapter.pageIndex, adapter.pageCount) {
            onSuccess { appendData(it) }
            onFail { errorCode, msg -> failedData() }
        }
    }

    override fun cancelRequest() {
        userRepository.cancelRequest()
    }
}