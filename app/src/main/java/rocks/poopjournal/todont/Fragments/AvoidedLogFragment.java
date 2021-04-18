package rocks.poopjournal.todont.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import rocks.poopjournal.todont.Adapters.AvoidedLogAdapter;
import rocks.poopjournal.todont.Db_Controller;
import rocks.poopjournal.todont.Helper;
import rocks.poopjournal.todont.MainActivity;
import rocks.poopjournal.todont.R;

public class AvoidedLogFragment extends Fragment {
    RecyclerView rv;
    ArrayList<String> gettingtasks = new ArrayList<>();
    ArrayList<String> gettingcatagory = new ArrayList<>();
    Db_Controller db;
    AvoidedLogAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_avoided, container, false);
        rv = view.findViewById(R.id.rv);
        db = new Db_Controller(getActivity(), "", null, 2);
        db.getDailyAvoidedRecord(Helper.getSelecteddate);
        db.getDailyDoneRecord(Helper.getSelecteddate);
        setDataInList();
        return view;
    }

    public void setDataInList() {
        for (int i = 0; i < Helper.avoidedlogdata.size(); i++) {
            gettingtasks.add(Helper.avoidedlogdata.get(i)[2]);
            gettingcatagory.add(Helper.avoidedlogdata.get(i)[4]);
        }
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new AvoidedLogAdapter(getActivity(), AvoidedLogFragment.this, db, gettingtasks, gettingcatagory);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));

    }

}



