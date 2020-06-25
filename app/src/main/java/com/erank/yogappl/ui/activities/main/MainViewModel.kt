package com.erank.yogappl.ui.activities.main

import android.content.Context
import androidx.lifecycle.ViewModel
import com.erank.yogappl.data.models.User
import com.erank.yogappl.data.models.User.Type
import com.erank.yogappl.data.repository.Repository
import com.erank.yogappl.utils.helpers.AdsManager
import com.erank.yogappl.utils.helpers.AuthHelper
import javax.inject.Inject

class MainViewModel @Inject constructor(
    val repository: Repository,
    val authHelper: AuthHelper,
    val adsManager: AdsManager
) : ViewModel() {

    fun signOut() {
        authHelper.signOut()
        repository.clearCurrentUser()
    }

    fun loadBannerAd(context: Context) = adsManager.loadBannerAd(context)

    val user: User? = repository.currentUser

    val isUserStudent = user!!.type == Type.STUDENT
}