package rocks.poopjournal.todont;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class AdapterRecyclerView extends RecyclerView.Adapter<AdapterRecyclerView.RecyclerViewHolder> {
    private String[] donts;

    Context con;

    public AdapterRecyclerView(Context con, String[] donts) {
        this.donts = donts;
        this.con = con;
    }

    @NonNull
    @Override
    public AdapterRecyclerView.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.recyclerview_layout, viewGroup, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterRecyclerView.RecyclerViewHolder holder, int position) {
        String donot=donts[position];
        holder.textView.setText(donot);

    }

    @Override
    public int getItemCount() {
        return donts.length;
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        RadioButton rbtn;
        TextView textView;
        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            rbtn =  itemView.findViewById(R.id.radioButton);
            textView = itemView.findViewById(R.id.todaytext);
        }
    }
}
