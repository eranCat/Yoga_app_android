package com.erank.yogappl.ui.activities.splash

import android.content.Context
import androidx.lifecycle.ViewModel
import com.erank.yogappl.data.repository.Repository
import com.erank.yogappl.utils.interfaces.TaskCallback
import com.erank.yogappl.utils.interfaces.UserTaskCallback
import javax.inject.Inject

class SplashViewModel @Inject constructor(val repository: Repository) : ViewModel() {
    fun fetchLoggedUser(callback: UserTaskCallback) =
        repository.fetchLoggedUser(callback)

    fun loadData(context: Context, callback: TaskCallback<Void, Exception>) {
        repository.loadData(context, callback)
    }

}