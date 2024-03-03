package rocks.poopjournal.todont.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import rocks.poopjournal.todont.Db_Controller;
import rocks.poopjournal.todont.Helper;
import rocks.poopjournal.todont.R;
import rocks.poopjournal.todont.databinding.FragmentLog2Binding;

public class FragmentLog extends Fragment {
    Db_Controller db;
    private FragmentLog2Binding binding;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLog2Binding.inflate(inflater, container, false);

        View view = inflater.inflate(R.layout.fragment_log2, container, false);
        db = new Db_Controller(getContext(), "", null, 2);
        Helper.isTodaySelected = false;
        Helper.SelectedButtonOfLogTab = 0;

        binding.day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.day.setBackgroundColor(getResources().getColor(R.color.bottom_sheet_background));
                binding.week.setBackgroundColor(Color.TRANSPARENT);
                binding.month.setBackgroundColor(Color.TRANSPARENT);
                binding.year.setBackgroundColor(Color.TRANSPARENT);
                Helper.SelectedButtonOfLogTab = 0;
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.containerLogFragment, new DailyFragment());
                ft.commit();
            }
        });
        binding.week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.day.setBackgroundColor(Color.TRANSPARENT);
                binding.week.setBackgroundColor(getResources().getColor(R.color.bottom_sheet_background));
                binding.month.setBackgroundColor(Color.TRANSPARENT);
                binding.year.setBackgroundColor(Color.TRANSPARENT);
                Helper.SelectedButtonOfLogTab = 1;
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.containerLogFragment, new WeeklyFragment());
                ft.commit();
            }
        });
        binding.month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.day.setBackgroundColor(Color.TRANSPARENT);
                binding.week.setBackgroundColor(Color.TRANSPARENT);
                binding.month.setBackgroundColor(getResources().getColor(R.color.bottom_sheet_background));
                binding.year.setBackgroundColor(Color.TRANSPARENT);
                Helper.SelectedButtonOfLogTab = 2;
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.containerLogFragment, new MonthlyFragment());
                ft.commit();
            }
        });
        binding.year.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.day.setBackgroundColor(Color.TRANSPARENT);
                binding.week.setBackgroundColor(Color.TRANSPARENT);
                binding.month.setBackgroundColor(Color.TRANSPARENT);
                binding.year.setBackgroundColor(getResources().getColor(R.color.bottom_sheet_background));
                Helper.SelectedButtonOfLogTab = 3;
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.containerLogFragment, new YearlyFragment());
                ft.commit();
            }
        });

        if (Helper.SelectedButtonOfLogTab == 0) {
            binding.day.setBackgroundColor(getResources().getColor(R.color.bottom_sheet_background));
            binding.week.setBackgroundColor(Color.TRANSPARENT);
            binding.month.setBackgroundColor(Color.TRANSPARENT);
            binding.year.setBackgroundColor(Color.TRANSPARENT);
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.containerLogFragment, new DailyFragment());
            ft.commit();
        } else if (Helper.SelectedButtonOfLogTab == 1) {
            binding.day.setBackgroundColor(Color.TRANSPARENT);
            binding.week.setBackgroundColor(getResources().getColor(R.color.bottom_sheet_background));
            binding.month.setBackgroundColor(Color.TRANSPARENT);
            binding.year.setBackgroundColor(Color.TRANSPARENT);
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.containerLogFragment, new WeeklyFragment());
            ft.commit();
        } else if (Helper.SelectedButtonOfLogTab == 2) {
            binding.day.setBackgroundColor(Color.TRANSPARENT);
            binding.week.setBackgroundColor(Color.TRANSPARENT);
            binding.month.setBackgroundColor(getResources().getColor(R.color.bottom_sheet_background));
            binding.year.setBackgroundColor(Color.TRANSPARENT);
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.containerLogFragment, new MonthlyFragment());
            ft.commit();
        } else if (Helper.SelectedButtonOfLogTab == 3) {
            binding.day.setBackgroundColor(Color.TRANSPARENT);
            binding.week.setBackgroundColor(Color.TRANSPARENT);
            binding.month.setBackgroundColor(Color.TRANSPARENT);
            binding.year.setBackgroundColor(getResources().getColor(R.color.bottom_sheet_background));
        }
        return binding.getRoot();

    }
}