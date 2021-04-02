package rocks.poopjournal.todont

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import rocks.poopjournal.todont.Fragments.FragmentLog
import rocks.poopjournal.todont.Fragments.FragmentToday


class MainActivity : AppCompatActivity() {
    var db: Db_Controller? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val fragment = FragmentToday();
        db = Db_Controller(applicationContext, "", null, 2)


        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment, fragment.javaClass.getSimpleName())
            .commit()
        toolbartext.setText("Today")
        label.visibility = View.VISIBLE
        settings.visibility = View.VISIBLE
        db?.getNightMode()
        if (Helper.isnightmodeon == "no") {
            label.setBackgroundResource(R.drawable.ic_label_light)
        }
        else if (Helper.isnightmodeon == "yes") {
            label.setBackgroundResource(R.drawable.ic_label_dark)

        }

        val actionBar: android.app.ActionBar? = actionBar
        actionBar?.setBackgroundDrawable(resources.getDrawable(R.drawable.mygradient))
        val mOnNavigationItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.navigation_today -> {
                        val fragment =
                            FragmentToday();
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.container, fragment, fragment.javaClass.getSimpleName())
                            .commit()
                        toolbartext.setText("Today")
                        label.visibility = View.VISIBLE
                        settings.visibility = View.VISIBLE

                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.navigation_log -> {
                        val fragment =
                            FragmentLog()
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.container, fragment, fragment.javaClass.getSimpleName())
                            .commit()
                        toolbartext.setText("Log")
                        label.visibility = View.INVISIBLE
                        settings.visibility = View.INVISIBLE
                        return@OnNavigationItemSelectedListener true
                    }
                }
                false
            }

        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        label.setOnClickListener {
            val intennt = Intent(this, Labels::class.java)
            startActivity(intennt)
            finish()
        }
    }

    //////////////////////////////////////////////////////////////////////////////////
    fun mysettings(view: View) {

        val intennt = Intent(this, Settings::class.java)
            startActivity(intennt)
            overridePendingTransition(0, 0)
            finish()

    }

}

