package com.erank.yogappl.utils.extensions

import android.graphics.Color.*

/**
 * @alpha - value range 0 - 255 ; where 0 is clear
 */
fun Int.withAlpha(alpha: Int): Int {
    val r = red(this)
    val g = green(this)
    val b = blue(this)
    return argb(alpha, r, g, b)
}
