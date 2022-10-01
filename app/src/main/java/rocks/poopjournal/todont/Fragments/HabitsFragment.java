package rocks.poopjournal.todont.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import rocks.poopjournal.todont.Db_Controller;
import rocks.poopjournal.todont.Adapters.HabitsAdapter;
import rocks.poopjournal.todont.Helper;
import rocks.poopjournal.todont.MainActivity;
import rocks.poopjournal.todont.R;


public class HabitsFragment extends Fragment {
    RecyclerView rv;
    ArrayList<String> gettingtasks = new ArrayList<>();
    ArrayList<String> gettingcatagory = new ArrayList<>();
    FloatingActionButton floatingActionButton;
    Date c = Calendar.getInstance().getTime();
    String catagoryselected;
    Db_Controller db;
    HabitsAdapter adapter;
    TextView tv1,tv2;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_habits, container, false);
        Helper.SelectedButtonOfTodayTab=0;
        rv = view.findViewById(R.id.rv);
        db = new Db_Controller(getActivity(), "", null, 2);
        db.show_labels();
        db.show_habits_data();
        db.getNightMode();
        tv1=view.findViewById(R.id.a);
        tv2=view.findViewById(R.id.b);
        floatingActionButton = view.findViewById(R.id.floatingbtn);
        floatingActionButton.bringToFront();
        floatingActionButton.setVisibility(View.VISIBLE);
        if(Helper.habitsdata.size()!=0){
            tv1.setVisibility(View.INVISIBLE);
            tv2.setVisibility(View.INVISIBLE);
        }
        else if(Helper.habitsdata.size()==0){
            tv1.setVisibility(View.VISIBLE);
            tv2.setVisibility(View.VISIBLE);
        }

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                final String formattedDate = df.format(c);
                final Dialog d = new Dialog(getActivity());
                d.requestWindowFeature(Window.FEATURE_NO_TITLE);
                d.setContentView(R.layout.dialogbox_floatingbutton);
                Button btndone=d.findViewById(R.id.btndone);
                WindowManager.LayoutParams lp = d.getWindow().getAttributes();
                lp.dimAmount=0.9f;
                lp.gravity = Gravity.BOTTOM;
                Window window = d.getWindow();
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                d.getWindow().setAttributes(lp);
                d.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

                final Spinner spinner = d.findViewById(R.id.spinner);
                final TextView txt=d.findViewById(R.id.txt);
                if(Helper.labels_array.size()==0){
                    txt.setVisibility(View.VISIBLE);
                    spinner.setVisibility(View.INVISIBLE);
                }
                else{
                    txt.setVisibility(View.INVISIBLE);
                    spinner.setVisibility(View.VISIBLE);
                }


                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,Helper.labels_array);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(arrayAdapter);


                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        catagoryselected = adapterView.getItemAtPosition(i).toString();
//                        if(Helper.isnightmodeon.equals("no")){
                            ((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(R.color.textcolor));
//
//                        }
//                        else if(Helper.isnightmodeon.equals("yes")){
//                            ((TextView) adapterView.getChildAt(0)).setTextColor(Color.WHITE);
//
//                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                Button saveTaskButton = d.findViewById(R.id.saveTaskButton);
                final EditText habit = d.findViewById(R.id.habit);
                final EditText detail = d.findViewById(R.id.detail);

                saveTaskButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String habit_text = habit.getText().toString();
                        String detail_text = detail.getText().toString();
                        //String formattedDate = df.format(c);
                        try {

                        } catch (SQLiteException e) {
                        }
                        if(habit_text.equals("")){

                        }
                        db.insert_habits(Helper.habitsdata.size(),formattedDate, habit_text, detail_text, catagoryselected);
                        db.show_habits_data();

                        Helper.SelectedButtonOfTodayTab=0;
                        Intent i=new Intent(getActivity(), MainActivity.class);
                        startActivity(i);
                        getActivity().overridePendingTransition(0,0);
                        d.dismiss();
                    }

                });
                spinner.setAdapter(arrayAdapter);
                d.show();


            }
        });


