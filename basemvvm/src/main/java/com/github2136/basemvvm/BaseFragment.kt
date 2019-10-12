package com.github2136.basemvvm


import android.app.ProgressDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Message
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import java.lang.ref.WeakReference
import java.lang.reflect.ParameterizedType

/**
 * Created by yb on 2018/11/2.
 */
abstract class BaseFragment<V : BaseVM, B : ViewDataBinding> : Fragment() {
    protected val TAG = this.javaClass.name
    protected lateinit var vm: V
    protected lateinit var bind: B
    protected lateinit var mContext: Context
    protected val mHandler by lazy { Handler(this) }
    protected lateinit var mToast: Toast
    protected val mDialog: ProgressDialog by lazy {
        ProgressDialog(activity)
    }
//    protected val mDialog: ProgressDialog by lazy {
//        val dialog = ProgressDialog.getInstance(false)
//        dialog
//    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        attach(context)
    }

    private fun attach(context: Context) {
        mContext = context
        mToast = Toast.makeText(mContext, "", Toast.LENGTH_SHORT)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bind = DataBindingUtil.inflate(inflater, getViewResId(), container, false)
        bind.lifecycleOwner = this
        return bind.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val type = (this.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments

        getVM(type[0] as Class<V>)

        vm.ldDialog.observe(this, Observer { str ->
            if (str != null) {
                showProgressDialog(str)
            } else {
                dismissProgressDialog()
            }
        })
        vm.ldToast.observe(this, Observer { str ->
            if (!TextUtils.isEmpty(str)) {
                showToast(str)
            }
        })
        initObserve()
        initData(savedInstanceState)
    }

    private fun getVM(clazz: Class<V>) {
        activity?.let {
            this.vm = ViewModelProvider.AndroidViewModelFactory.getInstance(it.application).create(clazz)
        }
    }

    override fun onDestroyView() {
        cancelRequest()
        super.onDestroyView()
    }


    fun showToast(msg: String) {
        mToast.let {
            it.setText(msg)
            it.duration = Toast.LENGTH_SHORT
            it.show()
        }
    }

    fun showToast(@StringRes resId: Int) {
        mToast.let {
            it.setText(resId)
            it.duration = Toast.LENGTH_SHORT
            it.show()
        }
    }

    fun showToastLong(msg: String) {
        mToast.let {
            it.setText(msg)
            it.duration = Toast.LENGTH_LONG
            it.show()
        }
    }

    fun showToastLong(@StringRes resId: Int) {
        mToast.let {
            it.setText(resId)
            it.duration = Toast.LENGTH_LONG
            it.show()
        }
    }

    open fun showProgressDialog(@StringRes resId: Int, cancelable: Boolean = false) {
        showProgressDialog(resources.getString(resId), cancelable)
    }

    open fun showProgressDialog(msg: String? = null, cancelable: Boolean = false) {
        if (msg == null) {
            mDialog.setMessage(vm.loadingStr)
        } else {
            mDialog.setMessage(msg)
        }
        mDialog.setCancelable(cancelable)
        if (isAdded && !isDetached && !mDialog.isShowing) {
            activity?.apply {
                mDialog.show()
            }
        }
    }

    open fun dismissProgressDialog() {
        if (mDialog.isShowing) {
            mDialog.dismiss()
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Handler
    ///////////////////////////////////////////////////////////////////////////
    class Handler(fragment: BaseFragment<out BaseVM, out ViewDataBinding>) : android.os.Handler() {
        var weakReference = WeakReference(fragment)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val fragment = weakReference.get()
            fragment?.handleMessage(msg)
        }
    }

    protected fun handleMessage(msg: Message) {}
    //布局ID
    protected abstract fun getViewResId(): Int

    //初始化
    protected abstract fun initData(savedInstanceState: Bundle?)

    //初始化回调
    protected abstract fun initObserve()

    //取消请求
    fun cancelRequest() {}
}