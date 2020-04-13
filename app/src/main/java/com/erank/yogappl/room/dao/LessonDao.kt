package com.erank.yogappl.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.erank.yogappl.models.Lesson

@Dao
interface LessonDao : GenericDao<Lesson> {

    @Query("SELECT * FROM LESSONS WHERE uid != :uid ORDER BY startDate")
    fun getAllLessons(uid: String): LiveData<List<Lesson>>

    @Query("SELECT * FROM LESSONS WHERE uid = :uid ORDER BY startDate")
    fun getUploadedLessons(uid: String): LiveData<List<Lesson>>

    //signed contains uid
    @Query(
        """SELECT * FROM LESSONS
        WHERE signed LIKE '%'+:uid+'%'
         ORDER BY startDate"""
    )
    fun getSignedLessons(uid: String): LiveData<List<Lesson>>

    @Query("SELECT * FROM LESSONS where id = :id LIMIT 1")
    override suspend fun getById(id: String): Lesson?


    //    TODO fix search queries
    @Query(
        """SELECT * FROM LESSONS 
        WHERE (uid != :uid) AND 
        (title LIKE '%'+:query+'%')"""
    )
    fun allLessonsFiltered(uid: String, query: String): LiveData<List<Lesson>>

    @Query(
        """SELECT * FROM LESSONS
            WHERE signed LIKE '%'+:uid+'%' 
            AND title LIKE '%'+:query+'%' """
    )
    fun signedLessonsFiltered(uid: String, query: String): LiveData<List<Lesson>>

    @Query("SELECT * FROM LESSONS WHERE uid = :uid AND title LIKE '%'+:query+'%'")
    fun uploadedLessonsFiltered(uid: String, query: String): LiveData<List<Lesson>>

}