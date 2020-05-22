package com.erank.yogappl.utils.enums

import com.erank.yogappl.utils.extensions.lowercaseName

enum class TableNames {

    USERS,
    LESSONS,
    EVENTS;

    companion object {
        fun name(dType: DataType) =
            when (dType) {
                DataType.LESSONS -> LESSONS.lowercaseName
                DataType.EVENTS -> EVENTS.lowercaseName
            }
    }
}