package com.erank.yogappl.data.enums

object TableNames {

    const val USERS = "users"
    const val LESSONS = "lessons"
    const val EVENTS = "events"

    fun name(dType: DataType) = when (dType) {
        DataType.LESSONS -> LESSONS
        DataType.EVENTS -> EVENTS
    }
}