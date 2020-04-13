package com.erank.yogappl.utils.extensions

fun Double.formattedDistance() = if (this < 1000) {
    String.format("%.0f meters", this)
} else String.format("%.1f km", this / 1000)