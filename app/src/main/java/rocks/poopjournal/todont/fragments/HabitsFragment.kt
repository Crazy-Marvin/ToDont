package rocks.poopjournal.todont.fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import jp.wasabeef.glide.transformations.BlurTransformation
import rocks.poopjournal.todont.adapters.HabitsAdapter
import rocks.poopjournal.todont.Helper
import rocks.poopjournal.todont.R
import rocks.poopjournal.todont.databinding.DialogboxAddNewHabitBinding
import rocks.poopjournal.todont.model.Habit
import rocks.poopjournal.todont.model.Label
import rocks.poopjournal.todont.showcaseview.ShowcaseViewBuilder
import rocks.poopjournal.todont.showcaseview.ShowcaseViewBuilder.Companion.init
import rocks.poopjournal.todont.utils.Constants
import rocks.poopjournal.todont.utils.DatabaseUtils
import rocks.poopjournal.todont.utils.SharedPrefUtils
import smartdevelop.ir.eram.showcaseviewlib.GuideView
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity
import smartdevelop.ir.eram.showcaseviewlib.config.PointerType
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HabitsFragment() : Fragment() {
    private var rv: RecyclerView? = null
    private var habitsList = ArrayList<Habit>()
    private var date: Date = Calendar.getInstance().time
    private var selectedLabel: Label? = null
    private var dbHelper: DatabaseUtils? = null
    private var adapter: HabitsAdapter? = null
    private var tvNoHabits: TextView? = null
    private var showcaseViewBuilder: ShowcaseViewBuilder? = null
    private var prefUtils: SharedPrefUtils? = null


    private val PICK_IMAGE_REQUEST = 1001
    private var selectedImageUri: Uri? = null
    private var addNewHabit=false;

    private var dialogView: DialogboxAddNewHabitBinding? = null;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getBoolean("ARG_DATA")?.let {
            addNewHabit=it
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_habits, container, false)
        Helper.SelectedButtonOfTodayTab = 0
        rv = view.findViewById(R.id.rv)
        dbHelper = DatabaseUtils(requireContext())

        showcaseViewBuilder = init(requireActivity())
        prefUtils = SharedPrefUtils(requireActivity())
        //  tv1 = view.findViewById(R.id.a);
        tvNoHabits = view.findViewById(R.id.b)
        if(addNewHabit){
            addNewHabit()
            addNewHabit=false
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    override fun onResume() {
        super.onResume()
        setDataInList()
    }

    fun addNewHabit() {

        val labelCount = dbHelper?.getLabelsCount()
        if (labelCount == 0) {
            Toast.makeText(activity, R.string.please_add_a_label_first, Toast.LENGTH_LONG)
                .show()
        } else {
            val dateFormat = SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault())
            val formattedDate = dateFormat.format(date)

            val dialog = Dialog(requireActivity())
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialogView = DialogboxAddNewHabitBinding.inflate(layoutInflater)
            dialog.setContentView(dialogView!!.root)

            val lp = dialog.window!!.attributes
            lp.dimAmount = 0.9f
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val window = dialog.window
            window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            dialog.window!!.attributes = lp

            if (labelCount == 0) {
                dialogView!!.txt.visibility = View.VISIBLE
                dialogView!!.spinner.visibility = View.GONE
            } else {
                dialogView!!.txt.visibility = View.GONE
                dialogView!!.spinner.visibility = View.VISIBLE
            }
            val labels = dbHelper?.getAllLabels()!!
            labels.add(0, Label(-1, "Select a label"))
            val arrayAdapter = ArrayAdapter(
                requireActivity(),
                android.R.layout.simple_spinner_item,
                labels
            )
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            dialogView!!.spinner.adapter = arrayAdapter


            dialogView!!.spinner.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        adapterView: AdapterView<*>,
                        view: View,
                        i: Int,
                        l: Long
                    ) {
                        if (i != 0) {
                            selectedLabel = labels[i]
                            val typedValue = TypedValue()
                            requireContext().theme.resolveAttribute(
                                R.attr.colorOnBackground2,
                                typedValue,
                                true
                            )
                            (adapterView.getChildAt(0) as TextView).setTextColor(
                                typedValue.data
                            )
                        }

                    }

                    override fun onNothingSelected(adapterView: AdapterView<*>?) {
                    }
                }

            dialogView!!.btnSelectImg.setOnClickListener {
                openImagePicker()
            }


            dialogView!!.saveTaskButton.setOnClickListener {
                val habit_text = dialogView?.habit?.text.toString()
                val detail_text = dialogView?.detail?.text.toString()

                val imageUriString = selectedImageUri?.toString()

                dbHelper?.insertHabit(
                    Habit(
                        date = formattedDate,
                        name = habit_text,
                        description = detail_text,
                        countAvoided = 0,
                        countDone = 0,
                        labelId = selectedLabel?.labelId!!,
                        coverImageUri = imageUriString  // Add this line
                    )
                )

                // Reset image selection
                selectedImageUri = null
                dialogView?.frameSelectedImage?.visibility = View.GONE

                Helper.SelectedButtonOfTodayTab = 0
//                    val i = Intent(activity, MainActivity::class.java)
//                    startActivity(i)
                setDataInList()
                requireActivity().overridePendingTransition(0, 0)
                dialog.dismiss()
            }
            dialogView!!.spinner.adapter = arrayAdapter
            dialog.show()
        }

    }

    // Function to load image with blur effect
    private fun loadImageWithBlur(imageUri: Uri) {
        // Show both ImageViews
        dialogView?.frameSelectedImage?.visibility = View.VISIBLE

        // Load blurred image into background ImageView
        dialogView?.ivBlurredBackground?.let {
            Glide.with(requireContext())
                .load(imageUri)
                .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 3)))
                .into(it)
        }

        // Load normal image into foreground ImageView
        dialogView?.ivSelectedImage?.let {
            Glide.with(requireContext())
                .load(imageUri)
                .into(it)
        }
    }

    // Add this function to handle image selection
    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
        }
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    // Handle the result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            val inputUri = data?.data
            inputUri?.let { uri ->
                // Save the image to internal storage and get the new URI
                val savedImageUri = saveImageToInternalStorage(uri)
                savedImageUri?.let {
                    // Set the selectedImageUri to the URI of the saved image
                    selectedImageUri = it
                    // Load the image (e.g., with blur)
                    loadImageWithBlur(it)
                }
            }
        }
    }

    private fun saveImageToInternalStorage(uri: Uri): Uri? {
        return try {
            // Open an input stream from the original URI
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            inputStream?.use { stream ->
                // Get the internal storage directory for the app
                val directory = File(requireContext().filesDir, "Cover_Images")
                if (!directory.exists()) {
                    directory.mkdirs() // Create the directory if it doesn't exist
                }

                // Create a file in the app's internal storage
                val fileName = "image_${System.currentTimeMillis()}.jpg"
                val file = File(directory, fileName)

                // Write the image data to the file
                val outputStream = FileOutputStream(file)
                outputStream.use { out ->
                    stream.copyTo(out)
                }

                // Return the URI of the saved file
                Uri.fromFile(file)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    private fun setDataInList() {
        dbHelper?.getAllHabits()?.let {
            habitsList = it
            if (habitsList.isNotEmpty()) {
                tvNoHabits?.visibility = View.INVISIBLE

                rv?.layoutManager = LinearLayoutManager(activity)
                ItemTouchHelper(itemtouchhelper).attachToRecyclerView(rv)
                adapter =
                    HabitsAdapter(
                        requireContext(),
                        dbHelper!!, habitsList
                    )

                rv!!.adapter = adapter
                rv!!.layoutManager = LinearLayoutManager(activity)

            } else {
                tvNoHabits?.visibility = View.VISIBLE
            }

        } ?: run { tvNoHabits?.setVisibility(View.VISIBLE) }

    }

    var itemtouchhelper: ItemTouchHelper.SimpleCallback =
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
                    builder1.setMessage(R.string.do_you_really_want_to_delete_this)
                    builder1.setCancelable(true)

                    builder1.setPositiveButton(
                        R.string.yes
                    ) { dialog, id ->
                        val i = viewHolder.adapterPosition
                        habitsList[i].coverImageUri?.let { it1 -> deleteImageFromInternalStorage(it1) }
                        dbHelper?.deleteHabit(habitsList[i].id)
                        dbHelper?.deleteAllHabitRecords(habitsList[i].id)
                        Helper.SelectedButtonOfTodayTab = 0
                        habitsList.removeAt(i)
                        adapter?.notifyItemRemoved(i)
                        adapter?.notifyItemRangeChanged(0, habitsList.size)
//                        val intent = Intent(activity, MainActivity::class.java)
//                        startActivity(intent)
                        activity!!.overridePendingTransition(0, 0)
                        dialog.cancel()
                    }

                    builder1.setNegativeButton(
                        R.string.no
                    ) { dialog, id ->
                        Helper.SelectedButtonOfTodayTab = 0
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

            private fun deleteImageFromInternalStorage(uriString: String): Boolean {
                val uri = Uri.parse(uriString)
                return try {
                    // Convert the URI to a File object
                    val file = File(uri.path)
                    // Check if the file exists and delete it
                    if (file.exists()) {
                        file.delete()
                    } else {
                        false // File does not exist
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
            }
        }

    companion object {
        fun newInstance(someData: Boolean): HabitsFragment {
            val fragment = HabitsFragment()
            val args = Bundle()
            args.putBoolean("ARG_DATA", someData)
            fragment.arguments = args
            return fragment
        }


    }
}
