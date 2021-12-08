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

public class MonthlyFragment extends Fragment {
    Calendar c = getInstance();
    Calendar d = getInstance();
    Calendar e = getInstance();
    SimpleDateFormat df;
    Db_Controller db;
    TextView date, year, mostavoided, leastavoided, daterange;
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
        View view = inflater.inflate(R.layout.fragment_monthly, container, false);
        db = new Db_Controller(getActivity(), "", null, 2);
        df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        prefs = getContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        pieChart = view.findViewById(R.id.pieChart);
        checkDate = prefs.getString("InitialDate", "");
        Helper.SelectedButtonOfLogTab = 2;
        date = view.findViewById(R.id.date);
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
        setMonth(splitteddate[1]);
        b_year = Integer.parseInt(splitteddate[0]);
        date.setText("" + curmonth);
        year.setText("" + splitteddate[0]);
        String newDate = splitteddate[0] + "-" + splitteddate[1] + "-01";
        String newDate1 = splitteddate[0] + "-" + splitteddate[1] + "-31";
        Log.d("splitteddate333 ", "" + newDate + " * " + newDate1);
        String avoided = db.getMonthlyAvoidedData(newDate, newDate1);
        String done = db.getMonthlyDoneData(newDate, newDate1);
        mostavoided.setText("" + avoided);
        leastavoided.setText("" + done);
        habitsSize = Helper.habitsdata.size() * 30;
        avoidedSize = Helper.avoidedmonthlydata.size();
        double per = (avoidedSize / habitsSize) * 100;
        Log.d("percentage", "per : " + per + " monthly" + Helper.avoidedmonthlydata.size() + "total" + Helper.habitsdata.size());
        funcPieChart((int) per);
        String[] checkdatea = checkDate.split("-");
        setMonth(checkdatea[1]);
        if (year.getText().toString().equals(checkdatea[0]) && date.getText().toString().equals(curmonth)) {
            btnbefore.setEnabled(false);
        }
        else{
            btnbefore.setEnabled(true);
            btnafter.setEnabled(true);
        }
        if(year.getText().toString().equals(splitteddate[0]) && date.getText().toString().equals(curmonth)){
            btnafter.setEnabled(false);
        }
        else{
            btnafter.setEnabled(true);
        }

