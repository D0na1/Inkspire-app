package com.example.inkspire_app

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import java.util.Calendar


//Service to handle notifications for the Inkspire app.
//This service creates a notification channel, runs in the foreground, and schedules notifications.

class NotificationService : Service() {
//Called when the service is created. Initializes the notification channel,
//starts the service in the foreground, and schedules the initial alarm for notifications.
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(1, createNotification("Service Running", "Inkspire notifications are active."))
        scheduleInitialAlarm()
    }
//creates a notiification channel, this is required fr sending notifications
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "JournalReminderChannel"
            val descriptionText = "Channel for Journal Reminder"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("journalReminder", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(title: String, content: String): Notification {
        return NotificationCompat.Builder(this, "journalReminder")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun scheduleInitialAlarm() {
        // Get the stored notification time from SharedPreferences and the keys are hour and minute
        val sharedPreferences = getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE)
        val hour = sharedPreferences.getInt("hour", 19) //Default hour if not set
        val minute = sharedPreferences.getInt("minute", 0) //Default minute if not set
        scheduleNotification(this, hour, minute)
    }

    //uses alarmManager to schedule the notification at a specified time
    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleNotification(context: Context, hour: Int, minute: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

    }
//This method is used for bound services. as this service isn't meant to be bound,it returns null.
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
