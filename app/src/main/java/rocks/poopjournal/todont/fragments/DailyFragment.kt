package rocks.poopjournal.todont.fragments


import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import rocks.poopjournal.todont.Helper
import rocks.poopjournal.todont.R
import rocks.poopjournal.todont.databinding.FragmentDailyBinding
import rocks.poopjournal.todont.model.HabitRecord
import rocks.poopjournal.todont.utils.Constants
import rocks.poopjournal.todont.utils.DatabaseUtils
import rocks.poopjournal.todont.utils.HabitStatus
import rocks.poopjournal.todont.utils.SharedPrefUtils
import rocks.poopjournal.todont.fragments.HabitsLogFragment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DailyFragment() : Fragment() {
    private var binding: FragmentDailyBinding? = null
    private val calendar: Calendar = Calendar.getInstance()
    private val currentDayCalendar: Calendar = Calendar.getInstance()
    private var formattedDate: String = ""
    private var databaseUtils: DatabaseUtils? = null
    private var habitsTotalCount: Double = 0.0
    private var avoidedCount: Double = 0.0
    private var avoidedPercentage: Int = 0
    private var sharedPreferences: SharedPrefUtils? = null
    private var initialDate: String? = null
    private var avoidedHabitRecords: List<String>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDailyBinding.inflate(inflater, container, false)
        sharedPreferences = SharedPrefUtils(requireContext())
        initialDate = sharedPreferences?.getString("InitialDate", "")
        Helper.SelectedButtonOfLogTab = 1

        databaseUtils = DatabaseUtils(requireContext())
        val dateFormatter = SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault())
        formattedDate = dateFormatter.format(calendar.time)

        binding?.apply {
            date.text = formattedDate
            updateHabitStatistics(formattedDate)
            before.setOnClickListener { handleDateChange(-1, dateFormatter) }
            after.setOnClickListener { handleDateChange(1, dateFormatter) }

        }

        return binding?.root
    }

    private fun handleDateChange(dayOffset: Int, dateFormatter: SimpleDateFormat) {
        calendar.add(Calendar.DATE, dayOffset)
        formattedDate = dateFormatter.format(calendar.time)

        binding?.apply {
            date.text = formattedDate
//            before.isEnabled = formattedDate != initialDate
//            after.isEnabled = formattedDate != dateFormatter.format(currentDayCalendar.time)

            updateHabitStatistics(formattedDate)
        }
    }

    private fun updateHabitStatistics(date: String) {
        avoidedHabitRecords = databaseUtils?.getRecordsByDateAndStatus(date, HabitStatus.AVOIDED.value)
        habitsTotalCount = databaseUtils?.getHabitsCount()?.toDouble() ?: 0.0
        avoidedCount = avoidedHabitRecords?.size?.toDouble() ?: 0.0
        avoidedPercentage = if (habitsTotalCount > 0) ((avoidedCount / habitsTotalCount) * 100).toInt() else 0

        binding?.apply {
            percentage.text = "$avoidedPercentage% "+getString(R.string.avoided)
            progressText.text = getString(
                if (avoidedPercentage == 100) R.string.habits_are_avoided else R.string.habits_are_avoided_way_to_go,
                avoidedCount.toInt(), habitsTotalCount.toInt()
            )
            updatePieChart(avoidedPercentage)
        }
    }



    private fun updatePieChart(avoidedPercentage: Int) {
        binding?.pieChart?.apply {
            setUsePercentValues(true)
            val entries = listOf(
                PieEntry(avoidedPercentage.toFloat(), getString(R.string.avoided)),
                PieEntry((100 - avoidedPercentage).toFloat(), getString(R.string.habits))
            )
            val dataSet = PieDataSet(entries, "").apply {
                valueTextColor = Color.WHITE
                val lightSurface=TypedValue()
                requireContext().theme.resolveAttribute(R.attr.colorOnBackground3,lightSurface,true)
                val primaryColor=TypedValue()
                requireContext().theme.resolveAttribute(R.attr.colorAccent,primaryColor,true)

                setColors(primaryColor.data, lightSurface.data)
            }
            data = PieData(dataSet)
            legend.isEnabled = false
            description = Description().apply { text = "" }
            holeRadius = 50f
            val typedValue = TypedValue()
            requireContext().theme.resolveAttribute(R.attr.colorBackground, typedValue, true)
            setHoleColor(typedValue.data)
            transparentCircleRadius = 50f
            animateXY(1000, 1000)
        }
    }
}
