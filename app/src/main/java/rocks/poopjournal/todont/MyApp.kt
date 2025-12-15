package rocks.poopjournal.todont

import android.app.Application
import io.sentry.android.core.SentryAndroid
import rocks.poopjournal.todont.utils.SharedPrefUtils

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val pref = SharedPrefUtils(this)

        if (pref.isMonitorEnabled()) {
            enableSentry()
        } else {
            disableSentry()
        }
    }

    private fun enableSentry() {
        SentryAndroid.init(this) { options ->
            // DSN will be read automatically from manifest
            options.isEnableUserInteractionTracing = true
            options.isAttachScreenshot = true
            options.isAttachViewHierarchy = true
        }
    }

    private fun disableSentry() {
        io.sentry.Sentry.close()
    }
}