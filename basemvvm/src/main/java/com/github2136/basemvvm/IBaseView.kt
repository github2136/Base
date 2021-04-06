package com.github2136.basemvvm

import android.view.View

/**
 * Created by YB on 2020/12/16
 * 所有Activity，Fragment基类
 */
interface IBaseView {
    fun leftBtnClick(btnLeft: View)
    fun rightBtnClick(btnRight: View)
}