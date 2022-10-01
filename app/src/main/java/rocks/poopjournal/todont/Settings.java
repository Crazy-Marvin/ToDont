package rocks.poopjournal.todont;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.Dialog;
import android.content.Intent;
import android.media.audiofx.EnvironmentalReverb;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class Settings extends AppCompatActivity {
    Db_Controller db;
    TextView modetitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        db = new Db_Controller(getApplicationContext(), "", null, 2);
        modetitle=findViewById(R.id.modetitle);
        Log.d("heyyyymode",""+Helper.isnightmodeon);
        switch(Helper.isnightmodeon){
            case "followsys":
                modetitle.setText(R.string.followsys);
                break;
            case "light":
                modetitle.setText(R.string.light);
                break;
            case "dark":
                modetitle.setText(R.string.dark);
                break;
        }
//        db.getNightMode();

    }
    public void changeMode(View view) {
        final Dialog d = new Dialog(this);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.dialogbox);
        Button btndone=d.findViewById(R.id.btndone);
        RadioButton light,dark,fsys;
        light=d.findViewById(R.id.light);
        dark=d.findViewById(R.id.dark);
        fsys=d.findViewById(R.id.followsys);
        String getmodetitle= modetitle.getText().toString();
        if(getmodetitle.equals("Follow System")){
            fsys.setChecked(true);
        }
        if(getmodetitle.equals("Light")){
            light.setChecked(true);
        }
        if(getmodetitle.equals("Dark")){
            dark.setChecked(true);
        }

        WindowManager.LayoutParams lp = d.getWindow().getAttributes();
        lp.dimAmount=0.9f;
        d.getWindow().setAttributes(lp);
        d.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

        btndone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.getNightMode();
                Log.d("modeisbuttondone:",""+Helper.isnightmodeon);
                if(Helper.isnightmodeon.equals("followsys")){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    Log.d("checkmode","f .. "+AppCompatDelegate.getDefaultNightMode());
                    Toast.makeText(getApplicationContext(), (R.string.toast_system), Toast.LENGTH_SHORT).show();
                    modetitle.setText(R.string.followsys);
                    d.dismiss();
                }
                if(Helper.isnightmodeon.equals("light")){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    Log.d("checkmode","l .. "+AppCompatDelegate.getDefaultNightMode());
                    modetitle.setText(R.string.dark);
                    Toast.makeText(getApplicationContext(), (R.string.toast_light), Toast.LENGTH_SHORT).show();
                    d.dismiss();
                }
                if(Helper.isnightmodeon.equals("dark")){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    Toast.makeText(getApplicationContext(), R.string.toast_dark, Toast.LENGTH_SHORT).show();
                    modetitle.setText(R.string.light);
                    d.dismiss();
                }

            }
        });d.show();
//                db.getNightMode();
//        Log.d("qqq","infunc : "+Helper.isnightmodeon );
//        if (Helper.isnightmodeon.equals("no")) {
//            Log.d("qqq","inif");
//
//            db.update_nightmode("yes");
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//            Intent intennt = new Intent(Settings.this,Settings.class);
//            startActivity(intennt);
//            overridePendingTransition(0, 0);
//            finish();
//        } else if (Helper.isnightmodeon.equals("yes")) {
//            Log.d("qqq","inelse");
//
//            db.update_nightmode("no");
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//            Intent intennt = new Intent(Settings.this,Settings.class);
//            startActivity(intennt);
//            overridePendingTransition(0, 0);
//            finish();
//
//        }
////        if(Helper.isnightmodeon.equals("no")){
////            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
////        }
////        else if(Helper.isnightmodeon.equals("yes")){
////            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
////        }
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.followsys:
                if (checked)
                {
                    modetitle.setText("Follow System");
                    db.update_nightmode("followsys");
                    Log.d("modeisf:",""+Helper.isnightmodeon);
                    break;
                }
            case R.id.light:
                if (checked)
                {
                    modetitle.setText("Light");
                    db.update_nightmode("light");
                    Log.d("modeisl:",""+Helper.isnightmodeon);
                    break;
                }
            case R.id.dark:
                if (checked)
                {
                    modetitle.setText("Dark");
                    db.update_nightmode("dark");
                    Log.d("modeisd:",""+Helper.isnightmodeon);
                    break;
                }
        }
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