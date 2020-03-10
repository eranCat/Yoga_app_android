package com.erank.yogappl.utils.extensions

import android.app.AlertDialog
import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

fun Context.toast(message: String, duration: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(this, message, duration).show()

fun Context.toast(@StringRes message: Int, duration: Int = Toast.LENGTH_SHORT) =
    Toast.makeText(this, message, duration).show()

fun Context.alert(title: String?, msg: String? = null): AlertDialog.Builder =
    AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(msg)

fun Context.alert(
    @StringRes title: Int,
    @StringRes msg: Int
): AlertDialog.Builder =
    AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(msg)
