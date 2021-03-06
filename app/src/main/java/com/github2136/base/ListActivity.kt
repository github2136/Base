package com.github2136.base

import android.os.Bundle
import com.github2136.base.databinding.ActivityListBinding
import com.github2136.base.vm.ListVM
import com.github2136.basemvvm.BaseActivity

class ListActivity : BaseActivity<ListVM, ActivityListBinding>() {
    override fun getLayoutId() = R.layout.activity_list

    override fun initData(savedInstanceState: Bundle?) {
        bind.vm = vm
        bind.view = this
        vm.setData()
    }

    override fun initObserve() {

    }


}
