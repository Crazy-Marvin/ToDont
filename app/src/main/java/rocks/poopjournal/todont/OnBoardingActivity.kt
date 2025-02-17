package rocks.poopjournal.todont

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import rocks.poopjournal.todont.databinding.ActivityOnBoardingBinding
import rocks.poopjournal.todont.utils.Constants
import rocks.poopjournal.todont.utils.SharedPrefUtils
import rocks.poopjournal.todont.utils.ThemeMode
import rocks.poopjournal.todont.utils.setAppTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class OnBoardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnBoardingBinding
    private lateinit var sharedPrefUtils: SharedPrefUtils
    private val calendar: Calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppTheme(this)
        // Use ViewBinding to inflate the layout
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //actionBar?.setBackgroundDrawable(resources.getDrawable(R.drawable.mygradient))

        // Change the status bar color for devices with Lollipop or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val typedValue = TypedValue()
            theme.resolveAttribute(com.google.android.material.R.attr.backgroundColor, typedValue, true)
            window.statusBarColor = typedValue.data
        }


        // Initialize SharedPrefUtils
        sharedPrefUtils = SharedPrefUtils(this)

        // Set the button click listener using coroutines for better handling of async tasks
        binding.btncontinue.setOnClickListener {
            MainScope().launch {
                handleOnBoardingCompletion()
            }
        }
    }

    /**
     * Handle the on-boarding completion, save necessary data to SharedPreferences and navigate to MainActivity.
     */
    private  fun handleOnBoardingCompletion() {
        // Save settings in SharedPreferences
        sharedPrefUtils.apply {
            putString(SharedPrefUtils.KEY_NIGHT_MODE, ThemeMode.LIGHT_MODE.value)
            putString(SharedPrefUtils.KEY_FIRST_TIME, Constants.NO)
            putString(Constants.INITIAL_DATE_KEY, dateFormat.format(calendar.time))
        }

        // Start MainActivity
        val intent = Intent(this@OnBoardingActivity, MainActivity::class.java)
        startActivity(intent)
    }
}
