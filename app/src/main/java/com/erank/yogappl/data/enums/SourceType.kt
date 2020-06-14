package com.erank.yogappl.data.enums

import android.content.Context
import com.erank.yogappl.R
import com.erank.yogappl.utils.extensions.LocalizedEnum

enum class SourceType: LocalizedEnum {
    ALL,
    SIGNED,
    UPLOADS;

    companion object {
        val resArray get() = R.array.sourceType
    }

    override fun getStringArray(context: Context): Array<String> =
        context.resources.getStringArray(resArray)

    override fun lowerCased(context: Context) = lowerCased(context,ordinal)

    override fun capitalized(context: Context) = capitalized(context,ordinal)
}