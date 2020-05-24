package com.erank.yogappl.ui.activities.register

import androidx.lifecycle.ViewModel
import com.erank.yogappl.data.models.User
import com.erank.yogappl.data.repository.Repository
import com.erank.yogappl.utils.helpers.MyImagePicker
import com.erank.yogappl.utils.interfaces.UserTaskCallback
import javax.inject.Inject

class RegisterViewModel @Inject constructor(val repository: Repository) : ViewModel() {

    var profileImage: MyImagePicker.Result? = null

    val canRemove: Boolean
        get() = currentUser?.profileImageUrl != null
                || profileImage?.urls != null

    fun updateCurrentUser(
        callback: UserTaskCallback
    ) {
        repository.updateCurrentUser(profileImage?.uri, profileImage?.bitmap, callback)
    }

    fun createUser(user: User, pass: String, callback: UserTaskCallback) {
        repository.createUser(
            user, pass,
            profileImage?.uri, profileImage?.bitmap,
            callback
        )
    }

    val currentUser = repository.currentUser
}