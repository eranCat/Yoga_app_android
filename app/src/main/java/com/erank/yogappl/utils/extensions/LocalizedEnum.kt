package com.erank.yogappl.utils.extensions

import android.content.Context
import java.util.*

interface LocalizedEnum{

    fun getStringArray(context: Context): Array<String>
    fun lowerCased(context: Context): String
    fun capitalized(context: Context):String

    fun lowerCased(context: Context, ordinal: Int): String {
        val name = getStringArray(context)[ordinal]
        return name.toLowerCase(Locale.getDefault())
    }

    fun capitalized(context: Context, ordinal: Int): String {
        val name = getStringArray(context)[ordinal]
        return name[0].toUpperCase() + name.substring(1)
            .toLowerCase(Locale.getDefault())
    }
}