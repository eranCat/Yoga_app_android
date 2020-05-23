package com.erank.yogappl.models

import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.erank.yogappl.utils.SMap
import com.erank.yogappl.utils.enums.DataType
import com.erank.yogappl.utils.enums.Status
import com.erank.yogappl.utils.extensions.*
import com.erank.yogappl.utils.interfaces.Searchable
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
import java.util.*

abstract class BaseData internal constructor() : Searchable {
    @PrimaryKey
    lateinit var id: String

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
    abstract val dataType: DataType

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

    @get:Ignore
    @set:Ignore
    @get:PropertyName("level")
    @set:PropertyName("level")
    var levelFB: Int
        get() = level.ordinal
        set(i) {
            level = Level.values()[i]
        }

    @get:Ignore
    @set:Ignore
    @get:PropertyName("status")
    @set:PropertyName("status")
    var statusFB: Int
        get() = status.ordinal
        set(i) {
            status = Status.values()[i]
        }

    @get:Ignore
    @set:Ignore
    @get:PropertyName("postedDate")
    @set:PropertyName("postedDate")
    var postedDateFB: Timestamp
        get() = Timestamp(postedDate)
        set(time) {
            postedDate = time.toDate()
        }

    @get:Ignore
    @set:Ignore
    @get:PropertyName("startDate")
    @set:PropertyName("startDate")
    var startTimestamp: Timestamp
        get() = Timestamp(startDate)
        set(time) {
            startDate = time.toDate()
        }

    @get:Ignore
    @set:Ignore
    @get:PropertyName("endDate")
    @set:PropertyName("endDate")
    var endDateFB: Timestamp
        get() = Timestamp(endDate)
        set(time) {
            endDate = time.toDate()
        }

    @get:Ignore
    @set:Ignore
    @get:PropertyName("cost")
    @set:PropertyName("cost")
    var costFB
        get() = cost.encoded
        set(value) {
            cost = Money(value)
        }

    @get:Ignore
    @set:Ignore
    @get:PropertyName("location")
    @set:PropertyName("location")
    var locationFB: SMap<Double>
        get() = location.mapped
        set(value) {
            location = newLatLng(value)
        }

    @get:Ignore
    @get:Exclude
    val isCanceled
        get() = status == Status.CANCELED

    @Ignore
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
        return title.contains(query, true)
                || locationName.contains(query, true)
//                || DataSource.getUser(uid)?.name
//            ?.contains(query, true) ?: false
//        TODO add search by user name
    }
}