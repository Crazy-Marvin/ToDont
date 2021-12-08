package rocks.poopjournal.todont.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ornach.nobobutton.NoboButton;

import java.util.Collection;

import rocks.poopjournal.todont.Helper;
import rocks.poopjournal.todont.R;

public class FragmentToday extends Fragment {
    NoboButton avoided,done,habits;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_today2, container, false);
        Helper.isTodaySelected=true;
        avoided=view.findViewById(R.id.avoided);
        done=view.findViewById(R.id.done);
        habits=view.findViewById(R.id.habits);
        if(Helper.SelectedButtonOfTodayTab==0){
            habits.setBackgroundColor(getResources().getColor(R.color.bottom_sheet_background));
            avoided.setBackgroundColor(Color.TRANSPARENT);
            done.setBackgroundColor(Color.TRANSPARENT);
            FragmentTransaction ft= getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.containerTodayFragment,new HabitsFragment());
            ft.commit();
        }
        else if(Helper.SelectedButtonOfTodayTab==1){
            habits.setBackgroundColor(Color.TRANSPARENT);
            avoided.setBackgroundColor(getResources().getColor(R.color.bottom_sheet_background));
            done.setBackgroundColor(Color.TRANSPARENT);
            FragmentTransaction ft= getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.containerTodayFragment,new AvoidedFragment());
            ft.commit();
        }
        else if(Helper.SelectedButtonOfTodayTab==2){
            habits.setBackgroundColor(Color.TRANSPARENT);
            avoided.setBackgroundColor(Color.TRANSPARENT);
            done.setBackgroundColor(getResources().getColor(R.color.bottom_sheet_background));
            FragmentTransaction ft= getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.containerTodayFragment,new DoneFragment());
            ft.commit();
        }


        habits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                habits.setBackgroundColor(getResources().getColor(R.color.bottom_sheet_background));
                avoided.setBackgroundColor(Color.TRANSPARENT);
                done.setBackgroundColor(Color.TRANSPARENT);
                FragmentTransaction ft= getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.containerTodayFragment,new HabitsFragment());
                ft.commit();


            }
        });
        avoided.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                habits.setBackgroundColor(Color.TRANSPARENT);
                avoided.setBackgroundColor(getResources().getColor(R.color.bottom_sheet_background));
                done.setBackgroundColor(Color.TRANSPARENT);
                FragmentTransaction ft= getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.containerTodayFragment,new AvoidedFragment());
                ft.commit();


            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                habits.setBackgroundColor(Color.TRANSPARENT);
                avoided.setBackgroundColor(Color.TRANSPARENT);
                done.setBackgroundColor(getResources().getColor(R.color.bottom_sheet_background));
                FragmentTransaction ft= getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.containerTodayFragment,new DoneFragment());
                ft.commit();

            }
        });
        return view;
    }



}