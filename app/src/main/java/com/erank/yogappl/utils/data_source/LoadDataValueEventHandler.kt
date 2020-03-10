package com.erank.yogappl.utils.data_source

import androidx.lifecycle.MutableLiveData
import com.erank.yogappl.models.BaseData
import com.erank.yogappl.models.Teacher
import com.erank.yogappl.models.User
import com.erank.yogappl.utils.UserErrors
import com.erank.yogappl.utils.enums.DataType
import com.erank.yogappl.utils.enums.DataType.EVENTS
import com.erank.yogappl.utils.enums.DataType.LESSONS
import com.erank.yogappl.utils.enums.SourceType
import com.erank.yogappl.utils.interfaces.TaskCallback
import com.erank.yogappl.utils.interfaces.UserTaskCallback
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.*

class LoadDataValueEventHandler(
    private val loaded: TaskCallback<Void,Exception>,
    private val dType: DataType,
    private var usersToFetch: MutableSet<String> = HashSet()
) : ValueEventListener {

    private val isFilteringByDate = false

    override fun onDataChange(snapshot: DataSnapshot) {

        if (!snapshot.exists()) {
            loaded.onSuccess()
            return
        }

        val user = DataSource.currentUser
            ?: run {
                loaded.onFailure(UserErrors.NoUserFound())
                return
            }

        val children = snapshot.children
        when (dType) {
            LESSONS -> convertValuesToLessons(children, user)
            EVENTS -> convertValuesToEvents(children, user)
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

    private fun convertValuesToLessons(children: MutableIterable<DataSnapshot>, user: User) {

        convertValues(
            children,
            DataSource.lessonsMap,
            user.signedLessonsIDS,
            (user as? Teacher)?.teachingLessonsIDs
        )
    }

    private fun convertValuesToEvents(children: MutableIterable<DataSnapshot>, user: User) {

        convertValues(
            children,
            DataSource.eventsMap,
            user.signedEventsIDS,
            user.createdEventsIDs
        )
    }

    private fun <T> initIfNeeded(liveData: MutableLiveData<MutableList<T>>) {
        liveData.value?.clear() ?: run {
            liveData.value = arrayListOf()
        }
    }


    private inline fun <reified T : BaseData> convertValues(
        values: MutableIterable<DataSnapshot>,
        map: MutableMap<SourceType, MutableLiveData<MutableList<T>>>,
        userSignedIds: MutableSet<String>,
        userUploadedIDs: MutableSet<String>?
    ) {

        map.values.forEach { initIfNeeded(it) }

        val all = map[SourceType.ALL]!!.value!!
        val signed = map[SourceType.SIGNED]!!.value!!
        val uploads = map[SourceType.UPLOADS]!!.value!!

        val today = Date()
        for (child in values) {
            val data = child.getValue(T::class.java)
                ?: continue

            //                        uploads
            if (userUploadedIDs?.contains(data.id) == true) {
                uploads.add(0, data)
            } else if (!isFilteringByDate || data.endDate >= today) {

                val allIndex = if (data.isCanceled) all.size - 1 else 0
                all.add(allIndex, data)

                if (userSignedIds.contains(data.id)) {
                    signed.add(data)
                }
            }
            usersToFetch.add(data.uid)
        }
    }
}