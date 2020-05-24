package com.erank.yogappl.data.data_source

import android.util.Log
import com.erank.yogappl.data.models.*
import com.erank.yogappl.utils.SSet
import com.erank.yogappl.utils.UserErrors
import com.erank.yogappl.data.enums.DataType
import com.erank.yogappl.utils.interfaces.TaskCallback
import com.erank.yogappl.utils.interfaces.UserTaskCallback
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import java.util.*

class LoadDataValueEventHandler(
    private val dType: DataType,
    private val loaded: TaskCallback<Void, Exception>
) : EventListener<QuerySnapshot> {

    val TAG = javaClass.name

    private val isFilteringByDate = false

    private var usersToFetch: MutableSet<String> = HashSet()

    override fun onEvent(
        snapshot: QuerySnapshot?,
        firebaseException: FirebaseFirestoreException?
    ) {

        firebaseException?.let {
            loaded.onFailure(it)
            return
        }
        if (snapshot == null || snapshot.isEmpty) {
            loaded.onSuccess()
            return
        }

        val children = snapshot.documents

        val user = DataSource.currentUser ?: run {
            loaded.onFailure(UserErrors.NoUserFound())
            return
        }

        when (dType) {
            DataType.LESSONS -> convertValuesToLessons(children, user)
            DataType.EVENTS -> convertValuesToEvents(children, user)
        }
    }

    private fun fetchUsers() {
        Log.d(TAG, "cached from firebase into room DB")

        if (usersToFetch.isEmpty()) {
            loaded.onSuccess()
            return
        }
        for (id in usersToFetch) {

            DataSource.fetchUserIfNeeded(id, object : UserTaskCallback {
                override fun onSuccessFetchingUser(user: User?) {
                    usersToFetch.remove(user!!.id)
                    if (usersToFetch.isEmpty())
                        loaded.onSuccess()
                }

                override fun onFailedFetchingUser(e: Exception) =
                    loaded.onFailure(e)
            })
        }
    }

    private fun convertValuesToLessons(
        children: MutableList<DocumentSnapshot>,
        user: User
    ) {

        val convertedValues = convertValues<Lesson>(
            children, user.signedLessonsIDS,
            (user as? Teacher)?.teachingLessonsIDs
        )

        DataSource.addAllLessons(convertedValues){fetchUsers()}
    }

    private fun convertValuesToEvents(children: MutableList<DocumentSnapshot>, user: User) {
        val convertedValues = convertValues<Event>(
            children, user.signedEventsIDS, user.createdEventsIDs
        )
        DataSource.addAllEvents(convertedValues){fetchUsers()}
    }

    private inline fun <reified T : BaseData> convertValues(
        values: MutableList<DocumentSnapshot>, signedIDS: SSet, uploadsIDs: Set<String>?
    ): MutableList<T> {

        val list = mutableListOf<T>()

        val today = Date()

        for (child in values) {
            val data = child.toObject<T>()!!

            if (uploadsIDs?.contains(data.id) == true
                || !isFilteringByDate || data.endDate >= today
                || signedIDS.contains(data.id)
            ) list.add(data)

            usersToFetch.add(data.uid)
        }

        return list
    }
}