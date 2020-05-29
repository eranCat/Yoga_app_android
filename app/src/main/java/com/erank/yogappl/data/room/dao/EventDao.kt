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
    @Query("SELECT * FROM events where :uid IN (signed) ORDER BY startDate")
    fun getSignedEvents(uid: String): LiveData<List<Event>>

    @Query("SELECT * FROM events where id = :id LIMIT 1")
    override suspend fun getById(id: String): Event?


    @Query("SELECT * FROM EVENTS WHERE uid != :uid AND :query IN(title)")
    suspend fun allEventsFiltered(uid: String, query: String): List<Event>

    @Query("SELECT * FROM EVENTS WHERE uid = :uid AND :query IN(title)")
    suspend fun uploadedEventsFiltered(uid: String, query: String): List<Event>

    @Query("SELECT * FROM EVENTS WHERE :uid IN (signed) AND :query IN(title)")
    suspend fun signedEventsFiltered(uid: String, query: String): List<Event>
}