package com.github2136.base

import android.graphics.Paint
import android.os.Bundle
import com.github2136.base.databinding.ActivityLoadMoreBinding
import com.github2136.base.divider.Divider
import com.github2136.base.entity.User
import com.github2136.base.vm.LoadMoreVM
import com.github2136.basemvvm.list.BaseListActivity
import kotlinx.android.synthetic.main.activity_load_more.*

/**
 * Created by YB on 2019/9/20
 */
class LoadMoreActivity : BaseListActivity<LoadMoreVM, ActivityLoadMoreBinding, User>() {

    override fun getLayoutId() = R.layout.activity_load_more

    override fun initData(savedInstanceState: Bundle?) {
        bind.view = this
        bind.vm = vm
        val d = Divider(this)
        d.align = Paint.Align.RIGHT
        rvList.addItemDecoration(d)
    }

    override fun initObserve() {

    }
}