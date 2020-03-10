package com.erank.yogappl.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LocationResult(
    val score: Double,
    @SerializedName("dist")
    val distance: Double/*distance in meters*/,
    val address: Address,
    @SerializedName("position")
    val location: Position,
    @SerializedName("poi")
    val POI: PointOfInterest?,
    val type: Type
) : Parcelable {
    constructor(
        address: Address,
        location: Position
    ) : this(
        .0, .0,
        address, location,
        null, Type.GEOGRAPHY
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LocationResult

        if (score != other.score) return false
        if (distance != other.distance) return false
        if (address != other.address) return false
        if (location != other.location) return false
        if (POI != other.POI) return false

        return true
    }

    override fun hashCode(): Int {
        var result = score.hashCode()
        result = 31 * result + distance.hashCode()
        result = 31 * result + address.hashCode()
        result = 31 * result + location.hashCode()
        result = 31 * result + POI.hashCode()
        return result
    }

    enum class Type {
        @SerializedName("POI")
        POI,
        @SerializedName("Street")
        STREET,
        @SerializedName("Geography")
        GEOGRAPHY,
        @SerializedName("Point Address")
        POINT_ADDRESS,
        @SerializedName("Address Range")
        ADDRESS_RANGE,
        @SerializedName("Cross Street")
        CROSS_STREET,
    }
}
