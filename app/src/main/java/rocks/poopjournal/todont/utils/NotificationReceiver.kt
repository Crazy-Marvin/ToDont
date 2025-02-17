package rocks.poopjournal.todont.utils

import android.Manifest
import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import rocks.poopjournal.todont.MainActivity
import rocks.poopjournal.todont.R

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val channel =
            NotificationChannel("Tasks", "Tasks Reminder", NotificationManager.IMPORTANCE_HIGH)
        val manager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        )
        manager?.createNotificationChannel(channel)

        val task = intent.getStringExtra("task") ?: "Your task"
        val id = intent.getIntExtra("task_id", -1)
        val deleteAlarm=intent.getBooleanExtra("delete",false)

        // Check if MainActivity is already running
        val isMainActivityRunning = isAppInForeground(context)


        if(deleteAlarm){
            val databaseHelper = DatabaseUtils(context)
            databaseHelper.deleteAlarm(id)
        }

        // Intent to open the app when clicking the notification
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            putExtra("openLog", true)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }

        val openAppPendingIntent=if(isMainActivityRunning){
               PendingIntent.getActivity(
                context,
                0,
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }else{
             null;
        }

        // Intent for "Avoided" action
        val avoidedIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = "ACTION_AVOIDED"
            putExtra("id", id)
        }
        val avoidedPendingIntent = PendingIntent.getBroadcast(
            context,
            1,
            avoidedIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Intent for "Done" action
        val doneIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = "ACTION_DONE"
            putExtra("id", id)
        }
        val donePendingIntent = PendingIntent.getBroadcast(
            context,
            2,
            doneIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


        val largeIcon = BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher)
        // Build the notification
        val builder = NotificationCompat.Builder(context, "Tasks")
            .setSmallIcon(R.drawable.notification_logo)
            .setLargeIcon(largeIcon)
            .setContentTitle(context.getString(R.string.todon_t_reminder))
            .setContentText(task)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(openAppPendingIntent) // Opens the app
            .addAction(0, "Avoided", avoidedPendingIntent) // "Avoided" button
            .addAction(0, "Done", donePendingIntent) // "Done" button

        // Show the notification
        val notificationManager = NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notificationManager.notify(1, builder.build())
    }

    private fun isAppInForeground(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningAppProcessInfo = activityManager.runningAppProcesses
        for (appProcess in runningAppProcessInfo) {
            if (appProcess.processName == context.packageName) {
                return appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
            }
        }
        return false
    }
}
