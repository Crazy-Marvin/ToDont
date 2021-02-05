package rocks.poopjournal.todont;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Labels extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_labels);
    }

    public void backbtnclicked(View view) {
        Intent i =new Intent(this,MainActivity.class);
        startActivity(i);
    }
}