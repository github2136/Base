package com.github2136.base.view.activity.loadmore

import android.graphics.Paint
import android.os.Bundle
import android.view.View
import com.github2136.base.R
import com.github2136.base.base.IBaseActivityImpl
import com.github2136.base.databinding.ActivityLoadMoreBinding
import com.github2136.base.divider.Divider
import com.github2136.base.model.entity.User
import com.github2136.basemvvm.loadmore.BaseLoadMoreActivity

/**
 * Created by YB on 2019/9/20
 */
class LoadMoreActivity : BaseLoadMoreActivity<LoadMoreVM, ActivityLoadMoreBinding, User>(IBaseActivityImpl()) {
    override fun getLayoutId() = R.layout.activity_load_more

    override fun initData(savedInstanceState: Bundle?) {
        bind.view = this
        bind.vm = vm
        val d = Divider(this)
        d.align = Paint.Align.RIGHT
        bind.rvList.addItemDecoration(d)
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.btnRefresh -> {
                refreshData()
            }
        }
    }
}