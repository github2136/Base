package com.github2136.base.repository

import android.content.Context
import androidx.core.content.edit
import com.github2136.base.model.entity.User
import com.github2136.basemvvm.BaseRepository
import com.github2136.basemvvm.ResultRepo
import kotlinx.coroutines.delay
import java.util.*

/**
 * Created by YB on 2020/7/8
 * 用户信息
 */
class UserRepository(context: Context) : BaseRepository(context) {

    suspend fun loginFlow(user: String, password: String) = launch {
        delay(2000)
        if (user == "admin" && password == "admin") {
            mSpUtil.edit {
                putString("username", user)
                putString("password", password)
            }
            ResultRepo.Success(User(user, password))
        } else {
            ResultRepo.Error(0, "error")
        }
    }

    suspend fun getUserFlow(pageIndex: Int, pageSize: Int) = launch {
        val data = mutableListOf<User>()
        val r = Random().nextInt()
        for (i in 0 until pageSize) {
            data.add(User("pageIndex $pageIndex i $i $r", "", "中文中"))
        }
        delay(500)
        if (Random().nextBoolean()) {
            ResultRepo.Success(data)
        } else {
            ResultRepo.Error(-1, failedStr)
        }
    }
}