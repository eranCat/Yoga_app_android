package com.erank.yogappl.ui.activities.register

import androidx.lifecycle.ViewModel
import com.erank.yogappl.data.models.User
import com.erank.yogappl.data.repository.Repository
import com.erank.yogappl.utils.helpers.MyImagePicker
import javax.inject.Inject

class RegisterViewModel @Inject constructor(val repository: Repository) : ViewModel() {

    var profileImage: MyImagePicker.Result? = null

    val canRemove: Boolean
        get() = currentUser?.profileImageUrl != null
                || profileImage?.urls != null

    suspend fun updateCurrentUser() {
        repository.updateCurrentUser(profileImage?.uri, profileImage?.bitmap)
    }

    suspend fun createUser(user: User, pass: String) =
        repository.createUser(
            user, pass,
            profileImage?.uri, profileImage?.bitmap
        )


    val currentUser = repository.currentUser
}