package com.github2136.base.common

import android.content.res.Resources
import android.util.TypedValue
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseMethod
import java.math.BigDecimal

/**
 * Created by YB on 2020/3/11
 * DataBindingUtil
 */
object DBUtil {
    @InverseMethod("str2int")
    @JvmStatic
    fun int2str(value: Int?): String? {
        return value?.let { BigDecimal(it.toString()).toPlainString() }
    }

    @JvmStatic
    fun str2int(value: String?): Int? {
        return value?.let {
            try {
                it.toInt()
            } catch (e: Exception) {
                null
            }
        }
    }

    @InverseMethod("str2double")
    @JvmStatic
    fun double2str(value: Double?): String? {
        return value?.let { BigDecimal(it.toString()).toPlainString() }
    }

    @JvmStatic
    fun str2double(value: String?): Double? {
        return value?.let {
            try {
                it.toDouble()
            } catch (e: Exception) {
                null
            }
        }
    }
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

/**
 * dp2px
 */
val Float.dp2px get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, Resources.getSystem().displayMetrics)
val Int.dp2px get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics)

/**
 * px2dp
 */
val Float.dp2dp get() = this / Resources.getSystem().displayMetrics.density
val Int.dp2dp get() = this / Resources.getSystem().displayMetrics.density

/**
 * sp2px
 */
val Float.sp2px get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this, Resources.getSystem().displayMetrics)
val Int.sp2px get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this.toFloat(), Resources.getSystem().displayMetrics)

/**
 * px2sp
 */
val Float.px2sp get() = this / Resources.getSystem().displayMetrics.scaledDensity
val Int.px2sp get() = this / Resources.getSystem().displayMetrics.scaledDensity