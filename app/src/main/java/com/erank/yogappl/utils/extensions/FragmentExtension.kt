package com.erank.yogappl.utils.extensions

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment

fun Fragment.toast(message: String, duration: Int = Toast.LENGTH_SHORT) =
    context?.toast(message, duration)

fun Fragment.toast(@StringRes message: Int, duration: Int = Toast.LENGTH_SHORT) =
    context?.toast(message, duration)

fun Fragment.alert(
    @StringRes title: Int? = null,
    @StringRes msg: Int? = null
) = context?.alert(title, msg)


fun Fragment.alert(title: String?, msg: String? = null) = context?.alert(title, msg)

fun Fragment.alert(@StringRes title: Int, msg: String) = context?.alert(title, msg)