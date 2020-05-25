package com.erank.yogappl.utils.helpers

import android.Manifest.permission.WRITE_CALENDAR
import android.app.Activity
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.provider.CalendarContract.Events
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.erank.yogappl.data.models.BaseData
import com.erank.yogappl.data.repository.SharedPrefsHelper
import com.erank.yogappl.utils.extensions.alert
import com.erank.yogappl.utils.extensions.lowercaseName
import com.erank.yogappl.utils.extensions.toast
import java.util.*

class CalendarAppHelper(val context:Context,
                        val prefs: SharedPrefsHelper
) {
    companion object {
        private const val PERMISSION_REQUEST_WRITE_CALENDAR = 3455
        private val TAG = CalendarAppHelper::class.java.name
    }

    fun createEvent(activity: Activity, data: BaseData) {
        if (needsCalendarPermission()) {
            requestPermission(activity)
            return
        }

        val calID: Long = 3

        val description = "${data.dataType.singular} ${data.level.lowercaseName}"

        val values = ContentValues().apply {
            put(Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
            put(Events.DTSTART, data.startDate.time)
            put(Events.DTEND, data.endDate.time)
            put(Events.TITLE, data.title)
            put(Events.DESCRIPTION, description)
            put(Events.EVENT_LOCATION, data.locationName)
            put(Events.CALENDAR_ID, calID)
        }
        val uri = activity.contentResolver.insert(Events.CONTENT_URI, values)
        // get the event ID that is the last element in the Uri
        uri?.lastPathSegment?.toLong()?.let {
            prefs.put(data.id, it)
        }
        activity.toast("Added to your calendar")
    }

    private fun requestPermission(activity: Activity) {
        // Here, thisActivity is the current activity
        if (!needsCalendarPermission()) {
            // Permission has already been granted
//            context.toast("got calendar permission")
            return
        }

        // Permission is not granted
        // Should we show an explanation?
        val shouldShowRequestPermissionRationale =
            ActivityCompat.shouldShowRequestPermissionRationale(activity, WRITE_CALENDAR)

        if (shouldShowRequestPermissionRationale) {
            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            activity.alert("please let us add events to your calendar")
                .setPositiveButton("alright") { _, _ ->
                    requestPermission(activity)
                }.setNegativeButton("nah", null)
                .show()
        } else {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(WRITE_CALENDAR),
                PERMISSION_REQUEST_WRITE_CALENDAR
            )
        }
    }

    fun checkPermission(
        activity: Activity,
        permissions: Array<out String>,
        grantResults: IntArray
    ): Boolean {
        val indexOfWriteCalPer = permissions.indexOf(WRITE_CALENDAR)
        if (indexOfWriteCalPer == -1)
            return false

        if (grantResults[indexOfWriteCalPer] == PERMISSION_GRANTED) {
            activity.toast("calendar permission granted")
            return true
        }

        return false
    }

    private fun needsCalendarPermission() =
        ContextCompat.checkSelfPermission(context, WRITE_CALENDAR) != PERMISSION_GRANTED

    fun <T : BaseData> deleteEvent(data: T) {

        val eventID = prefs.getLong(data.id) ?: return

        val deleteUri = ContentUris.withAppendedId(Events.CONTENT_URI, eventID)

        val rows = context.contentResolver.delete(deleteUri, null, null)

        Log.i(TAG, "Rows deleted: $rows")
    }
}