package com.erank.yogappl.utils.extensions

import android.graphics.Color
import android.view.MenuItem
import androidx.annotation.ColorInt

fun MenuItem.setIconTintCompat(@ColorInt color: Int = Color.WHITE) {
    icon = icon.getTintedCompat(color)
}