package com.github2136.base.base

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import com.github2136.basemvvm.IBaseActivity

/**
 * Created by YB on 2023/1/31
 */
class IBaseActivityImpl : IBaseActivity {
    override lateinit var activity: Activity
    override fun onCreate(savedInstanceState: Bundle?) {
        Toast.makeText(activity, "onCreate", Toast.LENGTH_SHORT).show()
    }

    override fun onRestart() {}

    override fun onStart() {}

    override fun onResume() {}

    override fun onPause() {}

    override fun onStop() {}

    override fun onDestroy() {}
}