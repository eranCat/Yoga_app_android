package com.erank.yogappl.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.erank.yogappl.data.models.Event

@Dao
interface EventDao : GenericDao<Event> {

    @Query("SELECT * FROM events where uid!=:uid ORDER BY startDate")
    fun getAllEvents(uid: String): LiveData<List<Event>>

    @Query("SELECT * FROM events where uid=:uid ORDER BY startDate")
    fun getUploadedEvents(uid: String): LiveData<List<Event>>

    //signed contains uid
    @Query("SELECT * FROM events where :uid LIKE '%'+signed+'%' ORDER BY startDate")
    fun getSignedEvents(uid: String): LiveData<List<Event>>

    @Query("SELECT * FROM events where id = :id LIMIT 1")
    override suspend fun getById(id: String): Event?


    @Query("SELECT * FROM EVENTS WHERE uid != :uid AND title LIKE '%'|:query|'%'")
    fun allEventsFiltered(uid: String, query: String): List<Event>

    @Query("SELECT * FROM EVENTS WHERE uid = :uid AND title LIKE '%'|:query|'%'")
    fun uploadedEventsFiltered(uid: String, query: String): List<Event>

    @Query("SELECT * FROM EVENTS WHERE signed LIKE '%'|:uid|'%' AND title LIKE '%'|:query|'%'")
    fun signedEventsFiltered(uid: String, query: String): List<Event>
}