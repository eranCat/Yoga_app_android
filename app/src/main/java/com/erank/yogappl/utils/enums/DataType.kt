package com.erank.yogappl.utils.enums

enum class DataType {
    LESSONS, EVENTS;

    val singular
        get() = when (this) {
            LESSONS -> "lesson"
            EVENTS -> "event"
        }
}