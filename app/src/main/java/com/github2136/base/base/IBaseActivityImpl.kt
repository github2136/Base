package com.github2136.base.base

import android.os.Bundle
import android.util.Log
import com.github2136.basemvvm.IBaseActivity

/**
 * Created by YB on 2023/1/31
 */
class IBaseActivityImpl : IBaseActivity {
    override fun onCreate(savedInstanceState: Bundle?) {}

    override fun onRestart() {}

    override fun onStart() {}

    override fun onResume() {}

    override fun onPause() {}

    override fun onStop() {}

    override fun onDestroy() {}
}