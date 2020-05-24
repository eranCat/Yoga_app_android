package com.erank.yogappl.data.models

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
import java.util.*

class Teacher : User {

    @get:Exclude
    @set:Exclude
    var teachingLessonsIDs: MutableSet<String>

    //Class id
    constructor(
        name: String,
        email: String,
        bDate: Date,
        level: Level,
        about: String? = null,
        selectedImage: String?
    ) : super(
        name, email, bDate, level, about, selectedImage, Type.TEACHER
    )

    init {
        teachingLessonsIDs = mutableSetOf()
    }

    @get:PropertyName("teachingClassesIDs")
    @set:PropertyName("teachingClassesIDs")
    var teachingClassesMap: Map<String, Any>
        get() = teachingLessonsIDs.associateWith { 0 }
        set(value) {
            teachingLessonsIDs = value.keys.toMutableSet()
        }

    constructor() : super()

    fun addLesson(id: String) = teachingLessonsIDs.add(id)
    fun removeLesson(id: String) = teachingLessonsIDs.remove(id)
}