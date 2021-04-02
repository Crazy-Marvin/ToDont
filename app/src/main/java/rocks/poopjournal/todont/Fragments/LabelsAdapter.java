package rocks.poopjournal.todont.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import rocks.poopjournal.todont.Db_Controller;
import rocks.poopjournal.todont.Helper;
import rocks.poopjournal.todont.Labels;
import rocks.poopjournal.todont.R;


public class LabelsAdapter extends RecyclerView.Adapter<LabelsAdapter.RecyclerViewHolder> {
    private ArrayList<String> labels_list=new ArrayList<>();
    Context con;
    Db_Controller db;

    public LabelsAdapter(Context con, Db_Controller db, ArrayList<String> labels_list) {
        this.labels_list = labels_list;
        this.con = con;
        this.db=db;

    }

    @NonNull
    @Override
    public LabelsAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.labels_recyclerview_layout, viewGroup, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LabelsAdapter.RecyclerViewHolder holder, final int position) {
        final String dTask=labels_list.get(position);
        holder.tv_label.setText(dTask);
        db.getNightMode();
        int count=db.countLabels(dTask);
        if(count>1){
            holder.tv_sum.setText(""+count+ " habits");
        }
        else{
            holder.tv_sum.setText(""+count+ " habit");
        }
        if(Helper.isnightmodeon.equals("no")){
            holder.btnlabel.setBackgroundResource(R.drawable.ic_label_light_labels);
        }
        else if(Helper.isnightmodeon.equals("yes")){
            holder.btnlabel.setBackgroundResource(R.drawable.ic_label_dark_labels);
        }
        holder.btndelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(con);
                builder1.setMessage("Do you really want to delete this?");
                builder1.setCancelable(true);
                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                db.delete_label(dTask);
                                Intent intent = new Intent(con, Labels.class);
                                con.startActivity(intent);
                                ((Activity) con).overridePendingTransition(0, 0);
                                dialog.cancel();
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return labels_list.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView tv_label,tv_sum;
        Button btndelete, btnlabel;
        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_label = itemView.findViewById(R.id.tv_label);
            tv_sum = itemView.findViewById(R.id.tv_sum);
            btndelete=itemView.findViewById(R.id.btndelete);
            btnlabel=itemView.findViewById(R.id.labelsbtn);

        }
    }
}
