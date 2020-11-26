package com.github2136.base.view.activity

import android.graphics.Paint
import android.os.Bundle
import com.github2136.base.R
import com.github2136.base.databinding.ActivityLoadMoreBinding
import com.github2136.base.divider.Divider
import com.github2136.base.model.entity.User
import com.github2136.base.vm.activity.LoadMoreVM
import com.github2136.basemvvm.loadmore.BaseLoadMoreActivity
import kotlinx.android.synthetic.main.activity_load_more.*

/**
 * Created by YB on 2019/9/20
 */
class LoadMoreActivity : BaseLoadMoreActivity<LoadMoreVM, ActivityLoadMoreBinding, User>() {

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