package com.erank.yogappl.utils.data_source

import android.util.Log
import com.erank.yogappl.models.*
import com.erank.yogappl.utils.SSet
import com.erank.yogappl.utils.UserErrors
import com.erank.yogappl.utils.enums.DataType
import com.erank.yogappl.utils.enums.DataType.EVENTS
import com.erank.yogappl.utils.enums.DataType.LESSONS
import com.erank.yogappl.utils.interfaces.TaskCallback
import com.erank.yogappl.utils.interfaces.UserTaskCallback
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import java.util.*

class LoadDataValueEventHandler(
    private val loaded: TaskCallback<Void, Exception>,
    private val dType: DataType,
    private var usersToFetch: MutableSet<String> = HashSet()
) : EventListener<QuerySnapshot> {

    val TAG = javaClass.name

    private val isFilteringByDate = false

    override fun onEvent(snapshot: QuerySnapshot?, firebaseException: FirebaseFirestoreException?) {

        firebaseException?.let {
            loaded.onFailure(it)
            return
        }
        snapshot?.let {
            if (it.isEmpty) {
                loaded.onSuccess()
                return
            }
        } ?: run {
            loaded.onSuccess()
            return
        }

        val children = snapshot.documents

        val user = DataSource.currentUser
        if (user == null) {
            loaded.onFailure(UserErrors.NoUserFound())
            return
        }

        val roomCallback: () -> Unit = {
            Log.d(TAG, "inserted stuff from firebase to room")
        }

        when (dType) {
            LESSONS -> convertValuesToLessons(children, user, roomCallback)
            EVENTS -> convertValuesToEvents(children, user, roomCallback)
        }

        if (usersToFetch.isEmpty()) {
            loaded.onSuccess()
            return
        }

        for ((i, id) in usersToFetch.withIndex()) {

            DataSource.fetchUserIfNeeded(id, object : UserTaskCallback {
                override fun onSuccessFetchingUser(user: User?) {
                    if (i == usersToFetch.size - 1)
                        loaded.onSuccess()
                }

                override fun onFailedFetchingUser(e: Exception) = loaded.onFailure(e)
            })
        }
    }

    private fun convertValuesToLessons(
        children: MutableList<DocumentSnapshot>,
        user: User,
        callback: () -> Unit
    ) {

        val convertedValues = convertValues<Lesson>(
            children,
            user.signedLessonsIDS,
            (user as? Teacher)?.teachingLessonsIDs
        )

        DataSource.addAllLessons(convertedValues, callback)
    }

    private fun convertValuesToEvents(
        children: MutableList<DocumentSnapshot>,
        user: User,
        callback: () -> Unit
    ) {
        val convertedValues = convertValues<Event>(
            children,
            user.signedLessonsIDS,
            (user as? Teacher)?.teachingLessonsIDs
        )
        DataSource.addAllEvents(convertedValues, callback)
    }

    private inline fun <reified T : BaseData> convertValues(
        values: MutableList<DocumentSnapshot>, signedIDS: SSet, uploadsIDs: Set<String>?
    ): MutableList<T> {

        val list = mutableListOf<T>()

        val today = Date()

        for (child in values) {
            val data = child.toObject<T>()
                ?: continue

            if (uploadsIDs != null && uploadsIDs.contains(data.id)
                || !isFilteringByDate || data.endDate >= today
                || signedIDS.contains(data.id)
            ) list.add(data)


            usersToFetch.add(data.uid)
        }

        return list
    }
}