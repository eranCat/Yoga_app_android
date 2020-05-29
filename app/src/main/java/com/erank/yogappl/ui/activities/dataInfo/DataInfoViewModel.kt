package com.erank.yogappl.ui.activities.dataInfo

import android.content.Intent
import androidx.lifecycle.ViewModel
import com.erank.yogappl.data.enums.DataType
import com.erank.yogappl.data.models.Event
import com.erank.yogappl.data.models.Lesson
import com.erank.yogappl.data.models.User
import com.erank.yogappl.data.repository.Repository
import com.erank.yogappl.utils.helpers.LocationHelper
import com.google.android.gms.maps.model.LatLng
import javax.inject.Inject

class DataInfoViewModel @Inject constructor(
    val repository: Repository, val locationHelper: LocationHelper
) : ViewModel() {
    val currentUser: User? = repository.currentUser

    suspend fun getData(dataType: DataType, id: String) =
        repository.getData(dataType, id)

    suspend fun getUser(uid: String) = repository.getUser(uid)

    suspend fun toggleSignToLesson(lesson: Lesson) = repository.toggleSignToLesson(lesson)

    suspend fun toggleSignToEvent(event: Event) = repository.toggleSignToEvent(event)

    fun getLocationIntent(location: LatLng): Intent? {
        return locationHelper.getLocationIntent(location)
    }
}