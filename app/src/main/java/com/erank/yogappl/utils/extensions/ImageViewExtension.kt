package com.erank.yogappl.utils.extensions

import android.widget.ImageView

fun ImageView.startZoomAnimation(done: () -> Unit) {
    val scaleFactor = 60f

    animate().scaleXBy(scaleFactor).scaleYBy(scaleFactor)
        .translationYBy(-100f)
        .alpha(0f)
        .setDuration(700)
        .setEndedListener { done() }
        .start()
}