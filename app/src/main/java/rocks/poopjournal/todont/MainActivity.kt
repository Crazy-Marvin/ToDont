package rocks.poopjournal.todont

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomnavigation.BottomNavigationView
import rocks.poopjournal.todont.Fragments.FragmentLog
import rocks.poopjournal.todont.Fragments.FragmentToday
import rocks.poopjournal.todont.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    // Database controller instance
    var db: Db_Controller? = null
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        // Initialize the database controller
        val fragment = FragmentToday()
        db = Db_Controller(applicationContext, "", null, 2)

        // Replace the current fragment with the 'FragmentToday'
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment, fragment.javaClass.simpleName).commit()


        // Set toolbar text to "Today"
        binding.toolbartext.setText(R.string.today)

        // Make the label and settings views visible
        binding.label.visibility = View.VISIBLE
        binding.settings.visibility = View.VISIBLE

        // Retrieve and apply the night mode setting from the database
        db?.getNightMode()
        // Note: The commented code below appears to have no effect; it may need further review
        // if (Helper.isnightmodeon == "no") {
        binding.label.setBackgroundResource(R.drawable.ic_label_light)
        // }
        // else if (Helper.isnightmodeon == "yes") {
        //     label.setBackgroundResource(R.drawable.ic_label_dark)
        // }

        // Customize the action bar background
        val actionBar: android.app.ActionBar? = actionBar
        actionBar?.setBackgroundDrawable(resources.getDrawable(R.drawable.mygradient))

        // Define a listener for the bottom navigation view
        val mOnNavigationItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.navigation_today -> {
                        // Switch to the 'FragmentToday' when "Today" is selected
                        val fragment = FragmentToday()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.container, fragment, fragment.javaClass.simpleName)
                            .commit()
                        // Update the toolbar text
                        binding.toolbartext.setText(R.string.today)
                        // Make label and settings views visible
                        binding.label.visibility = View.VISIBLE
                        binding.settings.visibility = View.VISIBLE
                        return@OnNavigationItemSelectedListener true
                    }

                    R.id.navigation_log -> {
                        // Switch to the 'FragmentLog' when "Log" is selected
                        val fragment = FragmentLog()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.container, fragment, fragment.javaClass.simpleName)
                            .commit()
                        // Update the toolbar text
                        binding.toolbartext.setText(R.string.log)
                        // Hide the label and settings views
                        binding.label.visibility = View.INVISIBLE
                        binding.settings.visibility = View.INVISIBLE
                        return@OnNavigationItemSelectedListener true
                    }
                }
                false
            }

        // Set the defined listener to the bottom navigation view
        binding.navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        // Define an action when the 'label' view is clicked
        binding.label.setOnClickListener {
            val intent = Intent(this, Labels::class.java)
            startActivity(intent)
            finish()
        }
    }

    // Handle the "Settings" action when a view is clicked
    fun mysettings(view: View) {
        val intent = Intent(this, Settings::class.java)
        startActivity(intent)
        overridePendingTransition(0, 0)
        finish()
    }

    // Handle the back button press
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
