package com.github2136.basemvvm

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

/**
 * Created by YB on 2023/1/31
 */
interface IBaseFragment {
    var fragment: Fragment
    fun onAttach(context: Context)
    fun onCreate(savedInstanceState: Bundle?)
    fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?)
    fun onViewCreated(view: View, savedInstanceState: Bundle?)
    fun onStart()
    fun onResume()
    fun onPause()
    fun onStop()
    fun onDestroyView()
    fun onDestroy()
    fun onDetach()
}