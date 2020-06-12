package com.erank.yogappl.data.enums

import androidx.annotation.StringRes
import com.erank.yogappl.R

enum class DataType {
    LESSONS, EVENTS;

    val singular: Int
        @StringRes
        get() =
            when (this) {
                LESSONS -> R.string.lesson
                EVENTS -> R.string.event
            }

}