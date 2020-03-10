package com.erank.yogappl.utils.data_source

import androidx.lifecycle.MutableLiveData
import com.erank.yogappl.models.BaseData
import com.erank.yogappl.models.Event
import com.erank.yogappl.models.Lesson
import com.erank.yogappl.models.User
import com.erank.yogappl.utils.enums.DataType
import com.erank.yogappl.utils.enums.DataType.EVENTS
import com.erank.yogappl.utils.enums.DataType.LESSONS
import com.erank.yogappl.utils.enums.SortType
import com.erank.yogappl.utils.enums.SourceType
import com.erank.yogappl.utils.enums.SourceType.*
import com.erank.yogappl.utils.extensions.replace
import com.erank.yogappl.utils.helpers.SortingHelper

class DataModelsHolder {

    var lessons = values().associate {
        it to MutableLiveData<MutableList<Lesson>>()
    }.toMutableMap()

    var events = values().associate {
        it to MutableLiveData<MutableList<Event>>()
    }.toMutableMap()

    private var usersList = mutableMapOf<String, User>()

    fun getLessons(type: SourceType) = lessons[type]!!

    fun getEvents(type: SourceType) = events[type]!!

    fun sort(sortType: SortType, sourceType: SourceType, dataType: DataType) {
        when (dataType) {
            LESSONS -> getLessons(sourceType)
            EVENTS -> getEvents(sourceType)

        }.value?.sortWith(SortingHelper.getSorter(sortType))
    }

    fun getUser(uid: String) = usersList[uid]

    fun addNewData(data: BaseData) {

        when (data) {
            is Lesson ->
                getLessons(UPLOADS).value?.add(0, data)

            is Event ->
                getEvents(UPLOADS).value?.add(0, data)
        }
    }

    fun removeData(data: BaseData) {

        when (data) {
            is Lesson ->
                getLessons(UPLOADS).value?.remove(data)

            is Event ->
                getEvents(UPLOADS).value?.remove(data)

        }
    }

    fun addUser(user: User) {
        usersList[user.id] = user
    }

    fun getLesson(pos: Int) = getLessons(ALL).value?.getOrNull(pos)
    fun getUserLesson(pos: Int) = getLessons(UPLOADS).value?.getOrNull(pos)
    fun getSignedLesson(pos: Int) = getLessons(SIGNED).value?.getOrNull(pos)

    fun getEvent(pos: Int) = getEvents(ALL).value?.getOrNull(pos)
    fun getUserEvent(pos: Int) = getEvents(UPLOADS).value?.getOrNull(pos)
    fun getSignedEvent(pos: Int) = getEvents(SIGNED).value?.getOrNull(pos)

    fun getUploadedData(dType: DataType, pos: Int) =
        when (dType) {
            LESSONS -> getUserLesson(pos)
            EVENTS -> getUserEvent(pos)
        }

    fun getData(dataType: DataType, sourceType: SourceType, pos: Int): BaseData? {
        return when (dataType) {
            LESSONS ->
                getLessons(sourceType).value?.getOrNull(pos)

            EVENTS ->
                getEvents(sourceType).value?.getOrNull(pos)
        }
    }

    private fun <T> updateData(
        liveData: MutableLiveData<MutableList<T>>,
        old: T, new: T
    ) {
        liveData.value?.let {
            it.replace(old, new)
            liveData.postValue(it)
        }
    }

    fun <T : BaseData> updateData(old: T, new: T) {
        when (old) {
            is Lesson -> updateData(getLessons(UPLOADS), old, new as Lesson)
            is Event -> updateData(getEvents(UPLOADS), old, new as Event)
        }
    }

    fun updateLesson(old: Lesson, new: Lesson) = updateData(getLessons(UPLOADS), old, new)
    fun updateEvent(old: Event, new: Event) = updateData(getEvents(UPLOADS), old, new)

    fun <T : BaseData> addToSigned(dbData: T) {
        when (dbData) {
            is Lesson -> addToSigned(dbData)
            is Event -> addToSigned(dbData)
        }
    }

    private fun addToSigned(lesson: Lesson) = addToSigned(getLessons(SIGNED), lesson)

    private fun addToSigned(event: Event) = addToSigned(getEvents(SIGNED), event)

    fun <T : BaseData> removeFromSigned(data: T) {
        when (data) {
            is Lesson -> removeFromSigned(data)
            is Event -> removeFromSigned(data)
        }
    }

    private fun removeFromSigned(lesson: Lesson) = removeFromSigned(getLessons(SIGNED), lesson)

    private fun removeFromSigned(event: Event) = removeFromSigned(getEvents(SIGNED), event)

    private fun <T> addToSigned(liveData: MutableLiveData<MutableList<T>>, item: T) {
        liveData.value?.let {
            it.add(item)
            liveData.postValue(it)
        }
    }

    private fun <T> removeFromSigned(liveData: MutableLiveData<MutableList<T>>, item: T) {
        liveData.value?.let {
            it.remove(item)
            liveData.postValue(it)
        }
    }

}