        btnbefore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnafter.setEnabled(true);
                btnbefore.setBackgroundResource(R.drawable.ic_backarrowpressed);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btnbefore.setBackgroundResource(R.drawable.ic_backarrow);
                    }
                }, 100);
                String[] checkdatearray = checkDate.split("-");
                b_year = Integer.parseInt(year.getText().toString());
                setBeforeMonth(date.getText().toString());
                Log.d("hehehe", "" + checkdatearray[0]);
                Log.d("hehehe2", "" + checkdatearray[1]);
                if ((year.getText().toString()).equals(checkdatearray[0])) {
                    if ((b_month.equals("-" + checkdatearray[1] + "-"))) {
                        btnbefore.setEnabled(false);
                    }
                }
                if (curmonth.equals("December")) {
                    b_year--;
                }
                date.setText("" + curmonth);
                year.setText("" + b_year);
                String newDate = b_year + b_month + "01";
                String newDate1 = b_year + b_month + "31";
                Log.d("sssssssss", "" + newDate + " * " + newDate1);
                String avoided = db.getMonthlyAvoidedData(newDate, newDate1);
                String done = db.getMonthlyDoneData(newDate, newDate1);
                mostavoided.setText("" + avoided);
                leastavoided.setText("" + done);
                habitsSize = Helper.habitsdata.size() * 30;
                avoidedSize = Helper.avoidedmonthlydata.size();
                double per = (avoidedSize / habitsSize) * 100;
                Log.d("percentage", "per : " + per + " monthly" + Helper.avoidedmonthlydata.size() + "total" + Helper.habitsdata.size());
                funcPieChart((int) per);
            }
        });
        btnafter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("currenttttClick", "" + curmonth);
                btnbefore.setEnabled(true);
                btnafter.setBackgroundResource(R.drawable.ic_nextpressed);
                new
                        Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btnafter.setBackgroundResource(R.drawable.ic_nextarrow);
                    }
                }, 100);
                String currdate = df.format(d.getTime());
                String[] currdatearray = currdate.split("-");
                b_year = Integer.parseInt(year.getText().toString());
                Log.d("currenttttdate", "" + date.getText().toString());
                setAfterMonth(date.getText().toString());
                Log.d("currenttttAfterMethod", "" + curmonth);
                Log.d("hehehe", "" + currdatearray[0]);
                Log.d("hehehe2", "" + currdatearray[1]);
                if ((year.getText().toString()).equals(currdatearray[0])) {
                    Log.d("nnn", "curr : " + currdatearray[0] + " year : " + year.getText().toString());
                    if ((b_month.equals("-" + currdatearray[1] + "-"))) {
                        btnafter.setEnabled(false);
                    }
                }
                if (curmonth.equals("January")) {
                    b_year++;
                }
                date.setText("" + curmonth);
                year.setText("" + b_year);
                String newDate = b_year + b_month + "01";
                String newDate1 = b_year + b_month + "31";
                Log.d("sssssssss", "" + newDate + " * " + newDate1);
                String avoided = db.getMonthlyAvoidedData(newDate, newDate1);
                String done = db.getMonthlyDoneData(newDate, newDate1);
                mostavoided.setText("" + avoided);
                leastavoided.setText("" +done);
                habitsSize = Helper.habitsdata.size() * 30;
                avoidedSize = Helper.avoidedmonthlydata.size();
                double per = (avoidedSize / habitsSize) * 100;
                Log.d("percentage", "per : " + per + " monthly" + Helper.avoidedmonthlydata.size() + "total" + Helper.habitsdata.size());
                funcPieChart((int) per);
            }
        });

        return view;
    }

    public void setMonth(String s) {
        switch (s) {
            case "01":
                curmonth = "January";
                break;
            case "02":
                curmonth = "February";
                break;
            case "03":
                curmonth = "March";
                break;
            case "04":
                curmonth = "April";
                break;
            case "05":
                curmonth = "May";
                break;
            case "06":
                curmonth = "June";
                break;
            case "07":
                curmonth = "July";
                break;
            case "08":
                curmonth = "August";
                break;
            case "09":
                curmonth = "September";
                break;
            case "10":
                curmonth = "October";
                break;
            case "11":
                curmonth = "November";
                break;
            case "12":
                curmonth = "December";
                break;
            default:
                curmonth = "";
                break;


        }
    }

    public void setBeforeMonth(String s) {
        switch (s) {
            case "January":
                curmonth = "December";
                b_month = "-12-";
                break;
            case "February":
                curmonth = "January";
                b_month = "-01-";
                break;
            case "March":
                curmonth = "February";
                b_month = "-02-";
                break;
            case "April":
                curmonth = "March";
                b_month = "-03-";
                break;
            case "May":
                curmonth = "April";
                b_month = "-04-";
                break;
            case "June":
                curmonth = "May";
                b_month = "-05-";
                break;
            case "July":
                curmonth = "June";
                b_month = "-06-";
                break;
            case "August":
                curmonth = "July";
                b_month = "-07-";
                break;
            case "September":
                curmonth = "August";
                b_month = "-08-";
                break;
            case "October":
                curmonth = "September";
                b_month = "-09-";
                break;
            case "November":
                curmonth = "October";
                b_month = "-10-";
                break;
            case "December":
                curmonth = "November";
                b_month = "-11-";
                break;
            default:
                curmonth = "";
                break;
        }

    }

    public void setAfterMonth(String s) {
        switch (s) {
            case "December":
                curmonth = "January";
                b_month = "-01-";
                break;
            case "January":
                curmonth = "February";
                b_month = "-02-";
                break;
            case "February":
                curmonth = "March";
                b_month = "-03-";
                break;
            case "March":
                curmonth = "April";
                b_month = "-04-";
                break;
            case "April":
                curmonth = "May";
                b_month = "-05-";
                break;
            case "May":
                curmonth = "June";
                b_month = "-06-";
                break;
            case "June":
                curmonth = "July";
                b_month = "-07-";
                break;
            case "July":
                curmonth = "August";
                b_month = "-08-";
                break;
            case "August":
                curmonth = "September";
                b_month = "-09-";
                break;
            case "September":
                curmonth = "October";
                b_month = "-10-";
                break;
            case "October":
                curmonth = "November";
                b_month = "-11-";
                break;
            case "November":
                curmonth = "December";
                b_month = "-12-";
                break;
            default:
                curmonth = "";
                break;
        }

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
}