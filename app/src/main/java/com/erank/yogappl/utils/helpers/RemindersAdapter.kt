package com.erank.yogappl.utils.helpers

import android.app.Activity
import android.content.Context
import com.erank.yogappl.R
import com.erank.yogappl.data.models.BaseData
import com.erank.yogappl.utils.extensions.alert

class RemindersAdapter<T : BaseData>(val data: T) {

    private val user = DataSource.currentUser!!

    fun showDialog(activity: Activity) {

        val prefs = SharedPrefsHelper.Builder(activity, user)
        val items = activity.resources.getStringArray(R.array.reminders_items)

        val actions = arrayOf(
            Runnable { NotificationsHelper.createNotification(activity, data) },
            Runnable { CalendarAppHelper.createEvent(activity, data, prefs) }
        )

        activity.alert("You may select a reminder")
            .setItems(items) { _, i -> actions[i].run() }
            .setNegativeButton("No thank you", null)
            .show()
    }

    fun removeReminder(context: Context, data: T) {
        NotificationsHelper.removeNotification(context, data)
        val prefs = SharedPrefsHelper.Builder(context, user)
        CalendarAppHelper.deleteEvent(context, data, prefs)
    }

    fun tryAgainIfAvailable(
        activity: Activity,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (CalendarAppHelper.checkPermission(activity, permissions, grantResults)) {
            val prefs = SharedPrefsHelper.Builder(activity, user)
            CalendarAppHelper.createEvent(activity, data, prefs)
        }
    }
}