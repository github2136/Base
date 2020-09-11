package com.github2136.base.repository

import android.app.Application
import androidx.core.content.edit
import com.github2136.base.entity.Result
import com.github2136.base.entity.User
import com.github2136.basemvvm.BaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.*

/**
 * Created by YB on 2020/7/8
 * 用户信息
 */
class UserRepository(app: Application) : BaseRepository(app) {

    fun loginFlow(user: String, password: String): Flow<Result<User>> = flow {
        delay(2000)
        if (user == "admin" && password == "admin") {
            mSpUtil.edit {
                putString("username", user)
                putString("password", password)
            }
            emit(Result.Success(User(user, password)))
        } else {
            emit(Result.Error(0, "error"))
        }
    }.flowOn(Dispatchers.IO)

    fun getUserFlow(pageIndex: Int, pageSize: Int): Flow<Result<MutableList<User>>> = flow {
        val data = mutableListOf<User>()
        val r = Random().nextInt()
        for (i in 0 until pageSize) {
            data.add(User("pageIndex $pageIndex i $i $r", "", "中文中"))
        }
        delay(500)
        if (Random().nextBoolean()) {
            emit(Result.Success(data))
        } else {
            emit(Result.Error(-1, failedStr))
        }
    }.flowOn(Dispatchers.IO)
}