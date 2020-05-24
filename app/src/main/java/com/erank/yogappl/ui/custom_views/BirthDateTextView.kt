package com.erank.yogappl.ui.custom_views

import android.app.DatePickerDialog
import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.widget.DatePicker
import androidx.appcompat.widget.AppCompatTextView
import com.erank.yogappl.R
import com.erank.yogappl.utils.extensions.*
import java.text.DateFormat.MEDIUM
import java.util.*

typealias OnDateSetListener = (Date?) -> Unit

class BirthDateTextView : AppCompatTextView,
    DatePickerDialog.OnDateSetListener {

    private val maxAge = 120
    private val minAge = 16
    var date: Date?

    private val datePickerDialog: DatePickerDialog
    private var onDateSetListener: OnDateSetListener?

    init {
        inputType = InputType.TYPE_DATETIME_VARIATION_DATE
        isClickable = true
        isFocusable = true
        setBackgroundResource(R.drawable.rounded_edittext)
        datePickerDialog = DatePickerDialog(context).apply {
            val today = Date()
            datePicker.maxDate = today.minusYears(minAge).epochTime
            datePicker.minDate = today.minusYears(maxAge).epochTime

            setOnDateSetListener(this@BirthDateTextView)
        }
        setOnClickListener { datePickerDialog.show() }
        date = null
        onDateSetListener = null
    }

    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
            super(context, attrs, defStyleAttr)

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)


    fun setOnDateSetListener(listener: OnDateSetListener) {
        onDateSetListener = listener
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        date = newDate(year, month, dayOfMonth)
        text = date?.formatted(MEDIUM)
        onDateSetListener?.invoke(date)
    }

    //    for presenting data
    fun setDatePickerDate(date: Date) {
        datePickerDialog.updateDate(date)
        text = date.formatted(MEDIUM)
    }
}
