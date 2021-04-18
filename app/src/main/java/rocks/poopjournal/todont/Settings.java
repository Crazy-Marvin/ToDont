package rocks.poopjournal.todont;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.media.audiofx.EnvironmentalReverb;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class Settings extends AppCompatActivity {
    Db_Controller db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        db = new Db_Controller(getApplicationContext(), "", null, 2);
        db.getNightMode();

    }
    public void changeMode(View view) {
                db.getNightMode();
        Log.d("qqq","infunc : "+Helper.isnightmodeon );
        if (Helper.isnightmodeon.equals("no")) {
            Log.d("qqq","inif");

            db.update_nightmode("yes");
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            Intent intennt = new Intent(Settings.this,Settings.class);
            startActivity(intennt);
            overridePendingTransition(0, 0);
            finish();
        } else if (Helper.isnightmodeon.equals("yes")) {
            Log.d("qqq","inelse");

            db.update_nightmode("no");
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            Intent intennt = new Intent(Settings.this,Settings.class);
            startActivity(intennt);
            overridePendingTransition(0, 0);
            finish();

        }
//        if(Helper.isnightmodeon.equals("no")){
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//        }
//        else if(Helper.isnightmodeon.equals("yes")){
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//        }
    }

    public void backbtn(View view) {
        Intent i = new Intent(Settings.this, MainActivity.class);
        finishAffinity();
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }

    public void aboutus(View view) {
        Intent i = new Intent(Settings.this, About.class);
        finishAffinity();
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}