package com.erank.yogappl.utils.helpers

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.erank.yogappl.models.Event
import com.erank.yogappl.models.User
import com.erank.yogappl.utils.UserErrors
import com.erank.yogappl.utils.data_source.DataSource
import com.erank.yogappl.utils.interfaces.TaskCallback
import com.erank.yogappl.utils.interfaces.UserTaskCallback
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream


object StorageManager {

    enum class StorageRefs(path: String) {

        USERS_IMAGES("Users/images/profile"),

        EVENTS_IMAGES("Events/images");

        val ref = FirebaseStorage.getInstance().getReference(path)
    }

    private val TAG = StorageManager::class.java.name

    fun removeCurrentUserProfileImage(completion: TaskCallback<Void,Exception>) {
        val user = DataSource.currentUser
            ?: run {
                completion.onFailure(UserErrors.NoUserFound())
                return
            }


        // remove the file from storage
        userImageRef(user).delete()
            .addOnFailureListener(completion::onFailure)
            .addOnSuccessListener { removeUserProfileImageFromDB(completion) }
    }

    private fun removeUserProfileImageFromDB(completion: TaskCallback<Void,Exception>) {
        DataSource.currentUser?.let {
            //save the image url in current users obj
            it.profileImageUrl = null
            //update in DB

            userImageRef(it).delete()
                .addOnFailureListener(completion::onFailure)

        } ?: run {
            completion.onFailure(UserErrors.NoUserFound())
            return
        }
    }

    fun saveUserImage(
        user: User, imageUri: Uri,
        listener: UserTaskCallback
    ): Task<Uri> {
        val userRef = userImageRef(user)
        val task = userRef.putFile(imageUri)
        return continueSaveUserImageTask(task, userRef, user, listener)
    }

    fun saveUserImage(user: User, bitmap: Bitmap, listener: UserTaskCallback): Task<Uri> {

        val userRef = userImageRef(user)

        val bytes = ByteArrayOutputStream().apply {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, this)
        }.toByteArray()

        val task = userRef.putBytes(bytes)
        return continueSaveUserImageTask(task, userRef, user, listener)
    }

    private fun continueSaveUserImageTask(
        uploadTask: UploadTask, userRef: StorageReference,
        user: User, listener: UserTaskCallback
    ): Task<Uri> {
        return uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let { throw it }
            }
            userRef.downloadUrl

        }.addOnSuccessListener {
            user.profileImageUrl = it.toString()
            DataSource.uploadUserToDB(user, listener)

        }.addOnFailureListener(listener::onFailedFetchingUser)
    }

    private fun userImageRef(user: User) = StorageRefs.USERS_IMAGES.ref.child(user.id)

    private fun eventRef(event: Event) =
        StorageRefs.EVENTS_IMAGES.ref.child(event.id)

    fun saveEventImage(event: Event, bitmap: Bitmap): Task<Uri> {

        val bytes = ByteArrayOutputStream().apply {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, this)
        }.toByteArray()

        val eventRef = eventRef(event)
        return continueWithEventTask(eventRef.putBytes(bytes), event, eventRef)
    }

    fun saveEventImage(event: Event, uri: Uri): Task<Uri> {
        val eventRef = eventRef(event)
        return continueWithEventTask(eventRef.putFile(uri), event, eventRef)
    }

    private fun continueWithEventTask(
        task: UploadTask, event: Event,
        eventRef: StorageReference
    ): Task<Uri> {
        return task.continueWithTask {
            if (it.isSuccessful.not()) {
                it.exception?.let { e -> throw e }
            }
            eventRef.downloadUrl
        }.addOnSuccessListener {
            event.imageUrl = it.toString()
        }
    }

    fun removeEventImage(event: Event): Task<Void>? {

        event.imageUrl ?: return null

        return eventRef(event).delete()
            .addOnSuccessListener {
                Log.d(TAG, "event ${event.id} image removed")
            }
            .addOnFailureListener {
                Log.d(TAG, "event ${event.id} failed to remove", it)
            }
    }
}