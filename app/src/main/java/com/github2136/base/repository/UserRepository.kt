package com.github2136.base.repository

import android.app.Application
import androidx.core.content.edit
import com.github2136.base.entity.User
import com.github2136.base.executor
import com.github2136.basemvvm.BaseRepository
import com.github2136.basemvvm.RepositoryCallback
import java.util.*

/**
 * Created by YB on 2020/7/8
 * 用户信息
 */
class UserRepository(app: Application) : BaseRepository(app) {

    fun login(user: String, password: String, callback: RepositoryCallback<User>) {
        executor.execute {
            Thread.sleep(2000)
            if (user == "admin" && password == "admin") {
                mSpUtil.edit {
                    putString("username", user)
                    putString("password", password)
                }
                callback.onSuccess(User(user, password))
            } else {
                callback.onFail(-1, failedStr)
            }
        }
    }

    fun getUser(pageIndex: Int, pageSize: Int, callback: RepositoryCallback<MutableList<User>>) {
        executor.submit {
            val data = mutableListOf<User>()
            val r = Random().nextInt()
            for (i in 0 until pageSize) {
                data.add(User("pageIndex $pageIndex i $i $r", "", "中文中"))
            }
            Thread.sleep(500)
            if (Random().nextBoolean()) {
                callback.onSuccess(data)
            } else {
                callback.onFail(-1, failedStr)
            }
        }
    }

}