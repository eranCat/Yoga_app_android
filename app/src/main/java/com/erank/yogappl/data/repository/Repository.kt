package com.erank.yogappl.data.repository

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import com.erank.yogappl.data.enums.DataType
import com.erank.yogappl.data.enums.SourceType
import com.erank.yogappl.data.models.*

interface Repository {
    var currentUser: User?
    fun getLessons(type: SourceType): LiveData<List<Lesson>>
    fun getEvents(type: SourceType): LiveData<List<Event>>
    suspend fun loadData()
    suspend fun loadAll(dType: DataType)
    suspend fun getUser(uid: String): User?
    suspend fun fetchUserIfNeeded(id: String): User?
    suspend fun fetchUsersIfNeeded(users: Set<String>)
    suspend fun addAllLessons(lessons: List<Lesson>)
    suspend fun addAllEvents(events: List<Event>)
    suspend fun getFilteredEvents(type: SourceType, query: String): List<Event>
    suspend fun getFilteredLessons(type: SourceType, query: String): List<Lesson>
    suspend fun createUser(
        user: User,
        pass: String,
        selectedImage: Uri?,
        bitmap: Bitmap?
    ):User?

    suspend fun updateCurrentUser(selectedImage: Uri?, selectedImageBitmap: Bitmap?)

    suspend fun uploadUserToDB(user: User)
    suspend fun uploadData(
        dType: DataType, data: BaseData, selectedImage: Uri?, selectedBitmap: Bitmap?
    )

    suspend fun updateEvent(
        event: Event, eventImg: Uri?, selectedEventImgBitmap: Bitmap?
    )

    suspend fun deleteLesson(lesson: Lesson)
    suspend fun deleteEvent(event: Event)
    suspend fun getData(dataType: DataType, id: String): BaseData?
    fun isUserSignedToLesson(lesson: Lesson): Boolean
    fun isUserSignedToEvent(event: Event): Boolean
    suspend fun toggleSignToLesson(lesson: Lesson): Boolean
    suspend fun toggleSignToEvent(event: Event): Boolean
    suspend fun updateLesson(lesson: Lesson)
    suspend fun fetchLoggedUser(): User?
    suspend fun getUsers(ids: Set<String>): Map<String, PreviewUser>
    fun clearCurrentUser()
}