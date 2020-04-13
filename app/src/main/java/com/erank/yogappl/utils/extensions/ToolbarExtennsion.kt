package com.erank.yogappl.utils.extensions

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.TransitionDrawable
import androidx.annotation.ColorInt
import androidx.appcompat.widget.Toolbar


fun Toolbar.animateColor(@ColorInt color: Int, durationMillis: Int = 600) {
    val colors = arrayOf(
        background,
        ColorDrawable(color)
    )

    val transition = TransitionDrawable(colors)

    background = transition
    transition.startTransition(durationMillis)
}