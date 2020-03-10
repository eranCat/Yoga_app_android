package com.erank.yogappl.models

import com.erank.yogappl.utils.enums.DataType
import com.google.android.gms.maps.model.LatLng
import java.util.*

class Lesson : BaseData {

    override val dataType = DataType.LESSONS

    constructor() : super()

    constructor(
        title: String,
        cost: Money,
        locationCoordinate: LatLng,
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
        title, cost, locationCoordinate, locationName, countryCode, startDate,
        endDate, level, equipment, xtraNotes, maxParticipants, uid
    )

    var type
        get() = title
        set(value) {
            title = value
        }
}