package com.erank.yogappl.data.repository

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat.JPEG
import android.net.Uri
import android.util.Log
import com.erank.yogappl.data.models.Event
import com.erank.yogappl.data.models.User
import com.erank.yogappl.utils.interfaces.TaskCallback
import com.erank.yogappl.utils.interfaces.UserTaskCallback
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream


class StorageManager() {

    enum class StorageRefs(path: String) {

        USERS_IMAGES("Users/images/profile"),

        EVENTS_IMAGES("Events/images");

        val ref = FirebaseStorage.getInstance().getReference(path)
    }

    private val TAG = StorageManager::class.java.name

    fun removeCurrentUserProfileImage(user:User,completion: TaskCallback<Void, Exception>) {
        // remove the file from storage
        userImageRef(user).delete()
            .addOnSuccessListener { removeUserProfileImageFromDB(user,completion) }
            .addOnFailureListener(completion::onFailure)
    }

    private fun removeUserProfileImageFromDB(user:User,completion: TaskCallback<Void, Exception>) {
        //save the image url in current users obj
        user.profileImageUrl = null
        //update in DB

        userImageRef(user).delete()
            .addOnFailureListener(completion::onFailure)
    }

    fun saveUserImage(user: User, imageUri: Uri, callback: UserTaskCallback): Task<Uri> =
        userImageRef(user).let {
            continueSaveUserImageTask(
                it.putFile(imageUri), it,
                user, callback
            )
        }

    fun saveUserImage(user: User, bitmap: Bitmap, callback: UserTaskCallback) =
        with(userImageRef(user)) {
            val uploadTask = putBytes(bitmapToJPEGBytes(bitmap))
            continueSaveUserImageTask(uploadTask, this, user, callback)
        }

    private fun bitmapToJPEGBytes(bitmap: Bitmap) = ByteArrayOutputStream()
        .also { bitmap.compress(JPEG, 100, it) }
        .toByteArray()

    private fun continueSaveUserImageTask(
        uploadTask: UploadTask,
        userRef: StorageReference,
        user: User, listener: UserTaskCallback
    ): Task<Uri> {
        return uploadTask.continueWithTask { task ->
            val exception = task.exception
            if (!task.isSuccessful && exception != null) {
                throw exception
            }
            userRef.downloadUrl
        }.addOnFailureListener(listener::onFailedFetchingUser)
            .addOnSuccessListener {
                user.profileImageUrl = it.toString()
            }
    }

    private fun userImageRef(user: User) = StorageRefs.USERS_IMAGES.ref.child(user.id)

    private fun eventRef(event: Event) =
        StorageRefs.EVENTS_IMAGES.ref.child(event.id)

    fun saveEventImage(event: Event, bitmap: Bitmap): Task<Uri> {

        val bytes = ByteArrayOutputStream().apply {
            bitmap.compress(JPEG, 100, this)
        }.toByteArray()

        val eventRef = eventRef(event)
        return continueWithEventTask(eventRef.putBytes(bytes), eventRef)
    }

    fun saveEventImage(event: Event, uri: Uri): Task<Uri> {
        return eventRef(event).let{
            continueWithEventTask(it.putFile(uri), it)
        }
    }

    private fun continueWithEventTask(
        task: UploadTask, eventRef: StorageReference
    ): Task<Uri> = task.continueWithTask {
        if (it.isSuccessful.not()) {
            it.exception?.let { e -> throw e }
        }
        eventRef.downloadUrl
    }

    fun removeEventImage(event: Event): Task<Void>? {
        return event.imageUrl?.let {
            eventRef(event).delete()
                .addOnSuccessListener {
                    Log.d(TAG, "event ${event.id} image removed")
                }
                .addOnFailureListener {
                    Log.d(TAG, "event ${event.id} failed to remove", it)
                }
        }
    }
}