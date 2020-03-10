package com.erank.yogappl.utils.extensions

fun <T> MutableList<T>.replace(oldItem: T, newItem: T): Boolean {
    val i = indexOf(oldItem)
    if (i == -1) return false

    this[i] = newItem
    return true
}