package com.erank.yogappl.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Address(
    val streetNumber: String = "",
    val streetName: String? = null,
    val countryCode: String = "",
    val country: String = "",
    @SerializedName("freeformAddress")
    val longName: String = "",
    val localName: String? = null
) : Parcelable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Address

        if (streetNumber != other.streetNumber) return false
        if (streetName != other.streetName) return false
        if (countryCode != other.countryCode) return false
        if (country != other.country) return false
        if (longName != other.longName) return false
        if (localName != other.localName) return false

        return true
    }

    override fun toString(): String {
        return "Address(streetNumber=$streetNumber,\n" +
                "streetName='$streetName',\n" +
                "countryCode='$countryCode',\n" +
                "country='$country',\n" +
                "freeformAddress='$longName',\n" +
                "localName='$localName')"
    }
}