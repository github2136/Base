package com.github2136.base.vm

import android.app.Application
import androidx.core.content.edit
import androidx.lifecycle.MutableLiveData
import com.github2136.base.HttpModel
import com.github2136.base.entity.User
import com.github2136.base.executor
import com.github2136.basemvvm.BaseVM
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by YB on 2019/8/28
 */
class LoginVM(application: Application) : BaseVM(application) {
    lateinit var httpModel: HttpModel
    val userName = MutableLiveData<String>()
    val passWord = MutableLiveData<String>()
    val userInfo = MutableLiveData<Any>()
    val weather = MutableLiveData<String>()

    override fun init(tag: String) {
        super.init(tag)
        httpModel = HttpModel(getApplication(), mTag)
    }

    fun getWeather() {
        val call = httpModel.api.getWeather("101010100")
        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

            }
        })
    }

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