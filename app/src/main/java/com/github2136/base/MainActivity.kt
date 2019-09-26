package com.github2136.base

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.github2136.base.databinding.ActivityMainBinding
import com.github2136.base.vm.MainVM
import com.github2136.basemvvm.BaseActivity

class MainActivity : BaseActivity<MainVM, ActivityMainBinding>() {
    override fun getLayoutId() = R.layout.activity_main

    override fun initData(savedInstanceState: Bundle?) {
        bind.view = this
        bind.vm = vm
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.btnClick1 -> {
                startActivity(Intent(this, ListActivity::class.java))
            }
            R.id.btnClick2 -> {
                startActivity(Intent(this, LoadMoreActivity::class.java))

            }
        }
    }

    override fun initObserve() {

    }
}