package com.erank.yogappl.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PointOfInterest(
    val name: String?,
    val phone: String?,
    val url: String?
) : Parcelable {

    @Parcelize
    data class Brand(val name: String) : Parcelable

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PointOfInterest

        if (name != other.name) return false
        if (phone != other.phone) return false
        if (url != other.url) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + phone.hashCode()
        result = 31 * result + url.hashCode()
        return result
    }

}