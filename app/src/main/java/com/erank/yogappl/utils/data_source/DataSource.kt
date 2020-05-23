package com.erank.yogappl.utils.data_source

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.erank.yogappl.models.*
import com.erank.yogappl.models.User.Type
import com.erank.yogappl.utils.DataTypeError
import com.erank.yogappl.utils.SigningErrors
import com.erank.yogappl.utils.UserErrors
import com.erank.yogappl.utils.enums.DataType
import com.erank.yogappl.utils.enums.DataType.EVENTS
import com.erank.yogappl.utils.enums.DataType.LESSONS
import com.erank.yogappl.utils.enums.SourceType
import com.erank.yogappl.utils.enums.Status
import com.erank.yogappl.utils.enums.TableNames
import com.erank.yogappl.utils.enums.TableNames.USERS
import com.erank.yogappl.utils.extensions.lowercaseName
import com.erank.yogappl.utils.helpers.AuthHelper
import com.erank.yogappl.utils.helpers.LocationHelper
import com.erank.yogappl.utils.helpers.StorageManager
import com.erank.yogappl.utils.helpers.StorageManager.saveUserImage
import com.erank.yogappl.utils.interfaces.TaskCallback
import com.erank.yogappl.utils.interfaces.UploadDataTaskCallback
import com.erank.yogappl.utils.interfaces.UserTaskCallback
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.JsonParseException


object DataSource {

    private val TAG = DataSource::class.java.name
    private val ds = Firebase.firestore

    var currentUser: User? = null

    private lateinit var dataModelHolder: DataModelsHolder

    private const val MaxPerBatch = 100L

    enum class DBRefs(name: String) {
        LESSONS_REF(TableNames.LESSONS.lowercaseName),
        EVENTS_REF(TableNames.EVENTS.lowercaseName),
        USERS_REF(USERS.lowercaseName);

        val ref: CollectionReference = ds.collection(name)

        companion object {
            fun refForType(dType: DataType) = values()[dType.ordinal].ref
        }
    }

    fun initRoom(context: Context) {
        dataModelHolder = DataModelsHolder(context)
    }

    fun getLessons(type: SourceType) =
        dataModelHolder.getLessons(type, currentUser!!.id)

    fun getEvents(type: SourceType) =
        dataModelHolder.getEvents(type, currentUser!!.id)

    fun loadData(
        context: Context,
        onLoadedCallback: TaskCallback<Void, Exception>
    ) {

        loadAll(context, LESSONS, object : TaskCallback<Void, Exception> {

            override fun onSuccess(result: Void?) =
                loadAll(context, EVENTS, onLoadedCallback)

            override fun onFailure(error: Exception) =
                onLoadedCallback.onFailure(error)

        })
    }

    private fun loadAll(
        context: Context,
        dType: DataType,
        loaded: TaskCallback<Void, Exception>
    ) {
//        TODO use real location locale, not setting one
        LocationHelper.getCountryCode(context) { code, _ ->
            //        MARK: Important! don't use 2 ordered or limited queries
            val handler = LoadDataValueEventHandler(loaded, dType)
            DBRefs.refForType(dType)
                .whereEqualTo("countryCode", code)
                .orderBy("postedDate")
                .limitToLast(MaxPerBatch)
                .addSnapshotListener(handler)
        }
    }

    fun getUser(uid: String, callback: (User?) -> Unit) =
        dataModelHolder.getUser(uid, callback)

    fun fetchLoggedUser(callback: UserTaskCallback) {

        val uid = AuthHelper.currentUser?.uid
        if (uid == null) {
            callback.onFailedFetchingUser(UserErrors.NoUserFound())
            return
        }

        //kind of caching
        currentUser?.let {
            callback.onSuccessFetchingUser(it)
            return
        }

        fetchUserIfNeeded(uid, object : UserTaskCallback {
            override fun onSuccessFetchingUser(user: User?) {
                currentUser = user
                callback.onSuccessFetchingUser(user)
            }

            override fun onFailedFetchingUser(e: Exception) = callback.onFailedFetchingUser(e)
        })
    }

    internal fun fetchUserIfNeeded(uid: String, callback: UserTaskCallback) {

        userRef(uid).get()
            .addOnFailureListener(callback::onFailedFetchingUser)
            .addOnSuccessListener { snapshot ->
                convertUser(snapshot)?.let {
                    callback.onSuccessFetchingUser(it)
                } ?: callback.onFailedFetchingUser(JsonParseException("user casting failed"))
            }
    }

