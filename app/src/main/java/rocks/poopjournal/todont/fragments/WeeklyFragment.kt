package rocks.poopjournal.todont.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.IntegerRes
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import rocks.poopjournal.todont.R
import rocks.poopjournal.todont.utils.Constants
import rocks.poopjournal.todont.utils.DatabaseUtils
import rocks.poopjournal.todont.utils.SharedPrefUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class WeeklyFragment : Fragment() {

    private lateinit var calendar: Calendar
    private lateinit var dateFormatter: SimpleDateFormat
    private lateinit var dbHelper: DatabaseUtils

    private var dateTextView: TextView? = null
    private var mostAvoidedTextView: TextView? = null
    private var leastAvoidedTextView: TextView? = null
    private var dateRangeTextView: TextView? = null
    private var previousButton: ImageView? = null
    private var nextButton: ImageView? = null
    private var pieChart: PieChart? = null

    private var startDate: String = ""
    private var endDate: String = ""
    private var habitCount: Double = 0.0
    private var avoidedCount: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_weekly, container, false)

        // Initialize components
        initializeComponents(view)
        updateDateRangeAndRecords()

        // Set button listeners
        setupButtonListeners()

        return view
    }

    private fun initializeComponents(view: View) {
        dbHelper = DatabaseUtils(requireContext())
        dateFormatter = SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault())


        calendar = Calendar.getInstance()
        getDates()
        dateTextView = view.findViewById(R.id.date)
        mostAvoidedTextView = view.findViewById(R.id.mostavoided)
        leastAvoidedTextView = view.findViewById(R.id.leastavoided)
        dateRangeTextView = view.findViewById(R.id.daterange)
        previousButton = view.findViewById(R.id.before)
        nextButton = view.findViewById(R.id.after)
        pieChart = view.findViewById(R.id.pieChart)

        previousButton?.setBackgroundResource(R.drawable.ic_backarrow)
        nextButton?.setBackgroundResource(R.drawable.ic_nextarrow)

        dateTextView?.text = dateFormatter.format(calendar.time)
    }

    private fun getDates() {
        startDate = getTodayDate()
        calendar.add(Calendar.DATE, 7)
        endDate = dateFormatter.format(calendar.time)
    }

    private fun setupButtonListeners() {
        previousButton?.setOnClickListener {
            handlePreviousButtonClick()
        }

        nextButton?.setOnClickListener {
            handleNextButtonClick()
        }
    }

    private fun handlePreviousButtonClick() {
        previousButton?.setBackgroundResource(R.drawable.ic_backarrowpressed)
        Handler().postDelayed({
            previousButton?.setBackgroundResource(R.drawable.ic_backarrow)
        }, 100)

        adjustDateBy(-7)
        updateDateRangeAndRecords()
    }

    private fun handleNextButtonClick() {
        nextButton?.setBackgroundResource(R.drawable.ic_nextpressed)
        Handler().postDelayed({
            nextButton?.setBackgroundResource(R.drawable.ic_nextarrow)
        }, 100)

        adjustDateBy(7)
        updateDateRangeAndRecords()
    }

    private fun adjustDateBy(days: Int) {
        if (days < 0) {
            calendar = getCalendarFromFormattedDate(startDate)
            endDate = startDate
            calendar.add(Calendar.DATE, days)
            startDate = dateFormatter.format(calendar.time)
        } else {
            calendar = getCalendarFromFormattedDate(endDate)
            startDate = endDate
            calendar.add(Calendar.DATE, days)
            endDate = dateFormatter.format(calendar.time)
        }
        dateTextView?.text = "$startDate To $endDate"
    }

    private fun updateDateRangeAndRecords() {

        dateRangeTextView?.text = "$startDate To $endDate"

        val mostAvoided = dbHelper.getWeeklyAvoidedRecord(startDate, endDate)
        val leastAvoided = dbHelper.getWeeklyDoneRecord(startDate, endDate)

        if (mostAvoided.isNotEmpty()) {
            mostAvoidedTextView?.text =
                dbHelper.getHabitById(Integer.parseInt(mostAvoided))?.name
            mostAvoidedTextView?.visibility = View.VISIBLE
        } else {
            mostAvoidedTextView?.visibility = View.GONE
        }
        if (leastAvoided.isNotEmpty()) {
            leastAvoidedTextView?.visibility = View.VISIBLE
            leastAvoidedTextView?.text =
                dbHelper.getHabitById(Integer.parseInt(leastAvoided))?.name
        } else {
            leastAvoidedTextView?.visibility = View.GONE
        }

        habitCount = dbHelper.getHabitsCount() * 7.0
        avoidedCount = dbHelper.getWeeklyAvoidedRecordList(startDate, endDate).size.toDouble()
        val avoidedPercentage = calculateAvoidedPercentage()

        updatePieChart(avoidedPercentage)
    }

    private fun calculateAvoidedPercentage(): Int {
        return if (habitCount == 0.0) 0 else ((avoidedCount / habitCount) * 100).toInt()
    }

    private fun updatePieChart(avoidedPercentage: Int) {
        pieChart?.apply {
            setUsePercentValues(true)
            data = PieData(PieDataSet(
                listOf(
                    PieEntry(avoidedPercentage.toFloat(), getString(R.string.avoided)),
                    PieEntry((100 - avoidedPercentage).toFloat(), getString(R.string.habits))
                ),
                ""
            ).apply {
                val lightSurface=TypedValue()
                requireContext().theme.resolveAttribute(R.attr.colorOnBackground3,lightSurface,true)
                val primaryColor=TypedValue()
                requireContext().theme.resolveAttribute(R.attr.colorAccent,primaryColor,true)
                setColors(primaryColor.data, lightSurface.data)
                valueTextColor = Color.WHITE
            })

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

    private fun getTodayDate(): String {
        var date: Date = Calendar.getInstance().time
        val dateFormat: SimpleDateFormat =
            SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault())
        var formattedDate: String = dateFormat.format(date)
        calendar.timeInMillis = Calendar.getInstance().timeInMillis
        return formattedDate
    }

    fun getCalendarFromFormattedDate(formattedDate: String): Calendar {
        val dateFormat = SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault())
        val date =
            dateFormat.parse(formattedDate) // Parse the formatted date string into a Date object
        val calendar = Calendar.getInstance()
        calendar.time = date // Set the Calendar time to the parsed date
        return calendar
    }
}
