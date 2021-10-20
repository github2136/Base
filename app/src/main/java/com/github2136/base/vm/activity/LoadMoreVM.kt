package com.github2136.base.vm.activity

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.github2136.base.model.entity.ResultRepo
import com.github2136.base.model.entity.User
import com.github2136.base.repository.UserRepository
import com.github2136.base.view.adapter.LoadMoreAdapter
import com.github2136.basemvvm.loadmore.BaseLoadMoreVM
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
            val resultRepo = userRepository.getUserFlow(adapter.pageIndex, adapter.pageCount)
            when (resultRepo) {
                is ResultRepo.Success -> setData(resultRepo.data)
                is ResultRepo.Error   -> failedData()
            }
        }
    }


    override fun loadMoreData() {
        viewModelScope.launch {
            val resultRepo = userRepository.getUserFlow(adapter.pageIndex, adapter.pageCount)
            when (resultRepo) {
                is ResultRepo.Success -> appendData(resultRepo.data)
                is ResultRepo.Error   -> failedData()
            }
        }
    }
}