    private fun convertUser(snapshot: DocumentSnapshot): User? {
        val userTypeIndex = snapshot.getString("type")
            ?: return null

        val type = when (Type.valueOf(userTypeIndex)) {
            Type.STUDENT -> User::class.java
            Type.TEACHER -> Teacher::class.java
        }

        return snapshot.toObject(type)
    }

    private fun addUser(user: User, callback: () -> Unit) {
        dataModelHolder.addUser(user, callback)
    }

    fun createUser(
        user: User, pass: String,
        selectedImage: Uri?, bitmap: Bitmap?,
        callback: UserTaskCallback
    ) {

        //        stage 1 - add new simple user to the firebase Auth
        AuthHelper.createUser(user.email, pass)
            .addOnFailureListener(callback::onFailedFetchingUser)
            .addOnSuccessListener { authResult ->
                //       set id to user
                user.id = authResult.user!!.uid

                selectedImage?.let { saveUserImage(user, it, callback) }
                    ?: bitmap?.let { saveUserImage(user, it, callback) }
                    ?: uploadUserToDB(user, callback)
            }
    }

    fun updateCurrentUser(
        selectedImage: Uri?,
        selectedImageBitmap: Bitmap?,
        callback: UserTaskCallback
    ) {
        val user = currentUser
        if (user == null) {
            callback.onFailedFetchingUser(NullPointerException())
            return
        }

        when {
            selectedImage != null ->
                saveUserImage(user, selectedImage, callback)
                    .addOnSuccessListener { updateUser(user, callback) }

            selectedImageBitmap != null ->
                saveUserImage(user, selectedImageBitmap, callback)
                    .addOnSuccessListener { updateUser(user, callback) }

            else -> updateUser(user, callback)
        }
    }

    private fun updateUser(user: User, callback: UserTaskCallback) {
        userRef(user).set(user)
            .addOnSuccessListener { callback.onSuccessFetchingUser(user) }
            .addOnFailureListener { callback.onFailedFetchingUser(it) }
    }


    fun uploadUserToDB(user: User, listener: UserTaskCallback) {
        //    stage  - add all user data to DB - Firebase database

        userRef(user).set(user)
            .addOnFailureListener(listener::onFailedFetchingUser)
            .addOnSuccessListener {
                currentUser = user
                listener.onSuccessFetchingUser(user)
            }
    }

    private fun userRef(uid: String) = DBRefs.USERS_REF.ref.document(uid)


    private fun userRef(user: User) = userRef(user.id)

    fun uploadData(
        dType: DataType,
        data: BaseData,
        selectedImage: Uri?,
        selectedBitmap: Bitmap?,
        callback: UploadDataTaskCallback
    ) {

        val ref = DBRefs.refForType(dType).document()

        data.id = ref.id

        ref.set(data).addOnFailureListener(callback::onFailure)
            .addOnSuccessListener {

                dataModelHolder.addNewData(data) {
                    Log.d(TAG, "added in room")
                }

                if (dType == LESSONS) {
                    saveUserLesson(data.id, callback)
                    return@addOnSuccessListener
                }

                val event = data as Event

                when {
                    selectedImage != null -> {
                        StorageManager.saveEventImage(event, selectedImage)
                            .addOnFailureListener(callback::onFailure)
                            .addOnSuccessListener {
                                saveUserEvent(data.id, callback)
                            }
                    }
                    selectedBitmap != null -> {
                        StorageManager.saveEventImage(event, selectedBitmap)
                            .addOnFailureListener(callback::onFailure)
                            .addOnSuccessListener {
                                saveUserEvent(data.id, callback)
                            }
                    }
                    else -> saveUserEvent(data.id, callback)
                }

            }
    }

    private fun saveUserEvent(eventId: String, callback: UploadDataTaskCallback) {
        val user = currentUser ?: run {
            callback.onFailure(NullPointerException("no user"))
            return
        }

        val event = eventId to Status.OPEN.ordinal
        userRef(user).update("createdEventsIds", event)
            .addOnFailureListener(callback::onFailure)
            .addOnSuccessListener {
                user.addEvent(eventId)
                callback.onSuccess()
            }
    }


