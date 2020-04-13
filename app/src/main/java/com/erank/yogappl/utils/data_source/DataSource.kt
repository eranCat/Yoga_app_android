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
import com.erank.yogappl.utils.enums.SourceType
import com.erank.yogappl.utils.enums.Status
import com.erank.yogappl.utils.enums.TableNames
import com.erank.yogappl.utils.extensions.lowercaseName
import com.erank.yogappl.utils.helpers.AuthHelper
import com.erank.yogappl.utils.helpers.LocationHelper
import com.erank.yogappl.utils.helpers.StorageManager
import com.erank.yogappl.utils.helpers.StorageManager.saveUserImage
import com.erank.yogappl.utils.interfaces.TaskCallback
import com.erank.yogappl.utils.interfaces.UploadDataTaskCallback
import com.erank.yogappl.utils.interfaces.UserTaskCallback
import com.google.firebase.database.*
import com.google.gson.JsonParseException


object DataSource {

    private val TAG = DataSource::class.java.name
    private val ds = FirebaseDatabase.getInstance()

    var currentUser: User? = null

    private lateinit var dataModelHolder: DataModelsHolder

    private const val MaxPerBatch = 100

    enum class DBRefs(name: String) {
        LESSONS(TableNames.name(DataType.LESSONS)),
        EVENTS(TableNames.name(DataType.EVENTS)),
        USERS(TableNames.USERS.lowercaseName);

        val ref: DatabaseReference = ds.reference.child(name)

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

        loadAll(context, DataType.LESSONS, object : TaskCallback<Void, Exception> {

            override fun onSuccess(result: Void?) =
                loadAll(context, DataType.EVENTS, onLoadedCallback)

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
                .orderByChild("countryCode").equalTo(code)
                .limitToLast(MaxPerBatch)
                .addListenerForSingleValueEvent(handler)
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

        userRef(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = convertUser(snapshot)
                    if (user == null) {
                        callback.onFailedFetchingUser(JsonParseException("user casting failed"))
                        return
                    }
                    addUser(user) {
                        callback.onSuccessFetchingUser(user)
                    }
                }

                override fun onCancelled(err: DatabaseError) =
                    callback.onFailedFetchingUser(err.toException())
            })
    }

    fun convertUser(snapshot: DataSnapshot): User? {
        val userTypeIndex = snapshot.child("type")
            .getValue(Int::class.java) ?: return null

        val type = when (Type.values()[userTypeIndex]) {
            Type.STUDENT -> User::class.java
            Type.TEACHER -> Teacher::class.java
        }

        return snapshot.getValue(type)
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
        userRef(user)
            .updateChildren(user.infoMap)
            .addOnSuccessListener { callback.onSuccessFetchingUser(user) }
            .addOnFailureListener { callback.onFailedFetchingUser(it) }
    }


    fun uploadUserToDB(user: User, listener: UserTaskCallback) {
        //    stage  - add all user data to DB - Firebase database

        userRef(user).setValue(user).addOnSuccessListener {
            currentUser = user
            listener.onSuccessFetchingUser(user)
        }.addOnFailureListener(listener::onFailedFetchingUser)
    }

    private fun userRef(uid: String) = DBRefs.USERS.ref.child(uid)


    private fun userRef(user: User) = userRef(user.id)

