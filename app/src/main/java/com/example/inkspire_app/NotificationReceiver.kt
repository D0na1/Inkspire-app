package com.example.inkspire_app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.*

//this class extends BroadcastReceiver to handle broadcast intents.
//It is responsible for sending notifications to remind the user to create or view journal entries.

class NotificationReceiver : BroadcastReceiver() {
//    This method is called when the BroadcastReceiver receives a broadcast intent.
//    It triggers the sending of a notification.
    override fun onReceive(context: Context, intent: Intent) {
        //daily reminder
        sendNotification(context, "Inkspire Reminder", "It's time to create today's entry!")
    }

    private fun sendNotification(context: Context, title: String, content: String) {
        //generates a random notification ID to ensure each notification is unique
        val notificationId = Random().nextInt(1000)
        val notification = NotificationCompat.Builder(context, "journalReminder")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        //uses NotificationManagerCompat to send the notification
        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }
}
