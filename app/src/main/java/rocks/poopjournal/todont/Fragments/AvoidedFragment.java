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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import rocks.poopjournal.todont.Adapters.AvoidedAdapter;
import rocks.poopjournal.todont.Db_Controller;
import rocks.poopjournal.todont.Helper;
import rocks.poopjournal.todont.MainActivity;
import rocks.poopjournal.todont.R;

public class AvoidedFragment extends Fragment {
    RecyclerView rv;
    ArrayList<String> gettingtasks = new ArrayList<>();
    ArrayList<String> gettingcatagory = new ArrayList<>();
    Date c = Calendar.getInstance().getTime();
    String catagoryselected = "";
    Db_Controller db;
    AvoidedAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_avoided, container, false);
        //Helper.SelectedButtonOfTodayTab=true;
        rv = view.findViewById(R.id.rv);
        db = new Db_Controller(getActivity(), "", null, 2);
        db.show_labels();
        db.show_data();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        setDataInList();
        return view;
    }

    public void setDataInList() {
        for (int i = 0; i < Helper.data.size(); i++) {
            gettingtasks.add(Helper.data.get(i)[2]);
            gettingcatagory.add(Helper.data.get(i)[4]);
        }
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        new ItemTouchHelper(itemtouchhelper).attachToRecyclerView(rv);
        adapter = new AvoidedAdapter(getActivity(), AvoidedFragment.this, db, gettingtasks, gettingcatagory);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        //rv.setAdapter(new AvoidedAdapter(getActivity(), AvoidedFragment.this, db, gettingtasks, gettingcatagory));

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
                                Log.d("aaaasize",""+Helper.data.size());
                                db.delete_avoided(i);
                                for (int j = i + 1; j < Helper.data.size(); j++) {
                                    Log.d("aaaaforloop",""+Integer.parseInt(Helper.data.get(j)[0]));
                                    db.updateIdsAfterDeletion(Integer.parseInt(Helper.data.get(j)[0]));
                                }
                                Helper.SelectedButtonOfTodayTab = 1;
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



