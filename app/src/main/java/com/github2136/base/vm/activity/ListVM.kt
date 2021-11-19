package com.github2136.base.vm.activity

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.github2136.base.repository.UserRepository
import com.github2136.base.view.adapter.ListMultipleAdapter
import com.github2136.basemvvm.BaseVM
import com.github2136.basemvvm.ResultRepo

/**
 * Created by YB on 2019/9/23
 */
class ListVM(app: Application) : BaseVM(app) {
    val userRepository by lazy { UserRepository(app) }

    val adapterLD = MutableLiveData<ListMultipleAdapter>()

    fun setData() {
        launch {
            val result = userRepository.getUserFlow(1, 20)
            if (result is ResultRepo.Success) {
                adapterLD.value = ListMultipleAdapter(result.data)
            }
        }
    }
}