    private fun saveUserLesson(id: String, callback: UploadDataTaskCallback) {
        val teacher = currentUser as? Teacher
        if (teacher == null) {
            callback.onFailure(NullPointerException("no user"))
            return
        }
        val lesson = id to Status.OPEN.ordinal
        userRef(teacher).update("teachingClassesIDs", lesson)
            .addOnFailureListener(callback::onFailure)
            .addOnSuccessListener {
                teacher.addLesson(id)
                callback.onSuccess()
            }
    }

    private fun lessonRef(lesson: Lesson) = DBRefs.LESSONS_REF.ref.document(lesson.id)

    private fun eventRef(event: Event) = DBRefs.EVENTS_REF.ref.document(event.id)

    fun updateLesson(
        lesson: Lesson,
        callback: UploadDataTaskCallback
    ) {
        callback.onLoading()
        lessonRef(lesson).set(lesson)
            .addOnFailureListener(callback::onFailure)
            .addOnSuccessListener { callback.onSuccess() }
    }

    fun updateEvent(
        event: Event, eventImg: Uri?, selectedEventImgBitmap: Bitmap?,
        callback: UploadDataTaskCallback
    ) {
        callback.onLoading()

        when {
            eventImg != null -> {
                StorageManager.saveEventImage(event, eventImg)
            }
            selectedEventImgBitmap != null -> {
                StorageManager.saveEventImage(event, selectedEventImgBitmap)
            }
            else -> {
                saveInDB(event)
                    .addOnFailureListener(callback::onFailure)
                    .addOnCompleteListener { callback.onSuccess() }
                return
            }
        }.continueWith {
            if (!it.isSuccessful) throw it.exception!!
            saveInDB(event)
        }
            .addOnFailureListener(callback::onFailure)
            .addOnCompleteListener { callback.onSuccess() }
    }

    private fun saveInDB(event: Event) = eventRef(event).set(event)

    fun deleteLesson(lesson: Lesson, callback: TaskCallback<Int, Exception>) {
//        TODO check if signed users
//        TODO if so, cancel ;else delete
        callback.onLoading()
        lessonRef(lesson).delete()
            .addOnFailureListener(callback::onFailure)
            .addOnSuccessListener {
                val teacher = currentUser as? Teacher
                teacher ?: run {
                    callback.onFailure(NullPointerException("no user"))
                    return@addOnSuccessListener
                }
                teacher.teachingLessonsIDs.remove(lesson.id)
                val map = teacher.teachingClassesMap

                userRef(teacher).update(map)
                    .addOnFailureListener(callback::onFailure)
                    .addOnSuccessListener {
                        teacher.removeLesson(lesson.id)
                        dataModelHolder.removeData(lesson) {
                            callback.onSuccess()
                        }
                    }
            }
    }

    fun deleteEvent(
        event: Event,
        callback: TaskCallback<Int, Exception>
    ) {
//        TODO check if signed users
//        TODO if so, cancel ;else delete
        callback.onLoading()
        eventRef(event).delete()
            .addOnFailureListener(callback::onFailure)
            .addOnSuccessListener {
                val user = currentUser
                user ?: run {
                    callback.onFailure(NullPointerException("no user"))
                    return@addOnSuccessListener
                }
                user.createdEventsIDs.remove(event.id)
                val map = user.createdEventsIDsMap

                userRef(user)
                    .update(map as Map<String, Any>)
                    .addOnFailureListener { callback.onFailure(it) }
                    .addOnSuccessListener {
                        user.removeEvent(event.id)

                        dataModelHolder.removeData(event) {
                            Log.d(TAG, "removed in Room")
                            callback.onSuccess()
                        }

                        StorageManager.removeEventImage(event)

                    }

            }
    }

    fun getData(
        dataType: DataType,
        id: String,
        callback: (BaseData?) -> Unit
    ) =
        dataModelHolder.getData(dataType, id, callback)

    fun isUserSignedToLesson(lesson: Lesson) =
        currentUser!!.signedLessonsIDS.contains(lesson.id)

    fun isUserSignedToEvent(event: Event) =
        currentUser!!.signedEventsIDS.contains(event.id)

    fun toggleSignToLesson(lesson: Lesson, callback: TaskCallback<Boolean, Exception>) {
        val user = currentUser
        user ?: run {
            callback.onFailure(UserErrors.NoUserFound())
            return
        }
        signToggleToData(
            user.id,
            user.signedLessonsIDS,
            user.signedClassesMap,
            lessonRef(lesson), lesson,
            callback
        )
    }

