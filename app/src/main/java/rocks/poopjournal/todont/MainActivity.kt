package rocks.poopjournal.todont

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val fragment = FragmentToday();
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment, fragment.javaClass.getSimpleName())
            .commit()
        toolbartext.setText("Today")
        label.visibility= View.VISIBLE
        floatingbtn.visibility= View.VISIBLE
        val actionBar: android.app.ActionBar? = actionBar
        actionBar?.setBackgroundDrawable(resources.getDrawable(R.drawable.mygradient))
        val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_today -> {
                    val fragment = FragmentToday();
                    supportFragmentManager.beginTransaction().replace(R.id.container, fragment, fragment.javaClass.getSimpleName())
                        .commit()
                    toolbartext.setText("Today")
                    label.visibility= View.VISIBLE
                    floatingbtn.visibility= View.VISIBLE
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_log -> {
                    val fragment = FragmentLog()
                    supportFragmentManager.beginTransaction().replace(R.id.container, fragment, fragment.javaClass.getSimpleName())
                        .commit()
                    toolbartext.setText("Log")
                    label.visibility= View.INVISIBLE
                    floatingbtn.visibility= View.INVISIBLE
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }
        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        label.setOnClickListener{
            val intennt= Intent(this,Labels::class.java)
            startActivity(intennt)
            finish()
        }
    }

}