package com.erank.yogappl.utils.extensions

operator fun Enum<*>.minus(b: Enum<*>) = ordinal - b.ordinal