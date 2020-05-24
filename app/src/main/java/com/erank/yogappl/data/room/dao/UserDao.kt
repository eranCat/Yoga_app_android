package com.erank.yogappl.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.erank.yogappl.data.models.User

@Dao
interface UserDao : GenericDao<User> {

    @Query("SELECT * FROM users WHERE id = :id")
    override suspend fun getById(id: String): User?
}