package rocks.poopjournal.todont.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import rocks.poopjournal.todont.R
import rocks.poopjournal.todont.utils.SharedPrefUtils.Companion.KEY_NIGHT_MODE
import rocks.poopjournal.todont.utils.SharedPrefUtils.Companion.PREF_NAME

class SharedPrefUtils(private val context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun save(key: String?, name: String?) {
        val editor = sharedPreferences.edit()
        editor.putString(key, name)
        editor.apply()
    }

    fun get(key: String?): String? {
        return sharedPreferences.getString(key, null)
    }

    fun setBool(key: String?, name: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(key, name)
        editor.apply()
    }

    fun getBool(key: String?): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }
    fun getString(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    fun putString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    fun putBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    fun getInt(key: String, defaultValue: Int): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }

    fun putInt(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
    }

    fun getLong(key: String, defaultValue: Long): Long {
        return sharedPreferences.getLong(key, defaultValue)
    }

    fun putLong(key: String, value: Long) {
        sharedPreferences.edit().putLong(key, value).apply()
    }
    fun getThemeMode():String{
        return sharedPreferences.getString(KEY_NIGHT_MODE,"1").toString()
    }

    fun setThemeMode(mode:String){
        sharedPreferences.edit().putString(KEY_NIGHT_MODE,mode).apply()
    }

    fun setMonitorEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_MONITOR_MODE, enabled).apply()
    }

    fun isMonitorEnabled(): Boolean {
        return sharedPreferences.getBoolean(KEY_MONITOR_MODE, false)
    }


    companion object {
        const val PREF_NAME = "MyPrefs"
        const val KEY_ADD_OR_AVOIDED: String = "AddOrAvoided"
        const val KEY_LOG: String = "Log"
        const val KEY_CONTRIBUTION_VIEW: String = "CONTRIBUTION_VIEW"
        const val KEY_APPEAR_VIEW: String = "APPEAR_VIEW"
        const val KEY_FIRST_TIME = "FirstTime"
        const val KEY_NIGHT_MODE = "NightMode"

        const val KEY_MONITOR_MODE = "MonitorMode"

    }
}

fun Context.getAppTheme():String{
     val sharedPreferences: SharedPreferences =
        this.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    return sharedPreferences.getString(KEY_NIGHT_MODE,"1").toString()
}

 fun setAppTheme(context:Context) {
    // Usage in when block
    when (context.getAppTheme()) {
        ThemeMode.FOLLOW_SYS.value -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }

        ThemeMode.LIGHT_MODE.value-> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            context.setTheme(R.style.Theme_Todon_Light)
        }

        ThemeMode.DARK_MODE.value -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            context.setTheme(R.style.Theme_Todon_Dark)

        }

        ThemeMode.DRACULA.value -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            context.setTheme(R.style.Theme_Todon_Dracula)
        }
        ThemeMode.DRACULA_PRO.value ->{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            context.setTheme(R.style.Theme_Todon_Dracula_Pro)
        }
        ThemeMode.DRACULA_PRO_ALUCARD.value ->{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            context.setTheme(R.style.Theme_Todon_Dracula_Alucard)
        }
        ThemeMode.DRACULA_PRO_BUFFY.value ->{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            context.setTheme(R.style.Theme_Todon_DraculaBlade_Buffy)
        }
        ThemeMode.DRACULA_PRO_BLADE.value ->{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            context.setTheme(R.style.Theme_Todon_DraculaBlade)
        }
        else ->{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }

    }
}
