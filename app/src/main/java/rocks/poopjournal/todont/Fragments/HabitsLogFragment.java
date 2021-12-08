package rocks.poopjournal.todont.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

import rocks.poopjournal.todont.Adapters.HabitsAdapter;
import rocks.poopjournal.todont.Adapters.HabitsLogAdapter;
import rocks.poopjournal.todont.Db_Controller;
import rocks.poopjournal.todont.Helper;
import rocks.poopjournal.todont.MainActivity;
import rocks.poopjournal.todont.R;


public class HabitsLogFragment extends Fragment {
    RecyclerView rv;
    ArrayList<String> gettingtasks = new ArrayList<>();
    ArrayList<String> gettingcatagory = new ArrayList<>();
    Db_Controller db;
    HabitsLogAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_log_habits, container, false);
        rv = view.findViewById(R.id.rv);
        db = new Db_Controller(getActivity(), "", null, 2);
        db.show_labels();
        db.show_habits_data();
        setDataInList();
        return view;

    }
    public void setDataInList() {
        for (int i = 0; i < Helper.habitsdata.size(); i++) {
            gettingtasks.add(Helper.habitsdata.get(i)[2]);
            gettingcatagory.add(Helper.habitsdata.get(i)[4]);
        }
       rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        Log.d("catttt",""+gettingcatagory);
        adapter= new HabitsLogAdapter(getActivity(), HabitsLogFragment.this,db,gettingtasks,gettingcatagory);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}