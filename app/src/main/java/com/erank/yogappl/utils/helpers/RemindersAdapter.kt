package com.erank.yogappl.utils.helpers

import android.app.Activity
import com.erank.yogappl.R
import com.erank.yogappl.data.models.BaseData
import com.erank.yogappl.data.repository.Repository
import com.erank.yogappl.utils.extensions.alert
import javax.inject.Inject

class RemindersAdapter<T : BaseData>(val data: T) {

    @Inject
    lateinit var prefs:SharedPrefsHelper

    @Inject
    lateinit var calendarHelper: CalendarAppHelper

    @Inject
    lateinit var notificationsHelper: NotificationsHelper


    fun showDialog(activity: Activity) {

        val items = activity.resources.getStringArray(R.array.reminders_items)

        val actions = arrayOf(
            Runnable { notificationsHelper.createNotification(data) },
            Runnable { calendarHelper.createEvent(activity, data) }
        )

        activity.alert("You may select a reminder")
            .setItems(items) { _, i -> actions[i].run() }
            .setNegativeButton("No thank you", null)
            .show()
    }

    fun removeReminder(data: T) {
        notificationsHelper.removeNotification(data)
        calendarHelper.deleteEvent(data)
    }

    fun tryAgainIfAvailable(
        activity: Activity,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (calendarHelper.checkPermission(activity, permissions, grantResults)) {
            calendarHelper.createEvent(activity, data)
        }
    }
}