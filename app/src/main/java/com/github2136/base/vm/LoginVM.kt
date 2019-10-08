package com.github2136.base.vm

import android.app.Application
import androidx.core.content.edit
import androidx.lifecycle.MutableLiveData
import com.github2136.base.HttpModel
import com.github2136.base.entity.User
import com.github2136.base.entity.Weather
import com.github2136.base.executor
import com.github2136.basemvvm.BaseVM
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by YB on 2019/8/28
 */
class LoginVM(application: Application) : BaseVM(application) {
    val httpModel: HttpModel by lazy { HttpModel.getInstance(application) }

    val userName = MutableLiveData<String>()
    val passWord = MutableLiveData<String>()
    val userInfo = MutableLiveData<Any>()
    val weather = MutableLiveData<String>()

    fun getWeather() {
        val call = httpModel.api.getWeather("101010100")
        addCall(call)
        call.enqueue(object : Callback<Weather> {
            override fun onFailure(call: Call<Weather>, t: Throwable) {

            }

            override fun onResponse(call: Call<Weather>, response: Response<Weather>) {
                if (response.isSuccessful) {
                    weather.postValue(response.body().toString())
                }
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
}