//        floatingActionButton.setOnClickListener(new View.OnClickListener() {
//            @SuppressLint("ResourceAsColor")
//            @Override
//            public void onClick(View view) {
//                final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//                final String formattedDate = df.format(c);
//                View bottomsheetview = null;
//                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity(),
//                        R.style.BottomSheetDialogTheme);
//                bottomsheetview = LayoutInflater.from(getActivity().getApplicationContext()).
//                            inflate(R.layout.layout_bottom_sheet,
//                                    (RelativeLayout) view.findViewById(R.id.bottomsheetContainer));
////                if(Helper.isnightmodeon.equals("no")){
////                    bottomsheetview = LayoutInflater.from(getActivity().getApplicationContext()).
////                            inflate(R.layout.layout_bottom_sheet,
////                                    (RelativeLayout) view.findViewById(R.id.bottomsheetContainer));
////                }
////                else if(Helper.isnightmodeon.equals("yes")){
////                    bottomsheetview = LayoutInflater.from(getActivity().getApplicationContext()).
////                            inflate(R.layout.layout_bottom_sheet_nightmode,
////                                    (RelativeLayout) view.findViewById(R.id.bottomsheetContainer));
////                }
//                final Spinner spinner = bottomsheetview.findViewById(R.id.spinner);
//                final TextView txt=bottomsheetview.findViewById(R.id.txt);
//                if(Helper.labels_array.size()==0){
//                    txt.setVisibility(View.VISIBLE);
//                    spinner.setVisibility(View.INVISIBLE);
//                }
//                else{
//                    txt.setVisibility(View.INVISIBLE);
//                    spinner.setVisibility(View.VISIBLE);
//                }
//
//
//                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,Helper.labels_array);
//                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                spinner.setAdapter(arrayAdapter);
//
//
//                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                    @Override
//                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                        catagoryselected = adapterView.getItemAtPosition(i).toString();
//                        if(Helper.isnightmodeon.equals("no")){
//                            ((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLACK);
//
//                        }
//                        else if(Helper.isnightmodeon.equals("yes")){
//                            ((TextView) adapterView.getChildAt(0)).setTextColor(Color.WHITE);
//
//                        }
//                    }
//
//                    @Override
//                    public void onNothingSelected(AdapterView<?> adapterView) {
//
//                    }
//                });
//
//                Button saveTaskButton = bottomsheetview.findViewById(R.id.saveTaskButton);
//                final EditText habit = bottomsheetview.findViewById(R.id.habit);
//                final EditText detail = bottomsheetview.findViewById(R.id.detail);
//
//                saveTaskButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//                        String habit_text = habit.getText().toString();
//                        String detail_text = detail.getText().toString();
//                        //String formattedDate = df.format(c);
//                        try {
//
//                        } catch (SQLiteException e) {
//                        }
//                        if(habit_text.equals("")){
//
//                        }
//                        db.insert_habits(Helper.habitsdata.size(),formattedDate, habit_text, detail_text, catagoryselected);
//                        db.show_habits_data();
//
//                        Helper.SelectedButtonOfTodayTab=0;
//                        Intent i=new Intent(getActivity(), MainActivity.class);
//                        startActivity(i);
//                        getActivity().overridePendingTransition(0,0);
//                        bottomSheetDialog.dismiss();
//                    }
//
//                });
//                spinner.setAdapter((SpinnerAdapter) arrayAdapter);
//                bottomSheetDialog.setContentView(bottomsheetview);
//                bottomSheetDialog.show();
//
//
//            }
//        });
        setDataInList();
        return view;

    }
    public void setDataInList() {
        for (int i = 0; i < Helper.habitsdata.size(); i++) {
            gettingtasks.add(Helper.habitsdata.get(i)[2]);
            gettingcatagory.add(Helper.habitsdata.get(i)[4]);
        }
       rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        new ItemTouchHelper(itemtouchhelper).attachToRecyclerView(rv);
        adapter= new HabitsAdapter(getActivity(), HabitsFragment.this,db,gettingtasks,gettingcatagory);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
    ItemTouchHelper.SimpleCallback itemtouchhelper = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
            if (direction == 8) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                builder1.setMessage("Do you really want to delete this?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                int i = viewHolder.getAdapterPosition();
                                db.delete_habits(i);
                                for (int j = i + 1; j < Helper.habitsdata.size(); j++) {
                                    db.updateHabitsIdsAfterDeletion(Integer.parseInt(Helper.habitsdata.get(j)[0]));
                                }
                                Helper.SelectedButtonOfTodayTab=0;
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                startActivity(intent);
                                getActivity().overridePendingTransition(0, 0);
                                dialog.cancel();
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Helper.SelectedButtonOfTodayTab=0;
                                Intent i = new Intent(getActivity(), MainActivity.class);
                                startActivity(i);
                                getActivity().overridePendingTransition(0, 0);
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();

            }

        }
    };
}