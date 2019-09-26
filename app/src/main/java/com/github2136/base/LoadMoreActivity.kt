package com.github2136.base

import android.os.Bundle
import com.github2136.base.databinding.ActivityListBinding
import com.github2136.base.databinding.ActivityLoadMoreBinding
import com.github2136.base.entity.User
import com.github2136.base.vm.LoadMoreVM
import com.github2136.basemvvm.list.BaseListActivity

/**
 * Created by YB on 2019/9/20
 */
class LoadMoreActivity : BaseListActivity<LoadMoreVM, ActivityLoadMoreBinding, User>() {

    override fun getLayoutId() = R.layout.activity_load_more

    override fun initData(savedInstanceState: Bundle?) {
        bind.view = this
        bind.vm = vm
    }

    fun onRefreshListener() {
        vm.initData()
    }
    override fun initObserve() {

    }
}