package com.example.inkspire_app

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(navController: NavHostController, darkTheme: MutableState<Boolean>) {
   val context = LocalContext.current
   //access shared preferences to retrieve stored values
   val sharedPreferences = context.getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE)
   val savedHour = sharedPreferences.getInt("hour", 20)
   val savedMinute = sharedPreferences.getInt("minute", 0)
   val time = remember { mutableStateOf(String.format("%02d:%02d", savedHour, savedMinute)) }
   //this is the state to hold the notification enabled status
   val notificationsEnabled = remember { mutableStateOf(sharedPreferences.getBoolean("notifications_enabled", true)) }

   Scaffold(
      topBar = {
         TopAppBar(
            title = { Text("Settings", color = Color.White) },
            colors = TopAppBarDefaults.topAppBarColors(
               containerColor = Color(0xFF6200EA)
            )
         )
      },
      content = { padding ->
         //main content
         Column(
            modifier = Modifier
               .fillMaxSize()
               .padding(padding)
               .padding(16.dp)
               .background(Color.White)
         ) {
            //displays the current notification time
            Text(text = "Notification Time: ${time.value}")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
               //button to modify the notification time
               val calendar = Calendar.getInstance()
               val hour = calendar.get(Calendar.HOUR_OF_DAY)
               val minute = calendar.get(Calendar.MINUTE)
               TimePickerDialog(context, { _, selectedHour, selectedMinute ->
                  time.value = String.format("%02d:%02d", selectedHour, selectedMinute)
                  saveNotificationTime(context, selectedHour, selectedMinute)
                  scheduleNotification(context, selectedHour, selectedMinute)
               }, savedHour, savedMinute, true).show()
            }) {
               Text("Modify Time")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
               //switch to enable or disable the notifications
               verticalAlignment = Alignment.CenterVertically,
               horizontalArrangement = Arrangement.SpaceBetween
            ) {
               Text(text = "Enable Notifications")
               Switch(
                  checked = notificationsEnabled.value,
                  onCheckedChange = { enabled ->
                     if (!enabled) {
                        openAppNotificationSettings(context)
                     }
                     notificationsEnabled.value = enabled
                     saveNotificationEnabled(context, enabled)
                  }
               )
            }
         }
      }
   )
}

//function to save the notification time in shared preferences
private fun saveNotificationTime(context: Context, hour: Int, minute: Int) {
   val sharedPreferences = context.getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE)
   val editor = sharedPreferences.edit()
   editor.putInt("hour", hour)
   editor.putInt("minute", minute)
   editor.apply()
}

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

private fun saveNotificationEnabled(context: Context, enabled: Boolean) {
   val sharedPreferences = context.getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE)
   val editor = sharedPreferences.edit()
   editor.putBoolean("notifications_enabled", enabled)
   editor.apply()
}

//function to open the app notification settings in the device settings
private fun openAppNotificationSettings(context: Context) {
   val intent = Intent().apply {
      action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
      putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
   }
   if (intent.resolveActivity(context.packageManager) != null) {
      context.startActivity(intent)
   } else {
      Toast.makeText(context, "No settings app found", Toast.LENGTH_SHORT).show()
   }
}
