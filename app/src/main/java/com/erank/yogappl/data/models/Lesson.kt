package com.erank.yogappl.data.models

import androidx.room.Entity
import androidx.room.Ignore
import com.erank.yogappl.data.enums.DataType
import com.google.android.gms.maps.model.LatLng
import java.util.*

@Entity(tableName = "lessons")
class Lesson : BaseData {

    @Ignore
    override val dataType = DataType.LESSONS

    constructor() : super()

    constructor(
        title: String,
        cost: Double,
        location: LatLng,
        locationName: String,
        countryCode: String,
        startDate: Date,
        endDate: Date,
        level: Level,
        equipment: String,
        xtraNotes: String?,
        maxParticipants: Int,
        uid: String
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
    )
}