package com.erank.yogappl.data.repository

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat.JPEG
import android.net.Uri
import com.erank.yogappl.data.models.Event
import com.erank.yogappl.data.models.User
import com.erank.yogappl.utils.extensions.await
import com.erank.yogappl.utils.helpers.MyImagePicker
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import java.net.URL


class StorageManager() {

    enum class StorageRefs(path: String) {

        USERS_IMAGES("Users/images/profile"),

        EVENTS_IMAGES("Events/images");

        val ref = FirebaseStorage.getInstance().getReference(path)
    }

    private val TAG = StorageManager::class.java.name

    //    TODO use this method user activity
    suspend fun removeUserProfileImage(user: User) {
        //save the image url in current users obj
        userImageRef(user.id).delete().await()
        user.profileImageUrl = null
    }

    suspend fun saveUserImage(uid: String, imageUri: Uri): Uri? =
        userImageRef(uid).let {
            continueSaveUserImageTask(it.putFile(imageUri), it)
        }.await()

    suspend fun saveUserImage(uid: String, bitmap: Bitmap): Uri? =
        with(userImageRef(uid)) {
            val uploadTask = putBytes(bitmapToJPEGBytes(bitmap))
            continueSaveUserImageTask(uploadTask, this).await()
        }

    private fun bitmapToJPEGBytes(bitmap: Bitmap) = ByteArrayOutputStream()
        .also { bitmap.compress(JPEG, 100, it) }
        .toByteArray()

    private fun continueSaveUserImageTask(
        uploadTask: UploadTask,
        userRef: StorageReference
    ): Task<Uri> {
        return uploadTask.continueWithTask { task ->
            val exception = task.exception
            if (!task.isSuccessful && exception != null) {
                throw exception
            }
            userRef.downloadUrl
        }
    }

    private fun userImageRef(uid: String) = StorageRefs.USERS_IMAGES.ref.child(uid)

    private fun eventRef(event: Event) =
        StorageRefs.EVENTS_IMAGES.ref.child(event.id)

    private suspend fun saveEventImage(event: Event, bitmap: Bitmap): Uri? {

        val bytes = ByteArrayOutputStream().apply {
            bitmap.compress(JPEG, 100, this)
        }.toByteArray()

        val eventRef = eventRef(event)
        return continueWithEventTask(eventRef.putBytes(bytes), eventRef).await()
    }

    private suspend fun saveEventImage(event: Event, url: String):Uri? {
        val stream = URL(url).openStream()
        val ref = eventRef(event)
        val putTask = ref.putStream(stream)
        return continueWithEventTask(putTask,ref).await()
    }

    private suspend fun saveEventImage(event: Event, uri: Uri): Uri? {
        val ref = eventRef(event)
        return continueWithEventTask(ref.putFile(uri), ref).await()
    }

    private fun continueWithEventTask(
        task: UploadTask, eventRef: StorageReference
    ): Task<Uri> = task.continueWithTask {
        if (it.isSuccessful.not()) {
            it.exception?.let { e -> throw e }
        }
        eventRef.downloadUrl
    }

    suspend fun removeEventImage(event: Event) {
        if (event.imageUrl != null) {
            eventRef(event).delete().await()
        }
    }

    suspend fun saveEventImage(event: Event, result: MyImagePicker.Result) {
        result.apply {

            val downloadUrl = when {
                uri != null -> saveEventImage(event, uri)

                bitmap != null -> saveEventImage(event, bitmap)

                urls?.small != null -> saveEventImage(event, urls.small)

                else -> return
            }
            downloadUrl?.let {event.imageUrl = it.toString()}
        }
    }
}