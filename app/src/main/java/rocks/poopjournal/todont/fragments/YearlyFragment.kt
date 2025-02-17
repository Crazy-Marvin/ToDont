package rocks.poopjournal.todont.fragments

import android.content.Context
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
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import rocks.poopjournal.todont.Helper
import rocks.poopjournal.todont.R
import rocks.poopjournal.todont.utils.Constants
import rocks.poopjournal.todont.utils.DatabaseUtils
import rocks.poopjournal.todont.utils.SharedPrefUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class YearlyFragment : Fragment() {

    private lateinit var calendar: Calendar
    private lateinit var dbHelper: DatabaseUtils
    private lateinit var dateFormat: SimpleDateFormat
    private lateinit var preferences: SharedPrefUtils

    private var pieChart: PieChart? = null
    private var currentYear: Int = 0
    private var initialDate: String? = null

    private lateinit var yearText: TextView
    private lateinit var mostAvoidedText: TextView
    private lateinit var leastAvoidedText: TextView
    private lateinit var dateRangeText: TextView
    private lateinit var btnPrevious: ImageView
    private lateinit var btnNext: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_yearly, container, false)

        initializeComponents(view)
        setupInitialData()
        updateYearData()

        btnPrevious.setOnClickListener {
            updateYear(-1)
        }
        btnNext.setOnClickListener {
            updateYear(1)
        }

        return view
    }

    private fun initializeComponents(view: View) {
        dbHelper = DatabaseUtils(requireContext())
        dateFormat = SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault())
        preferences = SharedPrefUtils(requireContext())

        yearText = view.findViewById(R.id.year)
        mostAvoidedText = view.findViewById(R.id.mostavoided)
        leastAvoidedText = view.findViewById(R.id.leastavoided)
        dateRangeText = view.findViewById(R.id.daterange)
        btnPrevious = view.findViewById(R.id.before)
        btnNext = view.findViewById(R.id.after)
        pieChart = view.findViewById(R.id.pieChart)

        btnPrevious.setBackgroundResource(R.drawable.ic_backarrow)
        btnNext.setBackgroundResource(R.drawable.ic_nextarrow)
    }

    private fun setupInitialData() {
        calendar = Calendar.getInstance()
        currentYear = calendar.get(Calendar.YEAR)
        yearText.text = currentYear.toString()

        initialDate = preferences.getString("InitialDate", getTodayDate())
        Helper.SelectedButtonOfLogTab = 3
    }

    private fun updateYear(offset: Int) {
        currentYear += offset
        yearText.text = currentYear.toString()

        Handler().postDelayed({
            btnPrevious.setBackgroundResource(R.drawable.ic_backarrow)
        }, 100)

        updateYearData()

//        btnPrevious.isEnabled = initialDate?.split("-")?.get(0)?.toInt() != currentYear
//        btnNext.isEnabled = currentYear != Calendar.getInstance().get(Calendar.YEAR)
    }

    private fun updateYearData() {
        val startDate = "$currentYear-01-01"
        val endDate = "$currentYear-12-31"

        val avoided = dbHelper.getYearlyAvoidedData(startDate, endDate)
        val done = dbHelper.getYearlyDoneData(startDate, endDate)

        if(avoided.isNotEmpty()){
            mostAvoidedText.text = dbHelper?.getHabitById(avoided.toInt())?.name
            mostAvoidedText.visibility=View.VISIBLE
        }else{
            mostAvoidedText.visibility=View.GONE
        }

        if(done.isNotEmpty()){
            leastAvoidedText.text = dbHelper?.getHabitById(done.toInt())?.name
            leastAvoidedText.visibility=View.VISIBLE
        }else{
            leastAvoidedText.visibility=View.GONE
        }


        val totalHabits = dbHelper.getHabitsCount() * 365.0
        val avoidedCount = dbHelper.getYearlyAvoidedDataList(startDate, endDate).size.toDouble()
        val percentage = (avoidedCount / totalHabits) * 100

        setupPieChart(percentage.toInt())
    }

    private fun setupPieChart(avoidedPercentage: Int) {
        pieChart?.apply {
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
            description = Description().apply { text = "" }
            holeRadius = 50f
            transparentCircleRadius = 50f
            val typedValue = TypedValue()
            requireContext().theme.resolveAttribute(R.attr.colorBackground, typedValue, true)
            setHoleColor(typedValue.data)
            animateXY(1000, 1000)
        }
    }
    private fun getTodayDate(): String {
        var date: Date = Calendar.getInstance().time
        val dateFormat: SimpleDateFormat = SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault())
        var formattedDate: String = dateFormat.format(date)
        //Calendar.getInstance().timeInMillis.toString()
        return formattedDate
    }
}
