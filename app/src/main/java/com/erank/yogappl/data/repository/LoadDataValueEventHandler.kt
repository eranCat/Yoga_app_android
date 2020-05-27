package com.erank.yogappl.data.repository

import android.util.Log
import com.erank.yogappl.data.enums.DataType
import com.erank.yogappl.data.models.*
import com.erank.yogappl.utils.SSet
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import java.util.*

class LoadDataValueEventHandler(
    private val dType: DataType,
    private val repository: Repository
) {

    val TAG = javaClass.name

    private var usersToFetch: MutableSet<String> = HashSet()

    suspend fun convertSnapshot(snapshot: QuerySnapshot) {
        if (snapshot.isEmpty) return

        val docs = snapshot.documents
        val user = repository.currentUser!!

        when (dType) {
            DataType.LESSONS -> convertValuesToLessons(docs, user)
            DataType.EVENTS -> convertValuesToEvents(docs, user)
        }
    }

    private suspend fun fetchUsers() {
        Log.d(TAG, "cached from firebase into room DB")

        if (usersToFetch.isEmpty()) return

        repository.fetchUsersIfNeeded(usersToFetch)
    }

    private suspend fun convertValuesToLessons(docs: MutableList<DocumentSnapshot>, user: User) {

        val convertedValues = convertValues<Lesson>(
            docs, user.signedLessonsIDS,
            (user as? Teacher)?.teachingLessonsIDs
        )

        repository.addAllLessons(convertedValues)
        fetchUsers()
    }

    private suspend fun convertValuesToEvents(docs: MutableList<DocumentSnapshot>, user: User) {
        val convertedValues = convertValues<Event>(
            docs, user.signedEventsIDS, user.createdEventsIDs
        )
        repository.addAllEvents(convertedValues)
        fetchUsers()
    }

    private inline fun <reified T : BaseData> convertValues(
        docs: MutableList<DocumentSnapshot>,
        signedIDS: SSet, uploadsIDs: Set<String>?
    ): MutableList<T> {

        val list = mutableListOf<T>()

        for (doc in docs) {
            val data = doc.toObject<T>()!!

            if (uploadsIDs?.contains(data.id) == true
                || signedIDS.contains(data.id)
            ) list.add(data)

            usersToFetch.add(data.uid)
        }

        return list
    }
}