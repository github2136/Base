package com.github2136.basemvvm


import android.app.ProgressDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.app.NotificationManagerCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github2136.util.CommonUtil
import com.google.android.material.snackbar.Snackbar
import java.lang.ref.WeakReference
import java.lang.reflect.ParameterizedType

/**
 * Created by yb on 2018/11/2.
 * 基础Fragment
 */
abstract class BaseFragment<V : BaseVM, B : ViewDataBinding> : Fragment(), IBaseView {
    protected val TAG = this.javaClass.name
    protected lateinit var vm: V
    protected lateinit var bind: B
    protected lateinit var mContext: Context
    protected val mHandler by lazy { Handler(this) }

    //根视图用于Snackbar
    protected val rootView by lazy { activity?.window?.decorView?.findViewById<ViewGroup>(android.R.id.content)!! }
    protected val mToast by lazy { Toast.makeText(mContext, "", Toast.LENGTH_SHORT) }
    protected val mSnackbar by lazy { Snackbar.make(rootView, "", Snackbar.LENGTH_SHORT) }
    protected val mDialog by lazy { ProgressDialog(activity) }

    //    protected val mDialog: ProgressDialog by lazy {
//        val dialog = ProgressDialog.getInstance(false)
//        dialog
//    }
    open protected var eventBusEnable = false

    //是否有应用通知权限
    protected var notificationEnable = false
    protected val notificationManagerCompat by lazy { NotificationManagerCompat.from(mContext) }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        attach(context)
    }

    private fun attach(context: Context) {
        mContext = context
        notificationEnable = notificationManagerCompat.areNotificationsEnabled()
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

        vm.dialogLD.observe(this, Observer { str ->
            if (str != null) {
                showProgressDialog(str)
            } else {
                dismissProgressDialog()
            }
        })
        vm.toastLD.observe(this, Observer { str ->
            if (!TextUtils.isEmpty(str)) {
                showToast(str)
            }
        })
        initObserve()
        initData(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        notificationEnable = notificationManagerCompat.areNotificationsEnabled()
    }

    override fun leftBtnClick(btnLeft: View) {
        activity?.finish()
    }

    override fun rightBtnClick(btnRight: View) {
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

    fun showSnackbar(msg: String) {
        mSnackbar.let {
            it.setText(msg)
            it.duration = Snackbar.LENGTH_SHORT
            it.show()
        }
    }

    fun showSnackbar(@StringRes resId: Int) {
        CommonUtil.closeKeybord(activity!!)
        mSnackbar.let {
            it.setText(resId)
            it.duration = Snackbar.LENGTH_SHORT
            it.show()
        }
    }

    fun showSnackbarLong(msg: String) {
        CommonUtil.closeKeybord(activity!!)
        mSnackbar.let {
            it.setText(msg)
            it.duration = Snackbar.LENGTH_LONG
            it.show()
        }
    }

    fun showSnackbarLong(@StringRes resId: Int) {
        CommonUtil.closeKeybord(activity!!)
        mSnackbar.let {
            it.setText(resId)
            it.duration = Snackbar.LENGTH_LONG
            it.show()
        }
    }

    fun showToast(msg: String) {
        if (notificationEnable) {
            mToast.let {
                it.setText(msg)
                it.duration = Toast.LENGTH_SHORT
                it.show()
            }
        } else {
            showSnackbar(msg)
        }
    }

    fun showToast(@StringRes resId: Int) {
        if (notificationEnable) {
            mToast.let {
                it.setText(resId)
                it.duration = Toast.LENGTH_SHORT
                it.show()
            }
        } else {
            showSnackbar(resId)
        }
    }

    fun showToastLong(msg: String) {
        if (notificationEnable) {
            mToast.let {
                it.setText(msg)
                it.duration = Toast.LENGTH_LONG
                it.show()
            }
        } else {
            showSnackbarLong(msg)
        }
    }

    fun showToastLong(@StringRes resId: Int) {
        if (notificationEnable) {
            mToast.let {
                it.setText(resId)
                it.duration = Toast.LENGTH_LONG
                it.show()
            }
        } else {
            showSnackbarLong(resId)
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
    class Handler(fragment: BaseFragment<out BaseVM, out ViewDataBinding>) : android.os.Handler(Looper.getMainLooper()) {
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
    protected fun cancelRequest() {
        vm.cancelRequest()
    }
}