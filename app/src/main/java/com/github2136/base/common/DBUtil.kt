package com.github2136.base.common

import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.math.BigDecimal

/**
 * Created by YB on 2020/3/11
 * DataBindingUtil
 */
object DBUtil {
    /**
     * TextView
     */
    @BindingAdapter("android:text")
    @JvmStatic
    fun setText(view: TextView, value: Byte?) {
        view.text = value?.toString()
    }

    @BindingAdapter("android:text")
    @JvmStatic
    fun setText(view: TextView, value: Short?) {
        view.text = value?.toString()
    }

    @BindingAdapter("android:text")
    @JvmStatic
    fun setText(view: TextView, value: Int?) {
        view.text = value?.toString()
    }

    @BindingAdapter("android:text")
    @JvmStatic
    fun setText(view: TextView, value: Long?) {
        view.text = value?.toString()
    }

    @BindingAdapter("android:text")
    @JvmStatic
    fun setText(view: TextView, value: Float?) {
        view.text = value?.let {
            BigDecimal(it.toString()).toPlainString()
        }
    }

    @BindingAdapter("android:text")
    @JvmStatic
    fun setText(view: TextView, value: Double?) {
        view.text = value?.let {
            BigDecimal(it.toString()).toPlainString()
        }
    }
}