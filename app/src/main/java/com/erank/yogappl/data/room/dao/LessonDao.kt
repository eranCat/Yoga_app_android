package com.erank.yogappl.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import com.erank.yogappl.data.models.Lesson

@Dao
interface LessonDao : GenericDao<Lesson> {

    @Query("SELECT * FROM LESSONS WHERE uid != :uid ORDER BY startDate")
    fun getAllLessons(uid: String): LiveData<List<Lesson>>

    @Query("SELECT * FROM LESSONS WHERE uid = :uid ORDER BY startDate")
    fun getUploadedLessons(uid: String): LiveData<List<Lesson>>

    //signed contains uid
    @Query("SELECT * FROM LESSONS WHERE :uid IN (signed) ORDER BY startDate")
    fun getSignedLessons(uid: String): LiveData<List<Lesson>>

    @Query("SELECT * FROM LESSONS where id = :id LIMIT 1")
    override suspend fun getById(id: String): Lesson?

    @Query("SELECT * FROM LESSONS WHERE uid != :uid AND :query IN(title)")
    suspend fun allLessonsFiltered(uid: String, query: String): List<Lesson>

    @Query("SELECT * FROM LESSONS WHERE  :uid IN (signed) AND :query IN(title)")
    suspend fun signedLessonsFiltered(uid: String, query: String): List<Lesson>

    @Query("SELECT * FROM LESSONS WHERE uid = :uid AND :query IN(title)")
    suspend fun uploadedLessonsFiltered(uid: String, query: String): List<Lesson>
}