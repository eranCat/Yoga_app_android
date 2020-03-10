package com.erank.yogappl.utils.extensions

import android.app.DatePickerDialog
import java.util.*

fun DatePickerDialog.updateDate(date: Date) {
    Calendar.getInstance().apply {
        time = date
        //the month of the dialog is 0-11
        updateDate(
            get(Calendar.YEAR),
            get(Calendar.MONTH) - 1, //Gets the month-of-year field from 1 to 12.
            get(Calendar.DAY_OF_MONTH)
        )
    }
}