package com.github2136.base.view.activity.list

import android.os.Bundle
import com.github2136.base.R
import com.github2136.base.base.AppBaseActivity
import com.github2136.base.databinding.ActivityListBinding

class ListActivity : AppBaseActivity<ListVM, ActivityListBinding>() {
    override fun getLayoutId() = R.layout.activity_list

    override fun initData(savedInstanceState: Bundle?) {
        bind.vm = vm
        bind.view = this

        vm.setData()
    }

    override fun onBackPressed() {
        // super.onBackPressed()
        vm.adapter.setOnItemClickListener {
            showToast("11")
        }
    }
}
