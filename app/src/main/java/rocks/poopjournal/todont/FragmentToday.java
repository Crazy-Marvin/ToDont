package rocks.poopjournal.todont;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentToday extends Fragment {
RecyclerView rv;
String[] donts=new  String[6];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_today2, container, false);
        rv=view.findViewById(R.id.rv);
        donts=new String[]{"Texting My GirlFriend", "Consume Sugar Drinks", "Lie", "Rage over Criticism",
                "Feel Sorry for my Self","Texting My GirlFriend", "Consume Sugar Drinks", "Lie", "Rage over Criticism",
                "Feel Sorry for my Self","Texting My GirlFriend", "Consume Sugar Drinks", "Lie", "Rage over Criticism",
                "Feel Sorry for my Self","Texting My GirlFriend", "Consume Sugar Drinks", "Lie", "Rage over Criticism",
                "Feel Sorry for my Self"};
        rv=view.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.setAdapter(new AdapterRecyclerView(getActivity(),donts));
        return view;
    }
}