package com.github2136.basemvvm

import android.app.ProgressDialog
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import java.lang.ref.WeakReference
import java.lang.reflect.ParameterizedType

/**
 * Created by YB on 2019/8/29
 */
abstract class BaseActivity<V : BaseVM, B : ViewDataBinding> : AppCompatActivity() {
    protected lateinit var vm: V
    protected lateinit var bind: B
    protected val TAG = this.javaClass.name
    protected val mApp by lazy { application as BaseApplication }
    protected val mHandler by lazy { Handler(this) }
    protected lateinit var mToast: Toast
    protected val mDialog: ProgressDialog by lazy {
        ProgressDialog(this)
    }
//    protected val mDialog: ProgressDialog by lazy {
//        val dialog = ProgressDialog.getInstance(false)
//        dialog
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mApp.addActivity(this)
        bind = DataBindingUtil.setContentView(this, getLayoutId())
        bind.lifecycleOwner = this

        val type = (this.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments
        getVM(type[0] as Class<V>)
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT)
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

    override fun onDestroy() {
        cancelRequest()
        mApp.removeActivity(this)
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

    ///////////////////////////////////////////////////////////////////////////
    // Handler
    ///////////////////////////////////////////////////////////////////////////
    class Handler(activity: BaseActivity<out BaseVM, out ViewDataBinding>) :android.os.Handler(Looper.getMainLooper()) {
        private var weakReference = WeakReference(activity)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val activity = weakReference.get()
            activity?.handleMessage(msg)
        }
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if ((!isDestroyed || !isFinishing) && !mDialog.isShowing) {
                mDialog.show()
            }
        } else {
            if (!isFinishing && !mDialog.isShowing) {
                mDialog.show()
            }
        }
    }

    open fun dismissProgressDialog() {
        if (mDialog.isShowing) {
            mDialog.dismiss()
        }
    }

    protected open fun handleMessage(msg: Message) {}

    //布局ID
    protected abstract fun getLayoutId(): Int

    //初始化
    protected abstract fun initData(savedInstanceState: Bundle?)

    //初始化回调
    protected abstract fun initObserve()

    //取消请求
    protected fun cancelRequest() {
        vm.cancelRequest()
    }
}