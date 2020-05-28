package com.erank.yogappl.data.repository

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.erank.yogappl.data.enums.DataType
import com.erank.yogappl.data.enums.SourceType
import com.erank.yogappl.data.enums.Status
import com.erank.yogappl.data.enums.TableNames
import com.erank.yogappl.data.models.*
import com.erank.yogappl.utils.SigningErrors
import com.erank.yogappl.utils.extensions.await
import com.erank.yogappl.utils.extensions.setLocation
import com.erank.yogappl.utils.helpers.AuthHelper
import com.erank.yogappl.utils.helpers.LocationHelper
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.JsonParseException
import org.imperiumlabs.geofirestore.GeoFirestore
import java.util.*
import javax.inject.Inject

class Repository @Inject constructor(
    val dataModelHolder: DataModelsHolder,
    val locationHelper: LocationHelper,
    val authHelper: AuthHelper,
    val storage: StorageManager
) {
    companion object {
        const val TAG = "Repository"
        private const val MaxPerBatch = 100L
        private const val MAX_KM = 20.0
    }

    var currentUser: User? = null
    private val isFilteringByDate = false

    enum class DBRefs(name: String) {
        LESSONS_REF(TableNames.LESSONS),
        EVENTS_REF(TableNames.EVENTS),
        USERS_REF(TableNames.USERS);

        val ref = Firebase.firestore.collection(name)

        companion object {
            fun refForType(dType: DataType) = values()[dType.ordinal].ref
        }
    }

    fun getLessons(type: SourceType) =
        dataModelHolder.getLessons(type, currentUser!!.id)

    fun getEvents(type: SourceType) =
        dataModelHolder.getEvents(type, currentUser!!.id)

    suspend fun loadData() {
        loadAll(DataType.LESSONS)
        loadAll(DataType.EVENTS)
    }

    suspend fun loadAll(dType: DataType) {
        val (code, latLng) = locationHelper.getCountryCode()

        val ref = DBRefs.refForType(dType)
        var query = latLng?.let {
            val center = GeoPoint(it.latitude, it.longitude)
            GeoFirestore(ref)
                .queryAtLocation(center, MAX_KM)
                .queries.first()
                ?: ref.whereEqualTo("countryCode", code)

        } ?: ref.whereEqualTo("countryCode", code)

        if (isFilteringByDate) {
           query = query.whereGreaterThanOrEqualTo("startDate", Date())
        }


        val snapshot = query.get().await()
            ?: throw NullPointerException("No snapshot found")

        LoadDataValueEventHandler(dType, this).convertSnapshot(snapshot)
    }

    suspend fun getUser(uid: String) = dataModelHolder.getUser(uid)

    suspend fun fetchLoggedUser(): User? {

        val uid = authHelper.currentUser?.uid
            ?: return null

        return currentUser ?: fetchUserIfNeeded(uid).also {
            currentUser = it
        }
    }

    suspend fun getUsers(ids: Set<String>): Map<String, PreviewUser> {
        return dataModelHolder.getUsers(ids)
    }

    suspend fun fetchUsersIfNeeded(users: Set<String>) {
        for (id in users) {
            fetchUserIfNeeded(id)
        }
    }

    suspend fun fetchUserIfNeeded(id: String): User? {
        dataModelHolder.getUser(id)?.let {
            return it
        }

        val snapshot = userRef(id).get().await()
            ?: return null

        val user = convertUser(snapshot)
            ?: throw JsonParseException("user casting failed")

        dataModelHolder.insertUser(user)
        return user
    }

    private fun convertUser(snapshot: DocumentSnapshot): User? {
        val userTypeIndex = snapshot.getString("type")
            ?: return null

        val type = when (User.Type.valueOf(userTypeIndex)) {
            User.Type.STUDENT -> User::class.java
            User.Type.TEACHER -> Teacher::class.java
        }

        return snapshot.toObject(type)
    }

    suspend fun createUser(
        user: User, pass: String,
        selectedImage: Uri?, bitmap: Bitmap?
    ): User? {

        //        stage 1 - add new simple user to the firebase Auth
        val authResult = authHelper.createUser(user.email, pass).await()
            ?: return null

        //       set id to user
        user.id = authResult.user!!.uid
        val uri = selectedImage?.let {
            storage.saveUserImage(user, it)
        }
            ?: bitmap?.let {
                storage.saveUserImage(user, it)
            }

        return uri?.run {
            uploadUserToDB(user)
            user
        }
    }

    suspend fun updateCurrentUser(selectedImage: Uri?, selectedImageBitmap: Bitmap?) {
        val user = currentUser!!

        when {
            selectedImage != null -> {
                storage.saveUserImage(user, selectedImage)
            }

            selectedImageBitmap != null -> {
                storage.saveUserImage(user, selectedImageBitmap)
            }
        }
        updateUser(user)
    }

    private suspend fun updateUser(user: User) {
        userRef(user).set(user).await()
    }


    suspend fun uploadUserToDB(user: User) {
        userRef(user).set(user).await()
        currentUser = user
    }

    private fun userRef(uid: String) = DBRefs.USERS_REF.ref.document(uid)

    private fun userRef(user: User) = userRef(user.id)

    suspend fun uploadData(
        dType: DataType,
        data: BaseData,
        selectedImage: Uri?,
        selectedBitmap: Bitmap?
    ) {

        val collection = DBRefs.refForType(dType)
        val ref = collection.document()

        data.id = ref.id

        ref.set(data).await()
        GeoFirestore(collection).setLocation(data.id, data.location)//uses callback

        dataModelHolder.addNewData(data)
        Log.d(TAG, "added in room")

        if (data is Lesson) {
            saveUserLesson(data.id)
            return
        }

        val event = data as Event

        val uri = when {
            selectedImage != null -> {
                storage.saveEventImage(event, selectedImage)
            }
            selectedBitmap != null -> {
                storage.saveEventImage(event, selectedBitmap)
            }
            else -> {
                saveUserEvent(event)
                return
            }
        }
        event.imageUrl = uri.toString()
        dataModelHolder.updateData(event) //update in Local DB
        ref.set(event).await()
        saveUserEvent(event)
    }

    private suspend fun saveUserEvent(event: Event) {
        val eventMap = event to Status.OPEN.ordinal
        val user = currentUser!!

        userRef(user).update("createdEventsIds", eventMap).await()

        dataModelHolder.addEvent(event)
        user.addEvent(event.id)
        dataModelHolder.updateUser(user)
    }


    private suspend fun saveUserLesson(id: String) {
        val teacher = currentUser as Teacher

        val lesson = id to Status.OPEN.ordinal
        userRef(teacher)
            .update("teachingClassesIDs", lesson).await()
        teacher.addLesson(id)
    }

    private fun lessonRef(lesson: Lesson) = DBRefs.LESSONS_REF.ref.document(lesson.id)

    private fun eventRef(event: Event) = DBRefs.EVENTS_REF.ref.document(event.id)

    suspend fun updateLesson(lesson: Lesson) {
        lessonRef(lesson).set(lesson).await()
    }

    suspend fun updateEvent(
        event: Event, localImgPath: Uri?, bitmap: Bitmap?
    ) {

        when {
            localImgPath != null -> storage
                .saveEventImage(event, localImgPath)

            bitmap != null -> storage
                .saveEventImage(event, bitmap)


//            TODO add image From url - upload to server
        }
        saveInDB(event)
    }

    private fun saveInDB(event: Event) = eventRef(event).set(event)

    suspend fun deleteLesson(lesson: Lesson) {
//        TODO check if signed users
//        TODO if so, cancel ;else delete
        lessonRef(lesson).delete().await()
        val teacher = currentUser as Teacher

        teacher.teachingLessonsIDs.remove(lesson.id)
        val map = teacher.teachingClassesMap

        userRef(teacher).update(map).await()
        teacher.removeLesson(lesson.id)
        dataModelHolder.removeData(lesson)
    }

    suspend fun deleteEvent(event: Event) {
//        TODO check if signed users
//        TODO if so, cancel ;else delete
        eventRef(event).delete().await()
        val user = currentUser!!
        user.createdEventsIDs.remove(event.id)
        val map = user.createdEventsIDsMap
        userRef(user).update(map).await()
        user.removeEvent(event.id)
        dataModelHolder.removeData(event)
        storage.removeEventImage(event)
    }

    suspend fun getData(dataType: DataType, id: String) =
        dataModelHolder.getData(dataType, id)

    fun isUserSignedToLesson(lesson: Lesson) =
        currentUser!!.signedLessonsIDS.contains(lesson.id)

    fun isUserSignedToEvent(event: Event) =
        currentUser!!.signedEventsIDS.contains(event.id)

    suspend fun toggleSignToLesson(lesson: Lesson): Boolean {
        currentUser!!.run {
            return signToggleToData(
                id,
                signedLessonsIDS, signedClassesMap,
                lessonRef(lesson), lesson
            )
        }
    }

    suspend fun toggleSignToEvent(event: Event): Boolean {
        currentUser!!.run {
            return signToggleToData(
                id,
                signedEventsIDS, signedEventsMap,
                eventRef(event), event
            )
        }
    }

    private suspend inline fun <reified T : BaseData> signToggleToData(
        userID: String,
        userSigned: MutableSet<String>,
        userSignedMap: Map<String, Any>,
        ref: DocumentReference, data: T
    ): Boolean {
        val isSigned = data.signed.contains(userID)
        if (isSigned) {
            val updates = mapOf(userID to FieldValue.delete())
            ref.update("signedUID", updates).await()
            data.signed.remove(userID)
//                    check if it was full and now it could be open again
            if (data.getNumOfParticipants() == data.maxParticipants) {
                return removeDataFromUser(data, userID, userSigned)
            }

            val map = mapOf("status" to Status.OPEN.ordinal)
            ref.update(map).await()
            return removeDataFromUser(data, userID, userSigned)
        }

        // not signed
        val snapshot = ref.get().await()!!
        val dbData = snapshot.toObject(T::class.java)!!

        dataModelHolder.updateData(dbData)

        if (dbData.status == Status.FULL) {
            throw SigningErrors.NoPlaceLeft()
        }

        dbData.signed[userID] = 0

        val map: MutableMap<String, Any> = mutableMapOf("signedUID" to dbData.signed)

        if (dbData.signed.size == dbData.maxParticipants) {
            dbData.status = Status.FULL
            map["status"] = dbData.status
        }

        snapshot.reference.update(map).await()
        return addDataToUser(dbData, userID, userSigned, userSignedMap)
    }

    private suspend fun <T : BaseData> removeDataFromUser(
        data: T, userID: String, userSigned: MutableSet<String>
    ): Boolean {
        val updateKey = when (data) {
            is Lesson -> "signedClasses"
            is Event -> "signedEvents"
            else -> throw IllegalArgumentException("Data isn't matching")
        }

        val updates = mapOf(data.id to FieldValue.delete())
        userRef(userID)
            .update(updateKey, updates)
            .await()

        userSigned.remove(data.id)

        dataModelHolder.updateData(data)
        return true
    }

    private suspend fun <T : BaseData> addDataToUser(
        data: T, uid: String,
        userSigned: MutableSet<String>,
        userSignedMap: Map<String, Any>
    ): Boolean {
        userSigned.add(data.id)
        val updateKey = when (data) {
            is Lesson -> "signedClasses"
            is Event -> "signedEvents"
            else -> throw IllegalArgumentException("Data isn't matching")
        }
        val map = mapOf(updateKey to userSignedMap)
        userRef(uid).update(map).await()
        userSigned.add(data.id)
        dataModelHolder.addNewData(data)
        return true
    }

    suspend fun addAllLessons(lessons: List<Lesson>) {
        dataModelHolder.addLessons(lessons)
    }

    suspend fun addAllEvents(events: List<Event>) {
        dataModelHolder.addEvents(events)
    }

    suspend fun getFilteredEvents(type: SourceType, query: String): List<Event> {
        return dataModelHolder.filterEvents(type, currentUser!!.id, query)
    }

    suspend fun getFilteredLessons(type: SourceType, query: String): List<Lesson> {
        return dataModelHolder.filterLessons(type, currentUser!!.id, query)
    }

    fun clearCurrentUser() {
        currentUser = null
    }
}