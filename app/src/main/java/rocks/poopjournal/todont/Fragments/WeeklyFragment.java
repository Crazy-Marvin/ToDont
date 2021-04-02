package rocks.poopjournal.todont.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

public class WeeklyFragment extends Fragment {
    Calendar c = getInstance();
    Calendar d = getInstance();
    SimpleDateFormat df;
    Db_Controller db;
    TextView date, mostavoided, leastavoided, daterange;
    Button btnbefore, btnafter;
    SharedPreferences prefs;
    String checkDate;
    PieChart pieChart;
    int count = 0;
    double habitsSize, avoidedSize;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weekly, container, false);
        db = new Db_Controller(getActivity(), "", null, 2);
        df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        prefs = getContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        pieChart = view.findViewById(R.id.pieChart);
        checkDate = prefs.getString("InitialDate", "");
        Helper.SelectedButtonOfLogTab = 1;
        date = view.findViewById(R.id.date);
        btnbefore = view.findViewById(R.id.before);
        btnafter = view.findViewById(R.id.after);
        btnbefore.setBackgroundResource(R.drawable.ic_backarrow);
        btnafter.setBackgroundResource(R.drawable.ic_nextarrow);
        daterange = view.findViewById(R.id.daterange);
        date.setText("" + df.format(c.getTime()));
        mostavoided = view.findViewById(R.id.mostavoided);
        leastavoided = view.findViewById(R.id.leastavoided);
        db.show_habits_data();

        for (int i = 0; i < 7; i++) {
            if (df.format(c.getTime()).equals(checkDate)) {

                break;
            } else {
                c.add(Calendar.DATE, -1);
            }
        }
        String get = db.getWeeklyAvoidedRecord(df.format(c.getTime()), date.getText().toString());
        String getdone = db.getWeeklyDoneRecord(df.format(c.getTime()), date.getText().toString());
        daterange.setText(df.format(c.getTime()) + " To " + date.getText().toString());

        mostavoided.setText("" + get);
        leastavoided.setText("" + getdone);

        habitsSize = Helper.habitsdata.size() * 7;
        avoidedSize = Helper.avoidedweeklydata.size();
        double per = (avoidedSize / habitsSize) * 100;
        Log.d("percentage", "per : " + per + " weekly" + Helper.avoidedweeklydata.size() + "total" + Helper.habitsdata.size());
        funcPieChart((int) per);

        btnbefore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(count!=0){
                    count=0;
                }
                btnafter.setEnabled(true);
                btnbefore.setBackgroundResource(R.drawable.ic_backarrowpressed);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btnbefore.setBackgroundResource(R.drawable.ic_backarrow);
                    }
                }, 100);
                btnafter.setEnabled(true);
                date.setText("" + df.format(c.getTime()));

                for (int i = 0; i < 7; i++) {
                    if (df.format(c.getTime()).equals(checkDate)) {
                       //nothing
                        break;
                    } else {
                        c.add(Calendar.DATE, -1);
                        count++;
                    }
                }
                String get = db.getWeeklyAvoidedRecord(df.format(c.getTime()), date.getText().toString());
                String getdone = db.getWeeklyDoneRecord(df.format(c.getTime()), date.getText().toString());
                daterange.setText(df.format(c.getTime()) + " To " + date.getText().toString());
                mostavoided.setText("" + get);
                leastavoided.setText("" + getdone);
                habitsSize = Helper.habitsdata.size() * 7;
                avoidedSize = Helper.avoidedweeklydata.size();
                double per = (avoidedSize / habitsSize) * 100;
                Log.d("percentage", "per : " + per + " weekly" + Helper.avoidedweeklydata.size() + "total" + Helper.habitsdata.size());
                funcPieChart((int) per);
//

            }
        });
        btnafter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String abhikidate=date.getText().toString();
                c.add(Calendar.DATE,count);
                String currdate = df.format(d.getTime());
                btnbefore.setEnabled(true);
                btnafter.setBackgroundResource(R.drawable.ic_nextpressed);
                new
                        Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btnafter.setBackgroundResource(R.drawable.ic_nextarrow);
                    }
                }, 100);
                for (int i = 0; i < 7; i++) {
                    if (df.format(c.getTime()).equals(currdate)) {
                     //nothing
                        break;
                    } else {
                        c.add(Calendar.DATE, +1);
                    }
                }
                date.setText("" + df.format(c.getTime()));

                String get = db.getWeeklyAvoidedRecord(abhikidate, df.format(c.getTime()));
                String getdone = db.getWeeklyDoneRecord(abhikidate, df.format(c.getTime()));
                daterange.setText( abhikidate+ " To " + df.format(c.getTime()));
                mostavoided.setText("" + get);
                leastavoided.setText("" + getdone);
                habitsSize = Helper.habitsdata.size() * 7;
                avoidedSize = Helper.avoidedweeklydata.size();
                double per = (avoidedSize / habitsSize) * 100;
                Log.d("percentage", "per : " + per + " weekly" + Helper.avoidedweeklydata.size() + "total" + Helper.habitsdata.size());
                funcPieChart((int) per);
                count=0;
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
}