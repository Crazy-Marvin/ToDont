package rocks.poopjournal.todont.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import rocks.poopjournal.todont.Helper
import rocks.poopjournal.todont.R
import rocks.poopjournal.todont.databinding.RecyclerviewLayoutHabitsBinding
import rocks.poopjournal.todont.model.Habit
import rocks.poopjournal.todont.model.HabitRecord
import rocks.poopjournal.todont.utils.Constants
import rocks.poopjournal.todont.utils.DatabaseUtils
import rocks.poopjournal.todont.utils.HabitStatus
import rocks.poopjournal.todont.utils.HabitsBottomSheetDialog
import rocks.poopjournal.todont.utils.SharedPrefUtils
import smartdevelop.ir.eram.showcaseviewlib.GuideView
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity
import smartdevelop.ir.eram.showcaseviewlib.config.PointerType
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HabitsAdapter(
    var context: Context,
    private var dbHelper: DatabaseUtils,
    var habits: ArrayList<Habit>,
    ) :
    RecyclerView.Adapter<HabitsAdapter.RecyclerViewHolder>() {

    private val prefUtils: SharedPrefUtils = SharedPrefUtils(context)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val binding=RecyclerviewLayoutHabitsBinding.inflate(inflater,viewGroup,false)
        return RecyclerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {

        val habit=habits[position]

        with(holder.binding){
            tvHabitName.text=habit.name
            tvLabelOfHabit.text=habit.label?.name
            tvAvoided.text=habit.countAvoided.toString()
            tvDone.text=habit.countDone.toString()
            llRootView.setOnClickListener {
                val bottomSheet=HabitsBottomSheetDialog(
                    context,habit,position,dbHelper,
                    object :HabitsBottomSheetDialog.HabitSheetListener{
                        override fun updateCount(_habit: Habit, _position: Int) {
                            habits[_position].countDone=_habit.countDone
                            habits[_position].countAvoided=_habit.countAvoided
                            tvAvoided.text=_habit.countAvoided.toString()
                            tvDone.text=_habit.countDone.toString()

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
                bottomSheet.show()
            }
            btnAddToDone.setOnClickListener {
                if (!prefUtils.getBool(SharedPrefUtils.KEY_ADD_OR_AVOIDED)) {
                    showGuidedView(viewDoneOrAvoided)
                } else if (Helper.isTodaySelected) {
                    habit.countDone++
                    dbHelper.updateHabit(habit)
                    dbHelper.insertRecord(
                        HabitRecord(
                            date = getTodayDate(),
                            status = HabitStatus.DONE.value,
                            habitId = habit.id
                        )
                    )
                    tvDone.text=habit.countDone.toString()
                }
            }
            btnAddToAvoided.setOnClickListener {
                if (!prefUtils.getBool(SharedPrefUtils.KEY_ADD_OR_AVOIDED)) {
                    showGuidedView(viewDoneOrAvoided)
                } else if (Helper.isTodaySelected) {
                    habit.countAvoided++
                    dbHelper.updateHabit(habit)
                    dbHelper.insertRecord(
                        HabitRecord(
                            date = getTodayDate(),
                            status = HabitStatus.AVOIDED.value,
                            habitId = habit.id
                        )
                    )
                    tvAvoided.text=habit.countAvoided.toString()
                }
            }
        }



    }

   private fun showGuidedView(view: View){
        val guideView = GuideView.Builder(context)
            .setContentText(context.getString(R.string.mark_as_done_or_avoided))
            .setTargetView(view)
            .setDismissType(DismissType.anywhere)
            .setPointerType(PointerType.arrow)
            .setGravity(Gravity.center)
            .setGuideListener {
                prefUtils.setBool(
                    SharedPrefUtils.KEY_ADD_OR_AVOIDED,
                    true
                )
            }
        guideView.build().show()
   }
    private fun getTodayDate(): String {
        var date: Date = Calendar.getInstance().time
        val dateFormat: SimpleDateFormat = SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault())
        var formattedDate: String = dateFormat.format(date)
        //Calendar.getInstance().timeInMillis.toString()
        return formattedDate
    }

    override fun getItemCount()=habits.size

    inner class RecyclerViewHolder(val binding: RecyclerviewLayoutHabitsBinding) : RecyclerView.ViewHolder(binding.root){
    }
}
