package com.github2136.base.vm.activity

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github2136.base.view.adapter.ListMultipleAdapter
import com.github2136.base.model.entity.ResultFlow
import com.github2136.base.repository.UserRepository
import com.github2136.basemvvm.BaseVM
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Created by YB on 2019/9/23
 */
class ListVM(app: Application) : BaseVM(app) {
    val userRepository by lazy { UserRepository(app) }

    val adapterLD = MutableLiveData<ListMultipleAdapter>()

    fun setData() {
        viewModelScope.launch {
            userRepository.getUserFlow(1, 20)
                .collect {
                    when (it) {
                        is ResultFlow.Success -> adapterLD.value= ListMultipleAdapter(it.data)
                    }
                }
        }
    }

    override fun cancelRequest() {
    }
}