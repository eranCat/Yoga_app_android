package com.erank.yogappl.utils.helpers

import com.erank.yogappl.data.models.User
import com.erank.yogappl.utils.Patterns
import com.erank.yogappl.data.enums.TextFieldValidStates
import com.erank.yogappl.data.enums.TextFieldValidStates.EMPTY
import com.erank.yogappl.data.enums.TextFieldValidStates.VALID
import java.util.*

class UserValidator(state: TextFieldValidStates, vararg fields: Fields) {

    private val validations = fields.associate { it to state }.toMutableMap()

    constructor() : this(EMPTY, *Fields.values())


    enum class Fields {
        NAME,
        LEVEL,
        TYPE,
        DATE,
        EMAIL,
        PASS
    }

    fun validateName(name: String) =
        Patterns.isNameValid(name).apply {
            validations[Fields.NAME] = this
        }

    fun validateLevel(level: Int) {
        Patterns.isEnumValid<User.Level>(level).apply {
            validations[Fields.LEVEL] = this
        }
    }


    fun validateType(type: Int) =
        Patterns.isEnumValid<User.Type>(type).apply {
            validations[Fields.TYPE] = this
        }

    fun validateBDate(date: Date?) =
        Patterns.isBDateValid(date).apply {
            validations[Fields.DATE] = this
        }


    fun validateEmail(email: String) =
        Patterns.isEmailValid(email).apply {
            validations[Fields.EMAIL] = this
        }


    fun validatePassword(pass: String) =
        Patterns.isPasswordValid(pass).apply {
            validations[Fields.PASS] = this
        }

    val isDataValid get() = validations.values.all { it == VALID }

    val isNotDataValid get() = !isDataValid
}