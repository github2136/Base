package com.github2136.base.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github2136.basemvvm.IBaseFragment

/**
 * Created by YB on 2023/1/31
 */
class IBaseFragmentImpl : IBaseFragment {
    override lateinit var fragment: Fragment
    override fun onAttach(context: Context) {}

    override fun onCreate(savedInstanceState: Bundle?) {}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {}

    override fun onStart() {}

    override fun onResume() {}

    override fun onPause() {}

    override fun onStop() {}

    override fun onDestroy() {}

    override fun onDestroyView() {}

    override fun onDetach() {}
}