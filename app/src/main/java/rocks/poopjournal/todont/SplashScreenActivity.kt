package rocks.poopjournal.todont

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import rocks.poopjournal.todont.databinding.ActivitySplashScreenBinding
import rocks.poopjournal.todont.utils.Constants
import rocks.poopjournal.todont.utils.SharedPrefUtils
import rocks.poopjournal.todont.utils.ThemeMode
import rocks.poopjournal.todont.utils.setAppTheme


class SplashScreenActivity : AppCompatActivity() {

    private lateinit var sharedPrefUtils: SharedPrefUtils
    private val splashScope = CoroutineScope(Dispatchers.Main)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppTheme(this)
        // Use viewBinding to inflate the layout
        val binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //actionBar?.setBackgroundDrawable(resources.getDrawable(R.drawable.mygradient))

        // Initialize SharedPrefUtils
        sharedPrefUtils = SharedPrefUtils(this)

        // Retrieve first-time check and apply theme if necessary
        val isFirstTime = sharedPrefUtils.getString(SharedPrefUtils.KEY_FIRST_TIME, Constants.NO)

        splashScope.launch {
            delay(2000) // 2-second delay before launching the next activity
            checkStatus(isFirstTime) // Pass the actual value from SharedPreferences or logic
        }
    }

    private fun checkStatus(isFirstTime: String?) {
        val nextActivity = if (isFirstTime == Constants.NO) {
            MainActivity::class.java
        } else {
            OnBoardingActivity::class.java
        }

        startActivity(Intent(this@SplashScreenActivity, nextActivity))
        finishAffinity() // Ensures the splash screen is properly closed
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    override fun onDestroy() {
        super.onDestroy()
        splashScope.cancel() // Cancel coroutine to prevent memory leaks
    }
}
