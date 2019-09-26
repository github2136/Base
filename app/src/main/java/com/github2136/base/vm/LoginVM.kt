package com.github2136.base.vm

import android.app.Application
import androidx.core.content.edit
import androidx.lifecycle.MutableLiveData
import com.github2136.base.entity.User
import com.github2136.base.executor
import com.github2136.basemvvm.BaseVM
import java.util.concurrent.Callable
import kotlin.concurrent.thread

/**
 * Created by YB on 2019/8/28
 */
class LoginVM(application: Application) : BaseVM(application) {
    val userName = MutableLiveData<String>()
    val passWord = MutableLiveData<String>()
    val userInfo = MutableLiveData<Any>()

    fun login() {
        executor.execute {
            Thread.sleep(2000)
            if (userName.value == "admin" && passWord.value == "admin") {
                mSpUtil.edit {
                    putString("username", "admin")
                    putString("password", "admin")
                }
                userInfo.postValue(User(userName.value!!, passWord.value!!))
            } else {
                userInfo.postValue("账号或密码错误")
            }
        }
    }

    override fun cancelRequest() {

    }
}