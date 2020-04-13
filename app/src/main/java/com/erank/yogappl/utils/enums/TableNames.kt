package com.erank.yogappl.utils.enums

import com.erank.yogappl.utils.extensions.lowercaseName

enum class TableNames {

    USERS,
    CLASSES,
    EVENTS;

    companion object {
        fun name(dType: DataType) =
            when (dType) {
                DataType.LESSONS -> CLASSES.lowercaseName
                DataType.EVENTS -> EVENTS.lowercaseName
            }
    }
}