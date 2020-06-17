package com.erank.yogappl.utils

import com.erank.yogappl.data.enums.TextFieldValidStates
import com.erank.yogappl.data.enums.TextFieldValidStates.*
import com.erank.yogappl.utils.extensions.addMinuets
import com.erank.yogappl.utils.extensions.compareDate
import com.erank.yogappl.utils.extensions.compareTime
import java.util.*

object Patterns {

    fun isStartDateValid(date: Date?): TextFieldValidStates {
        date ?: return EMPTY

        val now = Date()

        val dateDiff = date.compareDate(now)

        if (dateDiff < 0) return INVALID//before now
        if (dateDiff > 0) return VALID//after now

        //equal date - check for time diff
        return if (date.compareTime(now) > 0) VALID else INVALID
    }

    fun isEndDateValid(date: Date?, startDate: Date) = when {
        date == null -> EMPTY
        date < startDate.addMinuets(30) -> INVALID
        else -> VALID
    }
}