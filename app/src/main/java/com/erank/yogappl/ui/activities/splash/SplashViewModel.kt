package com.erank.yogappl.ui.activities.splash

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.erank.yogappl.data.repository.Repository
import com.erank.yogappl.utils.helpers.AuthHelper
import com.erank.yogappl.utils.helpers.LocationHelper
import com.erank.yogappl.utils.helpers.MoneyConverter
import javax.inject.Inject

class SplashViewModel @Inject constructor(
    val repository: Repository,
    val authHelper: AuthHelper,
    val locationHelper: LocationHelper,
    val moneyConverter: MoneyConverter
) : ViewModel() {
    val isFbUserConnected: Boolean
        get() = authHelper.isFbUserConnected

    suspend fun fetchLoggedUser() = repository.fetchLoggedUser()
    suspend fun loadData() = repository.loadData()

    fun getLocationPermissionIfNeeded(activity: Activity): Boolean {
        return locationHelper.getLocationPermissionIfNeeded(activity)
    }

    suspend fun connectMoneyConverter() {
        moneyConverter.connect()
    }

    fun checkAllPermissionResults(
        requestCode: Int, permissions: Array<String>, results: IntArray
    ): Boolean {
        return locationHelper.checkAllPermissionResults(requestCode, permissions, results)
    }

}