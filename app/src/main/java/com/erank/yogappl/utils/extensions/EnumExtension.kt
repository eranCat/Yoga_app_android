package com.erank.yogappl.utils.extensions

import android.annotation.SuppressLint

//lower cased name for printing and showing
//extension for all enums
val Enum<*>.lowercaseName
    @SuppressLint("DefaultLocale")
    get() = name.toLowerCase()

//capitalized name
// <Uppercase letter><followed by lower cased letters>
val Enum<*>.cName
    @SuppressLint("DefaultLocale")
    get() = name[0].toUpperCase() + name.substring(1).toLowerCase()

operator fun Enum<*>.minus(b: Enum<*>) = ordinal - b.ordinal

//TODO use in enums with R.string...
//@StringRes
interface LocalizedEnum {
    val lowerCased: Int
    val capitalized: Int
}