package com.erank.yogappl.ui.activities.main

import androidx.lifecycle.ViewModel
import com.erank.yogappl.data.models.User
import com.erank.yogappl.data.models.User.Type
import com.erank.yogappl.data.repository.Repository
import com.erank.yogappl.utils.helpers.AuthHelper
import javax.inject.Inject

class MainViewModel @Inject constructor(
    val repository: Repository, val authHelper: AuthHelper
) : ViewModel() {

    fun signOut() {
        authHelper.signOut()
        repository.clearCurrentUser()
    }

    val user: User? = repository.currentUser

    val isUserStudent = user!!.type == Type.STUDENT
}