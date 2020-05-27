package com.erank.yogappl.data.repository

import com.erank.yogappl.data.enums.DataType
import com.erank.yogappl.data.enums.SourceType
import com.erank.yogappl.data.enums.SourceType.*
import com.erank.yogappl.data.models.*
import com.erank.yogappl.data.room.AppDatabase
import com.erank.yogappl.data.room.dao.EventDao
import com.erank.yogappl.data.room.dao.LessonDao
import com.erank.yogappl.data.room.dao.UserDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DataModelsHolder(db: AppDatabase) {
    private val lessonsDao: LessonDao
    private val eventsDao: EventDao
    private val userDao: UserDao

    init {
        with(db) {
            CoroutineScope(IO).launch { clearAllTables() }
            lessonsDao = lessonsDao()
            eventsDao = eventsDao()
            userDao = usersDao()
        }
    }


    fun getLessons(type: SourceType, uid: String) = when (type) {
        ALL -> lessonsDao.getAllLessons(uid)
        UPLOADS -> lessonsDao.getUploadedLessons(uid)
        SIGNED -> lessonsDao.getSignedLessons(uid)
    }

    fun getEvents(type: SourceType, uid: String) = when (type) {
        ALL -> eventsDao.getAllEvents(uid)
        UPLOADS -> eventsDao.getUploadedEvents(uid)
        SIGNED -> eventsDao.getSignedEvents(uid)
    }

    suspend fun getUser(uid: String) = userDao.getById(uid)

    suspend fun addNewData(data: BaseData) {
        when (data) {
            is Lesson -> lessonsDao.insert(data)
            is Event -> eventsDao.insert(data)
        }
    }


    suspend fun removeData(data: BaseData) {
        when (data) {
            is Lesson -> lessonsDao.delete(data)
            is Event -> eventsDao.delete(data)
        }
    }

    suspend fun updateData(data: BaseData) {
        when (data) {
            is Lesson -> lessonsDao.update(data)
            is Event -> eventsDao.update(data)
        }
    }

    suspend fun getData(type: DataType, id: String) = when (type) {
        DataType.LESSONS -> lessonsDao
        DataType.EVENTS -> eventsDao
    }.getById(id)


    suspend fun addLesson(lesson: Lesson) {
        lessonsDao.insert(lesson)
    }

    suspend fun addLessons(lessons: List<Lesson>) {
        lessonsDao.insertAll(lessons)
    }

    suspend fun addEvent(event: Event) {
        eventsDao.insert(event)
    }

    suspend fun addEvents(events: List<Event>) {
        eventsDao.insertAll(events)
    }

    fun filterEvents(
        type: SourceType,
        uid: String,
        query: String
    ) = when (type) {
        ALL -> eventsDao.allEventsFiltered(uid, query)
        SIGNED -> eventsDao.signedEventsFiltered(uid, query)
        UPLOADS -> eventsDao.uploadedEventsFiltered(uid, query)
    }

    fun filterLessons(
        type: SourceType,
        uid: String,
        query: String
    ) = when (type) {
        ALL -> lessonsDao.allLessonsFiltered(uid, query)
        SIGNED -> lessonsDao.signedLessonsFiltered(uid, query)
        UPLOADS -> lessonsDao.uploadedLessonsFiltered(uid, query)
    }

    suspend fun insertUser(user: User) = userDao.insert(user)

    suspend fun getUsers(ids: Set<String>): Map<String, PreviewUser> =
        userDao.getPreviewUserById(ids).associateBy { it.id }

    suspend fun updateUser(user: User) = userDao.update(user)

}