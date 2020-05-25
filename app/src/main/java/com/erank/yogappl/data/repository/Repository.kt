package com.erank.yogappl.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import com.erank.yogappl.data.enums.DataType
import com.erank.yogappl.data.enums.SourceType
import com.erank.yogappl.data.models.*
import com.erank.yogappl.utils.interfaces.TaskCallback
import com.erank.yogappl.utils.interfaces.UploadDataTaskCallback
import com.erank.yogappl.utils.interfaces.UserTaskCallback

interface Repository {
    var currentUser: User?
    fun getLessons(type: SourceType): LiveData<List<Lesson>>
    fun getEvents(type: SourceType): LiveData<List<Event>>
    fun loadData(context: Context, onLoadedCallback: TaskCallback<Void, Exception>)
    fun loadAll(
        dType: DataType,
        loaded: TaskCallback<Void, Exception>
    )

    fun getUser(uid: String, callback: (User?) -> Unit)
    fun fetchUserIfNeeded(uid: String, callback: UserTaskCallback)
    fun addAllLessons(lessons: List<Lesson>, callback: () -> Unit)
    fun addAllEvents(events: List<Event>, callback: () -> Unit)
    suspend fun getFilteredEvents(type: SourceType, query: String): List<Event>
    suspend fun getFilteredLessons(type: SourceType, query: String): List<Lesson>
    fun createUser(
        user: User,
        pass: String,
        selectedImage: Uri?,
        bitmap: Bitmap?,
        callback: UserTaskCallback
    )

    fun updateCurrentUser(
        selectedImage: Uri?,
        selectedImageBitmap: Bitmap?,
        callback: UserTaskCallback
    )

    fun uploadUserToDB(user: User, listener: UserTaskCallback)
    fun uploadData(
        dType: DataType,
        data: BaseData,
        selectedImage: Uri?,
        selectedBitmap: Bitmap?,
        callback: UploadDataTaskCallback
    )

    fun updateEvent(
        event: Event,
        eventImg: Uri?,
        selectedEventImgBitmap: Bitmap?,
        callback: UploadDataTaskCallback
    )

    fun deleteLesson(lesson: Lesson, callback: TaskCallback<Int, Exception>)
    fun deleteEvent(event: Event, callback: TaskCallback<Int, Exception>)
    fun getData(dataType: DataType, id: String, callback: (BaseData?) -> Unit)
    fun isUserSignedToLesson(lesson: Lesson): Boolean
    fun isUserSignedToEvent(event: Event): Boolean
    fun toggleSignToLesson(lesson: Lesson, callback: TaskCallback<Boolean, Exception>)
    fun toggleSignToEvent(event: Event, callback: TaskCallback<Boolean, Exception>)
    fun updateLesson(lesson: Lesson, callback: UploadDataTaskCallback)
    fun fetchLoggedUser(callback: UserTaskCallback)
    suspend fun getUsers(ids: Set<String>):Map<String, PreviewUser>
    fun clearCurrentUser()
}