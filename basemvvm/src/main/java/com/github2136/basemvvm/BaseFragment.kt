package com.github2136.basemvvm

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
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
    protected var firstVisible = false //是否已经显示过
    protected val handler by lazy { Handler(this) }

    //根视图用于Snackbar
    protected val rootView by lazy { activity?.window?.decorView?.findViewById<ViewGroup>(android.R.id.content)!! }
    protected val toast by lazy { Toast.makeText(context, "", Toast.LENGTH_SHORT) }
    protected val snackbar by lazy { Snackbar.make(rootView, "", Snackbar.LENGTH_SHORT) }
    protected val dialog by lazy { ProgressDialog(activity) }

    //是否有应用通知权限
    protected var notificationEnable = false
    protected val notificationManagerCompat by lazy { NotificationManagerCompat.from(requireContext()) }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        notificationEnable = notificationManagerCompat.areNotificationsEnabled()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val type = (this.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments
        val dbType = type[1] as Class<B>
        val dbInflate = dbType.getDeclaredMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java)
        bind = dbInflate.invoke(null, inflater, container, false) as B
        bind.lifecycleOwner = this
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val type = (this.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments

        getVM(type[0] as Class<V>)

        vm.dialogLD.observe(viewLifecycleOwner, Observer { dialog ->
            if (dialog != null) {
                showProgressDialog(dialog.msg, dialog.cancelable, dialog.canceledOnTouchOutside)
            } else {
                dismissProgressDialog()
            }
        })
        vm.toastLD.observe(viewLifecycleOwner, Observer { str ->
            if (!TextUtils.isEmpty(str)) {
                showToast(str)
            }
        })
        initObserve()
        preInitData(savedInstanceState)
        initData(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        notificationEnable = notificationManagerCompat.areNotificationsEnabled()
        if (!firstVisible) {
            firstVisible()
            firstVisible = true
        }
    }
    /**
     * 首次展示
     */
    open fun firstVisible() {}

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
        snackbar.let {
            it.setText(msg)
            it.duration = Snackbar.LENGTH_SHORT
            it.show()
        }
    }

    fun showSnackbar(@StringRes resId: Int) {
        CommonUtil.closeKeybord(requireActivity())
        snackbar.let {
            it.setText(resId)
            it.duration = Snackbar.LENGTH_SHORT
            it.show()
        }
    }

    fun showSnackbarLong(msg: String) {
        CommonUtil.closeKeybord(requireActivity())
        snackbar.let {
            it.setText(msg)
            it.duration = Snackbar.LENGTH_LONG
            it.show()
        }
    }

    fun showSnackbarLong(@StringRes resId: Int) {
        CommonUtil.closeKeybord(requireActivity())
        snackbar.let {
            it.setText(resId)
            it.duration = Snackbar.LENGTH_LONG
            it.show()
        }
    }

    fun showToast(msg: String) {
        if (notificationEnable) {
            toast.let {
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
            toast.let {
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
            toast.let {
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
            toast.let {
                it.setText(resId)
                it.duration = Toast.LENGTH_LONG
                it.show()
            }
        } else {
            showSnackbarLong(resId)
        }
    }

    open fun showProgressDialog(@StringRes resId: Int, cancelable: Boolean = false, canceledOnTouchOutside: Boolean = false, onCancel: ((dialog: DialogInterface) -> Unit)? = null) {
        showProgressDialog(resources.getString(resId), cancelable, canceledOnTouchOutside, onCancel)
    }

    open fun showProgressDialog(msg: String? = null, cancelable: Boolean = false, canceledOnTouchOutside: Boolean = false, onCancel: ((dialog: DialogInterface) -> Unit)? = null) {
        if (msg == null) {
            dialog.setMessage(vm.loadingStr)
        } else {
            dialog.setMessage(msg)
        }
        dialog.setCancelable(cancelable)
        dialog.setOnCancelListener(onCancel)
        dialog.setCanceledOnTouchOutside(canceledOnTouchOutside)
        if (isAdded && !isDetached && !dialog.isShowing) {
            activity?.apply {
                dialog.show()
            }
        }
    }

    open fun dismissProgressDialog() {
        if (dialog.isShowing) {
            dialog.dismiss()
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

    protected open fun handleMessage(msg: Message) {}

    /**
     * 数据初始化前的操作
     */
    protected open fun preInitData(savedInstanceState: Bundle?) {}

    /**
     * 数据初始化
     */
    protected abstract fun initData(savedInstanceState: Bundle?)

    /**
     * 初始化LiveData回调
     */
    protected open fun initObserve() {}

    //取消请求
    protected fun cancelRequest() {
        vm.cancelRequest()
    }
}