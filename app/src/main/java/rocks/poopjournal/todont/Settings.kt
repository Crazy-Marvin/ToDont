package rocks.poopjournal.todont;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.app.LocaleManager;
import android.content.ContentValues
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources.Theme
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.LocaleList;
import android.provider.DocumentsContract;
import android.provider.MediaStore
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView
import android.widget.Toast;
import rocks.poopjournal.todont.databinding.ActivitySettingsBinding
import rocks.poopjournal.todont.utils.Constants
import rocks.poopjournal.todont.utils.DatabaseUtils

import java.io.FileOutputStream;
import java.util.Locale;

import rocks.poopjournal.todont.utils.SharedPrefUtils;
import rocks.poopjournal.todont.utils.ThemeMode
import rocks.poopjournal.todont.utils.getAppTheme
import rocks.poopjournal.todont.utils.setAppTheme

import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity;
import smartdevelop.ir.eram.showcaseviewlib.config.PointerType;
import java.io.File
import java.io.FileInputStream
import java.io.IOException

class Settings : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var prefUtils: SharedPrefUtils
    private lateinit var dbHelper: DatabaseUtils

    private val REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 100
    private val REQUEST_CODE_PICK_DB_FILE = 200




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppTheme(this)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //actionBar?.setBackgroundDrawable(resources.getDrawable(R.drawable.mygradient))

        prefUtils = SharedPrefUtils(this)


        dbHelper = DatabaseUtils(this)

        // Handle back press
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishMYActivity()
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }
        })

        binding.btnBack.setOnClickListener {
           finishMYActivity()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        binding.modetitle.text = getThemeMode(this.getAppTheme())

        binding.backUpButton.setOnClickListener {
            if (checkStoragePermission()) {
                backupDatabase()
            }
        }

        binding.restoreButton.setOnClickListener {
            if (checkStoragePermission()) {
                openFilePicker()
            }
        }
    }

    // Check and request storage permission
    private fun checkStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q || ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_CODE_WRITE_EXTERNAL_STORAGE
            )
            false
        }
    }

    // Backup the database to the Downloads folder
    private fun backupDatabase() {
        try {
            val dbFile = dbHelper.getDatabaseFile()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveFileToDownloadsUsingMediaStore(dbFile)
            } else {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val destFile = File(downloadsDir, dbFile.name)
                copyFile(dbFile, destFile)
                Toast.makeText(this, "Database backed up to Downloads", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to backup database: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // Save file to Downloads using MediaStore (Android 10+)
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveFileToDownloadsUsingMediaStore(sourceFile: File) {
        val contentResolver = contentResolver
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, sourceFile.name)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/octet-stream")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val fileUri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
        if (fileUri != null) {
            try {
                contentResolver.openOutputStream(fileUri)?.use { outputStream ->
                    FileInputStream(sourceFile).use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                Toast.makeText(this, "Database backed up to Downloads", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Failed to save file: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Open file picker to select a backup file
    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/octet-stream"
        }
        startActivityForResult(intent, REQUEST_CODE_PICK_DB_FILE)
    }

    // Handle file picker result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_DB_FILE && resultCode == RESULT_OK && data != null) {
            val fileUri = data.data
            if (fileUri != null) {
                restoreDatabase(fileUri)
            }
        }
    }

    // Restore the database from the selected file
    private fun restoreDatabase(fileUri: Uri) {
        try {
            dbHelper.close()
            val dbFile = dbHelper.getDatabaseFile()

            contentResolver.openInputStream(fileUri)?.use { inputStream ->
                FileOutputStream(dbFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            Toast.makeText(this, "Database restored successfully", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to restore database: ${e.message}", Toast.LENGTH_LONG).show()
        } finally {
            dbHelper = DatabaseUtils(this) // Reinitialize the database helper
        }
    }

    // Copy file from source to destination
    @Throws(IOException::class)
    private fun copyFile(sourceFile: File, destFile: File) {
        FileInputStream(sourceFile).use { inputStream ->
            FileOutputStream(destFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
    }

    // Handle permission request result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                backupDatabase()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun changeMode(view: View) {
        if (prefUtils.getBool(SharedPrefUtils.KEY_APPEAR_VIEW)) {
            val guideView = GuideView.Builder(this)
                .setContentText(getString(R.string.help_make_to_don_t_better))
                .setTargetView(view)
                .setDismissType(DismissType.anywhere)
                .setPointerType(PointerType.arrow)
                .setGravity(Gravity.center)
                .setGuideListener { prefUtils.setBool(SharedPrefUtils.KEY_APPEAR_VIEW, true) }
                .build()
            guideView.show()
        } else {
            val dialog = Dialog(this).apply {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                setContentView(R.layout.dialogbox)
                setCancelable(false)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
            val lp = dialog.window!!.attributes
            lp.dimAmount = 0.9f
            val window = dialog.window
            window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            dialog.window!!.attributes = lp

            val btndone = dialog.findViewById<TextView>(R.id.btndone)
            val light = dialog.findViewById<RadioButton>(R.id.light)
            val dark = dialog.findViewById<RadioButton>(R.id.dark)
            val dracula = dialog.findViewById<RadioButton>(R.id.dracula)
            val fsys = dialog.findViewById<RadioButton>(R.id.followsys)
            val draculaPro= dialog.findViewById<RadioButton>(R.id.dracula_pro)
            val draculaProAlucard= dialog.findViewById<RadioButton>(R.id.dracula_pro_alucard)
            val draculaProBuffy= dialog.findViewById<RadioButton>(R.id.dracula_pro_buffy)
            val draculaProBlade= dialog.findViewById<RadioButton>(R.id.dracula_pro_blade)


            when (binding.modetitle.text.toString()) {
                resources.getString(R.string.followsys) -> fsys.isChecked = true
                resources.getString(R.string.light) -> light.isChecked = true
                resources.getString(R.string.dark) -> dark.isChecked = true
                resources.getString(R.string.dracula) -> dracula.isChecked = true
                resources.getString(R.string.dracula_pro) -> draculaPro.isChecked = true
                resources.getString(R.string.dracula_pro_alucard) -> draculaProAlucard.isChecked = true
                resources.getString(R.string.dracula_pro_buffy) -> draculaProBuffy.isChecked = true
                resources.getString(R.string.dracula_pro_blade) -> draculaProBlade.isChecked = true
            }

            dialog.window?.attributes = dialog.window?.attributes?.apply {
                dimAmount = 0.9f
            }

            btndone.setOnClickListener {
                setNewThemeMode()
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    private fun setNewThemeMode() {
        // Usage in when block
        val theme=prefUtils.getThemeMode()
        when (theme) {
            ThemeMode.FOLLOW_SYS.value -> {
                applyThemeMode(
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,
                    String.format("To Don't is using the %s now",getThemeMode(theme)),
                    ThemeMode.FOLLOW_SYS.value
                )

            }

            ThemeMode.LIGHT_MODE.value-> {
                applyThemeMode(
                    //AppCompatDelegate.MODE_NIGHT_NO,
                    R.style.Theme_Todon_Light,
                    String.format("To Don't is using the %s now",getThemeMode(theme)),
                    ThemeMode.LIGHT_MODE.value
                )

            }

            ThemeMode.DARK_MODE.value -> {
                applyThemeMode(
                    // AppCompatDelegate.MODE_NIGHT_YES,
                    R.style.Theme_Todon_Dark,
                    String.format("To Don't is using the %s now",getThemeMode(theme)),
                    ThemeMode.DARK_MODE.value
                )

            }

            ThemeMode.DRACULA.value -> {
                applyThemeMode(
                    //AppCompatDelegate.MODE_NIGHT_YES,
                    R.style.Theme_Todon_Dracula,
                    String.format("To Don't is using the %s now",getThemeMode(theme)),
                    ThemeMode.DRACULA.value
                )
            }
            ThemeMode.DRACULA_PRO.value -> {
                applyThemeMode(
                    //AppCompatDelegate.MODE_NIGHT_YES,
                    R.style.Theme_Todon_Dracula_Pro,
                    String.format("To Don't is using the %s now",getThemeMode(theme)),
                    ThemeMode.DRACULA_PRO.value
                )
            }
            ThemeMode.DRACULA_PRO_ALUCARD.value -> {
                applyThemeMode(
                    //AppCompatDelegate.MODE_NIGHT_YES,
                    R.style.Theme_Todon_Dracula_Alucard,
                    String.format("To Don't is using the %s now",getThemeMode(theme)),
                    ThemeMode.DRACULA_PRO_ALUCARD.value
                )
            }
            ThemeMode.DRACULA_PRO_BUFFY.value -> {
                applyThemeMode(
                    //AppCompatDelegate.MODE_NIGHT_YES,
                    R.style.Theme_Todon_DraculaBlade_Buffy,
                    String.format("To Don't is using the %s now",getThemeMode(theme)),
                    ThemeMode.DRACULA_PRO_BUFFY.value
                )
            }
            ThemeMode.DRACULA_PRO_BLADE.value -> {
                applyThemeMode(
                    //AppCompatDelegate.MODE_NIGHT_YES,
                    R.style.Theme_Todon_DraculaBlade,
                    String.format("To Don't is using the %s now",getThemeMode(theme)),
                    ThemeMode.DRACULA_PRO_BLADE.value
                )
            }
            else -> {
                applyThemeMode(
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,
                    String.format("To Don't is using the %s now",getThemeMode(theme)),
                    ThemeMode.FOLLOW_SYS.value
                )
            }

        }
    }



    private fun applyThemeMode(mode: Int, toastMessageRes: String, modeTitle: String) {
        Log.d("checkmode", "Mode set to: $mode")
        //binding.modetitle.text = getThemeMode(modeTitle)
        //prefUtils.setThemeMode(modeTitle)
        //AppCompatDelegate.setDefaultNightMode(mode)
        //setTheme(mode)
        Constants.CURRENT_THEME=prefUtils.getThemeMode()


        if(Constants.IS_OK){
            Toast.makeText(applicationContext, toastMessageRes, Toast.LENGTH_SHORT).show()
            recreate()
        }
    }

    private fun getThemeMode(modeTitle: String): String {
        return when (modeTitle) {
            ThemeMode.FOLLOW_SYS.value -> {
                resources.getString(R.string.followsys)
            }

            ThemeMode.LIGHT_MODE.value -> {
                resources.getString(R.string.light)
            }

           ThemeMode.DARK_MODE.value -> {
                resources.getString(R.string.dark)
            }

            ThemeMode.DRACULA.value -> {
                resources.getString(R.string.dracula)
            }
            ThemeMode.DRACULA_PRO.value ->{
                resources.getString(R.string.dracula_pro)
            }
            ThemeMode.DRACULA_PRO_ALUCARD.value ->{
                resources.getString(R.string.dracula_pro_alucard)
            }
            ThemeMode.DRACULA_PRO_BUFFY.value ->{
                resources.getString(R.string.dracula_pro_buffy)
            }
            ThemeMode.DRACULA_PRO_BLADE.value ->{
                resources.getString(R.string.dracula_pro_blade)
            }
            else -> {
                modeTitle
            }
        }
    }


    fun onRadioButtonClicked(view: View) {
        // Check if the button is now checked
        val checked = (view as RadioButton).isChecked

        // Determine which radio button was clicked
        when (view.id) {
            R.id.followsys -> {
                if (checked) {
                    updateThemeMode(ThemeMode.FOLLOW_SYS.value)
                }
            }

            R.id.light -> {
                if (checked) {
                    updateThemeMode(ThemeMode.LIGHT_MODE.value)
                }
            }

            R.id.dark -> {
                if (checked) {
                    updateThemeMode(ThemeMode.DARK_MODE.value)
                }
            }

            R.id.dracula -> {
                if (checked) {
                    updateThemeMode(ThemeMode.DRACULA.value)
                }
            }
            R.id.dracula_pro -> {
                if (checked) {
                    updateThemeMode(ThemeMode.DRACULA_PRO.value)
                }
            }
            R.id.dracula_pro_alucard -> {
                if (checked) {
                    updateThemeMode(ThemeMode.DRACULA_PRO_ALUCARD.value)
                }
            }
            R.id.dracula_pro_buffy -> {
                if (checked) {
                    updateThemeMode(ThemeMode.DRACULA_PRO_BUFFY.value)
                }
            }
            R.id.dracula_pro_blade -> {
                if (checked) {
                    updateThemeMode(ThemeMode.DRACULA_PRO_BLADE.value)
                }
            }
        }
        Constants.IS_OK= Constants.CURRENT_THEME != prefUtils.getThemeMode()
    }

    private fun updateThemeMode(value: String) {
        binding.modetitle.text =getThemeMode(value)
        prefUtils.setThemeMode(value)
    }

    private fun backBtn(view: View) {
       finishMYActivity()
    }
    fun aboutus(view: View) {
        Intent(this, About::class.java).also { intent ->
            //finishAffinity()
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
    }

    fun restore(view: View) {
        openFilePicker()
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun setAppLocale(locale: Locale) {
        val localeManager = getSystemService(LocaleManager::class.java)
        localeManager?.applicationLocales = LocaleList(locale)
    }


    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.permission_required))
            .setMessage(getString(R.string.this_app_requires_access_to_your_external_storage_to_restore_the_database_please_enable_this_permission_in_the_app_settings))
            .setPositiveButton(getString(R.string.open_settings)) { _, _ ->
                val intent =
                    Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", packageName, null)
                    }
                startActivity(intent)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    fun finishMYActivity(){
        if(Constants.IS_OK){
            setResult(RESULT_OK)
        }else{
            setResult(RESULT_CANCELED)
        }
        finish()
    }


}
