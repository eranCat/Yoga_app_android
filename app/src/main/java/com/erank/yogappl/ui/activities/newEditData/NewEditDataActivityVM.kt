package com.erank.yogappl.ui.activities.newEditData

import androidx.lifecycle.ViewModel
import com.erank.yogappl.data.enums.DataType
import com.erank.yogappl.data.models.*
import com.erank.yogappl.data.repository.Repository
import com.erank.yogappl.utils.helpers.MyImagePicker
import java.util.*
import javax.inject.Inject

class NewEditDataActivityVM @Inject constructor(val repository: Repository) : ViewModel() {

    suspend fun getData(type: DataType, id: String) = repository.getData(type, id)

    suspend fun uploadData(data: BaseData) {
        repository.uploadData(
            dataInfo.type, data,
            result?.uri, result?.bitmap
        )
    }

    suspend fun updateLesson(lesson: Lesson) {
        repository.updateLesson(lesson)
    }

    suspend fun updateEvent(event: Event) {
        repository.updateEvent(event, result?.uri, result?.bitmap)
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
