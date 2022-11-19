package com.github2136.base.view.activity.list

import android.app.Application
import com.github2136.base.model.entity.User
import com.github2136.base.repository.UserRepository
import com.github2136.base.view.adapter.ListMultipleAdapter
import com.github2136.basemvvm.BaseVM

/**
 * Created by YB on 2019/9/23
 */
class ListVM(app: Application) : BaseVM(app) {
    val userRepository by lazy { UserRepository(app) }

    val adapter = ListMultipleAdapter(mutableListOf())

    fun setData() {
        launch {
            // val result = userRepository.getUserFlow()
            // if (result is ResultRepo.Success) {
            val data = mutableListOf<User>()

            for (i in 0 until 20) {
                data.add(User("pageIndex $i i $i ", "", "中文中"))
            }
                adapter.setData(data)
            // }
        }
    }
}