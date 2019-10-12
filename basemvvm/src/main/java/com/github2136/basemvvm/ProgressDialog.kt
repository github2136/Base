package com.github2136.basemvvm

import android.app.Dialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.annotation.NonNull
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager

/**
 * Created by YB on 2019/9/12
 */
@Deprecated(message = "快速显示时有错误")
class ProgressDialog private constructor() : DialogFragment() {

    private val dialog by lazy {
        ProgressDialog(context)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        arguments?.apply {
            dialog.setMessage(getString("message"))
            dialog.setCancelable(getBoolean("cancelable"))
        }
        return dialog
    }

    fun setMessage(msg: String) {
        arguments?.putString("message", msg)
    }

    companion object {
        fun getInstance(cancelable: Boolean = true, message: String = ""): com.github2136.basemvvm.ProgressDialog {
            val dialog = ProgressDialog()
            val bundle = Bundle()
            bundle.putBoolean("cancelable", cancelable)
            bundle.putString("message", message)
            dialog.arguments = bundle
            return dialog
        }
    }
}