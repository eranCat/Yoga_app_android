package com.erank.yogappl.ui.activities.main

import androidx.lifecycle.ViewModel
import com.erank.yogappl.data.models.User
import com.erank.yogappl.data.models.User.Type
import com.erank.yogappl.data.repository.Repository
import javax.inject.Inject

class MainViewModel @Inject constructor(val repository: Repository) : ViewModel() {

    val user: User? = repository.currentUser

    val isUserStudent = user?.type == Type.STUDENT
}