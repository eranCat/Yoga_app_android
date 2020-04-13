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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.*

class LoadDataValueEventHandler(
    private val loaded: TaskCallback<Void, Exception>,
    private val dType: DataType,
    private var usersToFetch: MutableSet<String> = HashSet()
) : ValueEventListener {

    val TAG = LoadDataValueEventHandler::class.java.name

    private val isFilteringByDate = false

    override fun onDataChange(snapshot: DataSnapshot) {

        if (!snapshot.exists()) {
            loaded.onSuccess()
            return
        }

        val children = snapshot.children

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

    override fun onCancelled(err: DatabaseError) = loaded.onFailure(err.toException())

    private fun convertValuesToLessons(
        children: MutableIterable<DataSnapshot>,
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
        children: MutableIterable<DataSnapshot>,
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
        values: MutableIterable<DataSnapshot>,
        signedIDS: SSet,
        uploadsIDs: Set<String>?
    ):
            MutableList<T> {

        val list = mutableListOf<T>()

        val today = Calendar.getInstance().time

        for (child in values) {
            val data = child.getValue(T::class.java)
                ?: continue

            if (uploadsIDs?.contains(data.id) == true
                || !isFilteringByDate || data.endDate >= today
                || signedIDS.contains(data.id)
            ) list.add(data)


            usersToFetch.add(data.uid)
        }

        return list
    }
}