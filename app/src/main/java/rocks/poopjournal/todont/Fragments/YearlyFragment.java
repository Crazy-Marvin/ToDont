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
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.github.mikephil.charting.charts.PieChart;
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

import static java.util.Calendar.getInstance;

public class YearlyFragment extends Fragment {
    Calendar c = getInstance();
    Calendar d = getInstance();
    Calendar e = getInstance();
    SimpleDateFormat df;
    Db_Controller db;
    TextView year, mostavoided, leastavoided, daterange;
    Button btnbefore, btnafter;
    String formattedDate;
    String getmostAvoidedHabit;
    SharedPreferences prefs;
    String checkDate;
    PieChart pieChart;
    String curmonth;
    String[] splitteddate;
    String b_month;
    int b_year;
    double habitsSize, avoidedSize;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_yearly, container, false);
        db = new Db_Controller(getActivity(), "", null, 2);
        df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        prefs = getContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        pieChart = view.findViewById(R.id.pieChart);
        checkDate = prefs.getString("InitialDate", "");
        Helper.SelectedButtonOfLogTab = 3;
        year = view.findViewById(R.id.year);
        pieChart = view.findViewById(R.id.pieChart);
        btnbefore = view.findViewById(R.id.before);
        btnafter = view.findViewById(R.id.after);
        btnbefore.setBackgroundResource(R.drawable.ic_backarrow);
        btnafter.setBackgroundResource(R.drawable.ic_nextarrow);
        daterange = view.findViewById(R.id.daterange);
        mostavoided = view.findViewById(R.id.mostavoided);
        leastavoided = view.findViewById(R.id.leastavoided);
        db.show_habits_data();
        final String cur_date = df.format(c.getTime());
        splitteddate = cur_date.split("-");
        b_year = Integer.parseInt(splitteddate[0]);
        year.setText("" + splitteddate[0]);
        String newDate = splitteddate[0] + "-01-" + "-01";
        String newDate1 = splitteddate[0] + "-12-" + "-31";
        Log.d("splitteddate333 ", "" + newDate + " * " + newDate1);
        String avoided = db.getYearlyAvoidedData(newDate, newDate1);
        String done = db.getYearlyDoneData(newDate, newDate1);
        mostavoided.setText("" + avoided);
        leastavoided.setText("" + done);
        habitsSize = Helper.habitsdata.size() * 365;
        avoidedSize = Helper.avoidedyearlydata.size();
        double per = (avoidedSize / habitsSize) * 100;
        Log.d("percentage", "per : " + per + " yearly" + Helper.avoidedyearlydata.size() + "total" + Helper.habitsdata.size());
        funcPieChart((int) per);
        String[] checkdatea = checkDate.split("-");
        if (year.getText().toString().equals(checkdatea[0])) {
            btnbefore.setEnabled(false);
        }
        else{
            btnbefore.setEnabled(true);
            btnafter.setEnabled(true);
        }
        if(year.getText().toString().equals(splitteddate[0])){
            btnafter.setEnabled(false);
        }
        else{
            btnafter.setEnabled(true);
        }

        btnbefore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnafter.setEnabled(true);
                b_year = b_year - 1;

                year.setText("" + b_year);
                btnbefore.setBackgroundResource(R.drawable.ic_backarrowpressed);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btnbefore.setBackgroundResource(R.drawable.ic_backarrow);
                    }
                }, 100);
                String[] checkdatearray = checkDate.split("-");
                b_year = Integer.parseInt(year.getText().toString());
                Log.d("fff", "" + year.getText().toString() + " ggg  " + checkdatearray[0]);
                if ((year.getText().toString()).equals(checkdatearray[0])) {
                    btnbefore.setEnabled(false);
                }
                String newDate = b_year + "-01" + "-01-";
                String newDate1 = b_year + "-12" + "-31-";
                Log.d("sssssssss", "" + newDate + " * " + newDate1);
                String avoided = db.getYearlyAvoidedData(newDate, newDate1);
                String done = db.getYearlyDoneData(newDate, newDate1);
                mostavoided.setText("" + avoided);
                leastavoided.setText("" + done);
                habitsSize = Helper.habitsdata.size() * 365;
                avoidedSize = Helper.avoidedyearlydata.size();
                double per = (avoidedSize / habitsSize) * 100;
                Log.d("percentage", "per : " + per + " monthly" + Helper.avoidedyearlydata.size() + "total" + Helper.habitsdata.size());
                funcPieChart((int) per);
            }
        });
        btnafter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnbefore.setEnabled(true);
                b_year = b_year + 1;
                year.setText("" + b_year);
                btnbefore.setBackgroundResource(R.drawable.ic_backarrowpressed);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btnbefore.setBackgroundResource(R.drawable.ic_backarrow);
                    }
                }, 100);
                String[] checkdatearray = checkDate.split("-");
                b_year = Integer.parseInt(year.getText().toString());
                final String cur_date = df.format(c.getTime());
                splitteddate = cur_date.split("-");
                if ((year.getText().toString()).equals(splitteddate[0])) {
                    btnafter.setEnabled(false);
                }
                String newDate = b_year + "-01" + "-01-";
                String newDate1 = b_year + "-12" + "-31-";
                String avoided = db.getYearlyAvoidedData(newDate, newDate1);
                String done = db.getYearlyDoneData(newDate, newDate1);
                mostavoided.setText("" + avoided);
                leastavoided.setText("" + done);
                habitsSize = Helper.habitsdata.size() * 365;
                avoidedSize = Helper.avoidedyearlydata.size();
                double per = (avoidedSize / habitsSize) * 100;
                Log.d("percentage", "per : " + per + " monthly" + Helper.avoidedyearlydata.size() + "total" + Helper.habitsdata.size());
                funcPieChart((int) per);
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
        pieChart.setTransparentCircleRadius(50f);
        pieChart.setHoleColor(getResources().getColor(R.color.backgroundcolor));
        pieChart.animateXY(1000, 1000);
    }
}