package rocks.poopjournal.todont.widgets

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import rocks.poopjournal.todont.MainActivity
import rocks.poopjournal.todont.R
import rocks.poopjournal.todont.utils.Constants
import rocks.poopjournal.todont.utils.DatabaseUtils

class MyAppSmallWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_layout)

            // Set up the Add Icon click
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra(Constants.ADD_NEW_HABIT,true)
            val pendingIntent = TaskStackBuilder.create(context).run {
                addNextIntentWithParentStack(intent)
                getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            }
            views.setOnClickPendingIntent(R.id.add_icon, pendingIntent)

            // Set up the ListView adapter
            val svcIntent = Intent(context, WidgetService::class.java)
            svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            views.setRemoteAdapter(R.id.widget_list_view, svcIntent)

            // Set up the PendingIntent template for list items
            val clickIntent = Intent(context, MyAppSmallWidgetProvider::class.java)
            val clickPendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                clickIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
            views.setPendingIntentTemplate(R.id.widget_list_view, clickPendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {

        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetId = appWidgetManager.getAppWidgetIds(ComponentName(context, MyAppSmallWidgetProvider::class.java))

        val action = intent.action
        val widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)
        val habitId = intent.getIntExtra("HABIT_ID", -1)

        if (widgetId != -1 && habitId != -1) {
            when (action) {
                "rocks.poopjournal.todont.INCREMENT_DONE_ACTION" -> {
                    DatabaseUtils(context).incrementCount(habitId, false)
                }
                "rocks.poopjournal.todont.INCREMENT_AVOIDED_ACTION" -> {
                    DatabaseUtils(context).incrementCount(habitId, true)
                }
            }
            AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_list_view)
        }
        super.onReceive(context, intent)
    }
}