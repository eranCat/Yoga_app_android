package com.erank.yogappl.utils.interfaces

import com.erank.yogappl.models.User

interface UserTaskCallback {
    fun onSuccessFetchingUser(user: User?)
    fun onFailedFetchingUser(e: Exception)
}