    fun uploadData(
        dType: DataType,
        data: BaseData,
        selectedImage: Uri?,
        selectedBitmap: Bitmap?,
        callback: UploadDataTaskCallback
    ) {

        val ref = DBRefs.refForType(dType).push()

        data.id = ref.key!!

        ref.setValue(data)
            .addOnFailureListener(callback::onFailure)
            .addOnSuccessListener {

                dataModelHolder.addNewData(data) {
                    Log.d(TAG, "added in room")
                }

                if (dType == DataType.LESSONS) {
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

    private fun saveUserEvent(id: String, callback: UploadDataTaskCallback) {
        val user = currentUser
        if (user == null) {
            callback.onFailure(NullPointerException("no user"))
            return
        }

        userRef(user).child("createdEventsIds")
            .child(id).setValue(Status.OPEN.ordinal)
            .addOnSuccessListener {
                user.addEvent(id)
                callback.onSuccess()
            }
            .addOnFailureListener { callback.onFailure(it) }
    }


    private fun saveUserLesson(id: String, callback: UploadDataTaskCallback) {
        val teacher = currentUser as? Teacher
        if (teacher == null) {
            callback.onFailure(NullPointerException("no user"))
            return
        }

        userRef(teacher).child("teachingClassesIDs")
            .child(id).setValue(Status.OPEN.ordinal)
            .addOnSuccessListener {
                teacher.addLesson(id)
                callback.onSuccess()
            }
            .addOnFailureListener { callback.onFailure(it) }
    }

    private fun lessonRef(lesson: Lesson) = DBRefs.LESSONS.ref.child(lesson.id)

    private fun eventRef(event: Event) = DBRefs.EVENTS.ref.child(event.id)

    fun updateLesson(
        lesson: Lesson,
        callback: UploadDataTaskCallback
    ) {
        callback.onLoading()
        lessonRef(lesson).setValue(lesson)
            .addOnFailureListener(callback::onFailure)
            .addOnSuccessListener { callback.onSuccess() }
    }

    fun updateEvent(
        event: Event, eventImg: Uri?,
        selectedEventImgBitmap: Bitmap?,
        callback: UploadDataTaskCallback
    ) {
        callback.onLoading()
        val task1 = eventImg?.let {

            StorageManager.saveEventImage(event, it)
                .continueWithTask { task ->
                    if (!task.isSuccessful) {
                        throw task.exception!!
                    }
                    saveInDB(event)
                }

        } ?: saveInDB(event)

        task1.addOnFailureListener(callback::onFailure)
            .addOnCompleteListener { callback.onSuccess() }
    }

    private fun saveInDB(event: Event) = eventRef(event).setValue(event)

    fun deleteLesson(
        lesson: Lesson,
        callback: TaskCallback<Int, Exception>
    ) {
//        TODO check if signed users
//        TODO if so, cancel ;else delete
        callback.onLoading()
        lessonRef(lesson).removeValue()
            .addOnFailureListener { callback.onFailure(it) }
            .addOnSuccessListener {
                val teacher = currentUser as? Teacher
                teacher ?: run {
                    callback.onFailure(NullPointerException("no user"))
                    return@addOnSuccessListener
                }

                val map = teacher.teachingClassesMap
                //must be saved in variable .
                // get() makes it new map
                map.remove(lesson.id)

                userRef(teacher)
                    .updateChildren(map as Map<String, Any>)
                    .addOnFailureListener { callback.onFailure(it) }
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
        eventRef(event).removeValue()
            .addOnFailureListener { callback.onFailure(it) }
            .addOnSuccessListener {
                val user = currentUser
                user ?: run {
                    callback.onFailure(NullPointerException("no user"))
                    return@addOnSuccessListener
                }

                val map = user.createdEventsIDsMap
                //must be saved in variable .
                // get() makes it new map
                map.remove(event.id)

                userRef(user)
                    .updateChildren(map as Map<String, Any>)
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
        signToData(
            user.id,
            user.signedLessonsIDS,
            user.signedClassesMap,
            lessonRef(lesson), lesson,
            callback
        )
    }

    fun toggleSignToEvent(event: Event, callback: TaskCallback<Boolean, Exception>) {
        val user = currentUser
        user ?: run {
            callback.onFailure(UserErrors.NoUserFound())
            return
        }
        signToData(
            user.id,
            user.signedEventsIDS,
            user.signedEventsMap,
            eventRef(event),
            event, callback
        )
    }

    private inline fun <reified T : BaseData> signToData(
        userID: String,
        userSigned: MutableSet<String>,
        userSignedMap: MutableMap<String, Boolean>,
        ref: DatabaseReference,
        data: T,
        callback: TaskCallback<Boolean, Exception>
    ) {
        val isSigned = data.signed.contains(userID)
        if (isSigned) {
            ref.child("signedUID")
                .child(userID).removeValue()
                .addOnFailureListener(callback::onFailure)
                .addOnSuccessListener {
                    data.signed.remove(userID)
//                    check if it was full and now it could be open again
                    if (data.getNumOfParticipants() == data.maxParticipants) {
                        removeDataFromUser(data, userID, userSigned, callback)
                        return@addOnSuccessListener
                    }

                    ref.child("status").setValue(Status.OPEN.ordinal)
                        .addOnFailureListener(callback::onFailure)
                        .addOnSuccessListener {
                            removeDataFromUser(data, userID, userSigned, callback)
                        }
                }
            return
        }

        // not signed
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val dbData = snapshot.getValue(T::class.java)
                    ?: run {
                        callback.onFailure(DataTypeError())
                        return
                    }

                dataModelHolder.updateData(dbData) {
                    Log.d(TAG, "updated in room")
                }

                if (dbData.status == Status.FULL) {
                    callback.onFailure(SigningErrors.NoPlaceLeft())
                    return
                }

                dbData.signed[userID] = 0

                val map: MutableMap<String, Any> = mutableMapOf("signedUID" to dbData.signed)

                if (dbData.signed.size == dbData.maxParticipants) {
                    dbData.status = Status.FULL
                    map["status"] = dbData.status
                }

                snapshot.ref.updateChildren(map as Map<String, Any>)
                    .addOnFailureListener(callback::onFailure)
                    .addOnSuccessListener {

                        addDataToUser(
                            dbData, userID, userSigned,
                            userSignedMap, callback
                        )
                    }
            }

            override fun onCancelled(dbErr: DatabaseError) =
                callback.onFailure(dbErr.toException())

        })
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

        userRef(userID).child(updateKey)
            .child(data.id).removeValue()
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
        userSignedMap: MutableMap<String, Boolean>,
        callback: TaskCallback<Boolean, Exception>
    ) {
        userSigned.add(data.id)

        userSignedMap[data.id] = true
        val updateKey = when (data) {
            is Lesson -> "signedClasses"
            is Event -> "signedEvents"
            else -> throw IllegalArgumentException("Data isn't matching")
        }
        val map = mapOf(updateKey to userSignedMap)
        userRef(uid).updateChildren(map)
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
