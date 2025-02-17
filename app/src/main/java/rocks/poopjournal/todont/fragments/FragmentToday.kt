package rocks.poopjournal.todont.fragments

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import rocks.poopjournal.todont.Helper
import rocks.poopjournal.todont.MainActivity
import rocks.poopjournal.todont.R

class FragmentToday : Fragment() {
    var avoided: MaterialButton? = null
    var done: Button? = null
    var habits: Button? = null

    private lateinit var habitsFrags: HabitsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.getBoolean("ARG_DATA")?.let {
            habitsFrags = HabitsFragment.newInstance(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_today2, container, false)
        Helper.isTodaySelected = true
        avoided = view.findViewById(R.id.avoided)
        done = view.findViewById(R.id.done)
        habits = view.findViewById(R.id.habits)

        if (Helper.SelectedButtonOfTodayTab == 0) {
            habits?.setBackgroundResource(R.drawable.continuebutton2)
            habits?.backgroundTintList = null
            (requireActivity() as MainActivity)?.showFAB()
            avoided?.setBackgroundResource(R.drawable.continuebuttontrans)
            done?.setBackgroundResource(R.drawable.continuebuttontrans)
            replaceFragment(habitsFrags)
        } else if (Helper.SelectedButtonOfTodayTab == 1) {
            habits?.setBackgroundResource(R.drawable.continuebuttontrans)
            avoided?.setBackgroundResource(R.drawable.continuebutton2)
            avoided?.backgroundTintList = null
            (requireActivity() as MainActivity)?.hideFAB()
            done?.setBackgroundResource(R.drawable.continuebuttontrans)
            val frag = AvoidedOrDoneFragment.newInstance(true)
            replaceFragment(frag)
        } else if (Helper.SelectedButtonOfTodayTab == 2) {
            habits?.setBackgroundResource(R.drawable.continuebuttontrans)
            avoided?.setBackgroundResource(R.drawable.continuebuttontrans)
            done?.setBackgroundResource(R.drawable.continuebutton2)
            (requireActivity() as MainActivity)?.hideFAB()
            done?.backgroundTintList = null
            val frag = AvoidedOrDoneFragment.newInstance(false)
            replaceFragment(frag)
        }


        habits?.setOnClickListener(View.OnClickListener {
            habits?.setBackgroundResource(R.drawable.continuebutton2)
            habits?.backgroundTintList = null
            (requireActivity() as MainActivity)?.showFAB()
            avoided?.setBackgroundResource(R.drawable.continuebuttontrans)
            done?.setBackgroundResource(R.drawable.continuebuttontrans)
            replaceFragment(habitsFrags)
        })
        avoided?.setOnClickListener(View.OnClickListener {
            habits?.setBackgroundResource(R.drawable.continuebuttontrans)
            avoided?.setBackgroundResource(R.drawable.continuebutton2)
            avoided?.backgroundTintList = null
            (requireActivity() as MainActivity)?.hideFAB()
            done?.setBackgroundResource(R.drawable.continuebuttontrans)
            val frag = AvoidedOrDoneFragment.newInstance(true)
            replaceFragment(frag)
        })
        done?.setOnClickListener(View.OnClickListener {
            habits?.setBackgroundResource(R.drawable.continuebuttontrans)
            avoided?.setBackgroundResource(R.drawable.continuebuttontrans)
            done?.setBackgroundResource(R.drawable.continuebutton2)
            (requireActivity() as MainActivity)?.hideFAB()
            done?.backgroundTintList = null
            val frag = AvoidedOrDoneFragment.newInstance(false)
            replaceFragment(frag)
        })



        return view
    }

    fun addNewHabit() {
        habitsFrags.addNewHabit()
    }

    fun replaceFragment(fragment: Fragment) {
        val ft = requireActivity().supportFragmentManager.beginTransaction()
        ft.replace(R.id.containerTodayFragment, fragment)
        ft.commit()
    }

    companion object {
        fun newInstance(someData: Boolean): FragmentToday {
            val fragment = FragmentToday()
            val args = Bundle()
            args.putBoolean("ARG_DATA", someData)
            fragment.arguments = args
            return fragment
        }


    }
}