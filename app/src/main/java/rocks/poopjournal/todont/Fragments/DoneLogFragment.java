package rocks.poopjournal.todont.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import rocks.poopjournal.todont.Adapters.DoneLogAdapter;
import rocks.poopjournal.todont.Db_Controller;
import rocks.poopjournal.todont.Helper;
import rocks.poopjournal.todont.R;


public class DoneLogFragment extends Fragment {
    RecyclerView rv;
    ArrayList<String> gettingtasks = new ArrayList<>();
    ArrayList<String> gettingcatagory = new ArrayList<>();
    Db_Controller db;
    DoneLogAdapter adapter;
    TextView tv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_done, container, false);
        rv = view.findViewById(R.id.rv);
        db = new Db_Controller(getActivity(), "", null, 2);
        db.getDailyAvoidedRecord(Helper.getSelecteddate);
        db.getDailyDoneRecord(Helper.getSelecteddate);
        Log.d("yeraha2","habit "+Helper.habitsdata.size()+" avoided "+Helper.avoidedlogdata.size()
                + " done "+Helper.donelogdata.size());
        setDataInList();
        return view;

    }

    public void setDataInList() {
        for (int i = 0; i < Helper.donelogdata.size(); i++) {
            gettingtasks.add(Helper.donelogdata.get(i)[2]);
            gettingcatagory.add(Helper.donelogdata.get(i)[4]);
        }
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new DoneLogAdapter(getActivity(), DoneLogFragment.this, db, gettingtasks, gettingcatagory);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

}