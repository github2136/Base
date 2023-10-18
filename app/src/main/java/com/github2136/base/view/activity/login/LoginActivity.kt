package com.github2136.base.view.activity.login

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.collection.ArrayMap
import androidx.lifecycle.Observer
import com.github2136.base.R
import com.github2136.base.base.AppBaseActivity
import com.github2136.base.databinding.ActivityLoginBinding
import com.github2136.base.model.entity.User
import com.github2136.base.view.activity.main.MainActivity
import com.github2136.util.PermissionUtil

class LoginActivity : AppBaseActivity<LoginVM, ActivityLoginBinding>() {
    private val permissionUtil by lazy { PermissionUtil(this) }
    override fun initData(savedInstanceState: Bundle?) {
        bind.view = this
        bind.vm = vm
        vm.userNameLD.value = "admin"
        vm.passWordLD.value = "admin"

        val permission = ArrayMap<String, String>()
        permission[Manifest.permission.WRITE_EXTERNAL_STORAGE] = "文件写入"
        permissionUtil.getPermission(permission) {
            vm.getWeather()
        }
        vm.titleTextLD.value = "登录"
    }

    fun onClick(view: View) {
        vm.login()
    }

    override fun onRestart() {
        super.onRestart()
        permissionUtil.onRestart()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionUtil.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun initObserve() {
        vm.userInfoLD.observe(this, Observer {
            dismissProgressDialog()
            if (it is String) {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            } else if (it is User) {
                startActivity(Intent(this, MainActivity::class.java))
            }
        })
        vm.weatherLD.observe(this, Observer {
            Log.e("getWeather", "xxxx")
        })
    }

}