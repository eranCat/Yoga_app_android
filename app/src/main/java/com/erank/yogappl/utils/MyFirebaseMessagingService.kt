package com.erank.yogappl.utils

import com.erank.yogappl.utils.helpers.NotificationsHelper
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        NotificationsHelper(this).notify(message)
    }

    override fun onMessageSent(msg: String) {
        super.onMessageSent(msg)
    }

    override fun onDeletedMessages() {
        super.onDeletedMessages()
    }

    override fun onSendError(p0: String, exception: Exception) {
        super.onSendError(p0, exception)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}