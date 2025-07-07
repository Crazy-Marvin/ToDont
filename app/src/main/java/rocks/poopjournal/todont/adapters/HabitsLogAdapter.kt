package rocks.poopjournal.todont.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import rocks.poopjournal.todont.R
import rocks.poopjournal.todont.databinding.RecyclerviewLayoutLogHabitsBinding
import rocks.poopjournal.todont.model.Habit
import rocks.poopjournal.todont.utils.DatabaseUtils
import rocks.poopjournal.todont.utils.HabitsBottomSheetDialog

class HabitsLogAdapter(
    val context: Context,
    val dbHelper: DatabaseUtils,
    val habits:ArrayList<Habit>
) :
    RecyclerView.Adapter<HabitsLogAdapter.RecyclerViewHolder>() {



    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val binding=RecyclerviewLayoutLogHabitsBinding.inflate(inflater)
        return RecyclerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val habit = habits[position]
        with(holder.binding){
            tvHabitName.text=habit.name
            tvLabelName.text=habit.label?.name
            llRootView.setOnClickListener{
                val bottomSheet=HabitsBottomSheetDialog(
                    context,
                    habit,
                    position,
                    dbHelper,
                    object : HabitsBottomSheetDialog.HabitSheetListener{
                        override fun updateCount(_habit: Habit, _position: Int) {
                            habits[_position].countDone=_habit.countDone
                            habits[_position].countAvoided=_habit.countAvoided
                        }
                        override fun deleted(_habit: Habit, _position: Int) {
                            habits.removeAt(_position)
                            notifyItemRemoved(_position)
                            notifyItemRangeChanged(_position,habits.size)
                        }
                        override fun dismissed() {
                            notifyDataSetChanged()
                        }

                    }

                )
            }
        }


    }





    override fun getItemCount()=habits.size

    inner class RecyclerViewHolder(val binding: RecyclerviewLayoutLogHabitsBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.habitsbutton.setBackgroundResource(R.drawable.ic_habitscircle)
        }
    }
}
