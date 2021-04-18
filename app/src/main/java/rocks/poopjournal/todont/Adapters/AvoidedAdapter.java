package rocks.poopjournal.todont.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import rocks.poopjournal.todont.Fragments.AvoidedFragment;
import rocks.poopjournal.todont.Db_Controller;
import rocks.poopjournal.todont.Helper;
import rocks.poopjournal.todont.MainActivity;
import rocks.poopjournal.todont.R;


public class AvoidedAdapter extends RecyclerView.Adapter<AvoidedAdapter.RecyclerViewHolder> {
    private ArrayList<String> donotTask = new ArrayList<>();
    private ArrayList<String> donotCatagory = new ArrayList<>();
    private ArrayList<String> updateddonotCatagory = new ArrayList<>();
    AvoidedFragment ft;
    Context con;
    Date c = Calendar.getInstance().getTime();
    String[] catagories;
    String catagoryselected = "";
    Db_Controller db;
    final SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
    String formattedDate = df.format(c);

    public AvoidedAdapter(Context con, AvoidedFragment ft, Db_Controller db, ArrayList<String> donotTask, ArrayList<String> donotCatagory) {
        this.donotTask = donotTask;
        this.donotCatagory = donotCatagory;
        this.con = con;
        this.ft = ft;
        this.db = db;

    }

    @NonNull
    @Override
    public AvoidedAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.recyclerview_layout, viewGroup, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AvoidedAdapter.RecyclerViewHolder holder, final int position) {
        db.show_data();
        String dTask = donotTask.get(position);
        String date = Helper.data.get(position)[1];
        holder.task.setText(dTask);
        holder.dateoftask.setText(date);
        holder.task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(con,
                        R.style.BottomSheetDialogTheme);
                final View bottomsheetview = LayoutInflater.from(con.getApplicationContext()).
                        inflate(R.layout.update_layout_bottom_sheet,
                                (RelativeLayout) view.findViewById(R.id.bottomsheetContainer));
                final Spinner spinner = bottomsheetview.findViewById(R.id.updatespinner);
                Button saveTaskButton = bottomsheetview.findViewById(R.id.updateTaskButton);
                final EditText habit = bottomsheetview.findViewById(R.id.updatehabit);
                final EditText detail = bottomsheetview.findViewById(R.id.updatedetail);
                TextView txt=bottomsheetview.findViewById(R.id.txt);
                if (Helper.labels_array.size() == 0) {
                    txt.setVisibility(View.VISIBLE);
                    spinner.setVisibility(View.INVISIBLE);
                } else {
                    txt.setVisibility(View.INVISIBLE);
                    spinner.setVisibility(View.VISIBLE);
                }
                habit.setText("" + Helper.data.get(position)[2]);
                detail.setText("" + Helper.data.get(position)[3]);
                final Adapter adapter = new ArrayAdapter<String>(con, android.R.layout.simple_list_item_1,
                      Helper.labels_array) {
                    @Override
                    public boolean isEnabled(int position) {
                        if (position == 0) {
                            return false;
                        } else {
                            return true;
                        }
                    }
                };
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        catagoryselected = adapterView.getItemAtPosition(i).toString();
                        TextView selectedText = (TextView) adapterView.getChildAt(i);
                        if (selectedText != null) {
                            selectedText.setTextColor(ContextCompat.getColor(con,R.color.g2));
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });


                saveTaskButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String habit_text = Helper.data.get(position)[2];
                        String detail_text = detail.getText().toString();
                        String formattedDate = df.format(c);
                        try {

                        } catch (SQLiteException e) {
                        }
                        Log.d("checkingz",""+habit.getText().toString());
                        db.update_data(position, formattedDate,habit.getText().toString(), detail.getText().toString(), catagoryselected);
                        db.show_data();
                        Helper.SelectedButtonOfTodayTab = 1;
                        Intent intent = new Intent(con, MainActivity.class);
                        con.startActivity(intent);
                        ((Activity) con).overridePendingTransition(0, 0);
                        bottomSheetDialog.dismiss();
                    }

                });
                  spinner.setAdapter((SpinnerAdapter) adapter);
                bottomSheetDialog.setContentView(bottomsheetview);
                bottomSheetDialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return donotTask.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        Button rbtn;
        TextView task;
        TextView dateoftask;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            rbtn = itemView.findViewById(R.id.addToAvoided);
            task = itemView.findViewById(R.id.task);
            dateoftask = itemView.findViewById(R.id.dateoftask);
            rbtn.setBackgroundResource(R.drawable.ic_avoided);

        }

    }
}
