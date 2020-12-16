package rocks.poopjournal.todont;

import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ornach.nobobutton.NoboButton;

public class FragmentLog extends Fragment {
NoboButton btnday,btnweek,btnmonth,btnyear;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_log2, container, false);
        btnday=view.findViewById(R.id.day);
        btnmonth=view.findViewById(R.id.month);
        btnweek=view.findViewById(R.id.week);
        btnyear=view.findViewById(R.id.year);
        btnday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnday.setBackgroundColor(Color.BLACK);
                btnweek.setBackgroundColor(Color.TRANSPARENT);
                btnmonth.setBackgroundColor(Color.TRANSPARENT);
                btnyear.setBackgroundColor(Color.TRANSPARENT);
                btnday.setTextColor(Color.WHITE);
                btnweek.setTextColor(Color.BLACK);
                btnmonth.setTextColor(Color.BLACK);
                btnyear.setTextColor(Color.BLACK);
            }
        });
        btnweek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnday.setBackgroundColor(Color.TRANSPARENT);
                btnweek.setBackgroundColor(Color.BLACK);
                btnmonth.setBackgroundColor(Color.TRANSPARENT);
                btnyear.setBackgroundColor(Color.TRANSPARENT);
                btnday.setTextColor(Color.BLACK);
                btnweek.setTextColor(Color.WHITE);
                btnmonth.setTextColor(Color.BLACK);
                btnyear.setTextColor(Color.BLACK);
            }
        });
        btnmonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnday.setBackgroundColor(Color.TRANSPARENT);
                btnweek.setBackgroundColor(Color.TRANSPARENT);
                btnmonth.setBackgroundColor(Color.BLACK);
                btnyear.setBackgroundColor(Color.TRANSPARENT);
                btnday.setTextColor(Color.BLACK);
                btnweek.setTextColor(Color.BLACK);
                btnmonth.setTextColor(Color.WHITE);
                btnyear.setTextColor(Color.BLACK);
            }
        });
        btnyear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnday.setBackgroundColor(Color.TRANSPARENT);
                btnweek.setBackgroundColor(Color.TRANSPARENT);
                btnmonth.setBackgroundColor(Color.TRANSPARENT);
                btnyear.setBackgroundColor(Color.BLACK);
                btnday.setTextColor(Color.BLACK);
                btnweek.setTextColor(Color.BLACK);
                btnmonth.setTextColor(Color.BLACK);
                btnyear.setTextColor(Color.WHITE);
            }
        });
        return view;
    }
}