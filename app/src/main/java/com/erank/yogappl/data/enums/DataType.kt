package com.erank.yogappl.data.enums

enum class DataType {
    LESSONS, EVENTS;

    val singular
        get() = when (this) {
            LESSONS -> "lesson"
            EVENTS -> "event"
        }
}