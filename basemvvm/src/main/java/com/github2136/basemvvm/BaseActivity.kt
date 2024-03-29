package com.github2136.basemvvm

import android.app.ProgressDialog
import android.content.DialogInterface
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
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationManagerCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github2136.util.CommonUtil
import com.google.android.material.snackbar.Snackbar
import java.lang.ref.WeakReference
import java.lang.reflect.ParameterizedType

/**
 * Created by YB on 2019/8/29
 * 基础Activity
 */
abstract class BaseActivity<V : BaseVM, B : ViewDataBinding> : AppCompatActivity(), IBaseView {
    protected lateinit var vm: V
    protected lateinit var bind: B
    protected val TAG = this.javaClass.name
    protected val app by lazy { application as BaseApplication }
    protected val handler by lazy { Handler(this) }
    protected val activity by lazy { this }

    //根视图用于Snackbar
    protected val rootView by lazy { window.decorView.findViewById<ViewGroup>(android.R.id.content) }
    protected val toast by lazy { Toast.makeText(this, "", Toast.LENGTH_SHORT) }
    protected val snackbar by lazy { Snackbar.make(rootView, "", Snackbar.LENGTH_SHORT) }
    protected val dialog by lazy { ProgressDialog(this) }

    //状态栏高度
    val statusBarHeight by lazy { resources.getDimensionPixelSize(resources.getIdentifier("status_bar_height", "dimen", "android")) }

    //导航栏高度
    val navigationBarHeight by lazy { resources.getDimensionPixelSize(resources.getIdentifier("navigation_bar_height", "dimen", "android")) }

    //是否有应用通知权限
    protected var notificationEnable = false
    protected val notificationManagerCompat by lazy { NotificationManagerCompat.from(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notificationEnable = notificationManagerCompat.areNotificationsEnabled()
        app.addActivity(this)

        val type = (this.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments
        getVM(type[0] as Class<V>)

        val dbType = type[1] as Class<B>
        val dbInflate = dbType.getDeclaredMethod("inflate", LayoutInflater::class.java)

        bind = dbInflate.invoke(null, layoutInflater) as B
        setContentView(bind.root)
        bind.lifecycleOwner = this
        vm.dialogLD.observe(this, Observer { dialog ->
            if (dialog != null) {
                showProgressDialog(dialog.msg, dialog.cancelable, dialog.canceledOnTouchOutside)
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
        preInitData(savedInstanceState)
        initData(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        notificationEnable = notificationManagerCompat.areNotificationsEnabled()
    }

    override fun onDestroy() {
        cancelRequest()
        app.removeActivity(this)
        super.onDestroy()
    }

    private fun getVM(clazz: Class<V>) {
        this.vm = ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(clazz)
    }

    protected fun setToolbar(tbTitle: Toolbar) {
        setSupportActionBar(tbTitle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        tbTitle.setNavigationOnClickListener { finish() }
    }

    override fun leftBtnClick(btnLeft: View) {
        finish()
    }

    override fun rightBtnClick(btnRight: View) {}

    ///////////////////////////////////////////////////////////////////////////
    // Handler
    ///////////////////////////////////////////////////////////////////////////
    class Handler(activity: BaseActivity<out BaseVM, out ViewDataBinding>) : android.os.Handler(Looper.getMainLooper()) {
        private var weakReference = WeakReference(activity)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val activity = weakReference.get()
            activity?.handleMessage(msg)
        }
    }

    fun showSnackbar(msg: String) {
        snackbar.let {
            it.setText(msg)
            it.duration = Snackbar.LENGTH_SHORT
            it.show()
        }
    }

    fun showSnackbar(@StringRes resId: Int) {
        CommonUtil.closeKeybord(this)
        snackbar.let {
            it.setText(resId)
            it.duration = Snackbar.LENGTH_SHORT
            it.show()
        }
    }

    fun showSnackbarLong(msg: String) {
        CommonUtil.closeKeybord(this)
        snackbar.let {
            it.setText(msg)
            it.duration = Snackbar.LENGTH_LONG
            it.show()
        }
    }

    fun showSnackbarLong(@StringRes resId: Int) {
        CommonUtil.closeKeybord(this)
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (!(isDestroyed || isFinishing) && !dialog.isShowing) {
                dialog.show()
            }
        } else {
            if (!isFinishing && !dialog.isShowing) {
                dialog.show()
            }
        }
    }

    open fun dismissProgressDialog() {
        if (dialog.isShowing) {
            dialog.dismiss()
        }
    }

    protected open fun handleMessage(msg: Message) {}

    /**
     * 初始化前操作
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