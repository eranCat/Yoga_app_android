package com.erank.yogappl.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Update

@Dao
interface GenericDao<T> {

    @Insert(onConflict = REPLACE)
    suspend fun insert(data: T)

    @Insert
    suspend fun insertAll(data: List<T>)

    @Update
    suspend fun update(vararg data: T)

    @Delete
    suspend fun delete(vararg data: T)

    //    @Query("SELECT * FROM <> where id = :id")
    suspend fun getById(id: String): T?
}