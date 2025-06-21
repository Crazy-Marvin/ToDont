package rocks.poopjournal.todont.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import rocks.poopjournal.todont.databinding.RecyclerviewLayoutBinding
import rocks.poopjournal.todont.model.Habit
import rocks.poopjournal.todont.utils.DatabaseUtils

class AvoidedOrDoneAdapter(
    val context: Context,
    val dbHelper: DatabaseUtils,
    val habits:ArrayList<Habit>,
    val isAvoided:Boolean,
    val resIcon:Int
) :
    RecyclerView.Adapter<AvoidedOrDoneAdapter.RecyclerViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val binding=RecyclerviewLayoutBinding.inflate(inflater,viewGroup,false)
        return RecyclerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {

        val habit = habits[position]
        with(holder.binding){
            tvAvoidedCount.text=if(isAvoided){
                habit.countAvoided.toString()
            }else{
                habit.countDone.toString()
            }
            tvHabitName.text=habit.name
            tvLabelName.text=habit.label?.name
        }
    }

    override fun getItemCount()=habits.size

    inner class RecyclerViewHolder(val binding: RecyclerviewLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.btnAddToAvoided.setBackgroundResource(resIcon)
        }
    }
}
