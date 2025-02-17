package rocks.poopjournal.todont.widgets

import android.content.Intent
import android.widget.RemoteViewsService

import android.appwidget.AppWidgetManager
import android.content.Context
import android.widget.RemoteViews
import rocks.poopjournal.todont.R
import rocks.poopjournal.todont.model.Habit
import rocks.poopjournal.todont.utils.DatabaseUtils

class WidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return WidgetItemFactory(applicationContext)
    }
}



class WidgetItemFactory(private val context: Context) : RemoteViewsService.RemoteViewsFactory {

    private val habitList = mutableListOf<Habit>()

    override fun onCreate() {}

    override fun onDataSetChanged() {
        habitList.clear()
        habitList.addAll(DatabaseUtils(context).getAllHabits())
    }

    override fun onDestroy() {
        habitList.clear()
    }

    override fun getCount(): Int = habitList.size

    override fun getViewAt(position: Int): RemoteViews {
        val habit = habitList[position]
        val views = RemoteViews(context.packageName, R.layout.widget_item)

        // Set text for the habit name, description, and counts
        views.setTextViewText(R.id.tvHabitName, habit.name)
        views.setTextViewText(R.id.tvLabelOfHabit, habit.description)
        views.setTextViewText(R.id.tvDone, habit.countDone.toString())
        views.setTextViewText(R.id.tvAvoided, habit.countAvoided.toString())

        // Set up the Done button click using setOnClickFillInIntent
        val incrementDoneIntent = Intent().apply {
            action = "rocks.poopjournal.todont.INCREMENT_DONE_ACTION"
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, position)
            putExtra("HABIT_ID", habit.id)
        }
        views.setOnClickFillInIntent(R.id.btnAddToDone, incrementDoneIntent)

        // Set up the Avoided button click using setOnClickFillInIntent
        val incrementAvoidedIntent = Intent().apply {
            action = "rocks.poopjournal.todont.INCREMENT_AVOIDED_ACTION"
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, position)
            putExtra("HABIT_ID", habit.id)
        }
        views.setOnClickFillInIntent(R.id.btnAddToAvoided, incrementAvoidedIntent)

        return views
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = position.toLong()

    override fun hasStableIds(): Boolean = true
}