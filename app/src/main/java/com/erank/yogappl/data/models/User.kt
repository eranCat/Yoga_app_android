package com.erank.yogappl.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.erank.yogappl.utils.SSet
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import java.util.*

@Entity(tableName = "users")
open class User {

    @PrimaryKey
    lateinit var id: String
    lateinit var name: String
    lateinit var email: String

    @get:Exclude
    @set:Exclude
    lateinit var bDate: Date
    lateinit var level: Level
    lateinit var type: Type
    var about: String? = null
    var profileImageUrl: String? = null

    @get:Exclude
    @set:Exclude
    var signedLessonsIDS: SSet = mutableSetOf()

    @get:Exclude
    @set:Exclude
    var signedEventsIDS: SSet = mutableSetOf()

    @get:Exclude
    @set:Exclude
    var createdEventsIDs: SSet = mutableSetOf()

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

    @get:PropertyName("bDate")
    @set:PropertyName("bDate")
    var bDateFB: Timestamp
        get() = Timestamp(bDate)
        set(value) {
            bDate = value.toDate()
        }

    @get:PropertyName("createdEventsIds")
    @set:PropertyName("createdEventsIds")
    var createdEventsIDsMap: Map<String, Any>
        get() = createdEventsIDs.associateWith { 0 }
        set(value) {
        createdEventsIDs = value.keys.toMutableSet()
    }

    @get:PropertyName("signedEvents")
    @set:PropertyName("signedEvents")
    var signedEventsMap: Map<String, Any>
        get() = signedEventsIDS.associateWith { true }
        set(value) {
            signedEventsIDS = value.keys.toMutableSet()
        }

    @get:PropertyName("signedClasses")
    @set:PropertyName("signedClasses")
    var signedClassesMap: Map<String, Any>
        get() = signedLessonsIDS.associateWith { true }
        set(value) {
            signedLessonsIDS = value.keys.toMutableSet()
        }

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