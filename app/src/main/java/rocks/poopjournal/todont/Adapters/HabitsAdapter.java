package rocks.poopjournal.todont.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.view.Gravity;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import rocks.poopjournal.todont.Db_Controller;
import rocks.poopjournal.todont.Fragments.HabitsFragment;
import rocks.poopjournal.todont.Fragments.HabitsLogFragment;
import rocks.poopjournal.todont.Helper;
import rocks.poopjournal.todont.MainActivity;
import rocks.poopjournal.todont.R;


public class HabitsAdapter extends RecyclerView.Adapter<HabitsAdapter.RecyclerViewHolder> {
    private ArrayList<String> donotTask = new ArrayList<>();
    private ArrayList<String> donotCatagory = new ArrayList<>();
    HabitsFragment ft;
    Context con;
    Date c = Calendar.getInstance().getTime();
    String[] catagories;
    String catagoryselected;
    Db_Controller db;
    final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    String formattedDate = df.format(c);

    public HabitsAdapter(Context con, HabitsFragment ft, Db_Controller db, ArrayList<String> donotTask, ArrayList<String> donotCatagory) {
        this.donotTask = donotTask;
        this.donotCatagory = donotCatagory;
        this.con = con;
        this.ft = ft;
        this.db = db;

    }

    @NonNull
    @Override
    public HabitsAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.recyclerview_layout_habits, viewGroup, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final HabitsAdapter.RecyclerViewHolder holder, final int position) {
        String dTask = donotTask.get(position);
        String dCatagory = donotCatagory.get(position);
        holder.task.setText(dTask);
        holder.catagoryoftask.setText(dCatagory);
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
                TextView txt = bottomsheetview.findViewById(R.id.txt);
                if (Helper.labels_array.size() == 0) {
                    txt.setVisibility(View.VISIBLE);
                    spinner.setVisibility(View.INVISIBLE);
                } else {
                    txt.setVisibility(View.INVISIBLE);
                    spinner.setVisibility(View.VISIBLE);
                }
                habit.setText("" + Helper.habitsdata.get(position)[2]);
                detail.setText("" + Helper.habitsdata.get(position)[3]);
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
                            selectedText.setTextColor(ContextCompat.getColor(con, R.color.g2));
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                saveTaskButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String formattedDate = df.format(c);
                        try {

                        } catch (SQLiteException e) {
                        }
                        db.update_habitsdata(position, formattedDate, habit.getText().toString()
                                , detail.getText().toString(), catagoryselected);
                        db.show_habits_data();
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
        holder.btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Helper.isTodaySelected == true) {
                    db.show_data();
                    db.show_habits_data();
                    Log.d("asize", "" + Helper.data.size());
                    int count = 0;
                    if (Helper.data.size() != 0) {
                        for (int i = 0; i < Helper.data.size(); i++) {
                            String getdate = Helper.data.get(i)[1];
                            String gethabit = Helper.data.get(i)[2];
                            if ((Helper.habitsdata.get(position)[2].equals(gethabit)) && (getdate.equals(df.format(c)))) {
                                Toast toast = Toast.makeText(con, "    Already Added    ", Toast.LENGTH_SHORT);
                                View v = toast.getView();
                                v.setBackground(con.getResources().getDrawable(R.drawable.roundbutton));
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                count++;
                            }

                        }
                        if (count == 0) {
                            db.insert_data(Helper.data.size(), df.format(c), Helper.habitsdata.get(position)[2],
                                    Helper.habitsdata.get(position)[3], catagoryselected);
                            Toast toast = Toast.makeText(con, "    Added To Avoided    ", Toast.LENGTH_SHORT);
                            View v = toast.getView();
                            v.setBackground(con.getResources().getDrawable(R.drawable.roundbutton));
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    } else {
                        db.insert_data(Helper.data.size(), df.format(c), Helper.habitsdata.get(position)[2],
                                Helper.habitsdata.get(position)[3], catagoryselected);
                        Toast toast = Toast.makeText(con, "    Added To Avoided    ", Toast.LENGTH_SHORT);
                        View v = toast.getView();
                        v.setBackground(con.getResources().getDrawable(R.drawable.roundbutton));
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
            }
        });
        holder.btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Helper.isTodaySelected == true) {
                    db.show_done_data();
                    db.show_habits_data();
                    int count = 0;
                    if (Helper.donedata.size() != 0) {
                        for (int i = 0; i < Helper.donedata.size(); i++) {
                            String getdate = Helper.donedata.get(i)[1];
                            String gethabit = Helper.donedata.get(i)[2];
                            if ((Helper.habitsdata.get(position)[2].equals(gethabit)) && (getdate.equals(df.format(c)))) {
                                Toast toast = Toast.makeText(con, "    Already Added    ", Toast.LENGTH_SHORT);
                                View v = toast.getView();
                                v.setBackground(con.getResources().getDrawable(R.drawable.roundbutton));
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                count++;
                            }

                        }
                        if (count == 0) {
                            Log.d("id", "" + Helper.donedata.size());
                            db.insert_done_data(Helper.donedata.size(), df.format(c), Helper.habitsdata.get(position)[2],
                                    Helper.habitsdata.get(position)[3], catagoryselected);
                            Toast toast = Toast.makeText(con, "    Added To Done    ", Toast.LENGTH_SHORT);
                            View v = toast.getView();
                            v.setBackground(con.getResources().getDrawable(R.drawable.roundbutton));
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    } else {
                        Log.d("id", "" + Helper.donedata.size());
                        db.insert_done_data(Helper.donedata.size(), df.format(c), Helper.habitsdata.get(position)[2],
                                Helper.habitsdata.get(position)[3], catagoryselected);
                        Toast toast = Toast.makeText(con, "    Added To Done    ", Toast.LENGTH_SHORT);
                        View v = toast.getView();
                        v.setBackground(con.getResources().getDrawable(R.drawable.roundbutton));
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return donotTask.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        Button btn1, btn2;
        TextView task;
        TextView catagoryoftask;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            btn1 = itemView.findViewById(R.id.addToAvoided);
            btn2 = itemView.findViewById(R.id.addToDone);
            task = itemView.findViewById(R.id.task);
            catagoryoftask = itemView.findViewById(R.id.catagoryoftask);
            btn1.setBackgroundResource(R.drawable.ic_avoided);
            btn2.setBackgroundResource(R.drawable.ic_done);
        }
    }
}
