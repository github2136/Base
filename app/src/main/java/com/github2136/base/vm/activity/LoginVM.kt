package com.github2136.base.vm.activity

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github2136.base.model.entity.ResultRepo
import com.github2136.base.repository.UserRepository
import com.github2136.base.repository.WeatherRepository
import com.github2136.basemvvm.BaseVM
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
            val resultRepo = weatherRepository.getWeatherFlow()
            when (resultRepo) {
                is ResultRepo.Success -> {
                    weatherLD.value = resultRepo.toString()
                }
                is ResultRepo.Error   -> {
                    weatherLD.value = resultRepo.msg
                }
            }
        }
    }

    fun login() {
        viewModelScope.launch {
            dialogLD.value = DialogData(loadingStr)
            val resultRepo = userRepository.loginFlow(userNameLD.value!!, passWordLD.value!!)
            dialogLD.value = null
            when (resultRepo) {
                is ResultRepo.Success -> userInfoLD.value = resultRepo.data
                is ResultRepo.Error   -> userInfoLD.value = resultRepo.msg
            }
        }
    }
}