    fun toggleSignToEvent(event: Event, callback: TaskCallback<Boolean, Exception>) {
        currentUser?.run {
            signToggleToData(
                id, signedEventsIDS, signedEventsMap,
                eventRef(event), event, callback
            )

        } ?: callback.onFailure(UserErrors.NoUserFound())
    }

    private inline fun <reified T : BaseData> signToggleToData(
        userID: String,
        userSigned: MutableSet<String>,
        userSignedMap: Map<String, Any>,
        ref: DocumentReference,
        data: T,
        callback: TaskCallback<Boolean, Exception>
    ) {
        val isSigned = data.signed.contains(userID)
        if (isSigned) {
            val updates = mapOf(userID to FieldValue.delete())
            ref.update("signedUID", updates)
                .addOnFailureListener(callback::onFailure)
                .addOnSuccessListener {
                    data.signed.remove(userID)
//                    check if it was full and now it could be open again
                    if (data.getNumOfParticipants() == data.maxParticipants) {
                        removeDataFromUser(data, userID, userSigned, callback)
                        return@addOnSuccessListener
                    }

                    val map = mapOf("status" to Status.OPEN.ordinal)
                    ref.update(map)
                        .addOnFailureListener(callback::onFailure)
                        .addOnSuccessListener {
                            removeDataFromUser(data, userID, userSigned, callback)
                        }
                }
            return
        }

        // not signed
        ref.get()
            .addOnFailureListener(callback::onFailure)
            .addOnSuccessListener { snapshot ->
                val dbData = snapshot.toObject(T::class.java)
                    ?: run {
                        callback.onFailure(DataTypeError())
                        return@addOnSuccessListener
                    }

                dataModelHolder.updateData(dbData) {
                    Log.d(TAG, "updated in room")
                }

                if (dbData.status == Status.FULL) {
                    callback.onFailure(SigningErrors.NoPlaceLeft())
                    return@addOnSuccessListener
                }

                dbData.signed[userID] = 0

                val map: MutableMap<String, Any> = mutableMapOf("signedUID" to dbData.signed)

                if (dbData.signed.size == dbData.maxParticipants) {
                    dbData.status = Status.FULL
                    map["status"] = dbData.status
                }

                snapshot.reference.update(map)
                    .addOnFailureListener(callback::onFailure)
                    .addOnSuccessListener {
                        addDataToUser(
                            dbData, userID, userSigned,
                            userSignedMap, callback
                        )
                    }
            }
    }

    private fun <T : BaseData> removeDataFromUser(
        data: T, userID: String, userSigned: MutableSet<String>,
        callback: TaskCallback<Boolean, Exception>
    ) {
        val updateKey = when (data) {
            is Lesson -> "signedClasses"
            is Event -> "signedEvents"
            else -> throw IllegalArgumentException("Data isn't matching")
        }

        val updates = mapOf(data.id to FieldValue.delete())
        userRef(userID).update(updateKey, updates)
            .addOnFailureListener(callback::onFailure)
            .addOnCompleteListener {
                userSigned.remove(data.id)

                dataModelHolder.updateData(data) {
                    callback.onSuccess(false)
                }
            }
    }

    private fun <T : BaseData> addDataToUser(
        data: T, uid: String,
        userSigned: MutableSet<String>,
        userSignedMap: Map<String, Any>,
        callback: TaskCallback<Boolean, Exception>
    ) {
        userSigned.add(data.id)
        val updateKey = when (data) {
            is Lesson -> "signedClasses"
            is Event -> "signedEvents"
            else -> throw IllegalArgumentException("Data isn't matching")
        }
        val map = mapOf(updateKey to userSignedMap)
        userRef(uid).update(map)
            .addOnFailureListener(callback::onFailure)
            .addOnSuccessListener {
                userSigned.add(data.id)
                dataModelHolder.addNewData(data) {
                    callback.onSuccess(true)
                }
            }
    }

    fun addAllLessons(lessons: List<Lesson>, callback: () -> Unit) =
        dataModelHolder.addLessons(lessons, callback)

    fun addAllEvents(events: List<Event>, callback: () -> Unit) =
        dataModelHolder.addEvents(events, callback)

    fun filterEvents(type: SourceType, query: String) =
        dataModelHolder.filterEvents(type, currentUser!!.id, query)

    fun filterLessons(type: SourceType, query: String) =
        dataModelHolder.filterLessons(type, currentUser!!.id, query)

}
