package com.github2136.base.vm

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.github2136.base.repository.UserRepository
import com.github2136.base.repository.WeatherRepository
import com.github2136.basemvvm.BaseVM

/**
 * Created by YB on 2019/8/28
 */
class LoginVM(app: Application) : BaseVM(app) {
    val userRepository by lazy { UserRepository(app) }
    val weatherRepository by lazy { WeatherRepository(app) }

    val userNameLD = MutableLiveData<String>()
    val passWordLD = MutableLiveData<String>()
    val userInfoLD = MutableLiveData<Any>()
    val weatherLD = MutableLiveData<String>()

    fun getWeather() {
        weatherRepository.getWeather {
            onSuccess { weatherLD.postValue(it) }
        }
    }

    fun login() {
        userRepository.login(userNameLD.value!!, passWordLD.value!!) {
            onSuccess { userInfoLD.postValue(it) }
            onFail { errorCode, msg -> userInfoLD.postValue(msg) }
        }
    }

    override fun cancelRequest() {
        userRepository.cancelRequest()
        weatherRepository.cancelRequest()
    }
}