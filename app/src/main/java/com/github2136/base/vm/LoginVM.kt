package com.github2136.base.vm

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github2136.base.entity.Result
import com.github2136.base.repository.UserRepository
import com.github2136.base.repository.WeatherRepository
import com.github2136.basemvvm.BaseVM
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

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
        viewModelScope.launch {
            weatherRepository.getWeatherFlow()
                .collect { weatherLD.value = it.toString() }
        }
    }

    fun login() {
        viewModelScope.launch {
            userRepository.loginFlow(userNameLD.value!!, passWordLD.value!!)
                .onStart { dialogLD.value = loadingStr }
                .onCompletion { dialogLD.value = null }
                .collect {
                    when (it) {
                        is Result.Success -> userInfoLD.value = it.data
                        is Result.Error   -> userInfoLD.value = it.msg
                    }
                }
        }
    }

    override fun cancelRequest() {
        userRepository.cancelRequest()
        weatherRepository.cancelRequest()
    }
}