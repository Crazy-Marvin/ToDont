package rocks.poopjournal.todont;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import rocks.poopjournal.todont.Adapters.HabitsAdapter;
import rocks.poopjournal.todont.Fragments.HabitsFragment;
import rocks.poopjournal.todont.Fragments.LabelsAdapter;

public class Labels extends AppCompatActivity {
    RecyclerView rv_labels;
    TextView tv_label;
    Db_Controller db;
    LabelsAdapter adapter;
    ArrayList<String> gettinglabels = new ArrayList<>();
    FloatingActionButton labels_floatingbutton;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_labels);
        rv_labels = findViewById(R.id.rv_labels);
        tv_label = findViewById(R.id.tv_label);

        sharedPreferences=getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        db = new Db_Controller(getApplicationContext(), "", null, 2);
        db.show_labels();
        labels_floatingbutton = findViewById(R.id.label_floatingbtn);

        for (int i = 0; i < Helper.labels_array.size(); i++) {
            gettinglabels.add(Helper.labels_array.get(i));
        }
        rv_labels.setLayoutManager(new LinearLayoutManager(this));
        new ItemTouchHelper(itemtouchhelper).attachToRecyclerView(rv_labels);
        adapter= new LabelsAdapter(this,db,gettinglabels);
        rv_labels.setAdapter(adapter);
        rv_labels.setLayoutManager(new LinearLayoutManager(this));

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
    ItemTouchHelper.SimpleCallback itemtouchhelper = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
            if (direction == 8) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(Labels.this);
                builder1.setMessage("Do you really want to delete this?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                int i = viewHolder.getAdapterPosition();
                                db.delete_label(gettinglabels.get(i));
                                Intent intent = new Intent(getApplicationContext(), Labels.class);
                                startActivity(intent);
                                overridePendingTransition(0, 0);
                                dialog.cancel();
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(getApplicationContext(), Labels.class);
                                startActivity(intent);
                                overridePendingTransition(0, 0);
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert11 = builder1.create();
                alert11.show();

            }

        }
    };
    public void backbtnclicked(View view) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}