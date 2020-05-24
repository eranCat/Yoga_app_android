package com.erank.yogappl.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Address(
    val streetNumber: Int,
    val streetName: String?,
    val countryCode: String,
    val country: String,
    val freeformAddress: String,
    val localName: String?
) : Parcelable {
    constructor(freeformAddress: String, countryCode: String) :
            this(
                0, "",
                countryCode, "",
                freeformAddress, ""
            )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Address

        if (streetNumber != other.streetNumber) return false
        if (streetName != other.streetName) return false
        if (countryCode != other.countryCode) return false
        if (country != other.country) return false
        if (freeformAddress != other.freeformAddress) return false
        if (localName != other.localName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = streetNumber
        result = 31 * result + streetName.hashCode()
        result = 31 * result + countryCode.hashCode()
        result = 31 * result + country.hashCode()
        result = 31 * result + freeformAddress.hashCode()
        result = 31 * result + localName.hashCode()
        return result
    }


    override fun toString(): String {
        return "Address(streetNumber=$streetNumber,\n" +
                "streetName='$streetName',\n" +
                "countryCode='$countryCode',\n" +
                "country='$country',\n" +
                "freeformAddress='$freeformAddress',\n" +
                "localName='$localName')"
    }
}