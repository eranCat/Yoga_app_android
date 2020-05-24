package com.erank.yogappl.utils

import com.erank.yogappl.data.enums.DataType

abstract class TypeNotSupported : Exception() {
    override fun getLocalizedMessage() = "type not supported"
}

object LevelErrors {
    class LevelTypeNotSupported : TypeNotSupported() {
        override fun getLocalizedMessage() = "Level ${super.getLocalizedMessage()}"
    }
}

object UserTypeErrors {
    class UserTypeNotSupported : TypeNotSupported() {
        override fun getLocalizedMessage() = "User ${super.getLocalizedMessage()}"
    }
}

object MoneyErrors {
    class MoneyTypeNotSupported : TypeNotSupported() {
        override fun getLocalizedMessage() = "Money ${super.getLocalizedMessage()}"
    }
}

class DateNotSupported : TypeNotSupported() {
    override fun getLocalizedMessage() = "Date ${super.getLocalizedMessage()}"
}

object LocationErrors {
    class LocationAmbiguous : Exception() {
        override fun getLocalizedMessage() = "Unclear location"
    }
}

object UserErrors {
    class NoUserFound : Exception() {
        override fun getLocalizedMessage() = "We couldn't find your user"
    }

    class UserIDUndefined : Exception() {
        override fun getLocalizedMessage() = "can't identify User id"
    }
}

class DataTypeError : Exception("incompatibleType") {
    override fun getLocalizedMessage() = "something wrong with data conversion"
}

object StorageErrors {
    class ProblemWithUrl : Exception() {
        override fun getLocalizedMessage() = "Problem with url"
    }
}

object SigningErrors {
    class CanNotSignOut : Exception() {
        override fun getLocalizedMessage() = "you can't sign in"
    }

    class NoPlaceLeft : Exception() {
        override fun getLocalizedMessage() = "There's no place left"
    }

    class AlreadySignedToClass : Exception() {
        override fun getLocalizedMessage() = "You're already signed in to this lesson"
    }

    class AlreadySignedToEvent : Exception() {
        override fun getLocalizedMessage() = "You're already signed in to this event"
    }

    class CantSignToCancled(private val type: DataType) : Exception() {
        override fun getLocalizedMessage() =
            "This ${type.singular} was cancelled ,so you can't sign in"
    }

}