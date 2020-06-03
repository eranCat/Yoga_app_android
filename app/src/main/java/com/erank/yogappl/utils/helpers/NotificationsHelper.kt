package com.erank.yogappl.utils.helpers

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_DEFAULT
import androidx.core.app.NotificationCompat.PRIORITY_HIGH
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.erank.yogappl.R
import com.erank.yogappl.data.models.BaseData
import com.erank.yogappl.ui.activities.splash.SplashActivity
import com.erank.yogappl.utils.extensions.addMinuets
import com.google.firebase.messaging.RemoteMessage


class NotificationsHelper(val context: Context) {

    fun <T : BaseData> createNotification(data: T) {

        val drawable = ContextCompat.getDrawable(context, R.drawable.app_icon_yoga)
        val largeIcon = (drawable as BitmapDrawable).bitmap
        val defaultRingtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val channel = context.getString(R.string.default_notification_channel_id)
        val builder =
            NotificationCompat.Builder(context, channel)
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

    fun <T : BaseData> removeNotification(data: T) {
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

    fun notify(message: RemoteMessage) {
        val channel = context.getString(R.string.default_notification_channel_id)
        val fbNotification = message.notification!!
        val color = ContextCompat.getColor(context, R.color.colorPrimary)
        val notification = NotificationCompat.Builder(context, channel)
            .setContentTitle(fbNotification.title)
            .setContentText(fbNotification.body)
            .setSmallIcon(R.drawable.logo_mask)
            .setColor(color)
            .build()

        NotificationManagerCompat.from(context).notify(0, notification)
    }

    fun createDefaultChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val id = context.getString(R.string.default_notification_channel_id)
            val name = context.getString(R.string.default_notification_channel_name)
            val descriptionText = context.getString(R.string.default_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(id, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}