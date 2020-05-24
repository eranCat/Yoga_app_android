package com.erank.yogappl.utils.helpers

import com.erank.yogappl.data.models.BaseData
import com.erank.yogappl.data.models.LocationResult
import com.erank.yogappl.utils.Patterns
import com.erank.yogappl.data.enums.TextFieldValidStates
import com.erank.yogappl.data.enums.TextFieldValidStates.VALID
import java.util.*

class BaseDataValidator(state: TextFieldValidStates, vararg fields: Fields) {

    private val validations = fields.associate { it to state }.toMutableMap()

    constructor(state: TextFieldValidStates) : this(state, *Fields.values())

    enum class Fields {
        TITLE,
        LOCATION,
        START_DATE,
        END_DATE,
        MAX_PPL,
        LEVEL,
        COST,
        EQUIP
    }

    fun validateTitle(title: String): TextFieldValidStates {
        val state = Patterns.isTitleValid(title)
        validations[Fields.TITLE] = state
        return state
    }

    fun validateLevel(level: Int): TextFieldValidStates {
        val state = Patterns.isEnumValid<BaseData.Level>(level)
        validations[Fields.LEVEL] = state
        return state
    }

    fun validateLocation(location: LocationResult?): TextFieldValidStates {
        val state = Patterns.isLocationValid(location)
        validations[Fields.LOCATION] = state
        return state
    }

    fun validateStartDate(date: Date?): TextFieldValidStates {
        val state = Patterns.isStartDateValid(date)
        validations[Fields.START_DATE] = state
        return state
    }

    fun validateEndDate(date: Date?, startDate: Date): TextFieldValidStates {
        val state = Patterns.isEndDateValid(date, startDate)
        validations[Fields.END_DATE] = state
        return state
    }


    fun validateMinMaxPPL(max: Int): TextFieldValidStates {
        val state = Patterns.isMinMaxPPLValid(max)
        validations[Fields.MAX_PPL] = state
        return state
    }

    fun validateCost(cost: String): TextFieldValidStates {
        val state = Patterns.isCostValid(cost)
        validations[Fields.COST] = state
        return state
    }

    fun validateEquipment(equipment: String): TextFieldValidStates {
        val state = Patterns.isEquipValid(equipment)
        validations[Fields.EQUIP] = state
        return state
    }


    val isDataValid: Boolean
        get() = validations.values.all { it == VALID }
}