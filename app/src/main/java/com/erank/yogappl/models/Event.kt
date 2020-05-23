package com.erank.yogappl.models

import androidx.room.Entity
import androidx.room.Ignore
import com.erank.yogappl.utils.enums.DataType
import com.google.android.gms.maps.model.LatLng
import java.util.*

@Entity(tableName = "events")
class Event : BaseData {

    @Ignore
    override val dataType = DataType.EVENTS

    var imageUrl: String? = null

    constructor() : super()

    constructor(
        title: String,
        cost: Money,
        location: LatLng,
        locationName: String,
        countryCode: String,
        startDate: Date,
        endDate: Date,
        level: Level,
        equipment: String,
        xtraNotes: String? = null,
        maxParticipants: Int,
        uid: String,
        imageUrl: String? = null
    ) : super(
        title,
        cost,
        location,
        locationName,
        countryCode,
        startDate,
        endDate,
        level,
        equipment,
        xtraNotes,
        maxParticipants,
        uid
    ) {
        this.imageUrl = imageUrl
    }

    override fun equals(other: Any?): Boolean {
        if (!super.equals(other)) return false
        if (javaClass != other.javaClass) return false

        other as Event

        return this.imageUrl == other.imageUrl
    }

    override fun hashCode(): Int {
        return super.hashCode() + (imageUrl?.hashCode() ?: 0)
    }
}