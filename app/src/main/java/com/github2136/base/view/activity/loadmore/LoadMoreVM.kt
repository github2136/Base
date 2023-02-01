package com.github2136.base.view.activity.loadmore

import android.app.Application
import com.github2136.base.model.entity.User
import com.github2136.base.repository.UserRepository
import com.github2136.basemvvm.ResultRepo
import com.github2136.basemvvm.loadmore.BaseLoadMoreVM

/**
 * Created by YB on 2019/9/20
 */
class LoadMoreVM(app: Application) : BaseLoadMoreVM<User>(app) {
    val userRepository by lazy { UserRepository(app) }

    override val pageFirstIndex = 1
    override val pageCount = 25

    override fun initData() {
        adapter.showCompleteItem = false
        launch {
            val resultRepo = userRepository.getUserFlow(adapter.pageIndex, adapter.pageCount)
            when (resultRepo) {
                is ResultRepo.Success -> setData(resultRepo.data)
                is ResultRepo.Error -> failedData()
            }
        }
    }

    override fun loadMoreData() {
        launch {
            if (adapter.pageIndex < 5) {
                val resultRepo = userRepository.getUserFlow(adapter.pageIndex, adapter.pageCount)
                when (resultRepo) {
                    is ResultRepo.Success -> setData(resultRepo.data)
                    is ResultRepo.Error -> failedData()
                }
            } else {
                setData(mutableListOf())
            }
        }
    }

    override fun initAdapter() = LoadMoreAdapter()
}