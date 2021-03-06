package com.erank.yogappl.utils

import android.util.Patterns
import com.erank.yogappl.data.enums.TextFieldValidStates
import com.erank.yogappl.data.enums.TextFieldValidStates.*
import com.erank.yogappl.utils.extensions.addMinuets
import com.erank.yogappl.utils.extensions.compareDate
import com.erank.yogappl.utils.extensions.compareTime
import java.util.*
import java.util.regex.Pattern

object Patterns {
    private val PASSWORD_PATTERN = Pattern.compile(
//            "^(?=.*[a-z])" + //must contain one
//                    "(?=.*[A-Z])" + //must have a capital letter
//                    "(?=.*[0-9])" + //must contain numbers
//                    "(?=.*[!@#$%^&*])" + //spacial chars
        "(.{6,})"//at least 6 characters
    )

    private val USERNAME_PATTERN =
        Pattern.compile("^(?=.{3,20}\$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])\$")

    fun isEmailValid(email: String) = when {
        email.isBlank() -> EMPTY

        Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
            VALID

        else -> INVALID
    }

    fun isPasswordValid(password: String) = when {
        password.isBlank() -> EMPTY

        PASSWORD_PATTERN.matcher(password).matches() ->
            VALID

        else -> INVALID
    }

    fun isNameValid(name: String) = when {
        name.isBlank() -> EMPTY

        USERNAME_PATTERN.matcher(name).matches() ->
            VALID

        else -> INVALID
    }

    fun isBDateValid(date: Date?) = when (date) {
        null -> EMPTY
        else -> VALID
    }

    inline fun <reified T : Enum<T>> isEnumValid(enumVal: Int) = when {
        enumVal < 0 -> EMPTY
        enumVal in 0..enumValues<T>().size -> VALID
        else -> INVALID
    }

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