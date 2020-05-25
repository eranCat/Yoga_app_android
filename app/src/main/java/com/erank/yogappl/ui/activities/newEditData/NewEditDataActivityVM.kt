package com.erank.yogappl.ui.activities.newEditData

import androidx.lifecycle.ViewModel
import com.erank.yogappl.data.enums.DataType
import com.erank.yogappl.data.models.*
import com.erank.yogappl.data.repository.Repository
import com.erank.yogappl.utils.helpers.MyImagePicker
import com.erank.yogappl.utils.interfaces.UploadDataTaskCallback
import java.util.*
import javax.inject.Inject

class NewEditDataActivityVM @Inject constructor(val repository: Repository) : ViewModel() {
    fun getData(
        type: DataType, id: String,
        callback: (BaseData?) -> Unit
    ) = repository.getData(type, id, callback)

    fun uploadData(data: BaseData, callback: UploadDataTaskCallback) {
        repository.uploadData(
            dataInfo.type, data,
            result?.uri, result?.bitmap,
            callback
        )
    }

    fun updateLesson(lesson: Lesson, callback: UploadDataTaskCallback) {
        repository.updateLesson(lesson, callback)
    }

    fun updateEvent(event: Event, callback: UploadDataTaskCallback) {
        repository.updateEvent(
            event,
            result?.uri, result?.bitmap,
            callback
        )
    }

    val currentUser = repository.currentUser
    lateinit var dataInfo: DataInfo
    var data: BaseData? = null
    var selectedLocation: LocationResult? = null

    var result: MyImagePicker.Result? = null
    var selectedStartDate: Date? = null
    var selectedEndDate: Date? = null

    val canRemoveImage: Boolean
        get() = result?.hasImage ?: false

}
