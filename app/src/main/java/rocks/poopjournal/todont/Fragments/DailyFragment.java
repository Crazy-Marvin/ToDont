package rocks.poopjournal.todont.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.ornach.nobobutton.NoboButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import rocks.poopjournal.todont.Db_Controller;
import rocks.poopjournal.todont.Helper;
import rocks.poopjournal.todont.R;

import static java.util.Calendar.getInstance;

public class DailyFragment extends Fragment {
    TextView date, progressText, percentage;
    Button btnbefore, btnafter;
    NoboButton habits, avoided, done;
    ProgressBar progressBar;
    Calendar c = getInstance();
    Calendar d = getInstance();
    String formattedDate;
    Db_Controller db;
    double habitsSize, avoidedSize, doneSize;
    PieChart pieChart;
    int avoidedPercentage, donePercentage;
    SharedPreferences prefs;
    String checkDate;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily, container, false);
        prefs = getContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        checkDate = prefs.getString("InitialDate", "");
        Helper.SelectedButtonOfLogTab = 1;
        db = new Db_Controller(getActivity(), "", null, 2);
        db.show_habits_data();
        final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        date = view.findViewById(R.id.date);
        progressText = view.findViewById(R.id.progressText);
        percentage = view.findViewById(R.id.percentage);
        btnbefore = view.findViewById(R.id.before);
        btnafter = view.findViewById(R.id.after);
        habits = view.findViewById(R.id.dhabits);
        avoided = view.findViewById(R.id.davoided);
        done = view.findViewById(R.id.ddone);
        date.setText("" + df.format(c.getTime()));
        pieChart = view.findViewById(R.id.pieChart);
        formattedDate = df.format(c.getTime());
        Helper.getSelecteddate = formattedDate;
        db.getDailyAvoidedRecord(Helper.getSelecteddate);
        db.getDailyDoneRecord(Helper.getSelecteddate);
        habitsSize = Helper.habitsdata.size();
        avoidedSize = Helper.avoidedlogdata.size();
        doneSize = Helper.donelogdata.size();
        avoidedPercentage = (int) ((avoidedSize / habitsSize) * 100);
        percentage.setText(avoidedPercentage + "% Avoided");
        Log.d("qqqqqq",""+avoidedPercentage);
        if(avoidedPercentage==100){
            progressText.setText((int) avoidedSize + " out of " + (int) habitsSize + " habits are avoided");
        }
        else{
            progressText.setText((int) avoidedSize + " out of " + (int) habitsSize + " habits are avoided, way to go!");
        }
        percentage.setText(avoidedPercentage + "% Avoided");
        btnbefore.setBackgroundResource(R.drawable.ic_backarrow);
        btnafter.setBackgroundResource(R.drawable.ic_nextarrow);
        if (Helper.SelectedButtonOfLogDailyTab == 0) {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.containerLogDailyFragment, new HabitsLogFragment());
            ft.commit();
        }
        funcPieChart(avoidedPercentage);
        if(date.getText().toString().equals(checkDate)){
            btnbefore.setEnabled(false);
            btnafter.setEnabled(false);
        }
