package com.erank.yogappl.utils.helpers

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.erank.yogappl.R
import com.erank.yogappl.ui.activities.register.RegisterActivity
import com.erank.yogappl.utils.extensions.alert
import com.erank.yogappl.utils.extensions.toast
import com.erank.yogappl.utils.interfaces.ImagePickerCallback
import com.theartofdev.edmodo.cropper.CropImage
import com.unsplash.pickerandroid.photopicker.data.UnsplashPhoto
import com.unsplash.pickerandroid.photopicker.data.UnsplashUrls
import com.unsplash.pickerandroid.photopicker.presentation.UnsplashPickerActivity
import com.unsplash.pickerandroid.photopicker.presentation.UnsplashPickerActivity.Companion.EXTRA_PHOTOS


class MyImagePicker(val callback: ImagePickerCallback) {

    companion object {
        const val RC_UNSPLASH = 349
        const val RC_GALLERY = 456
        const val RC_CAMERA = 130

        const val CAM_PERMISSION_REQUEST = 12
    }

    fun show(activity: Activity, canRemove: Boolean) {

        val arr = if (!canRemove) R.array.photoOptions
        else R.array.photoOptionsRemovable

        val items = activity.resources.getStringArray(arr)

        val actions = arrayOf(
            { pickFromGallery(activity) },
            { pickFromUnsplash(activity) },
            { takePhoto(activity) },
            { callback.onImageRemove() }
        )

        activity.alert(R.string.select_image)
            .setItems(items) { _, i ->
                actions[i]()
            }.setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun takePhoto(activity: Activity) {
        if (checkCameraHardware(activity).not()) {
            activity.alert(R.string.no_camera)
                .setPositiveButton(android.R.string.ok, null)
                .show()
            return
        }

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.CAMERA), CAM_PERMISSION_REQUEST
            )

            return
        }


        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        takePictureIntent.resolveActivity(activity.packageManager)?.also {
            activity.startActivityForResult(takePictureIntent, RC_CAMERA)
        }
    }

    fun checkActivityResult(activity: Activity, requestCode: Int, resultCode: Int, data: Intent?) {
        data ?: return
        when (requestCode) {
            RC_GALLERY -> if (resultCode == RESULT_OK) {
                //data.getData returns the content URI for the selected Image
                CropImage.activity(data.data).start(activity)
            }


            RC_CAMERA -> if (resultCode == RESULT_OK) {
                val extras = data.extras ?: return
                val imageBitmap = extras.get("data") as Bitmap
                callback.onSelectedImage(Result(bitmap = imageBitmap))
            }

            RC_UNSPLASH -> if (resultCode == RESULT_OK) {
                val photos = data
                    .getParcelableArrayListExtra<UnsplashPhoto>(EXTRA_PHOTOS)
                    .first()

                callback.onSelectedImage(Result(photos.urls))
            }

            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)

                if (resultCode == RESULT_OK) {
                    callback.onSelectedImage(Result(uri = result.uri))
                    return
                }
                if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
                    result.error.let {
                        val message = activity.getString(R.string.problem_crop, it.localizedMessage)
                        activity.toast(message)

                        Log.d(
                            RegisterActivity::class.java.name,
                            "couldn't crop properly", it
                        )
                    }
            }
        }
    }


    /** Check if this device has a camera  */
    private fun checkCameraHardware(context: Context) =
        context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)

    private fun pickFromGallery(activity: Activity) {
        //Create an Intent with action as ACTION_PICK
        val intent = Intent(Intent.ACTION_PICK)
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.type = "image/*"
        //We pass an extra array with the accepted mime types.
        // This will ensure only components with these MIME types as targeted.
        val mimeTypes = arrayOf("image/jpeg", "image/png")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        // Launching the Intent
        activity.startActivityForResult(intent, RC_GALLERY)
    }

    private fun pickFromUnsplash(activity: Activity) {
        val intent = UnsplashPickerActivity
            .getStartingIntent(activity, false)

        activity.startActivityForResult(intent, RC_UNSPLASH)
    }

    fun openCamIfPossible(activity: Activity, requestCode: Int, grantResults: IntArray) {

        if (requestCode == CAM_PERMISSION_REQUEST) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhoto(activity)
            } else activity.toast(R.string.no_camera_permission)
        }
    }

    class Result(
        val urls: UnsplashUrls? = null,
        val bitmap: Bitmap? = null,
        val uri: Uri? = null
    ) {
        val hasImage: Boolean
            get() = urls != null || uri != null || bitmap != null
    }
}