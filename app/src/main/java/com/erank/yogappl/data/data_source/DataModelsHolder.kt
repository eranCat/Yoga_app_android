package com.erank.yogappl.data.data_source

import android.content.Context
import com.erank.yogappl.data.models.BaseData
import com.erank.yogappl.data.models.Event
import com.erank.yogappl.data.models.Lesson
import com.erank.yogappl.data.models.User
import com.erank.yogappl.data.room.AppDatabase
import com.erank.yogappl.data.room.dao.EventDao
import com.erank.yogappl.data.room.dao.LessonDao
import com.erank.yogappl.data.room.dao.UserDao
import com.erank.yogappl.data.enums.DataType
import com.erank.yogappl.data.enums.SourceType
import com.erank.yogappl.data.enums.SourceType.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DataModelsHolder(context: Context) {
    private val lessonsDao: LessonDao
    private val eventsDao: EventDao
    private val userDao: UserDao

    init {
        with(AppDatabase.getDatabase(context)) {
            CoroutineScope(Default).launch { clearAllTables() }
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

    fun getUser(uid: String, callback: (User?) -> Unit) {
        CoroutineScope(Default).launch {
            val user = userDao.getById(uid)
            withContext(Main) { callback(user) }
        }
    }

    fun addNewData(data: BaseData, callback: () -> Unit) {
        CoroutineScope(Default).launch {
            when (data) {
                is Lesson -> lessonsDao.insert(data)
                is Event -> eventsDao.insert(data)
            }
            withContext(Main) { callback() }
        }
    }


    fun removeData(data: BaseData, callback: () -> Unit) {
        CoroutineScope(Default).launch {
            when (data) {
                is Lesson -> lessonsDao.delete(data)
                is Event -> eventsDao.delete(data)
            }
            withContext(Main) { callback() }
        }
    }

    fun addUser(user: User, callback: () -> Unit) {
        CoroutineScope(Default).launch {
            userDao.insert(user)

            withContext(Main) { callback() }
        }
    }

    fun updateData(data: BaseData, callback: () -> Unit) {
        CoroutineScope(Default).launch {
            when (data) {
                is Lesson -> lessonsDao.update(data)
                is Event -> eventsDao.update(data)
            }
            withContext(Main) { callback() }
        }

    }

    fun getData(type: DataType, id: String, callback: (BaseData?) -> Unit) {
        CoroutineScope(Default).launch {
            val data = when (type) {
                DataType.LESSONS -> lessonsDao
                DataType.EVENTS -> eventsDao
            }.getById(id)

            withContext(Main) { callback(data) }
        }
    }

    fun addLessons(lessons: List<Lesson>, callback: () -> Unit) {
        CoroutineScope(Default).launch {
            lessonsDao.insert(lessons)

            withContext(Main) { callback() }
        }
    }

    fun addEvents(events: List<Event>, callback: () -> Unit) {
        CoroutineScope(Default).launch {
            eventsDao.insert(events)

            withContext(Main) { callback() }
        }
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
}