//
        btnbefore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnbefore.setBackgroundResource(R.drawable.ic_backarrowpressed);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btnbefore.setBackgroundResource(R.drawable.ic_backarrow);
                    }
                }, 100);
                btnafter.setEnabled(true);
                c.add(Calendar.DATE, -1);
                formattedDate = df.format(c.getTime());
                if(date.getText().toString().equals(checkDate)){
                    btnbefore.setEnabled(false);
                }
                else{
                    Helper.getSelecteddate = formattedDate;
                    date.setText("" + formattedDate);
                    db.getDailyAvoidedRecord(Helper.getSelecteddate);
                    db.getDailyDoneRecord(Helper.getSelecteddate);
                    habitsSize = Helper.habitsdata.size();
                    avoidedSize = Helper.avoidedlogdata.size();
                    avoidedPercentage = (int) ((avoidedSize / habitsSize) * 100);
                    if(avoidedPercentage==100){
                        progressText.setText((int) avoidedSize + " out of " + (int) habitsSize + " habits are avoided");
                    }
                    else{
                        progressText.setText((int) avoidedSize + " out of " + (int) habitsSize + " habits are avoided, way to go!");
                    }
                    percentage.setText(avoidedPercentage + "% Avoided");
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
        btnafter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnafter.setBackgroundResource(R.drawable.ic_nextpressed);
                new
                        Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btnafter.setBackgroundResource(R.drawable.ic_nextarrow);
                    }
                }, 100);
                btnbefore.setEnabled(true);
                String currdate=df.format(d.getTime());
                c.add(Calendar.DATE, +1);
                formattedDate = df.format(c.getTime());
                if(date.getText().toString().equals(currdate)){
                    btnafter.setEnabled(false);
                }
               else{
                    Helper.getSelecteddate = formattedDate;
                    date.setText("" + formattedDate);
                    db.getDailyAvoidedRecord(Helper.getSelecteddate);
                    db.getDailyDoneRecord(Helper.getSelecteddate);
                    habitsSize = Helper.habitsdata.size();
                    avoidedSize = Helper.avoidedlogdata.size();
                    avoidedPercentage = (int) ((avoidedSize / habitsSize) * 100);
                    Log.d("qqqqqq",""+avoidedPercentage);
                    if(avoidedPercentage==100){
                        progressText.setText((int) avoidedSize + " out of " + (int) habitsSize + " habits are avoided");
                    }
                    else{
                        progressText.setText((int) avoidedSize + " out of " + (int) habitsSize + " habits are avoided, way to go!");
                    }
                    percentage.setText(avoidedPercentage + "% Avoided");
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
        habits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                habits.setBackgroundColor(getResources().getColor(R.color.spinnercolor));
                avoided.setBackgroundColor(Color.TRANSPARENT);
                done.setBackgroundColor(Color.TRANSPARENT);
                Helper.SelectedButtonOfLogDailyTab = 0;
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.containerLogDailyFragment, new HabitsLogFragment());
                ft.commit();
            }
        });
        avoided.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                habits.setBackgroundColor(Color.TRANSPARENT);
                avoided.setBackgroundColor(getResources().getColor(R.color.spinnercolor));
                done.setBackgroundColor(Color.TRANSPARENT);
                Helper.SelectedButtonOfLogDailyTab = 1;
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.containerLogDailyFragment, new AvoidedLogFragment());
                ft.commit();
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                habits.setBackgroundColor(Color.TRANSPARENT);
                avoided.setBackgroundColor(Color.TRANSPARENT);
                done.setBackgroundColor(getResources().getColor(R.color.spinnercolor));
                Helper.SelectedButtonOfLogDailyTab = 2;
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.containerLogDailyFragment, new DoneLogFragment());
                ft.commit();
            }
        });
        return view;
    }

    public void funcPieChart(int avoidedPer) {
        pieChart.setUsePercentValues(true);
        List<PieEntry> value = new ArrayList<>();
        value.add(new PieEntry((float) avoidedPer, "Avoided"));
        value.add(new PieEntry((float) (100.0 - avoidedPer), "Habits"));
        PieDataSet pieDataSet = new PieDataSet(value, "");
        pieDataSet.setValueTextColor(Color.WHITE);
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieDataSet.setColors(new int[]{Color.parseColor("#FFAF01"), Color.parseColor("#26272c")});
        Legend legend = pieChart.getLegend();
        legend.setEnabled(false);
        Description description = new Description();
        description.setText("");
        pieChart.setDescription(description);
        pieChart.setHoleRadius(50f);
        pieChart.setHoleColor(getResources().getColor(R.color.backgroundcolor));
        pieChart.setTransparentCircleRadius(50f);
        pieChart.animateXY(1000, 1000);
    }
//#ed8709

}