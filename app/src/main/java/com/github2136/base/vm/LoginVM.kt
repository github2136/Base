package com.github2136.base.vm

import android.app.Application
import android.util.Log
import androidx.core.content.edit
import androidx.lifecycle.MutableLiveData
import com.github2136.base.HttpModel
import com.github2136.base.entity.User
import com.github2136.base.entity.Weather
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
    val httpModel: HttpModel by lazy { HttpModel.getInstance(application) }

    val userNameLD = MutableLiveData<String>()
    val passWordLD = MutableLiveData<String>()
    val userInfoLD = MutableLiveData<Any>()
    val weatherLD = MutableLiveData<String>()

    fun getWeather() {
        val call = httpModel.api.getWeather("101010100")
        addCall(call)
        call.enqueue(object : Callback<Weather> {
            override fun onFailure(call: Call<Weather>, t: Throwable) {

            }

            override fun onResponse(call: Call<Weather>, response: Response<Weather>) {
                if (response.isSuccessful) {
                    weatherLD.postValue(response.body().toString())
                }
            }
        })
        httpModel.api.getUrl().enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("http", "getUrl F")
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.e("http", "getUrl")
            }
        })
        httpModel.api.getUrl404().enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("http", "getUrl404 F")
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.e("http", "getUrl404")
            }
        })
        httpModel.api.getUrl500().enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("http", "getUrl500 F")
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.e("http", "getUrl500")
            }
        })
        httpModel.api.getUrlTimeOut().enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("http", "getUrlTimeOut F")
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.e("http", "getUrlTimeOut")
            }
        })

    }

    fun login() {
        executor.execute {
            Thread.sleep(2000)
            if (userNameLD.value == "admin" && passWordLD.value == "admin") {
                mSpUtil.edit {
                    putString("username", "admin")
                    putString("password", "admin")
                }
                userInfoLD.postValue(User(userNameLD.value!!, passWordLD.value!!))
            } else {
                userInfoLD.postValue("账号或密码错误")
            }
        }
    }
}