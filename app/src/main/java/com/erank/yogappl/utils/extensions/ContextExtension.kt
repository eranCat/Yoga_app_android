package com.erank.yogappl.utils.extensions

import android.app.AlertDialog
import android.content.Context
import android.widget.Toast
import androidx.annotation.ArrayRes
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
    @StringRes title: Int? = null,
    @StringRes msg: Int? = null
): AlertDialog.Builder = AlertDialog.Builder(this)
    .apply {
        title?.let { setTitle(it) }
        msg?.let { setMessage(it) }
    }

fun Context.alert(
    @StringRes title: Int? = null, msg: String
) = alert(title).setMessage(msg)

fun Context.getStringArray(@ArrayRes res: Int) = resources.getStringArray(res)