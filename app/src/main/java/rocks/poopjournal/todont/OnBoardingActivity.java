package rocks.poopjournal.todont;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static java.util.Calendar.getInstance;

public class OnBoardingActivity extends AppCompatActivity {
Button btn;
SharedPreferences sharedPreferences;
    Calendar c = getInstance();
    SimpleDateFormat df;
    Db_Controller db_controller;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);
        db_controller = new Db_Controller(getApplicationContext(), "", null, 2);
        df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        sharedPreferences=getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        btn=findViewById(R.id.btncontinue);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db_controller.setNightMode("no");
                db_controller.getNightMode();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("FirstTime","no");
                editor.putString("InitialDate",df.format(c.getTime()));
                editor.apply();
                Intent i=new Intent(OnBoardingActivity.this,MainActivity.class);
                startActivity(i);
            }
        });
    }
}