package com.erank.yogappl.data.room.dao

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE

@Dao
interface GenericDao<T> {

    @Insert(onConflict = REPLACE)
    suspend fun insert(data: T)

    @Insert(onConflict = REPLACE)
    suspend fun insertAll(data: List<T>)

    @Update(onConflict = REPLACE)
    suspend fun update(vararg data: T)

    @Delete
    suspend fun delete(vararg data: T)

    //    @Query("SELECT * FROM <> where id = :id")
    suspend fun getById(id: String): T?
}