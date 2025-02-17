package rocks.poopjournal.todont.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import rocks.poopjournal.todont.R
import rocks.poopjournal.todont.databinding.LabelsRecyclerviewLayoutBinding
import rocks.poopjournal.todont.model.Label
import rocks.poopjournal.todont.utils.DatabaseUtils

class LabelsAdapter(var context: Context, var dbHelper: DatabaseUtils, var labels: ArrayList<Label>) :
    RecyclerView.Adapter<LabelsAdapter.RecyclerViewHolder>() {


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val binding = LabelsRecyclerviewLayoutBinding.inflate(inflater, viewGroup, false)
        return RecyclerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val label = labels[position]
        with(holder.binding) {
            tvLabel.text = label.name
            tvSum.text = label.habitCount.toString() + " " + context.resources.getString(R.string.habits)
        }
    }

    fun updateData(newLabels: List<Label>) {
        //labels.clear()
       // labels.addAll(newLabels)
        notifyDataSetChanged()
    }

    override fun getItemCount() = labels.size

    inner class RecyclerViewHolder(val binding: LabelsRecyclerviewLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)
}
