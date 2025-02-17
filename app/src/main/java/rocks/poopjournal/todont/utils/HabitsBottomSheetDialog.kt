package rocks.poopjournal.todont.utils

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import rocks.poopjournal.todont.R
import rocks.poopjournal.todont.databinding.LayoutHabitBottomSheetBinding
import rocks.poopjournal.todont.model.Alarm
import rocks.poopjournal.todont.model.Habit
import rocks.poopjournal.todont.model.HabitRecord
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import jp.wasabeef.glide.transformations.BlurTransformation
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException

class HabitsBottomSheetDialog(
    var context: Context, var habit: Habit,
    var position: Int,
    var dbHelper: DatabaseUtils,
    val listener: HabitSheetListener
) {

    val bottomSheet: BottomSheetDialog = BottomSheetDialog(context, R.style.BottomSheetDialogTheme)
    var binding: LayoutHabitBottomSheetBinding = LayoutHabitBottomSheetBinding.inflate(
        LayoutInflater.from(context)
    )


    init {
        bottomSheet.setContentView(binding.root)
        binding.tvHabitName.text = habit.name
        binding.tvHabitDescription.text = habit.description
        binding.tvLabel.text = habit.label?.name
        binding.tvAvoidedCount.text = habit.countAvoided.toString()
        binding.tvDoneCount.text = habit.countDone.toString()
        habit.coverImageUri?.let { loadImageWithBlur(it) }

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
                freq
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

            if (habit.countDone > 0) {
                habit.countDone--
                dbHelper.updateHabit(habit)
                //delete the last row where status is avoided
                dbHelper.deleteHabitRecordWithStatus(habit.id, HabitStatus.DONE.value)
                listener.updateCount(habit, position)
                binding.tvDoneCount.text = habit.countDone.toString()
            }

        }
        binding.tvNotification.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                if (!notificationManager.areNotificationsEnabled()) {
                    // Request notification permission for Android 13+
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            context as Activity, // Ensure the context is an Activity
                            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                            1001
                        )
                    } else {
                        showTimePickerDialog()
                    }
                } else {
                    showTimePickerDialog()
                }
            } else {
                showTimePickerDialog()
            }
        }

        binding.btnDelete.setOnClickListener(){
            habit.id?.let { it1 -> deleteAlarm(it1) }
            habit.coverImageUri?.let { it1 -> deleteImageFromInternalStorage(it1) }
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

    // Function to load image with blur effect
    private fun loadImageWithBlur(imageUri: String) {
        // Show both ImageViews
        try {
            val bitmap = readImageFromInternalStorage(imageUri)
            if(bitmap!=null){
                // Load blurred image into background ImageView
                Glide.with(context)
                    .load(bitmap)
                    .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 3))) // Adjust blur radius and sampling
                    .into(binding.ivBlurredBackground)

                // Load normal image into foreground ImageView
                Glide.with(context)
                    .load(bitmap)
                    .into(binding.ivSelectedImage)
                binding.frameSelectedImage.visibility= View.VISIBLE
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            binding.frameSelectedImage.visibility= View.GONE
        }


    }

    private fun deleteImageFromInternalStorage(uriString: String): Boolean {
        val uri = Uri.parse(uriString)
        return try {
            // Convert the URI to a File object
            val file = File(uri.path)
            // Check if the file exists and delete it
            if (file.exists()) {
                file.delete()
            } else {
                false // File does not exist
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun readImageFromInternalStorage(uriString: String): Bitmap? {
        return try {
            // Parse the URI string into a Uri object
            val uri = Uri.parse(uriString)
            // Open an input stream from the saved URI
            val inputStream = FileInputStream(uri.path)
            inputStream.use { stream ->
                // Decode the input stream into a Bitmap
                BitmapFactory.decodeStream(stream)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
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
                frequency
            )
            binding.tvNotification.text = notificationText

            // Schedule notification based on selected time and frequency
            scheduleNotification(
                habit.id,
                calendar,
                frequency,
                mapFreq[frequency] ?:""
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

        val delete:Boolean= mapFreq == "0";


        val intent = Intent(context, NotificationReceiver::class.java)
        intent.putExtra("task_id", habitId) // Pass task ID in intent
        intent.putExtra("delete",delete)
        intent.putExtra(
            "task",
            habit.name
        )
        val pendingIntent = habitId?.let {
            PendingIntent.getBroadcast(
                context,
                it,
                intent,
               PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
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
                alarmManager.set(
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