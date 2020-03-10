package com.erank.yogappl.models

import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.Exclude

class Position(
    val lat: Double,
    val lon: Double
) : Parcelable {
    @get:Exclude
    val latLng
        get() = LatLng(lat, lon)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Position

        if (lat != other.lat) return false
        if (lon != other.lon) return false

        return true
    }

    override fun hashCode(): Int {
        var result = lat.hashCode()
        result = 31 * result + lon.hashCode()
        return result
    }

    constructor(source: Parcel) : this(
        source.readDouble(),
        source.readDouble()
    )

    constructor(latLng: LatLng) :
            this(
                latLng.latitude,
                latLng.longitude
            )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeDouble(lat)
        writeDouble(lon)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Position> = object : Parcelable.Creator<Position> {
            override fun createFromParcel(source: Parcel): Position = Position(source)
            override fun newArray(size: Int): Array<Position?> = arrayOfNulls(size)
        }
    }
}