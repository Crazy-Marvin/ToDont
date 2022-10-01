package rocks.poopjournal.todont;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class About extends AppCompatActivity {
    TextView version;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        version=findViewById(R.id.versiontext);
        version.setText( BuildConfig.VERSION_NAME +" Beta ");
        //tabdeeli aa gai hai
}

    public void contact_codeaquaria(View view) {
        switch(view.getId()){
            case R.id.btnmail_codeaquaria:
                String mailto = "mailto:codeaquaria20@gmail.com";
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse(mailto));
                try {
                    startActivity(emailIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, "    Error to open Email    ", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btngit_codeaquaria:
                Uri uri = Uri.parse("https://github.com/arafaatqureshi"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case R.id.btntwitter_codeaquaria:
                Uri ui = Uri.parse("https://www.facebook.com/Code-Aquaria-109834144196326"); // missing 'http://' will cause crashed
                Intent it = new Intent(Intent.ACTION_VIEW, ui);
                startActivity(it);
                break;

        }
    }

    public void contact_marvin(View view) {
        switch(view.getId()){
            case R.id.btnmail_crazymarvin:
                String mailto = "mailto:marvin@poopjournal.rocks";
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse(mailto));
                try {
                    startActivity(emailIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, "    Error to open Email    ", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btngit_crazymarvin:
                Uri uri = Uri.parse("https://github.com/Crazy-Marvin"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case R.id.btntwitter_crazymarvin:
                Uri u = Uri.parse("https://twitter.com/CrazyMarvinApps"); // missing 'http://' will cause crashed
                Intent i = new Intent(Intent.ACTION_VIEW, u);
                startActivity(i);
                break;

        }
    }

    public void translate(View view) {
        Uri u = Uri.parse("https://hosted.weblate.org/projects/todont/");
        Intent i = new Intent(Intent.ACTION_VIEW, u);
        startActivity(i);
    }

    public void report(View view) {
        Uri u = Uri.parse("https://github.com/Crazy-Marvin/ToDont/issues");
        Intent i = new Intent(Intent.ACTION_VIEW, u);
        startActivity(i);
    }

    public void viewsource(View view) {
        Uri u = Uri.parse("https://github.com/Crazy-Marvin/ToDont");
        Intent i = new Intent(Intent.ACTION_VIEW, u);
        startActivity(i);
    }

    public void back(View view) {
        Intent i = new Intent(About.this, Settings.class);
        finishAffinity();
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }

    public void jetpack(View view) {
        Uri u = Uri.parse("https://developer.android.com/jetpack");
        Intent i = new Intent(Intent.ACTION_VIEW, u);
        startActivity(i);
    }

    public void logoclicked(View view) {
        Uri u = Uri.parse("https://crazymarvin.com/todont");
        Intent i = new Intent(Intent.ACTION_VIEW, u);
        startActivity(i);
    }

    public void feather(View view) {
        Uri u = Uri.parse("https://feathericons.com/");
        Intent i = new Intent(Intent.ACTION_VIEW, u);
        startActivity(i);
    }

    public void apacheee(View view) {
        Uri u = Uri.parse("https://github.com/Crazy-Marvin/ToDont/blob/development/LICENSE");
        Intent i = new Intent(Intent.ACTION_VIEW, u);
        startActivity(i);
    }
    public void spinner(View view) {
        Uri u = Uri.parse("https://github.com/jaredrummler/MaterialSpinner/blob/master/LICENSE");
        Intent i = new Intent(Intent.ACTION_VIEW, u);
        startActivity(i);
    }

    public void nobobutton(View view) {
        Uri u = Uri.parse("https://github.com/alex31n/NoboButton/blob/master/LICENSE");
        Intent i = new Intent(Intent.ACTION_VIEW, u);
        startActivity(i);
    }
    public void cImgButton(View view) {
        Uri u = Uri.parse("https://github.com/hdodenhof/CircleImageView/blob/master/LICENSE.txt");
        Intent i = new Intent(Intent.ACTION_VIEW, u);
        startActivity(i);
    }
    public void MPAndroidChart(View view) {
        Uri u = Uri.parse("https://github.com/PhilJay/MPAndroidChart/blob/master/LICENSE");
        Intent i = new Intent(Intent.ACTION_VIEW, u);
        startActivity(i);
    }
    public void JUnit(View view) {
        Uri u = Uri.parse("https://junit.org/junit4/license.html");
        Intent i = new Intent(Intent.ACTION_VIEW, u);
        startActivity(i);
    }

    public void Kotlin(View view) {
        Uri u = Uri.parse("https://github.com/JetBrains/kotlin/blob/master/license/LICENSE.txt");
        Intent i = new Intent(Intent.ACTION_VIEW, u);
        startActivity(i);
    }
    public void Java(View view) {
        Uri u = Uri.parse("http://openjdk.java.net/legal/gplv2+ce.html");
        Intent i = new Intent(Intent.ACTION_VIEW, u);
        startActivity(i);
    }


}