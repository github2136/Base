package com.github2136.basemvvm

import android.os.Bundle

/**
 * Created by YB on 2023/1/31
 */
interface IBaseActivity {
    fun onCreate(savedInstanceState: Bundle?)
    fun onRestart()
    fun onStart()
    fun onResume()
    fun onPause()
    fun onStop()
    fun onDestroy()
}