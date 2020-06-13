package com.erank.yogappl.data.enums

import android.content.Context
import com.erank.yogappl.R
import com.erank.yogappl.utils.extensions.LocalizedEnum
import com.erank.yogappl.utils.extensions.getStringArray

enum class DataType(val singular: Int) : LocalizedEnum {
    LESSONS(R.string.lesson),
    EVENTS(R.string.event);

    companion object{
        const val resArray = R.array.dataType
    }

    override fun getStringArray(context: Context): Array<String> {
        return context.getStringArray(resArray)
    }

    override fun lowerCased(context: Context)= lowerCased(context,ordinal)
    override fun capitalized(context: Context)= capitalized(context,ordinal)
}