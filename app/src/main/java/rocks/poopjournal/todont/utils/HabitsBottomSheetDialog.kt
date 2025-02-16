package rocks.poopjournal.todont.utils

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import rocks.poopjournal.todont.NotificationReceiver
import rocks.poopjournal.todont.R
import rocks.poopjournal.todont.databinding.UpdateLayoutBottomSheetTestBinding
import rocks.poopjournal.todont.model.Alarm
import rocks.poopjournal.todont.model.Habit
import rocks.poopjournal.todont.model.HabitRecord
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HabitsBottomSheetDialog(
    var context: Context, var habit: Habit,
    var position: Int,
    var dbHelper: DatabaseUtils,
    val listener: HabitSheetListener
) {

    val bottomSheet: BottomSheetDialog = BottomSheetDialog(context, R.style.BottomSheetDialogTheme)
    var binding: UpdateLayoutBottomSheetTestBinding = UpdateLayoutBottomSheetTestBinding.inflate(
        LayoutInflater.from(context)
    )


    init {
        bottomSheet.setContentView(binding.root)
        binding.tvHabitName.text = habit.name
        binding.tvHabitDescription.text = habit.description
        binding.tvLabel.text = habit.label?.name
        binding.tvAvoidedCount.text = habit.countAvoided.toString()
        binding.tvDoneCount.text = habit.countDone.toString()

        val alarm=dbHelper.getAlarmById(habit.id)
        alarm?.let {
            val freq= if(it.frequency=="0"){
                context.getString(R.string.once)
            }else if(it.frequency=="1"){
                context.getString(R.string.daily)
            }else{
                context.getString(R.string.weekly)
            }
            val calendar= Calendar.getInstance()
            calendar.timeInMillis=it.alarmTime
            binding.tvNotification.text=String.format(
                Constants.NOTIFICATION_TIME_FORMAT,
                calendar[Calendar.HOUR_OF_DAY],
                calendar[Calendar.MINUTE],
                alarm.frequency
            )
        }

        binding.btnIncrAvoidedCount.setOnClickListener {

            habit.countAvoided++
            dbHelper.updateHabit(habit)
            dbHelper.insertRecord(
                HabitRecord(
                    date = getTodayDate(),
                    status = HabitStatus.AVOIDED.value,
                    habitId = habit.id
                )
            )
            listener.updateCount(habit, position)
            binding.tvAvoidedCount.text = habit.countAvoided.toString()

        }

        binding.btnDecAvoidedCount.setOnClickListener {
            if (habit.countAvoided > 0) {
                habit.countAvoided--
                dbHelper.updateHabit(habit)
                //delete the last row where status is avoided
                dbHelper.deleteHabitRecordWithStatus(habit.id, HabitStatus.AVOIDED.value)
                listener.updateCount(habit, position)
                binding.tvAvoidedCount.text = habit.countAvoided.toString()
            }
        }

        binding.btnIncDoneCount.setOnClickListener()
        {
            habit.countDone++
            dbHelper.updateHabit(habit)
            dbHelper.insertRecord(
                HabitRecord(
                    date = getTodayDate(),
                    status = HabitStatus.DONE.value,
                    habitId = habit.id
                )
            )
            listener.updateCount(habit, position)
            binding.tvDoneCount.text = habit.countDone.toString()

        }
        binding.btnDecDoneCount.setOnClickListener()
        {

            if (habit.countAvoided > 0) {
                habit.countDone--
                dbHelper.updateHabit(habit)
                //delete the last row where status is avoided
                dbHelper.deleteHabitRecordWithStatus(habit.id, HabitStatus.DONE.value)
                listener.updateCount(habit, position)
                binding.tvDoneCount.text = habit.countDone.toString()
            }

        }
        binding.tvNotification.setOnClickListener(){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                if (!notificationManager.areNotificationsEnabled()) {
                    // Request notification permission
                    val intent =
                        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                            .putExtra(
                                Settings.EXTRA_APP_PACKAGE,
                                context.packageName
                            )
                    context.startActivity(intent)
                } else {
                    showTimePickerDialog()
                }
            } else {
                showTimePickerDialog()
            }
        }
        binding.btnDelete.setOnClickListener(){
            habit.id?.let { it1 -> deleteAlarm(it1) }
            dbHelper.deleteHabit(habit.id)
            dbHelper.deleteAllHabitRecords(habit.id)
            listener.deleted(habit,position)
            bottomSheet.dismiss()
        }
        bottomSheet.setOnDismissListener {
            listener.dismissed()
        }
    }

    private fun showTimePickerDialog() {
        // Get current time
        val calendar = Calendar.getInstance()
        val hour = calendar[Calendar.HOUR_OF_DAY]
        val minute = calendar[Calendar.MINUTE]

        // Create and show TimePickerDialog
        val timePickerDialog = TimePickerDialog(
            context,
            { _, hourOfDay, minute -> // Update the calendar with the selected time
                calendar[Calendar.HOUR_OF_DAY] = hourOfDay
                calendar[Calendar.MINUTE] = minute
                // After selecting time, show dialog for repetition frequency
                showFrequencyDialog(calendar, hourOfDay, minute)
            },
            hour,
            minute,
            true // Use 24-hour format
        )
        timePickerDialog.show()
    }

    private fun showFrequencyDialog(calendar: Calendar, hourOfDay: Int, minute: Int) {
        // Options for repetition frequency
        val frequencies = arrayOf(
            context.getString(R.string.once),
            context.getString(R.string.daily),
            context.getString(R.string.weekly)
        )
        val mapFreq = mapOf(
            context.getString(R.string.once) to "0",
            context.getString(R.string.daily) to "1",
            context.getString(R.string.weekly) to "7"
        )

        // Show an AlertDialog to select frequency
        val builder = AlertDialog.Builder(context)
        builder.setTitle(context.getString(R.string.select_repetition_frequency))
        builder.setItems(
            frequencies
        ) { _, which ->
            val frequency = frequencies[which]
            // Update tvNotification with selected time and frequency
            val notificationText = String.format(
                Constants.NOTIFICATION_TIME_FORMAT,
                hourOfDay,
                minute,
                mapFreq.get(frequency)
            )
            binding.tvNotification.text = notificationText

            // Schedule notification based on selected time and frequency
            scheduleNotification(
                habit.id,
                calendar,
                frequency,
                mapFreq.get(frequency)?:""
            )
        }
        builder.show()
    }

    private fun scheduleNotification(
        habitId: Int?,
        calendar: Calendar,
        frequency: String,
        mapFreq: String
    ) {
        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, NotificationReceiver::class.java)
        intent.putExtra("task_id", habitId) // Pass task ID in intent
        intent.putExtra(
            "task",
            habit.name
        )
        val pendingIntent = habitId?.let {
            PendingIntent.getBroadcast(
                context,
                it,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
        }

        val triggerTime = calendar.timeInMillis
        var interval: Long = 0

        // Determine repetition interval based on frequency
        if (frequency == context.getString(R.string.daily)) {
            interval = AlarmManager.INTERVAL_DAY
        } else if (frequency == context.getString(R.string.weekly)) {
            interval = AlarmManager.INTERVAL_DAY * 7
        }

        // Store alarm details in database
        dbHelper.insertOrUpdateAlarm(Alarm(habitId, triggerTime, mapFreq))

        // Set the alarm
        pendingIntent?.let {
            if (interval > 0 ) {
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    interval,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                ) // For "Once" frequency
            }
        }

        Toast.makeText(
            context,
            "Notification scheduled: $frequency",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun deleteAlarm(habitId: Int) {

        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Cancel alarm with habitId
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            habitId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        alarmManager.cancel(pendingIntent)

        // Remove alarm from the database
        dbHelper.deleteAlarm(habitId)

        //Toast.makeText(context, "Notification cancelled", Toast.LENGTH_SHORT).show()
    }

//    private fun updateAlarm(
//        habitId: Int,
//        newCalendar: Calendar,
//        newFrequency: String
//    ) {
//        // First, cancel the existing alarm
//        deleteAlarm(habitId)
//        // Then, set a new alarm with updated time and frequency
//        scheduleNotification(habitId, newCalendar, newFrequency, mapFreq.get(frequency))
//    }

    private fun getTodayDate(): String {
        var date: Date = Calendar.getInstance().time
        val dateFormat: SimpleDateFormat = SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault())
        var formattedDate: String = dateFormat.format(date)
        //Calendar.getInstance().timeInMillis.toString()
        return formattedDate
    }

    fun show() {
        bottomSheet.show()
    }

    interface HabitSheetListener {
        fun updateCount(_habit: Habit, _position: Int)
        fun deleted(_habit: Habit,_position: Int)
        fun dismissed()
    }

}