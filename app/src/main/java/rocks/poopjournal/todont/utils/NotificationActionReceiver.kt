package rocks.poopjournal.todont.utils

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import rocks.poopjournal.todont.model.HabitRecord
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val id:Int=intent.getIntExtra("id",-1)
        val dbHelper = DatabaseUtils(context)

        val notificationId = 1 // Same ID as used in NotificationReceiver

        // Get NotificationManager
        val notificationManager =
            ContextCompat.getSystemService(context, NotificationManager::class.java)


        when (intent.action) {
            "ACTION_AVOIDED" -> {
                dbHelper.incrementCount(id,true)
                dbHelper.insertRecord(
                    HabitRecord(
                        date = getTodayDate(),
                        status = HabitStatus.AVOIDED.value,
                        habitId = id
                    )
                )
            }
            "ACTION_DONE" -> {
                dbHelper.incrementCount(id,false)
                dbHelper.insertRecord(
                    HabitRecord(
                        date = getTodayDate(),
                        status = HabitStatus.DONE.value,
                        habitId = id
                    )
                )
            }
        }
        notificationManager?.cancel(notificationId)
    }

    private fun getTodayDate(): String {
        var date: Date = Calendar.getInstance().time
        val dateFormat: SimpleDateFormat = SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault())
        var formattedDate: String = dateFormat.format(date)
        //Calendar.getInstance().timeInMillis.toString()
        return formattedDate
    }
}
