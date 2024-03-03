package rocks.poopjournal.todont.Fragments;

import static java.util.Calendar.getInstance;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import rocks.poopjournal.todont.Db_Controller;
import rocks.poopjournal.todont.Helper;
import rocks.poopjournal.todont.R;
import rocks.poopjournal.todont.databinding.FragmentDailyBinding;

public class DailyFragment extends Fragment {
    private FragmentDailyBinding binding;

    ProgressBar progressBar;
    Calendar c = getInstance();
    Calendar d = getInstance();
    String formattedDate;
    Db_Controller db;
    double habitsSize, avoidedSize, doneSize;
    int avoidedPercentage, donePercentage;
    SharedPreferences prefs;
    String checkDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDailyBinding.inflate(inflater, container, false);
        prefs = getContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        checkDate = prefs.getString("InitialDate", "");
        Helper.SelectedButtonOfLogTab = 1;
        db = new Db_Controller(getActivity(), "", null, 2);
        db.show_habits_data();
        final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        binding.date.setText("" + df.format(c.getTime()));
        formattedDate = df.format(c.getTime());
        Helper.getSelecteddate = formattedDate;
        db.getDailyAvoidedRecord(Helper.getSelecteddate);
        db.getDailyDoneRecord(Helper.getSelecteddate);
        habitsSize = Helper.habitsdata.size();
        avoidedSize = Helper.avoidedlogdata.size();
        doneSize = Helper.donelogdata.size();
        avoidedPercentage = (int) ((avoidedSize / habitsSize) * 100);
        binding.percentage.setText(avoidedPercentage + "% Avoided");
        Log.d("qqqqqq", "" + avoidedPercentage);
        if (avoidedPercentage == 100) {
            binding.progressText.setText((int) avoidedSize + " out of " + (int) habitsSize + " habits are avoided");
        }
        else {
            binding.progressText.setText((int) avoidedSize + " out of " + (int) habitsSize + " habits are avoided, way to go!");
        }
        binding.percentage.setText(avoidedPercentage + "% Avoided");
        binding.before.setBackgroundResource(R.drawable.ic_backarrow);
        binding.after.setBackgroundResource(R.drawable.ic_nextarrow);
        if (Helper.SelectedButtonOfLogDailyTab == 0) {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.containerLogDailyFragment, new HabitsLogFragment());
            ft.commit();
        }
        funcPieChart(avoidedPercentage);
        if (binding.date.getText().toString().equals(checkDate)) {
            binding.before.setEnabled(false);
            binding.before.setEnabled(false);
        }
        binding.before.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.before.setBackgroundResource(R.drawable.ic_backarrowpressed);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.before.setBackgroundResource(R.drawable.ic_backarrow);
                    }
                }, 100);
                binding.after.setEnabled(true);
                c.add(Calendar.DATE, -1);
                formattedDate = df.format(c.getTime());
                if (binding.date.getText().toString().equals(checkDate)) {
                    binding.before.setEnabled(false);
                } else {
                    Helper.getSelecteddate = formattedDate;
                    binding.date.setText("" + formattedDate);
                    db.getDailyAvoidedRecord(Helper.getSelecteddate);
                    db.getDailyDoneRecord(Helper.getSelecteddate);
                    habitsSize = Helper.habitsdata.size();
                    avoidedSize = Helper.avoidedlogdata.size();
                    avoidedPercentage = (int) ((avoidedSize / habitsSize) * 100);
                    if (avoidedPercentage == 100) {
                        binding.progressText.setText((int) avoidedSize + " out of " + (int) habitsSize + " habits are avoided");
                    } else {
                        binding.progressText.setText((int) avoidedSize + " out of " + (int) habitsSize + " habits are avoided, way to go!");
                    }
                    binding.percentage.setText(avoidedPercentage + "% Avoided");
                    funcPieChart(avoidedPercentage);
                }


                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                if (Helper.SelectedButtonOfLogDailyTab == 0) {
                    ft.replace(R.id.containerLogDailyFragment, new HabitsLogFragment());

                } else if (Helper.SelectedButtonOfLogDailyTab == 1) {
                    ft.replace(R.id.containerLogDailyFragment, new AvoidedLogFragment());

                } else if (Helper.SelectedButtonOfLogDailyTab == 2) {
                    ft.replace(R.id.containerLogDailyFragment, new DoneLogFragment());
                }
                ft.commit();
            }
        });
        binding.after.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.after.setBackgroundResource(R.drawable.ic_nextpressed);
                new
                        Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.after.setBackgroundResource(R.drawable.ic_nextarrow);
                    }
                }, 100);
                binding.before.setEnabled(true);
                String currdate = df.format(d.getTime());
                c.add(Calendar.DATE, +1);
                formattedDate = df.format(c.getTime());
                if (binding.date.getText().toString().equals(currdate)) {
                    binding.after.setEnabled(false);
                } else {
                    Helper.getSelecteddate = formattedDate;
                    binding.date.setText("" + formattedDate);
                    db.getDailyAvoidedRecord(Helper.getSelecteddate);
                    db.getDailyDoneRecord(Helper.getSelecteddate);
                    habitsSize = Helper.habitsdata.size();
                    avoidedSize = Helper.avoidedlogdata.size();
                    avoidedPercentage = (int) ((avoidedSize / habitsSize) * 100);
                    Log.d("qqqqqq", "" + avoidedPercentage);
                    if (avoidedPercentage == 100) {
                        binding.progressText.setText((int) avoidedSize + " out of " + (int) habitsSize + " habits are avoided");
                    } else {
                        binding.progressText.setText((int) avoidedSize + " out of " + (int) habitsSize + " habits are avoided, way to go!");
                    }
                    binding.percentage.setText(avoidedPercentage + "% Avoided");
                    funcPieChart(avoidedPercentage);
                }

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                if (Helper.SelectedButtonOfLogDailyTab == 0) {
                    ft.replace(R.id.containerLogDailyFragment, new HabitsLogFragment());

                } else if (Helper.SelectedButtonOfLogDailyTab == 1) {
                    ft.replace(R.id.containerLogDailyFragment, new AvoidedLogFragment());

                } else if (Helper.SelectedButtonOfLogDailyTab == 2) {
                    ft.replace(R.id.containerLogDailyFragment, new DoneLogFragment());

                }
                ft.commit();
            }
        });
        binding.dhabits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.dhabits.setBackgroundColor(getResources().getColor(R.color.spinnercolor));
                binding.davoided.setBackgroundColor(Color.TRANSPARENT);
                binding.ddone.setBackgroundColor(Color.TRANSPARENT);
                Helper.SelectedButtonOfLogDailyTab = 0;
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.containerLogDailyFragment, new HabitsLogFragment());
                ft.commit();
            }
        });
        binding.davoided.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.dhabits.setBackgroundColor(Color.TRANSPARENT);
                binding.davoided.setBackgroundColor(getResources().getColor(R.color.spinnercolor));
                binding.ddone.setBackgroundColor(Color.TRANSPARENT);
                Helper.SelectedButtonOfLogDailyTab = 1;
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.containerLogDailyFragment, new AvoidedLogFragment());
                ft.commit();
            }
        });
        binding.ddone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.dhabits.setBackgroundColor(Color.TRANSPARENT);
                binding.davoided.setBackgroundColor(Color.TRANSPARENT);
                binding.ddone.setBackgroundColor(getResources().getColor(R.color.spinnercolor));
                Helper.SelectedButtonOfLogDailyTab = 2;
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.containerLogDailyFragment, new DoneLogFragment());
                ft.commit();
            }
        });
        return binding.getRoot();
    }


    public void funcPieChart(int avoidedPer) {
        binding.pieChart.setUsePercentValues(true);
        List<PieEntry> value = new ArrayList<>();
        value.add(new PieEntry((float) avoidedPer, "Avoided"));
        value.add(new PieEntry((float) (100.0 - avoidedPer), "Habits"));
        PieDataSet pieDataSet = new PieDataSet(value, "");
        pieDataSet.setValueTextColor(Color.WHITE);
        PieData pieData = new PieData(pieDataSet);
        binding.pieChart.setData(pieData);
        pieDataSet.setColors(Color.parseColor("#FFAF01"), Color.parseColor("#26272c"));
        Legend legend = binding.pieChart.getLegend();
        legend.setEnabled(false);
        Description description = new Description();
        description.setText("");
        binding.pieChart.setDescription(description);
        binding.pieChart.setHoleRadius(50f);
        binding.pieChart.setHoleColor(getResources().getColor(R.color.backgroundcolor));
        binding.pieChart.setTransparentCircleRadius(50f);
        binding.pieChart.animateXY(1000, 1000);
    }
//#ed8709

}