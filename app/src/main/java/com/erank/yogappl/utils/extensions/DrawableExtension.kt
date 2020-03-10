package com.erank.yogappl.utils.extensions

import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.core.graphics.drawable.DrawableCompat

fun Drawable.getTintedCompat(@ColorInt color: Int): Drawable {
    val drawable = DrawableCompat.wrap(this)
    DrawableCompat.setTint(drawable, color)
    return drawable
}