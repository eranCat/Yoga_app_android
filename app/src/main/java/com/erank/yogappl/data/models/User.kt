package com.erank.yogappl.data.models

import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.erank.yogappl.R
import com.erank.yogappl.utils.extensions.LocalizedEnum
import com.erank.yogappl.utils.extensions.getStringArray
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

    var signedLessonsIDS = mutableListOf<String>()
    var signedEventsIDS = mutableListOf<String>()
    var createdEventsIDs = mutableListOf<String>()

    constructor(
        name: String, email: String, bDate: Date, level: Level,
        about: String? = null, selectedImg: String? = null, type: Type = Type.STUDENT
    ) {
        this.name = name
        this.email = email
        this.bDate = bDate
        this.level = level
        this.about = about
        this.profileImageUrl = selectedImg
        this.type = type
    }

    constructor()

    @get:PropertyName("bDate")
    @set:PropertyName("bDate")
    var bDateFB: Timestamp
        get() = Timestamp(bDate)
        set(value) {
            bDate = value.toDate()
        }

    fun addEvent(id: String) = createdEventsIDs.add(id)
    fun removeEvent(id: String) = createdEventsIDs.remove(id)

    @IgnoreExtraProperties
    enum class Level : LocalizedEnum {
        BEGINNER,
        INTERMEDIATE,
        ADVANCED;

        companion object {
            const val resValues = R.array.userLevel
        }

        override fun getStringArray(context: Context): Array<String> {
            return context.getStringArray(resValues)
        }
        override fun lowerCased(context: Context) = lowerCased(context,ordinal)
        override fun capitalized(context: Context) = capitalized(context,ordinal)
    }

    @IgnoreExtraProperties
    enum class Type : LocalizedEnum {
        STUDENT,
        TEACHER;

        companion object {
            const val resValues = R.array.userType
        }

        override fun getStringArray(context: Context) =
            context.getStringArray(resValues)

        override fun lowerCased(context: Context) = lowerCased(context,ordinal)
        override fun capitalized(context: Context) = capitalized(context,ordinal)
    }

}

class PreviewUser(
    val id: String,
    val name: String,
    val profileImageUrl: String?
)