package com.example.quiz.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quiz.R
import com.example.quiz.adapters.QuizAdapter
import com.example.quiz.adapters.ShortQuizAdapter
import com.example.quiz.models.Quiz
import com.example.quiz.models.ShortQuiz
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import java.text.SimpleDateFormat
import java.util.Date
import android.content.Context
import android.content.res.ColorStateList
import android.os.Handler
import android.os.Looper
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import java.text.ParseException
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var databaseRef:FirebaseDatabase
    private lateinit var firebaseAuth:FirebaseAuth
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    lateinit var shortadapter:ShortQuizAdapter
    lateinit var adapter: QuizAdapter
    private var quizList=mutableListOf<Quiz>()
    private var shortquizlist= mutableListOf<ShortQuiz>()
    private lateinit var set: Set<String>
    lateinit var quizRecyclerView: RecyclerView
    lateinit var shortQuizRecyclerView: RecyclerView
    private lateinit var searchEditText:EditText
    private lateinit var AddQuizButton: FloatingActionButton
    private lateinit var btnNotification: FloatingActionButton
    private lateinit var userID:String
    private lateinit var email:String
    private lateinit var mail:String
    private lateinit var role:String
    private lateinit var Name:String
    private lateinit var list: MutableList<String>
    private lateinit var navigationView: NavigationView
    private lateinit var mainDrawer: DrawerLayout
    private lateinit var btnMCQ: MaterialButton
    private lateinit var btnShortQuestion: MaterialButton
    private lateinit var btnDatePicker: FloatingActionButton
    private lateinit var btnRequests : Button
    private lateinit var addQuiz : Button
    private lateinit var UpcomingQuiz : Button
    private lateinit var RunningQuiz : Button
    private lateinit var Results : Button
    private lateinit var profile : Button
    private var Notify =0
    private var A =0
    private var dialogShown = false

    object QuizManager {
        val NotifyquizList: MutableList<Quiz> = mutableListOf()
    }
    object ShortQuestion{
        val NotifyquizList: MutableList<ShortQuiz> = mutableListOf()
    }



    override fun onCreate(savedInstanceState: Bundle?) {


        FirebaseApp.initializeApp(this)
        firebaseAuth = FirebaseAuth.getInstance()
        mail = firebaseAuth.currentUser?.email.toString()

        list = mutableListOf()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnNotification=findViewById(R.id.notification)

        btnRequests = findViewById(R.id.btnRequests)
        addQuiz = findViewById(R.id.addQuiz)
        UpcomingQuiz = findViewById(R.id.upcomingQuizs)
        RunningQuiz = findViewById(R.id.runningQuiz)
        profile = findViewById(R.id.profile)
        btnRequests.visibility=View.GONE
        addQuiz.visibility=View.GONE
        UpcomingQuiz.visibility=View.GONE
        RunningQuiz.visibility=View.GONE
        profile.visibility=View.GONE

        mainDrawer=findViewById(R.id.mainDrawer)
        navigationView = findViewById(R.id.navigationView)
        quizRecyclerView = findViewById(R.id.quizRecyclerView)
        shortQuizRecyclerView = findViewById(R.id.shortQuizRecyclerView)
        btnMCQ = findViewById(R.id.btnMCQ)
        btnShortQuestion = findViewById(R.id.btnShortQuestion)
        Results = findViewById(R.id.studentResults)
        searchEditText = findViewById(R.id.searchEditText)
        btnDatePicker = findViewById(R.id.btnDatePicker)
        Results.visibility=View.GONE
        btnDatePicker.visibility = View.GONE
        btnMCQ.visibility = View.GONE
        btnShortQuestion.visibility = View.GONE
        searchEditText.visibility = View.GONE


        userSetUp()

        setUpViews()

        updateNotification()
        setUpButtonClickListeners()

    }

    private fun userSetUp() {
        val usersRef = FirebaseDatabase.getInstance().reference.child("users")

        val query = usersRef.orderByChild("email").equalTo(mail)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.exists()){

                    for (userSnapshot in dataSnapshot.children) {
                        userID = userSnapshot.key.toString()
                        val userData = userSnapshot.value as Map<*, *>
                        email = userData["email"] as String
                        Name = userData["name"] as String
                        role = userData["role"] as String
                        if(role=="Student"){
                            AddQuizButton=findViewById(R.id.AddQuizButton)
                            AddQuizButton.visibility=View.GONE
                            btnNotification.visibility=View.VISIBLE
                            btnMCQ.visibility = View.VISIBLE
                            btnShortQuestion.visibility = View.VISIBLE
                            btnDatePicker.visibility = View.VISIBLE
                            searchEditText.visibility = View.VISIBLE
                        }
                        else{
                            btnRequests.visibility=View.VISIBLE
                            addQuiz.visibility=View.VISIBLE
                            UpcomingQuiz.visibility=View.VISIBLE
                            RunningQuiz.visibility=View.VISIBLE
                            profile.visibility=View.VISIBLE
                            AddQuizButton.visibility=View.VISIBLE
                            Results.visibility=View.VISIBLE
                            btnNotification.visibility=View.GONE
                            navigationView.visibility=View.GONE
                            quizRecyclerView.visibility=View.GONE
                            shortQuizRecyclerView.visibility=View.GONE
                            btnMCQ.visibility=View.GONE
                            btnShortQuestion.visibility=View.GONE
                            searchEditText.visibility=View.GONE
                            disableDrawerForTeacher()
                        }
                        Log.d("email",role)
                        // Store user details in SharedPreferences

                        val sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
                        with(sharedPreferences.edit()) {
                            putString("email", email)
                            putString("Name", Name)
                            putString("Role", role)
                            putString("Phone", userID)
                            apply()
                        }
                        teachers()

                    }
                }

            }


            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
                Toast.makeText(this@MainActivity, "An Error occured: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })

    }
    private fun teachers() {
        databaseRef = FirebaseDatabase.getInstance()
        val requestsRef = databaseRef.reference.child("requests").child(userID)
        Log.e("requestsRef", requestsRef.toString())

        requestsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //list.clear()
                Log.e("DataSnapshot", dataSnapshot.toString()) // Log the entire dataSnapshot

                for (phoneEntry in dataSnapshot.children) {
                    val phoneNumber = phoneEntry.value.toString()
                    Log.e("PhoneEntry", phoneEntry.toString()) // Log each phone entry

                    if (phoneNumber.isNotEmpty()) {
                        list.add(phoneNumber)
                    }
                }
                Log.e("Numbers", list.toString())
                Log.e("ListOfNumbers",list.toString())

                if (list.size == 0 && !dialogShown) { // Check the flag
                    dialogShown = true // Set the flag to true
                    val dialogBuilder = android.app.AlertDialog.Builder(this@MainActivity)
                    dialogBuilder.setTitle("You didn't register any teacher.")
                    dialogBuilder.setMessage("Do you want to add teacher?")
                    dialogBuilder.setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss() // Dismiss the dialog before starting the delay
                        Handler(Looper.getMainLooper()).postDelayed({
                            val intent = Intent(this@MainActivity, AddCourse::class.java)
                            startActivity(intent)
                            finish()
                        }, 500) // Delay of 0.5 seconds
                    }
                    val dialog = dialogBuilder.create()
                    dialog.show()
                }
                for (item in list) {
                    Log.e("itemOfList",item)
                    setUpRealtimeDatabaseforMCQ(item)
                    setUpRealtimeDatabase(item)
                }

                // Save the list to SharedPreferences
                val sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                set = list.toSet()
                editor.putStringSet("myList", set)
                editor.apply()
                Log.d("myList", set.toString())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(
                    this@MainActivity,
                    "Error fetching data: ${databaseError.message}", Toast.LENGTH_SHORT
                ).show()
            }
        })
    }



    private fun disableDrawerForTeacher() {
        mainDrawer.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                // No action needed here
            }
            override fun onDrawerOpened(drawerView: View) {
                mainDrawer.closeDrawer(drawerView)
            }

            override fun onDrawerClosed(drawerView: View) {
                // No action needed here
            }

            override fun onDrawerStateChanged(newState: Int) {
                // No action needed here
            }
        })

        // Disable opening the drawer by swiping from the edge
        mainDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }


    fun setUpViews(){
        setUpDrawerLayout()



        setUpDatePicker()
        AddQuiz()

        setUpSearchFunctionality("Search MCQ")
    }
    private fun setUpButtonClickListeners() {




        btnNotification.setOnClickListener{
            val intent=Intent(this,NotificationActivity::class.java)
            startActivity(intent)
        }

        quizRecyclerView = findViewById(R.id.quizRecyclerView)
        shortQuizRecyclerView = findViewById(R.id.shortQuizRecyclerView)
        btnMCQ = findViewById(R.id.btnMCQ)
        btnShortQuestion = findViewById(R.id.btnShortQuestion)

        shortQuizRecyclerView.visibility = View.GONE

        //val lightBlueColor = Color.parseColor("#90CAF9") // Light blue color code

        findViewById<View>(R.id.underlineMCQ).visibility = View.VISIBLE
        findViewById<View>(R.id.underlineShortQuestion).visibility = View.INVISIBLE

        // Set up click listeners for MCQ and Short Question buttons
        btnMCQ.setOnClickListener {
            // Show underline for MCQ button and hide underline for Short Question button
            findViewById<View>(R.id.underlineMCQ).visibility = View.VISIBLE
            findViewById<View>(R.id.underlineShortQuestion).visibility = View.INVISIBLE

            // Set up search functionality for MCQ
            setUpSearchFunctionality("Search MCQ")
            // Show MCQ RecyclerView and hide Short Quiz RecyclerView
            shortQuizRecyclerView.visibility = View.GONE
            quizRecyclerView.visibility = View.VISIBLE
        }

        btnShortQuestion.setOnClickListener {
            // Show underline for Short Question button and hide underline for MCQ button
            findViewById<View>(R.id.underlineMCQ).visibility = View.INVISIBLE
            findViewById<View>(R.id.underlineShortQuestion).visibility = View.VISIBLE

            // Set up search functionality for Short Questions
            setUpSearchFunctionalityForShortQuiz("Search Short Question")
            // Show Short Quiz RecyclerView and hide MCQ RecyclerView
            quizRecyclerView.visibility = View.GONE
            shortQuizRecyclerView.visibility = View.VISIBLE
        }
        profile.setOnClickListener {
            val intent=Intent(this,ProfileActivity::class.java)
            startActivity(intent)
        }
        RunningQuiz.setOnClickListener{
            val intent = Intent(this, RunningQuestion::class.java)
            startActivity(intent)
        }
        UpcomingQuiz.setOnClickListener{
            val intent = Intent(this, NotificationActivity::class.java)
            startActivity(intent)
        }
        addQuiz.setOnClickListener{
            val intent = Intent(this, DecideActivity::class.java)
            startActivity(intent)
        }
        btnRequests.setOnClickListener{
            val intent = Intent(this, ActivityAddStudent::class.java)
            startActivity(intent)
        }
        Results.setOnClickListener {
            val intent = Intent(this, ShowResultsActivity::class.java)
            startActivity(intent)
        }

    }


    private fun setUpRealtimeDatabaseforMCQ(item: String) {
        databaseRef = Firebase.database
        val ref = databaseRef.reference.child("MCQ").child(item)




        ref.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                        val currentDate = Date()
                for (snap in dataSnapshot.children) {
                    val itm = snap.getValue(Quiz::class.java)
                    A++
                    if (itm != null) {
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm a")
                        val selectedDateTime = dateFormat.parse(itm.selectedDateTime)
                        val comparisonResult = currentDate.compareTo(selectedDateTime)

                        if (comparisonResult >= 0) {
                            itm.let {
                                quizList.add(it)
                            }
                        }
                        else{
                            itm.let{
                                QuizManager.NotifyquizList.add(it)
                            }
                        }


                    }
                    updateNotification()
                }


                        Log.e("QuizListSize",quizList.size.toString())
                setAdapterMCQ(quizList)
                        Log.d("quizList",quizList.toString())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
                Toast.makeText(this@MainActivity,
                    "Error fetching data: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })


    }


    private fun setAdapterMCQ(quizlist: MutableList<Quiz>) {

        Log.d("Quilist",quizList.toString())
        adapter= QuizAdapter(this,quizlist)
        var QuizRecyclerView=findViewById<RecyclerView>(R.id.quizRecyclerView)
        QuizRecyclerView.layoutManager=GridLayoutManager(this,2)
        quizRecyclerView.adapter=adapter

    }
    private fun setUpRealtimeDatabase(item: String) {
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
                                    if (comparisonResult >= 0) {
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
                    this@MainActivity,
                    "Error fetching data: ${databaseError.message}", Toast.LENGTH_SHORT
                ).show()
            }
        })
    }





    private fun setAdapter(shortquizlist: MutableList<ShortQuiz>) {

        shortadapter= ShortQuizAdapter(this,shortquizlist)
        var shortQuizRecyclerView=findViewById<RecyclerView>(R.id.shortQuizRecyclerView)
        shortQuizRecyclerView.layoutManager=GridLayoutManager(this,2)
        shortQuizRecyclerView.adapter=shortadapter

    }
    private fun AddQuiz(){
        AddQuizButton=findViewById(R.id.AddQuizButton)
        AddQuizButton.setOnClickListener(){
            AlertDialog.Builder(this)
                .setMessage("Add Quizzes?")
                .setPositiveButton("Yes") { _, _ ->
                    val intent=Intent(this,DecideActivity::class.java)
                    startActivity(intent)
                }
                .setNegativeButton("No", null) // Do nothing if user cancels
                .show()
        }
    }
    private fun setUpDatePicker() {


        btnDatePicker.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker().build()
            datePicker.show(supportFragmentManager, "DatePicker")
            datePicker.addOnPositiveButtonClickListener {
                Log.d("DATEPICKER",datePicker.headerText)
                val dateFormatter=SimpleDateFormat("dd-mm-yyyy")
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
    private fun updateNotification() {
        Log.e("Notify",Notify.toString())
        if(QuizManager.NotifyquizList.size>0 || ShortQuestion.NotifyquizList.size>0){
            btnNotification.setImageResource(R.drawable.red_notification)
            btnNotification.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
        }else{
            btnNotification.setImageResource(R.drawable.ic_notification)
        }
    }


    fun setUpDrawerLayout(){
        val appBar: MaterialToolbar = findViewById(R.id.appBar)


        setSupportActionBar(appBar)
        actionBarDrawerToggle=ActionBarDrawerToggle(this,mainDrawer,
            R.string.app_name,
            R.string.app_name
        )
        actionBarDrawerToggle.syncState()



        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.btnProfile -> {
                    // Handle profile item click
                    startActivity(Intent(this, ProfileActivity::class.java))
                    mainDrawer.closeDrawers()
                    true
                }
//                R.id.addCourse -> {
//                    startActivity(Intent(this, AddCourse::class.java))
//                    mainDrawer.closeDrawers()
//                    true
//                }
                R.id.notificationInDrawer ->{
                    startActivity(Intent(this, NotificationActivity::class.java))
                    mainDrawer.closeDrawers()
                    true
                }
                R.id.btnRunning ->{
                    startActivity(Intent(this, RunningQuestion::class.java))
                    mainDrawer.closeDrawers()
                    true
                }
                R.id.addCourse ->{
                    startActivity(Intent(this, AddCourse::class.java))
                    mainDrawer.closeDrawers()
                    true
                }
                R.id.Results->{
                    startActivity(Intent(this, ShowResultsActivity::class.java))
                    mainDrawer.closeDrawers()
                    true
                }
                else -> false

            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
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
       //Log.d("Filter", "Filtered List: $filteredList")
        setAdapterMCQ(filteredList)
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

    override fun onBackPressed() {
        // Build an AlertDialog for confirmation
        AlertDialog.Builder(this)
            .setMessage("Are you sure you want to exit the app?")
            .setPositiveButton("Yes") { _, _ ->
                // User confirmed, exit the app
                finishAffinity() // Finish all activities and exit
            }
            .setNegativeButton("No"){ _, _ ->
                super.onBackPressed()
            }
    }
}