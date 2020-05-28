package com.erank.yogappl.data.models

import java.util.*

class Teacher : User {

    var teachingLessonsIDs = mutableListOf<String>()

    //Class id
    constructor(
        name: String, email: String, bDate: Date, level: Level,
        about: String? = null, image: String? = null
    ) : super(
        name, email, bDate, level, about, image, Type.TEACHER
    )

    constructor() : super()

    fun addLesson(id: String) = teachingLessonsIDs.add(id)
    fun removeLesson(id: String) = teachingLessonsIDs.remove(id)
}