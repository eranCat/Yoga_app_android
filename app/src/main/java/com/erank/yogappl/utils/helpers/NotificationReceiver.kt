package com.erank.yogappl.utils.helpers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.erank.yogappl.R

class NotificationReceiver : BroadcastReceiver() {

    companion object {
        var NOTIFICATION_ID = "notification-id"
        var NOTIFICATION = "notification"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context
            .getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager

        val notification =
            intent.getParcelableExtra<Notification>(NOTIFICATION)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = context.getString(R.string.default_notification_channel_id)
            val notificationChannel = NotificationChannel(
                channel, "NOTIFICATION_CHANNEL_NAME", importance
            )
            notificationManager?.createNotificationChannel(notificationChannel)
        }
        val id = intent.getIntExtra(NOTIFICATION_ID, 0)
        notificationManager?.notify(id, notification)
    }
}