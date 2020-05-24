package com.erank.yogappl.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.erank.yogappl.utils.SSet
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.PropertyName
import java.util.*

@Entity(tableName = "users")
open class User//or string of path on DB
{
    @PrimaryKey
    lateinit var id: String
    lateinit var name: String
    lateinit var email: String

    @get:Exclude
    @set:Exclude
    lateinit var bDate: Date

    @get:Exclude
    @set:Exclude
    lateinit var level: Level

    @get:Exclude
    @set:Exclude
    lateinit var type: Type

    var about: String?

    @get:PropertyName("profileImage")
    @set:PropertyName("profileImage")
    var profileImageUrl: String?


    @get:Exclude
    @set:Exclude
    var signedLessonsIDS: SSet

    @get:Exclude
    @set:Exclude
    var signedEventsIDS: SSet

    @get:Exclude
    @set:Exclude
    var createdEventsIDs: SSet


    constructor(
        name: String, email: String,
        bDate: Date, level: Level,
        about: String? = null, selectedImg: String? = null,
        type: Type = Type.STUDENT
    ) {
        this.name = name
        this.email = email
        this.bDate = bDate
        this.level = level
        this.type = type
        this.about = about
        this.profileImageUrl = selectedImg
    }

    constructor()

    init {
        this.signedLessonsIDS = mutableSetOf()
        this.signedEventsIDS = mutableSetOf()
        this.createdEventsIDs = mutableSetOf()

        about = null
        profileImageUrl = null
    }

    //    for firebase
    @get:PropertyName("bDate")
    @set:PropertyName("bDate")
    var bDateFB: Long
        get() = bDate.time
        set(time) {
            bDate = Date(time)
        }

    @get:PropertyName("level")
    @set:PropertyName("level")
    var levelFB: Int
        get() = level.ordinal + 1
        set(i) {
            level = Level.values()[i - 1]
        }

    @get:PropertyName("type")
    @set:PropertyName("type")
    var typeFB: Int
        get() = type.ordinal
        set(i) {
            type = Type.values()[i]
        }


    @get:PropertyName("createdEventsIds")
    @set:PropertyName("createdEventsIds")
    var createdEventsIDsMap: MutableMap<String, Int>
        get() = createdEventsIDs.associateWith { 0 }.toMutableMap()
        set(value) {
            createdEventsIDs = value.keys.toMutableSet()
        }

    @get:PropertyName("signedEvents")
    @set:PropertyName("signedEvents")
    var signedEventsMap: MutableMap<String, Boolean>
        get() = signedEventsIDS.associateWith { true }.toMutableMap()
        set(value) {
            signedEventsIDS = value.keys.toMutableSet()
        }

    @get:PropertyName("signedClasses")
    @set:PropertyName("signedClasses")
    var signedClassesMap: MutableMap<String, Boolean>
        get() = signedLessonsIDS.associateWith { true }.toMutableMap()
        set(value) {
            signedLessonsIDS = value.keys.toMutableSet()
        }

    @get:Exclude
    val infoMap
        get() = mapOf(
            "name" to name,
            "bDate" to bDateFB,
            "level" to levelFB,
            "about" to about,
            "profileImage" to profileImageUrl
        )

    fun addEvent(id: String) = createdEventsIDs.add(id)
    fun removeEvent(id: String) = createdEventsIDs.remove(id)

    @IgnoreExtraProperties
    enum class Level {
        BEGINNER,
        INTERMEDIATE,
        ADVANCED;
    }

    @IgnoreExtraProperties
    enum class Type {
        STUDENT,
        TEACHER;
    }

}