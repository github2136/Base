package com.github2136.base.vm

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.github2136.base.adapter.LoadMoreAdapter
import com.github2136.base.entity.ResultFlow
import com.github2136.base.entity.User
import com.github2136.base.repository.UserRepository
import com.github2136.basemvvm.loadmore.BaseLoadMoreVM
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Created by YB on 2019/9/20
 */
class LoadMoreVM(app: Application) : BaseLoadMoreVM<User>(app) {
    val userRepository by lazy { UserRepository(app) }

    override fun initAdapter() = LoadMoreAdapter()

    override fun initData() {
        viewModelScope.launch {
            adapter.pageIndex = 1
            adapter.pageCount = 5
            userRepository.getUserFlow(adapter.pageIndex, adapter.pageCount)
                .collect {
                    when (it) {
                        is ResultFlow.Success -> setData(it.data)
                        is ResultFlow.Error   -> failedData()
                    }
                }
        }
    }

    override fun loadMoreData() {
        viewModelScope.launch {
            userRepository.getUserFlow(adapter.pageIndex, adapter.pageCount)
                .collect {
                    when (it) {
                        is ResultFlow.Success -> appendData(it.data)
                        is ResultFlow.Error   -> failedData()
                    }
                }
        }
    }

    override fun cancelRequest() {
        userRepository.cancelRequest()
    }
}