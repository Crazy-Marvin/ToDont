package rocks.poopjournal.todont.fragments

import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.Fragment
import rocks.poopjournal.todont.Helper
import rocks.poopjournal.todont.R
import rocks.poopjournal.todont.databinding.FragmentLog2Binding
import rocks.poopjournal.todont.utils.DatabaseUtils

class FragmentLog() : Fragment() {
    private var db: DatabaseUtils? = null
    private var binding: FragmentLog2Binding? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLog2Binding.inflate(inflater, container, false)
        val view = inflater.inflate(R.layout.fragment_log2, container, false)
        db = DatabaseUtils(requireContext())
        Helper.isTodaySelected = false
        Helper.SelectedButtonOfLogTab = 0



        binding?.dhabits?.setOnClickListener {

            updateTabSelection(0, HabitsLogFragment())
        }
        binding?.davoided?.setOnClickListener {
            val frag=AvoidedOrDoneLogFragment.newInstance(true)
            updateTabSelection(1, frag)
        }
        binding?.ddone?.setOnClickListener {
            val frag=AvoidedOrDoneLogFragment.newInstance(false)
            updateTabSelection(2, frag)
        }

        binding!!.day.setOnClickListener {
            binding!!.day.setBackgroundResource(R.drawable.continuebutton2)
            binding!!.day.backgroundTintList=null
            binding!!.week.setBackgroundResource(R.drawable.continuebuttontrans)
            binding!!.month.setBackgroundResource(R.drawable.continuebuttontrans)
            binding!!.year.setBackgroundResource(R.drawable.continuebuttontrans)
            Helper.SelectedButtonOfLogTab = 0
            val ft = requireActivity().supportFragmentManager.beginTransaction()
            ft.replace(R.id.containerLogFragment, DailyFragment())
            ft.commit()
        }
        binding!!.week.setOnClickListener {
            binding!!.day.setBackgroundResource(R.drawable.continuebuttontrans)
            binding!!.week.setBackgroundResource(R.drawable.continuebutton2)
            binding!!.week.backgroundTintList=null
            binding!!.month.setBackgroundResource(R.drawable.continuebuttontrans)
            binding!!.year.setBackgroundResource(R.drawable.continuebuttontrans)
            Helper.SelectedButtonOfLogTab = 1
            val ft = requireActivity().supportFragmentManager.beginTransaction()
            ft.replace(R.id.containerLogFragment, WeeklyFragment())
            ft.commit()
        }
        binding!!.month.setOnClickListener {
            binding!!.day.setBackgroundResource(R.drawable.continuebuttontrans)
            binding!!.week.setBackgroundResource(R.drawable.continuebuttontrans)
            binding!!.month.setBackgroundResource(R.drawable.continuebutton2)
            binding!!.month.backgroundTintList=null
            binding!!.year.setBackgroundResource(R.drawable.continuebuttontrans)
            Helper.SelectedButtonOfLogTab = 2
            val ft = requireActivity().supportFragmentManager.beginTransaction()
            ft.replace(R.id.containerLogFragment, MonthlyFragment())
            ft.commit()
        }
        binding!!.year.setOnClickListener {
            binding!!.day.setBackgroundResource(R.drawable.continuebuttontrans)
            binding!!.week.setBackgroundResource(R.drawable.continuebuttontrans)
            binding!!.month.setBackgroundResource(R.drawable.continuebuttontrans)
            binding!!.year.setBackgroundResource(R.drawable.continuebutton2)
            binding!!.year.backgroundTintList=null
            Helper.SelectedButtonOfLogTab = 3
            val ft = requireActivity().supportFragmentManager.beginTransaction()
            ft.replace(R.id.containerLogFragment, YearlyFragment())
            ft.commit()
        }
        updateTabSelection(0, HabitsLogFragment())

        when (Helper.SelectedButtonOfLogTab) {
            0 -> {
                binding!!.day.setBackgroundResource(R.drawable.continuebutton2)
                binding!!.day.backgroundTintList=null
                binding!!.week.setBackgroundResource(R.drawable.continuebuttontrans)
                binding!!.month.setBackgroundResource(R.drawable.continuebuttontrans)
                binding!!.year.setBackgroundResource(R.drawable.continuebuttontrans)
                val ft = requireActivity().supportFragmentManager.beginTransaction()
                ft.replace(R.id.containerLogFragment, DailyFragment())
                ft.commit()
            }
            1 -> {
                binding!!.day.setBackgroundResource(R.drawable.continuebuttontrans)
                binding!!.week.backgroundTintList=null
                binding!!.week.setBackgroundResource(R.drawable.continuebutton2)
                binding!!.month.setBackgroundResource(R.drawable.continuebuttontrans)
                binding!!.year.setBackgroundResource(R.drawable.continuebuttontrans)
                val ft = requireActivity().supportFragmentManager.beginTransaction()
                ft.replace(R.id.containerLogFragment, WeeklyFragment())
                ft.commit()
            }
            2 -> {
                binding!!.day.setBackgroundResource(R.drawable.continuebuttontrans)
                binding!!.week.setBackgroundResource(R.drawable.continuebuttontrans)
                binding!!.month.setBackgroundResource(R.drawable.continuebutton2)
                binding!!.month.backgroundTintList=null
                binding!!.year.setBackgroundResource(R.drawable.continuebuttontrans)
                val ft = requireActivity().supportFragmentManager.beginTransaction()
                ft.replace(R.id.containerLogFragment, MonthlyFragment())
                ft.commit()
            }
            3 -> {
                binding!!.day.setBackgroundResource(R.drawable.continuebuttontrans)
                binding!!.week.setBackgroundResource(R.drawable.continuebuttontrans)
                binding!!.month.setBackgroundResource(R.drawable.continuebuttontrans)
                binding!!.year.setBackgroundResource(R.drawable.continuebutton2)
                binding!!.year.backgroundTintList=null
                val ft = requireActivity().supportFragmentManager.beginTransaction()
                ft.replace(R.id.containerLogFragment, YearlyFragment())
                ft.commit()
            }
        }
        return binding!!.root
    }

    private fun updateTabSelection(selectedTab: Int, fragment: Fragment) {
        Helper.SelectedButtonOfLogDailyTab = selectedTab
        binding?.apply {
            when(selectedTab) {
                0->{
                    dhabits?.setBackgroundResource(R.drawable.continuebutton2)
                    dhabits?.backgroundTintList=null
                    davoided?.setBackgroundResource(R.drawable.continuebuttontrans)
                    ddone?.setBackgroundResource(R.drawable.continuebuttontrans)
                }
                1->{
                    dhabits?.setBackgroundResource(R.drawable.continuebuttontrans)
                    davoided?.setBackgroundResource(R.drawable.continuebutton2)
                    davoided?.backgroundTintList=null
                    ddone?.setBackgroundResource(R.drawable.continuebuttontrans)
                }
                2->{
                    dhabits?.setBackgroundResource(R.drawable.continuebuttontrans)
                    davoided?.setBackgroundResource(R.drawable.continuebuttontrans)
                    ddone?.setBackgroundResource(R.drawable.continuebutton2)
                    ddone?.backgroundTintList=null
                }
                else-> {
                    dhabits?.setBackgroundResource(R.drawable.continuebutton2)
                    dhabits?.backgroundTintList=null
                    davoided?.setBackgroundResource(R.drawable.continuebuttontrans)
                    ddone?.setBackgroundResource(R.drawable.continuebuttontrans)
                }

            }
        }

        activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.containerLogDailyFragment, fragment)?.commit()
    }
}