package com.erank.yogappl.ui.activities.dataInfo

import androidx.lifecycle.ViewModel
import com.erank.yogappl.data.enums.DataType
import com.erank.yogappl.data.models.BaseData
import com.erank.yogappl.data.models.Event
import com.erank.yogappl.data.models.Lesson
import com.erank.yogappl.data.models.User
import com.erank.yogappl.data.repository.Repository
import com.erank.yogappl.utils.interfaces.TaskCallback
import javax.inject.Inject

class DataInfoViewModel @Inject constructor(val repository: Repository) : ViewModel() {
    val currentUser: User? = repository.currentUser

    fun getData(dataType: DataType, id: String, callback: (BaseData?) -> Unit) {
        repository.getData(dataType, id, callback)
    }

    fun getUser(uid: String, callback: (User?) -> Unit) {
        repository.getUser(uid, callback)
    }

    fun toggleSignToLesson(
        lesson: Lesson, callback: TaskCallback<Boolean, Exception>
    ) = repository.toggleSignToLesson(lesson, callback)

    fun toggleSignToEvent(
        event: Event, callback: TaskCallback<Boolean, Exception>
    ) = repository.toggleSignToEvent(event, callback)

}