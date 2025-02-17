package rocks.poopjournal.todont.fragments

import android.graphics.Color
import android.os.Bundle
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
class MonthlyFragment : Fragment() {
    private lateinit var calendar: Calendar
    private lateinit var dbHelper: DatabaseUtils
    private lateinit var dateFormatter: SimpleDateFormat


    private lateinit var pieChart: PieChart
    private lateinit var dateText: TextView
    private lateinit var yearText: TextView
    private lateinit var mostAvoidedText: TextView
    private lateinit var leastAvoidedText: TextView
    private lateinit var dateRangeText: TextView
    private lateinit var btnBefore: ImageView
    private lateinit var btnAfter: ImageView

    private var currentMonth: String = ""
    private var habitsSize: Double = 0.0
    private var avoidedSize: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_monthly, container, false)
        initViews(view)
        setupInitialData()
        setupListeners()
        return view
    }

    private fun initViews(view: View) {
        dbHelper = DatabaseUtils(requireContext())
        dateFormatter = SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault())

        pieChart = view.findViewById(R.id.pieChart)
        dateText = view.findViewById(R.id.date)
        yearText = view.findViewById(R.id.year)
        mostAvoidedText = view.findViewById(R.id.mostavoided)
        leastAvoidedText = view.findViewById(R.id.leastavoided)
        dateRangeText = view.findViewById(R.id.daterange)
        btnBefore = view.findViewById(R.id.before)
        btnAfter = view.findViewById(R.id.after)

        btnBefore.setBackgroundResource(R.drawable.ic_backarrow)
        btnAfter.setBackgroundResource(R.drawable.ic_nextarrow)
    }

    private fun setupInitialData() {
        calendar = Calendar.getInstance()
        val currentDate = dateFormatter.format(calendar.time).split("-")
        currentMonth = getMonthName(currentDate[1])
        val currentYear = currentDate[0]

        dateText.text = currentMonth
        yearText.text = currentYear

        val monthStart = "${currentYear}-${currentDate[1]}-01"
        val monthEnd = "${currentYear}-${currentDate[1]}-31"

        habitsSize = (dbHelper.getHabitsCount() * 30).toDouble()
        avoidedSize = dbHelper.getMonthlyAvoidedDataList(monthStart, monthEnd).size.toDouble()
        val percentageAvoided = (avoidedSize / habitsSize) * 100

        dbHelper.getMonthlyAvoidedData(monthStart, monthEnd).apply {
          if(this.isNotEmpty()){
              mostAvoidedText.text = dbHelper.getHabitById(this.toInt())?.name
              mostAvoidedText.visibility = View.VISIBLE
          }else{
              mostAvoidedText.visibility = View.GONE
          }

        }
        dbHelper.getMonthlyDoneData(monthStart, monthEnd).apply {
            if(this.isNotEmpty()){
                leastAvoidedText.text =dbHelper.getHabitById(this.toInt())?.name
                leastAvoidedText.visibility = View.VISIBLE
            }else{
                leastAvoidedText.visibility = View.GONE
            }
        }
        setupPieChart(percentageAvoided.toInt())
    }

    private fun setupListeners() {
        btnBefore.setOnClickListener {
            updateMonthData(isNext = false)
        }
        btnAfter.setOnClickListener {
            updateMonthData(isNext = true)
        }
    }

    private fun updateMonthData(isNext: Boolean) {
        val currentMonthIndex = calendar.get(Calendar.MONTH)
        calendar.set(Calendar.MONTH, if (isNext) currentMonthIndex + 1 else currentMonthIndex - 1)

        val newMonth = calendar.get(Calendar.MONTH) + 1 // 0-based index
        val newYear = calendar.get(Calendar.YEAR)

        currentMonth = getMonthName(newMonth.toString().padStart(2, '0'))
        dateText.text = currentMonth
        yearText.text = newYear.toString()

        val monthStart = "${newYear}-${newMonth.toString().padStart(2, '0')}-01"
        val monthEnd = "${newYear}-${newMonth.toString().padStart(2, '0')}-31"

        dbHelper.getMonthlyAvoidedData(monthStart, monthEnd).apply {
            if(this.isNotEmpty()){
                mostAvoidedText.text = dbHelper.getHabitById(this.toInt())?.name
                mostAvoidedText.visibility = View.VISIBLE
            }else{
                mostAvoidedText.visibility = View.GONE
            }

        }
        dbHelper.getMonthlyDoneData(monthStart, monthEnd).apply {
            if(this.isNotEmpty()){
                leastAvoidedText.text =dbHelper.getHabitById(this.toInt())?.name
                leastAvoidedText.visibility = View.VISIBLE
            }else{
                leastAvoidedText.visibility = View.GONE
            }
        }

        avoidedSize = dbHelper.getMonthlyAvoidedDataList(monthStart, monthEnd).size.toDouble()
        val percentageAvoided = (avoidedSize / habitsSize) * 100

        setupPieChart(percentageAvoided.toInt())
    }

    private fun setupPieChart(avoidedPercentage: Int) {
        val pieEntries = listOf(
            PieEntry(avoidedPercentage.toFloat(), getString(R.string.avoided)),
            PieEntry((100 - avoidedPercentage).toFloat(), getString(R.string.habits))
        )

        val pieDataSet = PieDataSet(pieEntries, "").apply {
            val lightSurface=TypedValue()
            requireContext().theme.resolveAttribute(R.attr.colorOnBackground3,lightSurface,true)
            val primaryColor=TypedValue()
            requireContext().theme.resolveAttribute(R.attr.colorAccent,primaryColor,true)
            setColors(primaryColor.data, lightSurface.data)
            valueTextColor = Color.WHITE
        }

        pieChart.data = PieData(pieDataSet).apply {
            setValueTextColor(Color.WHITE)
        }

        pieChart.apply {
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

    private fun getMonthName(monthNumber: String): String {
        return resources.getStringArray(R.array.month_names)[monthNumber.toInt() - 1]
    }

    private fun getTodayDate(): String {
        var date: Date = Calendar.getInstance().time
        val dateFormat: SimpleDateFormat = SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault())
        var formattedDate: String = dateFormat.format(date)
        //Calendar.getInstance().timeInMillis.toString()
        return formattedDate
    }


}
