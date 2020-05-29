package com.erank.yogappl.data.repository

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.erank.yogappl.data.enums.DataType
import com.erank.yogappl.data.enums.SourceType
import com.erank.yogappl.data.enums.Status
import com.erank.yogappl.data.enums.TableNames
import com.erank.yogappl.data.models.*
import com.erank.yogappl.data.models.User.Type.STUDENT
import com.erank.yogappl.data.models.User.Type.TEACHER
import com.erank.yogappl.utils.SigningErrors
import com.erank.yogappl.utils.extensions.await
import com.erank.yogappl.utils.extensions.setLocation
import com.erank.yogappl.utils.helpers.AuthHelper
import com.erank.yogappl.utils.helpers.LocationHelper
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
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
        private const val MAX_KM = 20.0
    }

    var currentUser: User? = null
    private val isFilteringByDate = false

    object DBRefs {
        val LESSONS_REF get() = ref(TableNames.LESSONS)
        val EVENTS_REF get() = ref(TableNames.EVENTS)
        val USERS_REF get() = ref(TableNames.USERS)

        private fun ref(name: String) =
            Firebase.firestore.collection(name)

        fun refForType(type: DataType) = when (type) {
            DataType.LESSONS -> LESSONS_REF
            DataType.EVENTS -> EVENTS_REF
        }
    }

    fun getLessons(type: SourceType) =
        dataModelHolder.getLessons(type, currentUser!!.id)

    fun getEvents(type: SourceType) =
        dataModelHolder.getEvents(type, currentUser!!.id)

    suspend fun loadData() {
        val usersToFetch = loadAllLessons()
        val otherUsersToFetch = loadAllEvents()
        fetchUsersIfNeeded(usersToFetch + otherUsersToFetch)
    }

    private suspend fun loadAllLessons(): MutableSet<String> {
        val documents = getQuerySnapshot(DBRefs.LESSONS_REF)
        val users = mutableSetOf<String>()
        val lessons = documents.map { doc ->
            doc.toObject<Lesson>()!!.also {
                users.add(it.uid)
            }
        }
        dataModelHolder.addLessons(lessons)
        return users
    }

    private suspend fun loadAllEvents(): MutableSet<String> {
        val documents = getQuerySnapshot(DBRefs.EVENTS_REF)
        val users = mutableSetOf<String>()
        val events = documents.map { doc ->
            doc.toObject<Event>()!!.also {
                users.add(it.uid)
            }
        }
        dataModelHolder.addEvents(events)
        return users
    }

    private suspend fun getQuerySnapshot(ref: CollectionReference): List<DocumentSnapshot> {
        var query = locationHelper.getLastKnownLocation()?.let {
            val center = GeoPoint(it.latitude, it.longitude)
            GeoFirestore(ref)
                .queryAtLocation(center, MAX_KM)
                .queries[0]
        } ?: ref

        val code = locationHelper.getCountryCode()
        query = query.whereEqualTo("countryCode", code)

        if (isFilteringByDate) {
            query = query.whereGreaterThanOrEqualTo("startDate", Date())
        }

        return query.get().await()!!.documents
    }

    suspend fun getUser(uid: String) = dataModelHolder.getUser(uid)

    suspend fun fetchLoggedUser(): User {
        val uid = authHelper.currentUser!!.uid
        return loadUserFromFirebase(uid).also {
            currentUser = it
        }
    }

    suspend fun getUsers(ids: Set<String>): Map<String, PreviewUser> {
        return dataModelHolder.getUsers(ids)
    }

    private suspend fun fetchUsersIfNeeded(users: Set<String>) =
        users.forEach { fetchUserIfNeeded(it) }

    private suspend fun fetchUserIfNeeded(id: String): User {
        return dataModelHolder.getUser(id)
            ?: loadUserFromFirebase(id)
    }

    private suspend fun loadUserFromFirebase(id: String): User {
        val snapshot = userRef(id).get().await()!!
        return convertUser(snapshot).also {
            dataModelHolder.addUser(it)
        }
    }

    private fun convertUser(snapshot: DocumentSnapshot): User {
        val value = snapshot.getString("type")!!

        return when (User.Type.valueOf(value)) {
            TEACHER -> snapshot.toObject<Teacher>()
            STUDENT -> snapshot.toObject<User>()
        }!!
    }

    suspend fun createUser(
        user: User, pass: String,
        selectedImage: Uri?, bitmap: Bitmap?
    ): User? {

        //        stage 1 - add new simple user to the firebase Auth
        val authResult = authHelper.createUser(user.email, pass).await()!!
        //       set id to user
        val uid = authResult.user!!.uid
        user.id = uid
        val uri = selectedImage?.let {
            storage.saveUserImage(uid, it)
        } ?: bitmap?.let { storage.saveUserImage(uid, it) }

        uri?.let {
            user.profileImageUrl = it.toString()
        }
        uploadUserToDB(user)
        return user
    }

    suspend fun updateCurrentUser(selectedImage: Uri?, selectedImageBitmap: Bitmap?) {
        val user = currentUser!!

        when {
            selectedImage != null -> {
                storage.saveUserImage(user.id, selectedImage)
            }

            selectedImageBitmap != null -> {
                storage.saveUserImage(user.id, selectedImageBitmap)
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

    private fun userRef(uid: String) = DBRefs.USERS_REF.document(uid)

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
            saveUserLesson(data)
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
        val user = currentUser!!

        userRef(user).update("createdEventsIds", event.id).await()

        dataModelHolder.addEvent(event)
        user.addEvent(event.id)
        dataModelHolder.updateUser(user)
    }

    private suspend fun saveUserLesson(lesson: Lesson) {
        val teacher = currentUser as Teacher

        userRef(teacher).update("teachingClassesIDs", lesson.id).await()

        dataModelHolder.addLesson(lesson)
        teacher.addLesson(lesson.id)
        dataModelHolder.updateUser(teacher)
    }

    private fun lessonRef(lesson: Lesson) = DBRefs.LESSONS_REF.document(lesson.id)

    private fun eventRef(event: Event) = DBRefs.EVENTS_REF.document(event.id)

    suspend fun updateLesson(lesson: Lesson) {
        lessonRef(lesson).set(lesson).await()
        dataModelHolder.updateData(lesson)
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

    private suspend fun saveInDB(event: Event) {
        eventRef(event).set(event).await()
        dataModelHolder.updateData(event)
    }

    suspend fun deleteLesson(lesson: Lesson) {
//        TODO check if signed users
//        TODO if so, cancel ;else delete
        lessonRef(lesson).delete().await()
        val teacher = currentUser as Teacher
        teacher.teachingLessonsIDs.remove(lesson.id)
        val uploads = teacher.teachingLessonsIDs.toList()
        val map = mapOf("teachingLessonsIDs" to uploads)
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
        val uploads = user.createdEventsIDs.toList()
        val map = mapOf("createdEventsIDs" to uploads)
        userRef(user).update(map).await()
        user.removeEvent(event.id)
        dataModelHolder.removeData(event)
        storage.removeEventImage(event)
    }

    suspend fun getData(dataType: DataType, id: String) =
        dataModelHolder.getData(dataType, id)

    suspend fun toggleSignToLesson(lesson: Lesson): Boolean {
        currentUser!!.run {
            return signToggleToData(
                id, signedLessonsIDS,
                lessonRef(lesson), lesson
            )
        }
    }

    suspend fun toggleSignToEvent(event: Event): Boolean {
        currentUser!!.run {
            return signToggleToData(
                id, signedEventsIDS,
                eventRef(event), event
            )
        }
    }

    private suspend inline fun <reified T : BaseData> signToggleToData(
        userID: String, userSigned: MutableList<String>,
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
        return addDataToUser(dbData, userID, userSigned)
    }

    private suspend fun <T : BaseData> removeDataFromUser(
        data: T, userID: String, userSigned: MutableList<String>
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
        data: T, uid: String, userSigned: MutableList<String>
    ): Boolean {
        userSigned.add(data.id)
        val updateKey = when (data) {
            is Lesson -> "signedClasses"
            is Event -> "signedEvents"
            else -> throw IllegalArgumentException("Data isn't matching")
        }
        val map = mapOf(updateKey to userSigned)
        userRef(uid).update(map).await()
        userSigned.add(data.id)
        dataModelHolder.addNewData(data)
        return true
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