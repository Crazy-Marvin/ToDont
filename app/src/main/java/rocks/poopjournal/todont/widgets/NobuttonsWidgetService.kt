package rocks.poopjournal.todont.widgets

import android.content.Intent
import android.widget.RemoteViewsService

import android.appwidget.AppWidgetManager
import android.content.Context
import android.widget.RemoteViews
import rocks.poopjournal.todont.R
import rocks.poopjournal.todont.model.Habit
import rocks.poopjournal.todont.utils.DatabaseUtils

class NoButtonsWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return NoButtonsWidgetItemFactory(applicationContext)
    }
}



class NoButtonsWidgetItemFactory(private val context: Context) : RemoteViewsService.RemoteViewsFactory {

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
        val views = RemoteViews(context.packageName, R.layout.widget_item_no_buttons)

        // Set text for the habit name, description, and counts
        views.setTextViewText(R.id.tvHabitName, habit.name)
        views.setTextViewText(R.id.tvLabelOfHabit, habit.description)
        views.setTextViewText(R.id.tvDone, habit.countDone.toString())
        views.setTextViewText(R.id.tvAvoided, habit.countAvoided.toString())

        return views
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = position.toLong()

    override fun hasStableIds(): Boolean = true
}