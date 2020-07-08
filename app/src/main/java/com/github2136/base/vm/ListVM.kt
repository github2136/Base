package com.github2136.base.vm

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.github2136.base.adapter.ListMultipleAdapter
import com.github2136.base.entity.User
import com.github2136.base.repository.UserRepository
import com.github2136.basemvvm.BaseVM
import com.github2136.basemvvm.RepositoryCallback

/**
 * Created by YB on 2019/9/23
 */
class ListVM(app: Application) : BaseVM(app) {
    val userRepository by lazy { UserRepository(app) }

    val adapterLD = MutableLiveData<ListMultipleAdapter>()

    fun setData() {
        userRepository.getUser(1, 20, object : RepositoryCallback<MutableList<User>> {
            override fun onSuccess(t: MutableList<User>) {
                adapterLD.postValue(ListMultipleAdapter(t))
            }

            override fun onFail(errorCode: Int, msg: String) {}
        })
    }

    override fun cancelRequest() {
    }
}