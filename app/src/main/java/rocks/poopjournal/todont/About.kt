package rocks.poopjournal.todont

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.View.OnTouchListener
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import rocks.poopjournal.todont.utils.SharedPrefUtils
import rocks.poopjournal.todont.utils.setAppTheme
import smartdevelop.ir.eram.showcaseviewlib.GuideView
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity
import smartdevelop.ir.eram.showcaseviewlib.config.PointerType

class About : AppCompatActivity() {
    var version: TextView? = null
    private var contributionView: LinearLayout? = null
    private var prefUtils: SharedPrefUtils? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setAppTheme(this)
        setContentView(R.layout.activity_about)
        //actionBar?.setBackgroundDrawable(resources.getDrawable(R.drawable.mygradient))


        contributionView = findViewById(R.id.contributionView)
        version = findViewById(R.id.versiontext)
        version?.setText(BuildConfig.VERSION_NAME + " Beta ")

        prefUtils = SharedPrefUtils(this)
        contributionView?.setOnTouchListener(OnTouchListener { view, motionEvent ->
            if (!prefUtils!!.getBool(SharedPrefUtils.KEY_CONTRIBUTION_VIEW)) {
                val guideView = GuideView.Builder(this@About)
                    .setContentText(this@About.resources.getString(R.string.help_make_to_don_t_better))
                    .setTargetView(contributionView)
                    .setDismissType(DismissType.anywhere)
                    .setPointerType(PointerType.arrow)
                    .setGravity(Gravity.center)
                    .setGuideListener {
                        prefUtils!!.setBool(
                            SharedPrefUtils.KEY_CONTRIBUTION_VIEW,
                            true
                        )
                    }
                guideView.build().show()
                return@OnTouchListener true
            }
            false
        })

    }

    fun contact_codeaquaria(view: View) {
        when (view.id) {
            R.id.btnmail_codeaquaria -> {
                val mailto = "mailto:codeaquaria20@gmail.com"
                val emailIntent = Intent(Intent.ACTION_SENDTO)
                emailIntent.setData(Uri.parse(mailto))
                try {
                    startActivity(emailIntent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(this, "    Error to open Email    ", Toast.LENGTH_SHORT).show()
                }
            }

            R.id.btngit_codeaquaria -> {
                val uri =
                    Uri.parse("https://github.com/arafaatqureshi") // missing 'http://' will cause crashed
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }

            R.id.btntwitter_codeaquaria -> {
                val ui =
                    Uri.parse("https://www.facebook.com/Code-Aquaria-109834144196326") // missing 'http://' will cause crashed
                val it = Intent(Intent.ACTION_VIEW, ui)
                startActivity(it)
            }
        }
    }

    fun contact_codeaquariatar(view: View) {
        when (view.id) {
            R.id.btnmail_tarik -> {
                val mailto = "mailto:imamtariq7@gmail.com"
                val emailIntent = Intent(Intent.ACTION_SENDTO)
                emailIntent.setData(Uri.parse(mailto))
                try {
                    startActivity(emailIntent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(this, "    Error to open Email    ", Toast.LENGTH_SHORT).show()
                }
            }

            R.id.btngit_tarik -> {
                val uri =
                    Uri.parse("https://github.com/theftzoku") // missing 'http://' will cause crashed
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }

            R.id.btntwitter_tarik -> {
                val ui =
                    Uri.parse("https://www.facebook.com/Code-Aquaria-109834144196326") // missing 'http://' will cause crashed
                val it = Intent(Intent.ACTION_VIEW, ui)
                startActivity(it)
            }
        }
    }

    fun contact_marvin(view: View) {
        when (view.id) {
            R.id.btnmail_crazymarvin -> {
                val mailto = "mailto:marvin@poopjournal.rocks"
                val emailIntent = Intent(Intent.ACTION_SENDTO)
                emailIntent.setData(Uri.parse(mailto))
                try {
                    startActivity(emailIntent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(this, "    Error to open Email    ", Toast.LENGTH_SHORT).show()
                }
            }

            R.id.btngit_crazymarvin -> {
                val uri =
                    Uri.parse("https://github.com/Crazy-Marvin") // missing 'http://' will cause crashed
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }

            R.id.btntwitter_crazymarvin -> {
                val u =
                    Uri.parse("https://twitter.com/CrazyMarvinApps") // missing 'http://' will cause crashed
                val i = Intent(Intent.ACTION_VIEW, u)
                startActivity(i)
            }
        }
    }

    fun translate(view: View?) {
        val u = Uri.parse("https://hosted.weblate.org/projects/todont/")
        val i = Intent(Intent.ACTION_VIEW, u)
        startActivity(i)
    }

    fun report(view: View?) {
        val u = Uri.parse("https://github.com/Crazy-Marvin/ToDont/issues")
        val i = Intent(Intent.ACTION_VIEW, u)
        startActivity(i)
    }

    fun viewsource(view: View?) {
        val u = Uri.parse("https://github.com/Crazy-Marvin/ToDont")
        val i = Intent(Intent.ACTION_VIEW, u)
        startActivity(i)
    }

    fun back(view: View?) {
//        val i = Intent(this@About, Settings::class.java)
//        finishAffinity()
//        startActivity(i)
        finish()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    fun jetpack(view: View?) {
        val u = Uri.parse("https://developer.android.com/jetpack")
        val i = Intent(Intent.ACTION_VIEW, u)
        startActivity(i)
    }

    fun logoclicked(view: View?) {
        val u = Uri.parse("https://crazymarvin.com/todont")
        val i = Intent(Intent.ACTION_VIEW, u)
        startActivity(i)
    }

    fun feather(view: View?) {
        val u = Uri.parse("https://feathericons.com/")
        val i = Intent(Intent.ACTION_VIEW, u)
        startActivity(i)
    }

    fun apacheee(view: View?) {
        val u = Uri.parse("https://github.com/Crazy-Marvin/ToDont/blob/development/LICENSE")
        val i = Intent(Intent.ACTION_VIEW, u)
        startActivity(i)
    }

    fun spinner(view: View?) {
        val u = Uri.parse("https://github.com/jaredrummler/MaterialSpinner/blob/master/LICENSE")
        val i = Intent(Intent.ACTION_VIEW, u)
        startActivity(i)
    }

    fun nobobutton(view: View?) {
        val u = Uri.parse("https://github.com/alex31n/NoboButton/blob/master/LICENSE")
        val i = Intent(Intent.ACTION_VIEW, u)
        startActivity(i)
    }

    fun cImgButton(view: View?) {
        val u = Uri.parse("https://github.com/hdodenhof/CircleImageView/blob/master/LICENSE.txt")
        val i = Intent(Intent.ACTION_VIEW, u)
        startActivity(i)
    }

    fun MPAndroidChart(view: View?) {
        val u = Uri.parse("https://github.com/PhilJay/MPAndroidChart/blob/master/LICENSE")
        val i = Intent(Intent.ACTION_VIEW, u)
        startActivity(i)
    }

    fun JUnit(view: View?) {
        val u = Uri.parse("https://junit.org/junit4/license.html")
        val i = Intent(Intent.ACTION_VIEW, u)
        startActivity(i)
    }

    fun Kotlin(view: View?) {
        val u = Uri.parse("https://github.com/JetBrains/kotlin/blob/master/license/LICENSE.txt")
        val i = Intent(Intent.ACTION_VIEW, u)
        startActivity(i)
    }

    fun Java(view: View?) {
        val u = Uri.parse("http://openjdk.java.net/legal/gplv2+ce.html")
        val i = Intent(Intent.ACTION_VIEW, u)
        startActivity(i)
    }

    fun showCaseView(view: View?) {
        val u = Uri.parse(resources.getString(R.string.showCaseViewLink))
        val i = Intent(Intent.ACTION_VIEW, u)
        startActivity(i)
    }

    override fun onBackPressed() {
        super.onBackPressed()
//        val i = Intent(this@About, Settings::class.java)
//        finishAffinity()
//        startActivity(i)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
}
