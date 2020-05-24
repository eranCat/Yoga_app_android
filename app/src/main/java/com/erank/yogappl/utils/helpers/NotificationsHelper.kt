package com.erank.yogappl.utils.helpers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.erank.yogappl.R
import com.erank.yogappl.ui.activities.SplashActivity
import com.erank.yogappl.data.models.BaseData
import com.erank.yogappl.utils.extensions.addMinuets


object NotificationsHelper {

    const val CHANNEL_ID = "1101010"


    fun <T : BaseData> createNotification(context: Context, data: T) {

        val drawable = ContextCompat.getDrawable(context, R.drawable.app_icon_yoga)
        val largeIcon = (drawable as BitmapDrawable).bitmap
        val defaultRingtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder =
            NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(data.title)
                .setContentText(data.locationName)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.app_icon_yoga)
                .setLargeIcon(largeIcon)
                .setSound(defaultRingtone)

        val intent = Intent(context, SplashActivity::class.java)

        val notificationId = data.id.hashCode()
        val activity = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        builder.setContentIntent(activity)
        val notification = builder.build()
        val notificationIntent = Intent(context, NotificationReceiver::class.java)
            .putExtra(NotificationReceiver.NOTIFICATION_ID, notificationId)
            .putExtra(NotificationReceiver.NOTIFICATION, notification)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId, notificationIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        val futureInMillis = data.startDate.addMinuets(-30).time//half an hour before

        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager[AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis] = pendingIntent
    }

    fun <T : BaseData> removeNotification(context: Context, data: T) {
        val intent = Intent(context, NotificationReceiver::class.java)
        intent.putExtra("title", data.title)
        val pending =
            PendingIntent.getBroadcast(
                context, 42,
                intent, PendingIntent.FLAG_UPDATE_CURRENT
            )
        // Cancel notification
        val manager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.cancel(pending)
    }
}