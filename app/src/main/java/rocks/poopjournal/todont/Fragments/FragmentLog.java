package rocks.poopjournal.todont.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ornach.nobobutton.NoboButton;

import rocks.poopjournal.todont.Db_Controller;
import rocks.poopjournal.todont.Helper;
import rocks.poopjournal.todont.R;

public class FragmentLog extends Fragment {
    NoboButton btnday, btnweek, btnmonth, btnyear;
    Db_Controller db;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_log2, container, false);
        db = new Db_Controller(getContext(), "", null, 2);
        Helper.isTodaySelected = false;
        Helper.SelectedButtonOfLogTab = 0;
        btnday = view.findViewById(R.id.day);
        btnmonth = view.findViewById(R.id.month);
        btnweek = view.findViewById(R.id.week);
        btnyear = view.findViewById(R.id.year);
        btnday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnday.setBackgroundColor(getResources().getColor(R.color.bottom_sheet_background));
                btnweek.setBackgroundColor(Color.TRANSPARENT);
                btnmonth.setBackgroundColor(Color.TRANSPARENT);
                btnyear.setBackgroundColor(Color.TRANSPARENT);
                Helper.SelectedButtonOfLogTab = 0;
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.containerLogFragment, new DailyFragment());
                ft.commit();
            }
        });
        btnweek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnday.setBackgroundColor(Color.TRANSPARENT);
                btnweek.setBackgroundColor(getResources().getColor(R.color.bottom_sheet_background));
                btnmonth.setBackgroundColor(Color.TRANSPARENT);
                btnyear.setBackgroundColor(Color.TRANSPARENT);
                Helper.SelectedButtonOfLogTab = 1;
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.containerLogFragment, new WeeklyFragment());
                ft.commit();
            }
        });
        btnmonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnday.setBackgroundColor(Color.TRANSPARENT);
                btnweek.setBackgroundColor(Color.TRANSPARENT);
                btnmonth.setBackgroundColor(getResources().getColor(R.color.bottom_sheet_background));
                btnyear.setBackgroundColor(Color.TRANSPARENT);
                Helper.SelectedButtonOfLogTab = 2;
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.containerLogFragment, new MonthlyFragment());
                ft.commit();
            }
        });
        btnyear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnday.setBackgroundColor(Color.TRANSPARENT);
                btnweek.setBackgroundColor(Color.TRANSPARENT);
                btnmonth.setBackgroundColor(Color.TRANSPARENT);
                btnyear.setBackgroundColor(getResources().getColor(R.color.bottom_sheet_background));
                Helper.SelectedButtonOfLogTab = 3;
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.containerLogFragment, new YearlyFragment());
                ft.commit();
            }
        });

        if (Helper.SelectedButtonOfLogTab == 0) {
            btnday.setBackgroundColor(getResources().getColor(R.color.bottom_sheet_background));
            btnweek.setBackgroundColor(Color.TRANSPARENT);
            btnmonth.setBackgroundColor(Color.TRANSPARENT);
            btnyear.setBackgroundColor(Color.TRANSPARENT);
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.containerLogFragment, new DailyFragment());
            ft.commit();
        } else if (Helper.SelectedButtonOfLogTab == 1) {
            btnday.setBackgroundColor(Color.TRANSPARENT);
            btnweek.setBackgroundColor(getResources().getColor(R.color.bottom_sheet_background));
            btnmonth.setBackgroundColor(Color.TRANSPARENT);
            btnyear.setBackgroundColor(Color.TRANSPARENT);
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.containerLogFragment, new WeeklyFragment());
            ft.commit();
        } else if (Helper.SelectedButtonOfLogTab == 2) {
            btnday.setBackgroundColor(Color.TRANSPARENT);
            btnweek.setBackgroundColor(Color.TRANSPARENT);
            btnmonth.setBackgroundColor(getResources().getColor(R.color.bottom_sheet_background));
            btnyear.setBackgroundColor(Color.TRANSPARENT);
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.containerLogFragment, new MonthlyFragment());
            ft.commit();
        } else if (Helper.SelectedButtonOfLogTab == 3) {
            btnday.setBackgroundColor(Color.TRANSPARENT);
            btnweek.setBackgroundColor(Color.TRANSPARENT);
            btnmonth.setBackgroundColor(Color.TRANSPARENT);
            btnyear.setBackgroundColor(getResources().getColor(R.color.bottom_sheet_background));
        }
        return view;
    }
}