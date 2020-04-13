package com.erank.yogappl.room.repo

import com.erank.yogappl.models.User
import com.erank.yogappl.room.dao.UserDao

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class UserRepository(private val userDao: UserDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    suspend fun insert(user: User) {
        userDao.insert(user)
    }
}