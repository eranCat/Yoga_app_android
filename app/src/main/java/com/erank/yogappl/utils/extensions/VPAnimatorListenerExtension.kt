package com.erank.yogappl.utils.extensions

import android.animation.Animator
import android.view.ViewPropertyAnimator

fun ViewPropertyAnimator.setEndedListener(ended: (Animator?) -> Unit): ViewPropertyAnimator =
    setListener(object : Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {
        }

        override fun onAnimationEnd(animation: Animator?) = ended(animation)

        override fun onAnimationCancel(animation: Animator?) {

        }

        override fun onAnimationStart(animation: Animator?) {

        }

    })