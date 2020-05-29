package com.erank.yogappl.data.models

import java.util.*

abstract class PreviewData {
    abstract val id: String
    abstract val title: String
    abstract val date: DateRange
}

class DateRange(val start: Date, val end: Date)