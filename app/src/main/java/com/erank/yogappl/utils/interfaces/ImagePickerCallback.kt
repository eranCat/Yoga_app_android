package com.erank.yogappl.utils.interfaces

import com.erank.yogappl.utils.helpers.MyImagePicker

interface ImagePickerCallback {
    fun onImageRemove()
    fun onSelectedImage(result: MyImagePicker.Result)
}