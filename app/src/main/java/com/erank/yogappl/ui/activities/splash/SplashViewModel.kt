package com.erank.yogappl.ui.activities.splash

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import com.erank.yogappl.data.repository.Repository
import com.erank.yogappl.utils.helpers.AuthHelper
import com.erank.yogappl.utils.helpers.LocationHelper
import com.erank.yogappl.utils.helpers.MoneyConverter
import com.erank.yogappl.utils.interfaces.MoneyConnectionCallback
import com.erank.yogappl.utils.interfaces.TaskCallback
import com.erank.yogappl.utils.interfaces.UserTaskCallback
import javax.inject.Inject

class SplashViewModel @Inject constructor(
    val repository: Repository,
    val authHelper: AuthHelper,
    val locationHelper: LocationHelper,
    val moneyConverter: MoneyConverter
) : ViewModel() {
    val isFbUserConnected: Boolean
        get() = authHelper.isFbUserConnected

    fun fetchLoggedUser(callback: UserTaskCallback) =
        repository.fetchLoggedUser(callback)

    fun loadData(context: Context, callback: TaskCallback<Void, Exception>) {
        repository.loadData(context, callback)
    }

    fun getLocationPermissionIfNeeded(activity: Activity): Boolean {
        return locationHelper.getLocationPermissionIfNeeded(activity)
    }

    fun initLocationService() {
        locationHelper.initLocationService()
    }

    fun connectMoneyConverter(callback: MoneyConnectionCallback) {
        moneyConverter.connect(callback)
    }

    fun checkAllPermissionResults(
        requestCode: Int, permissions: Array<String>, results: IntArray
    ): Boolean {
        return locationHelper.checkAllPermissionResults( requestCode,permissions,results)
    }

}