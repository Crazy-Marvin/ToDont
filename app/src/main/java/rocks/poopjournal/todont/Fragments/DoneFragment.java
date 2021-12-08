package rocks.poopjournal.todont.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import rocks.poopjournal.todont.Db_Controller;
import rocks.poopjournal.todont.Adapters.DoneAdapter;
import rocks.poopjournal.todont.Helper;
import rocks.poopjournal.todont.MainActivity;
import rocks.poopjournal.todont.R;


public class DoneFragment extends Fragment {
    RecyclerView rv;
    ArrayList<String> gettingtasks = new ArrayList<>();
    ArrayList<String> gettinglabels = new ArrayList<>();
    ArrayList<String> gettingcatagory = new ArrayList<>();
    Date c = Calendar.getInstance().getTime();
    String catagoryselected;
    Db_Controller db;
    DoneAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_done, container, false);
        rv = view.findViewById(R.id.rv);
        db = new Db_Controller(getActivity(), "", null, 2);
        db.show_done_data();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

//        floatingActionButton.setOnClickListener(new View.OnClickListener() {
//            @SuppressLint("ResourceAsColor")
//            @Override
//            public void onClick(View view) {
//                final SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
//                String formattedDate = df.format(c);
//                final View bottomsheetview ;
//                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity(),
//                        R.style.BottomSheetDialogTheme);
//                if(Helper.isnightmodeon==true){
//                    bottomsheetview= LayoutInflater.from(getActivity().getApplicationContext()).
//                            inflate(R.layout.layout_bottom_sheet_nightmode,
//                                    (RelativeLayout) view.findViewById(R.id.bottomsheetContainer));
//                }
//                else{
//                    bottomsheetview = LayoutInflater.from(getActivity().getApplicationContext()).
//                            inflate(R.layout.layout_bottom_sheet,
//                                    (RelativeLayout) view.findViewById(R.id.bottomsheetContainer));
//                }
//
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
//
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
//                        String formattedDate = df.format(c);
//                        try {
//
//                        } catch (SQLiteException e) {
//                        }
//
//                        db.insert_done_data(Helper.donedata.size(),formattedDate, habit_text, detail_text, catagoryselected);
//                        db.show_done_data();
//                        Helper.SelectedButtonOfTodayTab=2;
//                        Intent i=new Intent(getActivity(),MainActivity.class);
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
        for (int i = 0; i < Helper.donedata.size(); i++) {
            gettingtasks.add(Helper.donedata.get(i)[2]);
            gettingcatagory.add(Helper.donedata.get(i)[4]);
        }
       rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        new ItemTouchHelper(itemtouchhelper).attachToRecyclerView(rv);
        adapter= new DoneAdapter(getActivity(),DoneFragment.this,db,gettingtasks,gettingcatagory);
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
                                db.delete_done(i);
                                for (int j = i + 1; j < Helper.donedata.size(); j++) {
                                    db.updateDoneIdsAfterDeletion(Integer.parseInt(Helper.donedata.get(j)[0]));
                                }
                                Helper.SelectedButtonOfTodayTab=2;
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
                                Helper.SelectedButtonOfTodayTab=2;
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