package com.github2136.base

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import com.github2136.base.databinding.ActivityLoginBinding
import com.github2136.base.entity.User
import com.github2136.base.vm.LoginVM
import com.github2136.basemvvm.BaseActivity
import java.util.*

class LoginActivity : BaseActivity<LoginVM, ActivityLoginBinding>() {
    override fun getLayoutId() = R.layout.activity_login

    override fun initData(savedInstanceState: Bundle?) {
        bind.view = this
        bind.vm = vm
        vm.userName.value = "admin"
        vm.passWord.value = "admin"
    }

    fun onClick(view: View) {
        showDialog("asdf${Random().nextInt()}")
        vm.login()
    }

    override fun initObserve() {
        vm.userInfo.observe(this, Observer {
            dismissDialog()
            if (it is String) {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            } else if (it is User) {
                startActivity(Intent(this, MainActivity::class.java))
            }
        })
    }

}