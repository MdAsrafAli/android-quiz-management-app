package com.example.quiz.activities

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ImageSpan
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quiz.R
import com.example.quiz.adapters.QuizAdapter
import com.example.quiz.adapters.QuizAdapterForNotify
import com.example.quiz.adapters.ShortQuizAdapter
import com.example.quiz.adapters.ShortQuizAdapterForNotify
import com.example.quiz.models.Quiz
import com.example.quiz.models.ShortQuiz
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificationActivity:AppCompatActivity() {
    private lateinit var databaseRef: FirebaseDatabase
    lateinit var shortadapter: ShortQuizAdapterForNotify
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    lateinit var adapter: QuizAdapterForNotify
    lateinit var quizRecyclerView: RecyclerView
    lateinit var shortQuizRecyclerView: RecyclerView
    private lateinit var searchEditText: EditText
    private lateinit var refreshButton:Button
    private var quizList=mutableListOf<Quiz>()
    private var shortquizlist= mutableListOf<ShortQuiz>()
    private lateinit var searchLayout: TextInputLayout
    private var isSearchBarExpanded = false
    private lateinit var set: Set<String>

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notification)
        refreshButton=findViewById(R.id.refreshButton)

        setUpViews()
        setUpButtonClickListeners()

    }


    fun setUpViews(){
        quizList=MainActivity.QuizManager.NotifyquizList
        setAdapter(MainActivity.ShortQuestion.NotifyquizList)
        setAdapterMCQ(quizList)
        scheduleRealtimeDatabaseUpdate()
        Log.e("Notification", MainActivity.QuizManager.NotifyquizList.toString())
        setUpDrawerLayout()
        setUpDatePicker()
        searchEditText = findViewById(R.id.searchEditText)

        setUpSearchFunctionality("Search MCQ")
    }

    private fun scheduleRealtimeDatabaseUpdate() {
        val sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        // Create a new Runnable that calls the setup function
        runnable = object : Runnable {
            override fun run() {


                set = sharedPreferences.getStringSet("myList", null)!!

                if (set != null) {

                    val list: MutableList<String> = set.toMutableList()
                    for(item in list){
                        Log.e("ItemOfMyListOfSe",item)
                        setUpRealtimeDatabaseforMCQ(item)
                        setUpRealtimeDatabase(item)
                    }
                }
                // Schedule the next execution of this Runnable after 5 minutes
                handler.postDelayed(this, 5 * 60 * 1000) // 5 minutes in milliseconds
            }
        }

        // Post the initial execution of the Runnable
        handler.post(runnable)
    }


    fun setUpDrawerLayout(){
        val appBar: MaterialToolbar = findViewById(R.id.appBar)
        val mainDrawer: DrawerLayout =findViewById(R.id.mainDrawer)
        setSupportActionBar(appBar)
        actionBarDrawerToggle=ActionBarDrawerToggle(this,mainDrawer,
            R.string.app_name,
            R.string.app_name
        )
        actionBarDrawerToggle.syncState()
        val navigationView: NavigationView = findViewById(R.id.navigationView)

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.btnProfile -> {
                    // Handle profile item click
                    startActivity(Intent(this, ProfileActivity::class.java))
                    mainDrawer.closeDrawers()
                    true
                }
                // Add more cases for other menu items as needed
                else -> false
            }
        }
    }

    private fun setUpDatePicker() {
        val btnDatePicker: FloatingActionButton = findViewById(R.id.btnDatePicker)
        quizRecyclerView=findViewById(R.id.quizRecyclerView)



        btnDatePicker.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker().build()
            datePicker.show(supportFragmentManager, "DatePicker")
            datePicker.addOnPositiveButtonClickListener {
                Log.d("DATEPICKER",datePicker.headerText)
                val dateFormatter= SimpleDateFormat("dd-mm-yyyy")
                val date=dateFormatter.format(Date(it))
                val intent= Intent(this, QuestionActivity::class.java)
                intent.putExtra("DATE",date)
                startActivity(intent)
            }
            datePicker.addOnNegativeButtonClickListener {
                Log.d("DATEPICKER",datePicker.headerText)
            }
            datePicker.addOnCancelListener() {
                Log.d("DATEPICKER","DatePicker Cancelled")
            }

        }

    }
    private fun setAdapter(shortquizlist: MutableList<ShortQuiz>) {

        shortadapter= ShortQuizAdapterForNotify(this,shortquizlist)
        var shortQuizRecyclerView=findViewById<RecyclerView>(R.id.shortQuizRecyclerView)
        shortQuizRecyclerView.layoutManager= GridLayoutManager(this,2)
        shortQuizRecyclerView.adapter=shortadapter

    }
    private fun setUpRealtimeDatabaseforMCQ(item:String) {
        quizList.clear()
        databaseRef = Firebase.database
        val ref = databaseRef.reference.child("MCQ").child(item)

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val currentDate = Date()
                val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault())

                for (snap in dataSnapshot.children) {
                    val itm = snap.getValue(Quiz::class.java)
                    if (itm != null) {
                        val selectedDateTimeStr = itm.selectedDateTime
                        if (!selectedDateTimeStr.isNullOrEmpty()) {
                            try {
                                val selectedDateTime = dateFormat.parse(selectedDateTimeStr)
                                if (selectedDateTime != null) {
                                    val comparisonResult = currentDate.compareTo(selectedDateTime)
                                    if (comparisonResult < 0) {
                                        quizList.add(itm)
                                    }
                                } else {
                                    Log.e("ParseError", "Parsed date is null: $selectedDateTimeStr")
                                }
                            } catch (e: ParseException) {
                                // Handle parse exception
                                Log.e("ParseError", "Error parsing date: $selectedDateTimeStr", e)
                            }
                        } else {
                            Log.e("ParseError", "Empty or null date: $selectedDateTimeStr")
                        }
                    }
                }

                setAdapterMCQ(quizList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
                Toast.makeText(
                    this@NotificationActivity,
                    "Error fetching data: ${databaseError.message}", Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun setAdapterMCQ(quizlist: MutableList<Quiz>) {

        adapter= QuizAdapterForNotify(this,quizlist)
        var QuizRecyclerView=findViewById<RecyclerView>(R.id.quizRecyclerView)
        QuizRecyclerView.layoutManager=GridLayoutManager(this,2)
        QuizRecyclerView.adapter=adapter

    }
    private fun setUpSearchFunctionalityForShortQuiz(hint: String){
        searchEditText.hint=hint
        searchEditText = findViewById(R.id.searchEditText)
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                filterShortQuiz(s.toString())
            }
        })
    }
    private fun filterShortQuiz(text: String){
        val filteredList = mutableListOf<ShortQuiz>()
        for (item in shortquizlist) {
            val title = item.title.orEmpty() // Handle null titles
            if (title.contains(text, true)) {
                filteredList.add(item)
            }
        }
        Log.d("Filter", "Filtered List: $filteredList")
        setAdapter(filteredList)
    }

    private fun setUpSearchFunctionality(hint: String) {
        searchEditText.hint=hint
        searchEditText= findViewById(R.id.searchEditText)
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                filter(s.toString())
            }
        })
    }

    private fun filter(text: String) {
        val filteredList = mutableListOf<Quiz>()
        for (item in quizList) {
            val title = item.title.orEmpty() // Handle null titles
            if (title.contains(text, true)) {
                filteredList.add(item)
            }
        }
        Log.d("Filter", "Filtered List: $filteredList")
        setAdapterMCQ(filteredList)
    }
    private fun setUpRealtimeDatabase(item:String) {
        databaseRef = Firebase.database
        val ref = databaseRef.reference.child("Qizzs").child(item)

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val currentDate = Date()
                val shortquizlist = mutableListOf<ShortQuiz>()

                // Adjusted date format to match input string
                val dateFormat = SimpleDateFormat("yyyy-M-d hh:mm a", Locale.getDefault())

                for (snap in dataSnapshot.children) {
                    val itm = snap.getValue(ShortQuiz::class.java)
                    if (itm != null) {
                        val selectedDateTimeStr = itm.selectedDateTime
                        if (!selectedDateTimeStr.isNullOrEmpty()) {
                            try {
                                val selectedDateTime = dateFormat.parse(selectedDateTimeStr)
                                if (selectedDateTime != null) {
                                    val comparisonResult = currentDate.compareTo(selectedDateTime)
                                    if (comparisonResult < 0) {
                                        shortquizlist.add(itm)
                                    } else {
                                        // Handle future dates if needed
                                    }
                                }
                            } catch (e: ParseException) {
                                // Handle parse exception
                                Log.e("ParseError", "Error parsing date: $selectedDateTimeStr", e)
                            }
                        } else {
                            Log.e("ParseError", "Empty or null date: $selectedDateTimeStr")
                        }
                    }
                }

                setAdapter(shortquizlist)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
                Toast.makeText(
                    this@NotificationActivity,
                    "Error fetching data: ${databaseError.message}", Toast.LENGTH_SHORT
                ).show()
            }
        })
    }






    private fun setUpButtonClickListeners() {
        val notifySize=MainActivity.QuizManager.NotifyquizList.size

        refreshButton.setOnClickListener{
            val sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
            set = sharedPreferences.getStringSet("myList", null)!!

            if (set != null) {

                val list: MutableList<String> = set.toMutableList()
                for(item in list){
                    Log.e("ItemOfMyListOfSe",item)
                    setUpRealtimeDatabaseforMCQ(item)
                    setUpRealtimeDatabase(item)
                }
            }
            if(MainActivity.QuizManager.NotifyquizList.size!=notifySize){
                Log.e("DataChange",notifySize.toString())
            }
            val handler = Handler()

// Runnable to perform action after delay
            val runnable = Runnable {
                // Do nothing here
            }

// Delay for 1 second (1000 milliseconds)
            handler.postDelayed(runnable, 1000)
        }

        val btnMCQ: MaterialButton = findViewById(R.id.btnMCQ)
        val btnShortQuestion: MaterialButton = findViewById(R.id.btnShortQuestion)
        quizRecyclerView = findViewById(R.id.quizRecyclerView)
        shortQuizRecyclerView = findViewById(R.id.shortQuizRecyclerView)
        shortQuizRecyclerView.visibility = View.GONE



        findViewById<View>(R.id.underlineMCQ).visibility = View.VISIBLE
        findViewById<View>(R.id.underlineShortQuestion).visibility = View.INVISIBLE

        // Set up click listeners for MCQ and Short Question buttons
        btnMCQ.setOnClickListener {
            // Show underline for MCQ button and hide underline for Short Question button
            findViewById<View>(R.id.underlineMCQ).visibility = View.VISIBLE
            findViewById<View>(R.id.underlineShortQuestion).visibility = View.INVISIBLE
            shortQuizRecyclerView.visibility = View.GONE
            quizRecyclerView.visibility = View.VISIBLE
            // Set up search functionality for MCQ
            setUpSearchFunctionality("Search MCQ")
            // Show MCQ RecyclerView and hide Short Quiz RecyclerView

        }

        btnShortQuestion.setOnClickListener {
            // Show underline for Short Question button and hide underline for MCQ button
            findViewById<View>(R.id.underlineMCQ).visibility = View.INVISIBLE
            findViewById<View>(R.id.underlineShortQuestion).visibility = View.VISIBLE
            quizRecyclerView.visibility = View.GONE
            shortQuizRecyclerView.visibility = View.VISIBLE
            // Set up search functionality for Short Questions
            setUpSearchFunctionalityForShortQuiz("Search Short Question")
            // Show Short Quiz RecyclerView and hide MCQ RecyclerView

        }

    }
}