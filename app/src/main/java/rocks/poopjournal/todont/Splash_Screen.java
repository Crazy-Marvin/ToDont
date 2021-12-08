package rocks.poopjournal.todont;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class Splash_Screen extends AppCompatActivity {
    SharedPreferences prefs;
    String checkFirstTime;
    Db_Controller db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash__screen);
        db = new Db_Controller(getApplicationContext(), "", null, 2);
        prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        checkFirstTime = prefs.getString("FirstTime", "");
        db.getNightMode();
        if(checkFirstTime.equals("no")){
            if(Helper.isnightmodeon.equals("light")){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            else if(Helper.isnightmodeon.equals("dark")){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
        }

        checkStatus();
    }

    public void checkStatus() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(checkFirstTime.equals("no")){
                    Intent i = new Intent(Splash_Screen.this, MainActivity.class);
                    finishAffinity();
                    startActivity(i);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                }
                else{
                    Intent i = new Intent(Splash_Screen.this, OnBoardingActivity.class);
                    finishAffinity();
                    startActivity(i);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                }
            }
        }, 2000);
    }
}

