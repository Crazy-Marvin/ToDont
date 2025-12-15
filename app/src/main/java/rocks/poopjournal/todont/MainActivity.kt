package rocks.poopjournal.todont

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.databinding.DataBindingUtil
import rocks.poopjournal.todont.databinding.ActivityMainBinding
import rocks.poopjournal.todont.fragments.FragmentLog
import rocks.poopjournal.todont.fragments.FragmentToday
import rocks.poopjournal.todont.utils.Constants
import rocks.poopjournal.todont.utils.DatabaseUtils
import rocks.poopjournal.todont.utils.SharedPrefUtils
import rocks.poopjournal.todont.utils.setAppTheme
import smartdevelop.ir.eram.showcaseviewlib.GuideView
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity
import smartdevelop.ir.eram.showcaseviewlib.config.PointerType

class MainActivity : AppCompatActivity() {
    // Database controller instance
    private var dbHelper: DatabaseUtils? = null
    private lateinit var binding: ActivityMainBinding
    private var prefUtils: SharedPrefUtils? = null
    private lateinit var fragmentToday: FragmentToday

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setTheme(R.style.Theme_Todon_Dracula)
        setAppTheme(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        WindowCompat.setDecorFitsSystemWindows(window, false);
        // Apply insets padding to avoid notch / status bar / nav bar overlap
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            view.updatePadding(
                left = systemBars.left,
                top = systemBars.top,
                right = systemBars.right,
            )

            insets
        }

        window.statusBarColor = Color.TRANSPARENT

        WindowCompat.getInsetsController(window, window.decorView).apply {
            val isDark =
                (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
                        Configuration.UI_MODE_NIGHT_YES
            isAppearanceLightStatusBars = !isDark
        }

        // Initialize the database controller
        prefUtils = SharedPrefUtils(this)
        dbHelper = DatabaseUtils(this)

        // Set toolbar text to "Today"
        binding.toolbartext.setText(R.string.today)

        // Make the label and settings views visible
        binding.label.visibility = View.VISIBLE
        binding.settings.visibility = View.VISIBLE

        // Retrieve and apply the night mode setting from the database
        //db?.getNightMode()

        binding.floatingbtn.setOnClickListener {
            if (!prefUtils!!.getBool("plus")) {
                showcaseFab(it)
            }else{
                fragmentToday.addNewHabit()
            }
        }


        // Customize the action bar background
        //actionBar?.setBackgroundDrawable(resources.getDrawable(R.drawable.mygradient))

        // Define a listener for the bottom navigation view
        binding.navigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_today -> {
                    // Switch to the 'FragmentToday' when "Today" is selected
                    replaceFragment(fragmentToday)
                    showFAB()
                    binding.toolbartext.setText(R.string.today)
                    binding.label.visibility = View.VISIBLE
                    binding.settings.visibility = View.VISIBLE
                    true
                }

                R.id.navigation_log -> {
                    // Show tutorial guide for first-time usage
                    if (prefUtils?.getBool(SharedPrefUtils.KEY_LOG) != true && !intent.getBooleanExtra(
                            "openLog",
                            false
                        )
                    ) {
                        showLogGuide()
                    } else {
                        // Switch to the 'FragmentLog' when "Log" is selected
                        replaceFragment(FragmentLog())
                        hideFAB()
                        binding.toolbartext.setText(R.string.log)
                        binding.label.visibility = View.INVISIBLE
                        binding.settings.visibility = View.INVISIBLE
                    }
                    true
                }

                else -> false
            }
        }

        // Initialize the default fragment
        if (intent.getBooleanExtra("openLog", false)) {
            binding.navigationView.selectedItemId = R.id.navigation_log
        } else if (intent.getBooleanExtra(Constants.ADD_NEW_HABIT, false)) {
            fragmentToday=FragmentToday.newInstance(true)
            replaceFragment(fragmentToday)
        } else {
            fragmentToday=FragmentToday.newInstance(false)
            replaceFragment(fragmentToday)
        }

        // Set label click action
        binding.label.setOnClickListener {
            val intent = Intent(this, LabelsActivity::class.java)
            startActivity(intent)
            //finish()
        }
    }

    private fun showLogGuide() {
        GuideView.Builder(this@MainActivity)
            .setContentText(getString(R.string.view_your_stats))
            .setTargetView(binding.navigationView.findViewById(R.id.navigation_log))
            .setDismissType(DismissType.anywhere)
            .setPointerType(PointerType.arrow)
            .setGravity(Gravity.center)
            .setGuideListener {
                prefUtils?.setBool(SharedPrefUtils.KEY_LOG, true)
            }
            .build()
            .show()
    }

    private fun showcaseFab(view:View) {
        val guideView = GuideView.Builder(this)
            .setContentText(getString(R.string.to_start_off_put_down_a_bad_habit))
            .setTargetView(view)
            .setDismissType(DismissType.anywhere)
            .setPointerType(PointerType.arrow)
            .setGravity(Gravity.center)
            .setGuideListener {
                prefUtils!!.setBool(
                    "plus",
                    true
                )
            }
        guideView.build().show()
    }

    fun hideFAB() {
        binding.floatingbtn.visibility = View.INVISIBLE
    }

    fun showFAB() {
        binding.floatingbtn.visibility = View.VISIBLE
    }

    private fun replaceFragment(fragment: androidx.fragment.app.Fragment) {
        val fragmentTag = fragment.javaClass.simpleName
        // Replace the fragment and add it to the back stack
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment, fragmentTag)
            .commit()
    }

    private val settingsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                recreate() // Recreate the activity if the result is OK
            }
        }

    // Handle the "Settings" action when a view is clicked
    fun mySettings(view: View) {
        val intent = Intent(this, Settings::class.java)
        settingsLauncher.launch(intent)
        overridePendingTransition(0, 0)
        //finish()
    }

    // Handle the back button press
    override fun onBackPressed() {
//        // Customize the back button behavior
//        if (supportFragmentManager.backStackEntryCount > 0) {
//            // If fragments are in the back stack, pop the latest one
//            supportFragmentManager.popBackStack()
//        } else {
            // If no fragments are in the back stack, exit the activity
            super.onBackPressed()
            //finish()
      //  }
    }
}
