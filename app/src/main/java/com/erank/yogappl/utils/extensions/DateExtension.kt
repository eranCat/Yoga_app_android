package com.erank.yogappl.utils.extensions;

import android.content.Context
import android.text.format.DateUtils
import java.text.DateFormat
import java.util.*

fun Date.addMinuets(amount: Int) = add(Calendar.MINUTE, amount)

fun Date.minusYears(years: Int) = add(Calendar.YEAR, -years)

fun Date.add(field: Int, amount: Int): Date =
    cal().apply { add(field, amount) }.time

fun Date.addMonths(amount: Int) = add(Calendar.MONTH, amount)

val Date.epochTime: Long get() = cal().timeInMillis

fun Date.formatted(style: Int = DateFormat.MEDIUM): String? =
    DateFormat.getDateInstance(style).format(this)

fun Date.formatted(
    dateStyle: Int = DateFormat.SHORT,
    timeStyle: Int = DateFormat.SHORT
): String? {
    return DateFormat.getDateTimeInstance(dateStyle, timeStyle).format(this)
}

private fun Date.cal() = Calendar.getInstance().apply { time = this@cal }

fun newDate(year: Int, month: Int, day: Int): Date {
    return cal(year, month, day).time
}

fun newDate(time: Double) = Date((time * 1000).toLong())
val Date.timeStamp get() = time / 1000.0

private fun cal(year: Int, month: Int, day: Int) =
    Calendar.getInstance().apply {
        set(year, month, day)
    }

fun Calendar.getNextMonth() = time.addMonths(1)

fun Date.relativeTimeString(context: Context): CharSequence =
    DateUtils.getRelativeDateTimeString(context, time, 0, 0, 0)

var Calendar.year
    get() = get(Calendar.YEAR)
    set(value) = set(Calendar.YEAR, value)

var Calendar.month
    get() = get(Calendar.MONTH)
    set(value) = set(Calendar.MONTH, value)

var Calendar.dayOfMonth
    get() = get(Calendar.DAY_OF_MONTH)
    set(value) = set(Calendar.DAY_OF_YEAR, value)

var Calendar.hourOfDay
    get() = get(Calendar.HOUR_OF_DAY)
    set(value) = set(Calendar.HOUR_OF_DAY, value)

var Calendar.hour
    get() = get(Calendar.HOUR)
    set(value) = set(Calendar.HOUR, value)

fun Calendar.getHour(is24HourFormat: Boolean) =
    if (is24HourFormat) hourOfDay else hour

fun Calendar.setHour(is24HourFormat: Boolean, hour: Int) {
    if (is24HourFormat)
        this.hourOfDay = hour
    else
        this.hour = hour
}


var Calendar.minute
    get() = get(Calendar.MINUTE)
    set(value) = set(Calendar.MINUTE, value)

fun Date.compareDate(other: Date): Int {
    val yearDiff = year - other.year
    val monthDiff = month - other.month
    val dayDiff = day - other.day

    return yearDiff + monthDiff + dayDiff
}

fun Date.compareTime(other: Date, secondsIncluded: Boolean = false): Int {
    val hourDiff = hours - other.hours
    val minutesDiff = minutes - other.minutes
    if (!secondsIncluded)
        return hourDiff + minutesDiff

    val secondsDiff = seconds - other.seconds

    return hourDiff + minutesDiff + secondsDiff
}

fun Date.equalDate(other: Date) = compareDate(other) == 0

fun Date.equalTime(other: Date, secondsIncluded: Boolean = false)
        = compareTime(other,secondsIncluded) == 0