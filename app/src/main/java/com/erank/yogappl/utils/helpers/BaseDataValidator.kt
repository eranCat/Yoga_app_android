package com.erank.yogappl.utils.helpers

import com.erank.yogappl.utils.Patterns
import java.util.*

object BaseDataValidator {

    fun validateStartDate(date: Date?) = Patterns.isStartDateValid(date)

    fun validateEndDate(date: Date?, startDate: Date) = Patterns.isEndDateValid(date, startDate)
}