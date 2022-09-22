package com.github2136.base.view.dialog

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.github2136.base.R
import java.util.*

/**
 * Created by YB on 2020/4/16
 * 时间日期选择器
 */
class DateTimePickerDialog private constructor() : DialogFragment() {
    val className by lazy { javaClass.simpleName }
    private var title: String? = null
    private var year: Int? = null
    private var month: Int? = null
    private var dayOfMonth: Int? = null
    private var hourOfDay: Int? = null
    private var minute: Int? = null
    private var is24Hours: Boolean? = null
    private var dateChange: ((year: Int, monthOfYear: Int, dayOfMonth: Int) -> Unit)? = null
    private var timeChange: ((hourOfDay: Int, minute: Int) -> Unit)? = null
    private var dateTimeChange: ((year: Int, monthOfYear: Int, dayOfMonth: Int, hourOfDay: Int, minute: Int) -> Unit)? = null
    private var positive = false
    lateinit var datePicker: DatePicker
    lateinit var timePicker: TimePicker

    val dialogView by lazy { requireActivity().layoutInflater.inflate(R.layout.dialog_date_time_picker, null, false) }
    val alertDialog by lazy {
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setView(dialogView)
            .setPositiveButton("确定") { _, _ ->
                positive = true
                if (::datePicker.isInitialized && datePicker.visibility == View.VISIBLE) {
                    year = datePicker.year
                    month = datePicker.month
                    dayOfMonth = datePicker.dayOfMonth
                }
                if (::timePicker.isInitialized && timePicker.visibility == View.VISIBLE) {
                    hourOfDay = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        timePicker.hour
                    } else {
                        timePicker.currentHour
                    }
                    minute = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        timePicker.minute
                    } else {
                        timePicker.currentMinute
                    }
                }
                when {
                    dateTimeChange != null -> dateTimeChange?.invoke(
                        datePicker.year,
                        datePicker.month,
                        datePicker.dayOfMonth,
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            timePicker.hour
                        } else {
                            timePicker.currentHour
                        }, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            timePicker.minute
                        } else {
                            timePicker.currentMinute
                        }
                    )
                    dateChange != null -> dateChange?.invoke(
                        datePicker.year,
                        datePicker.month,
                        datePicker.dayOfMonth
                    )
                    timeChange != null -> timeChange?.invoke(
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            timePicker.hour
                        } else {
                            timePicker.currentHour
                        }, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            timePicker.minute
                        } else {
                            timePicker.currentMinute
                        }
                    )
                }
            }
            .setNegativeButton("取消") { _, _ ->
                //点取消后日期时间还原
                if (::datePicker.isInitialized && datePicker.visibility == View.VISIBLE) {
                    datePicker.init(year!!, month!!, dayOfMonth!!, null)
                }
                if (::timePicker.isInitialized && timePicker.visibility == View.VISIBLE) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        timePicker.hour = hourOfDay!!
                    } else {
                        timePicker.currentHour = hourOfDay!!
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        timePicker.minute = minute!!
                    } else {
                        timePicker.currentMinute = minute!!
                    }
                }
            }
            .create()
    }

    override fun onStop() {
        super.onStop()
        if (!positive) {
            //点取消后日期时间还原
            if (::datePicker.isInitialized && datePicker.visibility == View.VISIBLE) {
                datePicker.init(year!!, month!!, dayOfMonth!!, null)
            }
            if (::timePicker.isInitialized && timePicker.visibility == View.VISIBLE) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    timePicker.hour = hourOfDay!!
                } else {
                    timePicker.currentHour = hourOfDay!!
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    timePicker.minute = minute!!
                } else {
                    timePicker.currentMinute = minute!!
                }
            }
        }
    }

    constructor (year: Int, month: Int, dayOfMonth: Int, onChanged: (year: Int, monthOfYear: Int, dayOfMonth: Int) -> Unit) :
        this(null, year, month, dayOfMonth, onChanged)

    constructor (title: String? = null, year: Int, month: Int, dayOfMonth: Int, onChanged: (year: Int, monthOfYear: Int, dayOfMonth: Int) -> Unit) :
        this() {
        this.title = title; this.year = year; this.month = month; this.dayOfMonth = dayOfMonth; dateChange = onChanged
    }

    constructor (hourOfDay: Int, minute: Int, onChanged: (hourOfDay: Int, minute: Int) -> Unit, is24Hours: Boolean = true) :
        this(null, hourOfDay, minute, onChanged, is24Hours)

    constructor (title: String? = null, hourOfDay: Int, minute: Int, onChanged: (hourOfDay: Int, minute: Int) -> Unit, is24Hours: Boolean = true) :
        this() {
        this.title = title; this.hourOfDay = hourOfDay; this.minute = minute; this.is24Hours = is24Hours; timeChange = onChanged
    }

    constructor(year: Int, month: Int, dayOfMonth: Int, hourOfDay: Int, minute: Int, is24Hours: Boolean = true, onChanged: (year: Int, monthOfYear: Int, dayOfMonth: Int, hour: Int, minute: Int) -> Unit) :
        this(null, year, month, dayOfMonth, hourOfDay, minute, is24Hours, onChanged)

    constructor(title: String? = null, year: Int, month: Int, dayOfMonth: Int, hourOfDay: Int, minute: Int, is24Hours: Boolean = true, onChanged: (year: Int, monthOfYear: Int, dayOfMonth: Int, hour: Int, minute: Int) -> Unit) :
        this() {
        this.title = title; this.year = year; this.month = month; this.dayOfMonth = dayOfMonth; this.hourOfDay = hourOfDay; this.minute = minute; this.is24Hours = is24Hours; dateTimeChange = onChanged
    }

    fun set(year: Int, month: Int, dayOfMonth: Int) {
        this.year = year
        this.month = month
        this.dayOfMonth = dayOfMonth
    }

    fun set(hourOfDay: Int, minute: Int) {
        this.hourOfDay = hourOfDay
        this.minute = minute
    }

    fun set(year: Int, month: Int, dayOfMonth: Int, hourOfDay: Int, minute: Int) {
        this.year = year
        this.month = month
        this.dayOfMonth = dayOfMonth
        this.hourOfDay = hourOfDay
        this.minute = minute
    }

    fun setTitle(title: String): DateTimePickerDialog {
        this.title = title
        return this
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        positive = false

        year?.let {
            datePicker = dialogView.findViewById<DatePicker>(R.id.dpDate).apply {
                visibility = View.VISIBLE
                init(this@DateTimePickerDialog.year!!, this@DateTimePickerDialog.month!!, this@DateTimePickerDialog.dayOfMonth!!, null)
            }
        }
        hourOfDay?.let {
            timePicker = dialogView.findViewById<TimePicker>(R.id.tpTime).apply {
                visibility = View.VISIBLE
                setIs24HourView(this@DateTimePickerDialog.is24Hours!!)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    hour = this@DateTimePickerDialog.hourOfDay!!
                } else {
                    currentHour = this@DateTimePickerDialog.hourOfDay!!
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    minute = this@DateTimePickerDialog.minute!!
                } else {
                    currentMinute = this@DateTimePickerDialog.minute!!
                }
            }
        }
        return alertDialog
    }

    fun show(manager: FragmentManager) {
        show(manager, className)
    }
}