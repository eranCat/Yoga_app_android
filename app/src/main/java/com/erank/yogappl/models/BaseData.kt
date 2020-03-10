package com.erank.yogappl.models

import com.erank.yogappl.utils.SMap
import com.erank.yogappl.utils.data_source.DataSource
import com.erank.yogappl.utils.enums.DataType
import com.erank.yogappl.utils.enums.Status
import com.erank.yogappl.utils.extensions.mapped
import com.erank.yogappl.utils.extensions.newDate
import com.erank.yogappl.utils.extensions.newLatLng
import com.erank.yogappl.utils.extensions.timeStamp
import com.erank.yogappl.utils.interfaces.Searchable
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.Exclude
import com.google.firebase.database.PropertyName
import java.util.*

abstract class BaseData internal constructor() : Searchable {
    lateinit var id: String

    //    TODO check how to have type in lesson
    lateinit var title: String

    @PropertyName("place")
    lateinit var locationName: String

    lateinit var countryCode: String

    lateinit var equip: String

    @get:PropertyName("xtraNotes")
    @set:PropertyName("xtraNotes")
    var extraNotes: String? = null

    var maxParticipants = 0

    //the creators id
    lateinit var uid: String

    @PropertyName("age_min")
    var minAge = 0
    @PropertyName("age_max")
    var maxAge = 0

    //user id : age
    @get:PropertyName("signedUID")
    @set:PropertyName("signedUID")
    var signed: SMap<Int> = hashMapOf()

    @get:Exclude
    @set:Exclude
    lateinit var cost: Money

    @get:Exclude
    @set:Exclude
    lateinit var location: LatLng

    @get:Exclude
    @set:Exclude
    lateinit var postedDate: Date

    @get:Exclude
    @set:Exclude
    lateinit var startDate: Date

    @get:Exclude
    @set:Exclude
    lateinit var endDate: Date

    @get:Exclude
    @set:Exclude
    lateinit var level: Level

    @get:Exclude
    @set:Exclude
    lateinit var status: Status

    @get:Exclude
    abstract val dataType:DataType

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
        xtraNotes: String?,
        maxParticipants: Int,
        uid: String
    ) : this() {
        this.title = title
        this.cost = cost
        this.location = location
        this.locationName = locationName
        this.countryCode = countryCode
        this.postedDate = Date()//now
        this.startDate = startDate
        this.endDate = endDate
        this.level = level
        this.equip = equipment
        this.extraNotes = xtraNotes
        this.maxParticipants = maxParticipants
        this.uid = uid
        this.status = Status.OPEN
        this.minAge = -1
        this.maxAge = 0
        this.signed = mutableMapOf()
    }

    @get:PropertyName("level")
    @set:PropertyName("level")
    var levelFB: Int
        get() = level.ordinal
        set(i) {
            level = Level.values()[i]
        }

    @get:PropertyName("status")
    @set:PropertyName("status")
    var statusFB: Int
        get() = status.ordinal
        set(i) {
            status = Status.values()[i]
        }

    @get:PropertyName("postedDate")
    @set:PropertyName("postedDate")
    var postedDateFB: Double
        get() = postedDate.timeStamp
        set(time) {
            postedDate = newDate(time)
        }

    @get:PropertyName("startDate")
    @set:PropertyName("startDate")
    var startTimestamp: Double
        get() = startDate.timeStamp
        set(time) {
            startDate = newDate(time)
        }

    @get:PropertyName("endDate")
    @set:PropertyName("endDate")
    var endDateFB: Double
        get() = endDate.timeStamp
        set(time) {
            endDate = newDate(time)
        }

    @get:PropertyName("cost")
    @set:PropertyName("cost")
    var costFB
        get() = cost.encoded
        set(value) {
            cost = Money(value)
        }

    @get:PropertyName("location")
    @set:PropertyName("location")
    var locationFB: SMap<Double>
        get() = location.mapped
        set(value) {
            location = newLatLng(value)
        }

    @get:Exclude
    val isCanceled get() = status == Status.CANCELED

    fun getNumOfParticipants() = signed.size

    override fun toString(): String {
        return "BaseData{id='$id'," +
                " title='$title', " +
                "locationName='$locationName'," +
                " countryCode='$countryCode', " +
                "equipment='$equip'," +
                " xtraNotes='$extraNotes', " +
                " maxParticipants=$maxParticipants," +
                "uid='$uid'," +
                " minAge=$minAge," +
                " maxAge=$maxAge, " +
                "signed=$signed, " +
                "cost=$cost," +
                " location=$location," +
                " postedDate=$postedDate, " +
                "startDate=$startDate," +
                " endDate=$endDate," +
                " level=$level, " +
                "status=$status}"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val data = other as BaseData

        return id == data.id &&
                maxParticipants == data.maxParticipants &&
                minAge == data.minAge &&
                maxAge == data.maxAge &&
                title == data.title &&
                locationName == data.locationName &&
                countryCode == data.countryCode &&
                equip == data.equip &&
                extraNotes == data.extraNotes &&
                uid == data.uid &&
                signed == data.signed &&
                cost == data.cost &&
                location == data.location &&
                postedDate == data.postedDate &&
                startDate == data.startDate &&
                endDate == data.endDate &&
                level == data.level &&
                status === data.status
    }

    override fun hashCode() = Objects.hash(id)

    enum class Level {
        ANYONE,
        BEGINNERS,
        INTERMEDIATES,
        ADVANCED;
    }

    override fun searchApplies(query: String): Boolean {
        return (title.contains(query, true)
                || locationName.contains(query, true)
                || DataSource.getUser(uid)?.name
            ?.contains(query, true) ?: false)
    }
}