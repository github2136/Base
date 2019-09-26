package com.github2136.basemvvm

import android.app.Dialog
import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

/**
 * Created by YB on 2019/9/12
 */
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

    fun isShowing() = dialog.isShowing

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