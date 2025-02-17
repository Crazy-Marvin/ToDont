package rocks.poopjournal.todont.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import rocks.poopjournal.todont.adapters.AvoidedOrDoneAdapter
import rocks.poopjournal.todont.Helper
import rocks.poopjournal.todont.MainActivity
import rocks.poopjournal.todont.R
import rocks.poopjournal.todont.model.Habit
import rocks.poopjournal.todont.utils.DatabaseUtils

class AvoidedOrDoneFragment() : Fragment() {
    var rv: RecyclerView? = null

    var habits=ArrayList<Habit>()
    var dbHelper: DatabaseUtils? = null
    var adapter: AvoidedOrDoneAdapter? = null

    var isAvoided:Boolean=false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getBoolean("ARG_DATA")?.let {
            isAvoided=it
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_avoided, container, false)
        //Helper.SelectedButtonOfTodayTab=true;
        rv = view.findViewById(R.id.rv)
        dbHelper = DatabaseUtils(requireContext())


        return view
    }



    override fun onResume() {
        super.onResume()
        setDataInList()
    }

    private fun setDataInList() {
        getData()?.let {
            habits=it
            rv!!.layoutManager = LinearLayoutManager(activity)
            ItemTouchHelper(itemtouchhelper).attachToRecyclerView(rv)
            val resIcon=if(isAvoided) R.drawable.ic_avoided else R.drawable.ic_done
            adapter = context?.let {
                AvoidedOrDoneAdapter(
                    requireContext(), dbHelper!!,habits,isAvoided,resIcon
                )
            }
            rv!!.adapter = adapter
            rv!!.layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun getData(): ArrayList<Habit>? {
        return if(isAvoided){
            dbHelper?.getAvoidedHabits()
        }else{
            dbHelper?.getDoneHabits()
        }
    }

    private var itemtouchhelper: ItemTouchHelper.SimpleCallback =
        object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if (direction == 8) {
                    val builder1 = AlertDialog.Builder(
                        context
                    )
                    builder1.setMessage(R.string.delete_habit)
                    builder1.setCancelable(true)

                    builder1.setPositiveButton(
                        "Yes"
                    ) { dialog, id ->
                        val i = viewHolder.adapterPosition
                        dbHelper?.deleteHabit(habits[i].id)
                        Helper.SelectedButtonOfTodayTab = 1
                        habits.removeAt(i)
                        adapter?.notifyItemRemoved(i)
                        adapter?.notifyItemRangeChanged(i, habits.size)
//                        val intent = Intent(activity, MainActivity::class.java)
//                        startActivity(intent)
                        activity!!.overridePendingTransition(0, 0)
                        dialog.cancel()
                    }

                    builder1.setNegativeButton(
                        R.string.no
                    ) { dialog, id ->
//                        val i = Intent(activity, MainActivity::class.java)
//                        startActivity(i)
                        adapter?.notifyDataSetChanged()
                        activity!!.overridePendingTransition(0, 0)
                        dialog.cancel()
                    }

                    val alert11 = builder1.create()
                    alert11.show()
                }
            }
        }

    companion object {
        fun newInstance(someData: Boolean): AvoidedOrDoneFragment {
            val fragment = AvoidedOrDoneFragment()
            val args = Bundle()
            args.putBoolean("ARG_DATA", someData)
            fragment.arguments = args
            return fragment
        }
    }
}