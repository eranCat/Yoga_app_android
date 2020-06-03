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
import com.erank.yogappl.utils.extensions.LatLng
import com.erank.yogappl.utils.extensions.await
import com.erank.yogappl.utils.extensions.setLocation
import com.erank.yogappl.utils.helpers.AuthHelper
import com.erank.yogappl.utils.helpers.LocationHelper
import com.erank.yogappl.utils.helpers.MyImagePicker
import com.google.android.gms.maps.model.LatLng
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
        loadUserUploads()
        val usersToFetch = loadAllLessons()
        val otherUsersToFetch = loadAllEvents()
        fetchUsersIfNeeded(usersToFetch + otherUsersToFetch)
    }
    private suspend fun loadUserUploads(){
        val id = currentUser!!.id
        val lessDocs = DBRefs.LESSONS_REF
            .whereEqualTo("uid", id)
            .get().await()!!
            .documents

        val eveDocs = DBRefs.EVENTS_REF
            .whereEqualTo("uid", id)
            .get().await()!!
            .documents

        val lessons = lessDocs.map { tryConvertLesson(it) }
        val events = eveDocs.map { tryConvertEvent(it) }

        dataModelHolder.addLessons(lessons)
        dataModelHolder.addEvents(events)
    }
    private suspend fun loadAllLessons(): MutableSet<String> {
        val documents = getAllSnapshotDocs(DBRefs.LESSONS_REF)
        val users = mutableSetOf<String>()
        val lessons = documents.map { doc ->
            tryConvertLesson(doc).also {
                users.add(it.uid)
            }
        }
        dataModelHolder.addLessons(lessons)
        return users
    }

    private suspend fun loadAllEvents(): MutableSet<String> {
        val documents = getAllSnapshotDocs(DBRefs.EVENTS_REF)
        val users = mutableSetOf<String>()
        val events = documents.map { doc ->
            tryConvertEvent(doc).also {
                users.add(it.uid)
            }
        }
        dataModelHolder.addEvents(events)
        return users
    }

    private fun tryConvertLesson(doc: DocumentSnapshot):Lesson{
        return try {
            doc.toObject<Lesson>()!!
        } catch (e: RuntimeException) {
    //            Maybe old version of cost
            Lesson().apply {
                applyData(this,doc)
            }
        }
    }
    private fun tryConvertEvent(doc: DocumentSnapshot):Event{
        return try {
            doc.toObject<Event>()!!
        } catch (e: RuntimeException) {
    //            Maybe old version of cost
            Event().apply {
                applyData(this,doc)
                imageUrl = doc.getString("imageUrl")
            }
        }
    }

    private fun applyData(baseData: BaseData,doc: DocumentSnapshot) =with(baseData) {
        id = doc.getString("id")!!
        title = doc.getString("title")!!
        locationName = doc.getString("place")!!//check if place or locationName
        countryCode = doc.getString("countryCode")!!
        equip = doc.getString("equip")!!
        extraNotes = doc.getString("xtraNotes")
        uid = doc.getString("uid")!!

        maxParticipants = doc.getDouble("maxParticipants")!!.toInt()
        minAge = doc.getDouble("minAge")!!.toInt()
        maxAge = doc.getDouble("maxAge")!!.toInt()

        signed = doc["signedUID"] as MutableMap<String, Int>

        val locMap = doc["location"] as Map<String, Any>
        location = LatLng(locMap)

        val map = doc["cost"] as Map<String, Double>
        cost = map["amount"]!!

        postedDate = doc.getTimestamp("postedDate")!!.toDate()
        startDate = doc.getTimestamp("startDate")!!.toDate()
        endDate = doc.getTimestamp("endDate")!!.toDate()

        level = BaseData.Level.values()[doc.getDouble("level")!!.toInt()]
        status = Status.values()[doc.getDouble("status")!!.toInt()]
    }


    private suspend fun getAllSnapshotDocs(ref: CollectionReference): List<DocumentSnapshot> {

        val location = locationHelper.getLastKnownLocation()
        var query = if (location != null) {
            val center = GeoPoint(
                location.latitude,
                location.longitude
            )
            GeoFirestore(ref).queryAtLocation(center, MAX_KM).queries.first()
        } else {
            val code = locationHelper.getCountryCode()
            ref.whereEqualTo("countryCode", code)
        }
        query = query.whereGreaterThanOrEqualTo("startDate", Date())

        return query.get().await()!!.documents
    }

    suspend fun getUser(uid: String) = dataModelHolder.getUser(uid)

    suspend fun fetchLoggedUser(): User {
        val uid = authHelper.currentUser!!.uid
        return loadUserFromFirebase(uid).also {
            currentUser = it
        }
    }

    suspend fun getUsers(ids: Set<String>) = dataModelHolder.getUsers(ids)

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
        result: MyImagePicker.Result?
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

        result?.let {
            storage.saveEventImage(event, it)
            dataModelHolder.updateData(event) //update in Local DB
            ref.set(event).await()
        }
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

    suspend fun updateEvent(event: Event, result: MyImagePicker.Result?) {
        result?.let { storage.saveEventImage(event, it) }
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