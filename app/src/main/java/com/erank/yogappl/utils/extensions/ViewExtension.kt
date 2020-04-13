package com.erank.yogappl.utils.extensions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE

fun View.toggleRotation(isVisible: Boolean) {
    val rotationAngle = if (isVisible) 0f else 180f //toggle
    animate().rotation(rotationAngle).setDuration(300).start()
}

fun View.toggleSlide(isVisible: Boolean) {
    if (!isVisible) {
        alpha = 0f
        visibility = VISIBLE
    }
    val fl = if (isVisible) 0f else 1f
    animate().scaleY(fl).alpha(fl).setDuration(200)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                if (isVisible)
                    visibility = GONE
            }
        })
}