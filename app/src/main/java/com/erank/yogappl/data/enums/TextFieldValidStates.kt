package com.erank.yogappl.data.enums

enum class TextFieldValidStates(val errorMsg: String?) {
    EMPTY("Can't be empty"),
    INVALID("invalid format"),
    VALID(null)
}