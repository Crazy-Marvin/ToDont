package rocks.poopjournal.todont.utils

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import rocks.poopjournal.todont.model.Habit

import rocks.poopjournal.todont.model.Alarm
import rocks.poopjournal.todont.model.Label
import rocks.poopjournal.todont.model.HabitRecord
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*
import kotlin.collections.ArrayList

class DatabaseUtils(val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_ALARM)
        db.execSQL(CREATE_TABLE_HABIT)
        db.execSQL(CREATE_TABLE_LABEL)
        db.execSQL(CREATE_TABLE_RECORD)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ALARM")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_HABIT")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_LABEL")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_RECORD")
        onCreate(db)
    }

    // CREATE OPERATIONS
    fun insertOrUpdateAlarm(alarm: Alarm): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ALARM_TIME, alarm.alarmTime)
            put(COLUMN_FREQUENCY, alarm.frequency)
            put(COLUMN_HABIT_ID, alarm.habitId)
        }
        return db.replace(TABLE_ALARM, null, values)
    }

    fun insertHabit(habit: Habit): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_DATE, habit.date)
            put(COLUMN_HABIT, habit.name)
            put(COLUMN_DESCRIPTION, habit.description)
            put(COLUMN_COUNT_AVOIDED, habit.countAvoided)
            put(COLUMN_COUNT_DONE, habit.countDone)
            put(COLUMN_LABEL_ID, habit.labelId)
        }
        var count=0L
        try {
            count=db.insert(TABLE_HABIT, null, values)
        }catch (ex:Exception){
            ex.printStackTrace()
        }
        return count
    }

    fun insertLabel(label: Label): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, label.name)
        }
        return db.insert(TABLE_LABEL, null, values)
    }

    fun insertRecord(record: HabitRecord): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_DATE, record.date)
            put(COLUMN_STATUS, record.status)
            put(COLUMN_HABIT_ID, record.habitId)
        }
        return db.insert(TABLE_RECORD, null, values)
    }

    fun closeDatabase(){
        val db = writableDatabase
        db.close()
    }


    fun getLabelsCount(): Int {
        var count = 0
        val db = readableDatabase
        val cursor: Cursor? = null
        try {
            val query = "SELECT COUNT(*) FROM $TABLE_LABEL"
            val cursor = db.rawQuery(query, null)
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0) // Get the count from the first column
            }
        } catch (e: Exception) {
            e.printStackTrace() // Log exception if any
        } finally {
            cursor?.close() // Ensure cursor is closed
            db.close()      // Ensure database is closed
        }
        return count
    }

    fun getAllHabits(): ArrayList<Habit> {
        val habitsList = ArrayList<Habit>()
        val db = readableDatabase
        var cursor: Cursor? = null

        try {
            // Query to fetch all habits
            cursor = db.query(TABLE_HABIT, null, null, null, null, null, null)

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    // Extract habit data
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                    val date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))
                    val habit = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HABIT))
                    val description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                    val countAvoided = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COUNT_AVOIDED))
                    val countDone = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COUNT_DONE))
                    val labelId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LABEL_ID))

                    // Create a habit object
                    val habitObj = Habit(
                        id = id,
                        date = date,
                        name = habit,
                        description = description,
                        countAvoided = countAvoided,
                        countDone = countDone,
                        labelId = labelId
                    )

                    // Fetch the associated label and set it
                    habitObj.label = getLabelById(labelId)
                    habitsList.add(habitObj)
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            e.printStackTrace() // Log the exception for debugging
        } finally {
            cursor?.close() // Close the cursor
            db.close()      // Close the database
        }

        return habitsList
    }

    // Helper function to fetch a label by its ID
    private fun getLabelById(labelId: Int): Label? {
        val db = readableDatabase
        var label: Label? = null
        var cursor: Cursor? = null

        try {
            // Query to fetch the label by ID
            val selection = "$COLUMN_ID = ?"
            val selectionArgs = arrayOf(labelId.toString())
            cursor = db.query(TABLE_LABEL, null, selection, selectionArgs, null, null, null)

            if (cursor != null && cursor.moveToFirst()) {
                // Extract label data
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))

                label = Label(
                    labelId = id,
                    name = name
                )
            }
        } catch (e: Exception) {
            e.printStackTrace() // Log the exception for debugging
        } finally {
            cursor?.close() // Close the cursor
            db.close()      // Close the database
        }

        return label
    }




    private fun countHabitsRelatedToLabel(labelId: Int?): Int {
        if (labelId == null) return 0 // If the label ID is null, return 0

        var count = 0
        val db = this.readableDatabase
        val query = "SELECT COUNT(*) FROM $TABLE_HABIT WHERE $COLUMN_LABEL_ID = ?"

        val cursor = db.rawQuery(query, arrayOf(labelId.toString()))
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        db.close()

        return count
    }



    // READ OPERATIONS
    fun getAllAlarms(): List<Alarm> {
        val alarms = mutableListOf<Alarm>()
        val db = readableDatabase
        val cursor = db.query(TABLE_ALARM, null, null, null, null, null, null)
        with(cursor) {
            while (moveToNext()) {
                alarms.add(
                    Alarm(
                        getInt(getColumnIndexOrThrow(COLUMN_HABIT_ID)),
                        getLong(getColumnIndexOrThrow(COLUMN_ALARM_TIME)),
                        getString(getColumnIndexOrThrow(COLUMN_FREQUENCY)),
                    )
                )
            }
        }
        cursor.close()
        return alarms
    }

    fun getHabitById(id: Int): Habit? {
        val db = readableDatabase
        val cursor = db.query(TABLE_HABIT, null, "$COLUMN_ID = ?", arrayOf(id.toString()), null, null, null)
        return if (cursor.moveToFirst()) {
            Habit(
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HABIT)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COUNT_AVOIDED)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COUNT_DONE)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LABEL_ID))
            )
        } else null
    }

    fun getAllLabels(): ArrayList<Label> {
        val labels = ArrayList<Label>()
        val db = readableDatabase
        val cursor = db.query(TABLE_LABEL, null, null, null, null, null, null)
        with(cursor) {
            while (moveToNext()) {
                val label=Label(
                    getInt(getColumnIndexOrThrow(COLUMN_ID)),
                    getString(getColumnIndexOrThrow(COLUMN_NAME))
                )
                label.habitCount=countHabitsRelatedToLabel(label.labelId)
                labels.add(
                    label
                )
            }
        }
        cursor.close()
        return labels
    }

    // UPDATE OPERATIONS
    fun updateAlarm(alarm: Alarm): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ALARM_TIME, alarm.alarmTime)
            put(COLUMN_FREQUENCY, alarm.frequency)
            put(COLUMN_HABIT_ID, alarm.habitId)
        }
        return db.update(TABLE_ALARM, values, "$COLUMN_HABIT_ID = ?", arrayOf(alarm.habitId.toString()))
    }

    fun updateHabit(habit: Habit): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_DATE, habit.date)
            put(COLUMN_HABIT, habit.name)
            put(COLUMN_DESCRIPTION, habit.description)
            put(COLUMN_COUNT_AVOIDED, habit.countAvoided)
            put(COLUMN_COUNT_DONE, habit.countDone)
            put(COLUMN_LABEL_ID, habit.labelId)
        }
        return db.update(TABLE_HABIT, values, "$COLUMN_ID = ?", arrayOf(habit.id.toString()))
    }

    // DELETE OPERATIONS
    fun deleteAlarm(habitId: Int): Int {
        val db = writableDatabase
        return db.delete(TABLE_ALARM, "$COLUMN_HABIT_ID = ?", arrayOf(habitId.toString()))
    }

    fun deleteHabit(id: Int?): Int {
        val db = writableDatabase
        return db.delete(TABLE_HABIT, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }
    fun deleteHabitRecordWithStatus(habitId: Int?, status: String): Int {
        val db = writableDatabase
        val selection = "$COLUMN_HABIT_ID = ? AND $COLUMN_STATUS = ?"
        val selectionArgs = arrayOf(habitId.toString(), status)
        return db.delete(TABLE_RECORD, selection, selectionArgs)
    }


    fun deleteLabel(id: Int?):Int {
        val db = writableDatabase
        return db.delete(TABLE_LABEL, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    fun deleteAllHabitRecords(id: Int?):Int {
        if (id == null) return -1// If the habit ID is null, return immediately

        val db = writableDatabase
        var rowsDeleted=-1
        try {
            // Delete all records related to the provided habit ID
            rowsDeleted = db.delete(TABLE_RECORD, "$COLUMN_HABIT_ID = ?", arrayOf(id.toString()))


        } catch (e: Exception) {
            e.printStackTrace() // Log the exception for debugging
        } finally {
            db.close() // Ensure database is closed
        }
        return rowsDeleted;
    }

    fun getAlarmById(habitId: Int?): Alarm? {
        if (habitId == null) return null // Return null if the habitId is null

        val db = readableDatabase
        var alarm: Alarm? = null

        val cursor = db.query(
            TABLE_ALARM, // The table to query
            null, // All columns
            "$COLUMN_HABIT_ID = ?", // The WHERE clause
            arrayOf(habitId.toString()), // The values for the WHERE clause
            null, // GROUP BY
            null, // HAVING
            null // ORDER BY
        )

        if (cursor != null && cursor.moveToFirst()) {
            // Assuming Alarm class has a constructor or method to map data from cursor
            alarm = Alarm(
                habitId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_HABIT_ID)),
                frequency = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FREQUENCY)),
                alarmTime = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ALARM_TIME)),
            )
        }

        cursor.close() // Always close the cursor when done

        return alarm // Return the alarm object or null if no match was found
    }

    fun getAvoidedHabits(): ArrayList<Habit> {
        return getHabits(COLUMN_COUNT_AVOIDED)
    }
    fun getDoneHabits(): ArrayList<Habit> {
        return getHabits(COLUMN_COUNT_DONE)
    }

    private fun getHabits(column:String):ArrayList<Habit>{
        val habitsList = ArrayList<Habit>()
        val db = readableDatabase
        var cursor: Cursor? = null

        try {
            // Query to fetch all habits where countDone is greater than 0
            val selection = "$column > ?"
            val selectionArgs = arrayOf("0")

            cursor = db.query(
                TABLE_HABIT,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
            )

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    // Extract habit data
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                    val date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))
                    val habit = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HABIT))
                    val description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                    val countAvoided = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COUNT_AVOIDED))
                    val countDone = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COUNT_DONE))
                    val labelId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LABEL_ID))

                    // Create a habit object
                    val habitObj = Habit(
                        id = id,
                        date = date,
                        name = habit,
                        description = description,
                        countAvoided = countAvoided,
                        countDone = countDone,
                        labelId = labelId
                    )

                    // Fetch the associated label and set it
                    habitObj.label = getLabelById(labelId)
                    habitsList.add(habitObj)
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            e.printStackTrace() // Log the exception for debugging
        } finally {
            cursor?.close() // Close the cursor
            db.close()      // Close the database
        }

        return habitsList
    }

    fun getHabitsCount(): Int {
        val db = this.readableDatabase
        val query = "SELECT COUNT(*) FROM $TABLE_HABIT"

        db.use {
            val cursor = db.rawQuery(query, null)
            cursor.use {
                if (cursor.moveToFirst()) {
                    return cursor.getInt(0)
                }
            }
        }
        return 0
    }

    fun getRecordsByDateAndStatus(date: String, status: String):ArrayList<String> {
        val records = ArrayList<String>()
        val db = this.readableDatabase
        val query = "SELECT $COLUMN_HABIT_ID FROM Record WHERE date = ? AND status = ?"

        db.use {
            val cursor = db.rawQuery(query, arrayOf(date, status))
            cursor.use {
                if (cursor.moveToFirst()) {
                    do {
                        val id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HABIT_ID))

                        // Create and add the record object to the list
                        if(!records.contains(id)){
                            records.add(id)
                        }

                    } while (cursor.moveToNext())
                }
            }
        }
        return records
    }


    private fun getMostFrequentDataInRange(status: String, startDate: String, endDate: String): String {
        val dataList = ArrayList<String>()
        val db = this.readableDatabase
        val query = "SELECT $COLUMN_HABIT_ID FROM $TABLE_RECORD WHERE date BETWEEN ? AND ? AND status = ?"

        db.use {
            val cursor = db.rawQuery(query, arrayOf(startDate, endDate,status))
            cursor.use {
                while (cursor.moveToNext()) {
                    dataList.add(cursor.getString(cursor.getColumnIndexOrThrow("$COLUMN_HABIT_ID")))
                }
            }
        }

        // Find the most frequent item
        val frequencyMap = dataList.groupingBy { it }.eachCount()
        val mostFrequentData = frequencyMap.maxByOrNull { it.value }?.key

        return mostFrequentData ?: ""
    }
    private fun getDataInRange(status: String, startDate: String, endDate: String): ArrayList<Habit> {
        val dataList = ArrayList<Habit>()
        val db = this.readableDatabase
        val query = "SELECT $COLUMN_HABIT_ID, $COLUMN_DATE FROM $TABLE_RECORD WHERE date BETWEEN ? AND ? AND status = ?"

        db.use {
            val cursor = db.rawQuery(query, arrayOf(startDate, endDate,status))
            cursor.use {
                while (cursor.moveToNext()) {
                    val id=cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HABIT_ID))
                    val date=cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))
                    val habit=Habit(id.toInt(),date,"","",0,0,0)
                    if(!dataList.contains(habit)){
                        dataList.add(habit)
                    }
                }
            }
        }

        return dataList
    }

    // Wrapper methods for each specific use case
    fun getWeeklyDoneRecord(startDate: String, endDate: String): String {
        return getMostFrequentDataInRange(HabitStatus.DONE.value, startDate, endDate)
    }
    fun getWeeklyDoneRecordList(startDate: String, endDate: String):ArrayList<Habit>{
        return getDataInRange(HabitStatus.DONE.value, startDate, endDate)
    }

    fun getWeeklyAvoidedRecord(startDate: String, endDate: String): String {
        return getMostFrequentDataInRange(HabitStatus.AVOIDED.value, startDate, endDate)
    }
    fun getWeeklyAvoidedRecordList(startDate: String, endDate: String): ArrayList<Habit> {
        return getDataInRange(HabitStatus.AVOIDED.value, startDate, endDate)
    }

    fun getMonthlyDoneData(startDate: String, endDate: String): String {
        return getMostFrequentDataInRange(HabitStatus.DONE.value, startDate, endDate)
    }
    fun getMonthlyDoneDataList(startDate: String, endDate: String): ArrayList<Habit> {
        return getDataInRange(HabitStatus.DONE.value, startDate, endDate)
    }

    fun getMonthlyAvoidedData(startDate: String, endDate: String): String {
        return getMostFrequentDataInRange(HabitStatus.AVOIDED.value, startDate, endDate)
    }
    fun getMonthlyAvoidedDataList(startDate: String, endDate: String): ArrayList<Habit> {
        return getDataInRange(HabitStatus.AVOIDED.value, startDate, endDate)
    }

    fun getYearlyDoneData(startDate: String, endDate: String): String {
        return getMostFrequentDataInRange(HabitStatus.DONE.value, startDate, endDate)
    }
    fun getYearlyDoneDataList(startDate: String, endDate: String): ArrayList<Habit> {
        return getDataInRange(HabitStatus.DONE.value, startDate, endDate)
    }

    fun getYearlyAvoidedData(startDate: String, endDate: String): String {
        return getMostFrequentDataInRange(HabitStatus.AVOIDED.value, startDate, endDate)
    }
    fun getYearlyAvoidedDataList(startDate: String, endDate: String): ArrayList<Habit> {
        return getDataInRange(HabitStatus.AVOIDED.value, startDate, endDate)
    }

    fun deleteCurrentDatabase():Boolean {
        return try {
            context.deleteDatabase(DATABASE_NAME)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun getHabitsWithLabelId(labelId: Int?): ArrayList<Habit> {

        var habitsList = ArrayList<Habit> ()
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_HABIT WHERE $COLUMN_LABEL_ID = ?"

        val cursor = db.rawQuery(query, arrayOf(labelId.toString()))
        if (cursor.moveToFirst()) {
            do{
                // Extract habit data
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))
                val habit = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HABIT))
                val description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                val countAvoided = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COUNT_AVOIDED))
                val countDone = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COUNT_DONE))
                val labelId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LABEL_ID))

                // Create a habit object
                val habitObj = Habit(
                    id = id,
                    date = date,
                    name = habit,
                    description = description,
                    countAvoided = countAvoided,
                    countDone = countDone,
                    labelId = labelId
                )
                habitsList.add(habitObj)
            }while (cursor.moveToNext())

        }
        cursor.close()
        db.close()

        return habitsList
    }


    companion object {
        const val DATABASE_NAME = "todont.sqlite"
        const val DATABASE_VERSION = 1

        const val TABLE_ALARM = "Alarm"
        const val TABLE_HABIT = "Habit"
        const val TABLE_LABEL = "Label"
        const val TABLE_RECORD = "Record"

        const val COLUMN_ID = "id"
        const val COLUMN_ALARM_TIME = "alarm_time"
        const val COLUMN_FREQUENCY = "frequency"
        const val COLUMN_HABIT_ID = "habit_id"

        const val COLUMN_DATE = "date"
        const val COLUMN_HABIT = "habit"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_COUNT_AVOIDED = "count_avoided"
        const val COLUMN_COUNT_DONE = "count_done"
        const val COLUMN_LABEL_ID = "label_id"

        const val COLUMN_NAME = "name"

        const val COLUMN_STATUS = "status"

        const val CREATE_TABLE_ALARM = "CREATE TABLE $TABLE_ALARM ($COLUMN_HABIT_ID INTEGER PRIMARY KEY, $COLUMN_ALARM_TIME INTEGER, $COLUMN_FREQUENCY TEXT)"

        const val CREATE_TABLE_HABIT = "CREATE TABLE $TABLE_HABIT ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_DATE TEXT, $COLUMN_HABIT TEXT, $COLUMN_DESCRIPTION TEXT, $COLUMN_COUNT_AVOIDED INTEGER, $COLUMN_COUNT_DONE INTEGER, $COLUMN_LABEL_ID INTEGER)"

        const val CREATE_TABLE_LABEL = "CREATE TABLE $TABLE_LABEL ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_NAME TEXT)"

        const val CREATE_TABLE_RECORD = "CREATE TABLE $TABLE_RECORD ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_DATE TEXT, $COLUMN_STATUS TEXT, $COLUMN_HABIT_ID INTEGER)"

        const val TAG="TO_DON'T"

        fun copyDatabaseToDownloads(context: Context, dbName: String?) {
            try {
                // Get the database file from the app's private directory
                val dbFile = context.getDatabasePath(dbName)

                // For Android 10 (API 29) and above, use the MediaStore API to write to Downloads folder
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    saveFileToDownloadsUsingMediaStore(context, dbFile)
                } else {
                    // For Android 9 (API 28) and below, use traditional file writing with WRITE_EXTERNAL_STORAGE permission
                    val downloadsDir =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    val destFile = File(downloadsDir, dbFile.name)
                    copyFile(dbFile, destFile)
                    Toast.makeText(
                        context,
                        "Database copied to: " + destFile.absolutePath,
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Failed to copy database: " + e.message, Toast.LENGTH_LONG)
                    .show()
            }
        }

        // Method for copying the file for Android 10 and above using MediaStore API
        private fun saveFileToDownloadsUsingMediaStore(context: Context, sourceFile: File) {
            val contentResolver = context.contentResolver
            val values = ContentValues()

            // Set up the details for the file in MediaStore
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, sourceFile.name)
            values.put(MediaStore.MediaColumns.MIME_TYPE, "application/octet-stream")
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)

            // Insert the file details to MediaStore, getting the URI
            val fileUri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)

            if (fileUri != null) {
                try {
                    contentResolver.openOutputStream(fileUri).use { outputStream ->
                        FileInputStream(sourceFile).use { inputStream ->

                            // Copy the data from the source file to the destination stream
                            val buffer = ByteArray(1024)
                            var length: Int
                            while ((inputStream.read(buffer).also { length = it }) > 0) {
                                outputStream!!.write(buffer, 0, length)
                            }
                            Toast.makeText(
                                context,
                                "Database copied to Downloads folder",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(context, "Failed to save file: " + e.message, Toast.LENGTH_LONG)
                        .show()
                }
            }
        }

        // Method for copying the file in older Android versions
        @Throws(Exception::class)
        private fun copyFile(sourceFile: File, destFile: File) {
            var inputStream: FileInputStream? = null
            var outputStream: OutputStream? = null

            try {
                inputStream = FileInputStream(sourceFile)
                outputStream = FileOutputStream(destFile)

                val buffer = ByteArray(1024)
                var length: Int
                while ((inputStream.read(buffer).also { length = it }) > 0) {
                    outputStream.write(buffer, 0, length)
                }
            } finally {
                outputStream?.flush()
                inputStream?.close()
                outputStream?.close()
            }
        }

        fun deleteCurrentDatabase(context: Context) {
            // Get the path of the current database
            val currentDatabase = context.getDatabasePath(DATABASE_NAME)

            // Check if the current database exists
            if (currentDatabase.exists()) {
                // Delete the existing database
                if (currentDatabase.delete()) {
                    Log.d(TAG, "Existing database deleted successfully.")
                } else {
                    Log.e(TAG, "Failed to delete existing database.")
                    return  // Exit if unable to delete
                }
            }
        }

        fun restoreDatabase(context: Context) {
            val currentDatabase = context.getDatabasePath(DATABASE_NAME)
            deleteCurrentDatabase(context)

            // Now proceed to restore the new database from the backup file
            val backupFile = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                DATABASE_NAME
            )

            if (!backupFile.exists()) {
                Log.e(TAG, "Backup file does not exist.")
                return  // Exit if backup file is not found
            }

            // Copy the backup file to the database location
            try {
                copyFile(backupFile, currentDatabase)
                Log.d(TAG, "Database restored successfully.")
            } catch (e: IOException) {
                Log.e(TAG, "Failed to restore database: " + e.message)
            }
        }

    }
}

