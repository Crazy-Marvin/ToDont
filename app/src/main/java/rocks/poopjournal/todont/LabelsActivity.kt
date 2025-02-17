package rocks.poopjournal.todont

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import rocks.poopjournal.todont.adapters.LabelsAdapter
import rocks.poopjournal.todont.databinding.ActivityLabelsBinding
import rocks.poopjournal.todont.databinding.DialogboxLabelsBinding
import rocks.poopjournal.todont.model.Label
import rocks.poopjournal.todont.showcaseview.RippleBackground
import rocks.poopjournal.todont.showcaseview.ShowcaseViewBuilder
import rocks.poopjournal.todont.utils.DatabaseUtils
import rocks.poopjournal.todont.utils.SharedPrefUtils
import rocks.poopjournal.todont.utils.setAppTheme
import smartdevelop.ir.eram.showcaseviewlib.GuideView
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity
import smartdevelop.ir.eram.showcaseviewlib.config.PointerType

class LabelsActivity : AppCompatActivity() {
    private var rvLabels: RecyclerView? = null
    private var dbHelper: DatabaseUtils? = null
    private var adapter: LabelsAdapter? = null
    private var labels = ArrayList<Label>()
    private var labelsFloatingButton: FloatingActionButton? = null
    private var showcaseViewBuilder: ShowcaseViewBuilder? = null
    private var prefUtils: SharedPrefUtils? = null
    private var fabHighlighter: RippleBackground? = null

    private lateinit var binding: ActivityLabelsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppTheme(this)
        binding = ActivityLabelsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //actionBar?.setBackgroundDrawable(resources.getDrawable(R.drawable.mygradient))

        rvLabels = binding.rvLabels

        prefUtils = SharedPrefUtils(this)
        showcaseViewBuilder = ShowcaseViewBuilder.init(this)
        fabHighlighter = binding.fabHighlighter

        dbHelper = DatabaseUtils(this)
        labelsFloatingButton = binding.labelFloatingbtn

        loadLabel()

        rvLabels?.setLayoutManager(LinearLayoutManager(this))
        ItemTouchHelper(itemTouchHelper).attachToRecyclerView(rvLabels)
        adapter = LabelsAdapter(this, dbHelper!!, labels)
        rvLabels?.setAdapter(adapter)
        rvLabels?.setLayoutManager(LinearLayoutManager(this))

        labelsFloatingButton?.setOnClickListener(View.OnClickListener {
            if (!prefUtils!!.getBool("plus1")) {
                showcaseFab()
            } else {
                showLabelAddingDialog()
            }
        })
    }

    private fun showLabelAddingDialog() {
        val dialog = Dialog(this@LabelsActivity,R.style.Dialog_Theme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        // Inflate the custom layout
        val dialogView = DialogboxLabelsBinding.inflate(layoutInflater)
        dialog.setContentView(dialogView.root)

        // Set the dialog background to transparent to support rounded corners from CardView
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Set the layout parameters for the dialog
        val lp = dialog.window?.attributes
        lp?.width = WindowManager.LayoutParams.MATCH_PARENT
        lp?.height = WindowManager.LayoutParams.WRAP_CONTENT
        //lp?.dimAmount = 0.8f // Adjust the dimming amount for background
        // dialog.window?.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND) // Ensure the dim effect is enabled
        dialog.window?.attributes = lp

        // Handle the Save button click
        dialogView.saveLabelButton.setOnClickListener {
            val enteredText = dialogView.label.text.toString().replace("'", "''")
            if (enteredText.trim().isNotEmpty()) {
                dbHelper?.insertLabel(Label(name = enteredText))
                loadLabel() // Refresh the label list
                adapter?.updateData(labels) // Update the adapter data
                dialog.dismiss() // Dismiss the dialog
            } else {
                dialogView.label.error = "Please enter a label" // Show error for empty input
            }
        }

        // Show the dialog
        dialog.show()
    }

    private fun loadLabel() {
        dbHelper?.getAllLabels()?.let {
            labels.clear()
            labels.addAll(it)
        }
    }

    private var itemTouchHelper: ItemTouchHelper.SimpleCallback =
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
                    val dialog = AlertDialog.Builder(this@LabelsActivity)
                    dialog.setMessage(getString(R.string.do_you_really_want_to_delete_this_this_will_also_delete_all_habits_related_to_this_label))
                    dialog.setCancelable(true)

                    dialog.setPositiveButton(
                        "Yes"
                    ) { dialog, id ->
                        val i = viewHolder.adapterPosition
                        val habits=dbHelper?.getHabitsWithLabelId(labels[i].labelId)
                        for(habit in habits!!){
                            dbHelper?.deleteAllHabitRecords(habit.id)
                            dbHelper?.deleteHabit(habit.id)
                        }
                        dbHelper?.deleteLabel(labels[i].labelId)
                        labels.removeAt(i)
                        adapter?.notifyItemRemoved(i)
                        overridePendingTransition(0, 0)
                        dialog.cancel()
                    }

                    dialog.setNegativeButton(
                        "No"
                    ) { dialog, id ->
//                        val intent = Intent(applicationContext, LabelsActivity::class.java)
//                        startActivity(intent)
                        adapter?.notifyDataSetChanged()
                        overridePendingTransition(0, 0)
                        dialog.dismiss()
                    }
                    val alert11 = dialog.create()
                    alert11.show()
                }
            }
        }

    fun backBtnClicked(view: View?) {
//        val i = Intent(this, MainActivity::class.java)
//        startActivity(i)
        finish()
    }

    private fun showcaseFab() {

        val guideView = GuideView.Builder(this@LabelsActivity)
            .setContentText(getString(R.string.add_label))
            .setTargetView(labelsFloatingButton)
            .setDismissType(DismissType.anywhere)
            .setPointerType(PointerType.arrow)
            .setGravity(Gravity.center)
            .setGuideListener {
                prefUtils!!.setBool(
                    "plus1",
                    true
                )
            }
        guideView.build().show()
    }


    override fun onBackPressed() {
        super.onBackPressed()
//        val i = Intent(this, MainActivity::class.java)
//        startActivity(i)
    }
}
