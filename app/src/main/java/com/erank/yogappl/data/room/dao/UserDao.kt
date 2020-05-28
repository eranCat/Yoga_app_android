package com.erank.yogappl.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.erank.yogappl.data.models.PreviewUser
import com.erank.yogappl.data.models.User

@Dao
interface UserDao : GenericDao<User> {

    @Query("SELECT * FROM users WHERE id = :id")
    override suspend fun getById(id: String): User?

    @Query("SELECT id,name,profileImageUrl FROM users WHERE id IN(:ids)")
    suspend fun getPreviewUserById(ids: Set<String>): List<PreviewUser>
}