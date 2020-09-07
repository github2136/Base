package com.github2136.base.repository

import android.app.Application
import androidx.core.content.edit
import com.github2136.base.entity.User
import com.github2136.basemvvm.BaseRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

/**
 * Created by YB on 2020/7/8
 * 用户信息
 */
class UserRepository(app: Application) : BaseRepository(app) {

    fun login(user: String, password: String, callback: RepositoryCallback<User>.() -> Unit) {
        val mCallback = RepositoryCallback<User>().apply(callback)
        GlobalScope.launch {
            delay(2000)
            if (user == "admin" && password == "admin") {
                mSpUtil.edit {
                    putString("username", user)
                    putString("password", password)
                }
                mCallback.mComplete?.invoke()
                mCallback.mSuccess?.invoke(User(user, password))
            } else {
                mCallback.mComplete?.invoke()
                mCallback.mFail?.invoke(-1, failedStr)
            }
        }
    }

    fun getUser(pageIndex: Int, pageSize: Int, callback: RepositoryCallback<MutableList<User>>.() -> Unit) {
        val mCallback = RepositoryCallback<MutableList<User>>().apply(callback)
        GlobalScope.launch {
            val data = mutableListOf<User>()
            val r = Random().nextInt()
            for (i in 0 until pageSize) {
                data.add(User("pageIndex $pageIndex i $i $r", "", "中文中"))
            }
            delay(500)
            if (Random().nextBoolean()) {
                mCallback.mComplete?.invoke()
                mCallback.mSuccess?.invoke(data)
            } else {
                mCallback.mComplete?.invoke()
                mCallback.mFail?.invoke(-1, failedStr)
            }
        }
    }
}