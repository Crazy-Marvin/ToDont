package rocks.poopjournal.todont;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import rocks.poopjournal.todont.Fragments.LabelsAdapter;

public class Labels extends AppCompatActivity {
    RecyclerView rv_labels;
    Db_Controller db;
    ArrayList<String> gettinglabels = new ArrayList<>();
    FloatingActionButton labels_floatingbutton;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_labels);
        rv_labels = findViewById(R.id.rv_labels);

        sharedPreferences=getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        db = new Db_Controller(getApplicationContext(), "", null, 2);
        db.show_labels();
        labels_floatingbutton = findViewById(R.id.label_floatingbtn);

        for (int i = 0; i < Helper.labels_array.size(); i++) {
            gettinglabels.add(Helper.labels_array.get(i));
        }
        rv_labels.setLayoutManager(new LinearLayoutManager(this));
        rv_labels.setAdapter(new LabelsAdapter(this, db, gettinglabels));

        labels_floatingbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View bottomsheetview=null;
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(Labels.this,
                        R.style.BottomSheetDialogTheme);
                if(Helper.isnightmodeon.equals("no")){
                    bottomsheetview = LayoutInflater.from(getApplicationContext()).
                            inflate(R.layout.labels_bottom_sheet,
                                    (RelativeLayout) view.findViewById(R.id.bottomsheetContainer));
                }
                else if(Helper.isnightmodeon.equals("yes")){
                    bottomsheetview = LayoutInflater.from(getApplicationContext()).
                            inflate(R.layout.labels_bottom_sheet_night,
                                    (RelativeLayout) view.findViewById(R.id.bottomsheetContainer));
                }
                final EditText editText = bottomsheetview.findViewById(R.id.label);
                Button saveLabelButton = bottomsheetview.findViewById(R.id.saveLabelButton);
                saveLabelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        db.insert_label(editText.getText().toString());
                        Helper.labels_array.add(editText.getText().toString());
                        db.show_labels();
                        Intent i = new Intent(getApplicationContext(), Labels.class);
                        startActivity(i);
                        overridePendingTransition(0, 0);
                        bottomSheetDialog.dismiss();
                    }
                });
                bottomSheetDialog.setContentView(bottomsheetview);
                bottomSheetDialog.show();
            }

        });
    }

    public void backbtnclicked(View view) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}