package rocks.poopjournal.todont

import android.app.Application
import com.rohitss.uceh.UCEHandler

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        UCEHandler.Builder(applicationContext).build()
    }
}