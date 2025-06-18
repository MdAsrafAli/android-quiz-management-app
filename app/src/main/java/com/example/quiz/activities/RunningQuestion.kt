package com.example.quiz.activities

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quiz.R
import com.example.quiz.adapters.QuizAdapter
import com.example.quiz.adapters.ShortQuizAdapter
import com.example.quiz.models.Quiz
import com.example.quiz.models.ShortQuiz
import com.google.android.material.button.MaterialButton
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class RunningQuestion: AppCompatActivity() {
    private lateinit var databaseRef: FirebaseDatabase
    private var quizList=mutableListOf<Quiz>()
    private var shortquizlist= mutableListOf<ShortQuiz>()
    lateinit var shortadapter:ShortQuizAdapter
    lateinit var adapter: QuizAdapter
    lateinit var quizRecyclerView: RecyclerView
    lateinit var shortQuizRecyclerView: RecyclerView
    private lateinit var refreshButton : Button
    private lateinit var set: Set<String>

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.running_question)
        FirebaseApp.initializeApp(this)
        setUpButtonClickListeners()
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

    }
    private fun setUpRealtimeDatabaseforMCQ(item:String) {
        databaseRef = Firebase.database
        val ref = databaseRef.reference.child("MCQ").child(item)

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val currentDate = Date()
                quizList.clear()
                for (snap in dataSnapshot.children) {
                    val itm = snap.getValue(Quiz::class.java)
                    if (itm != null) {
                        val selectedDateTimeStr = itm.selectedDateTime
                        if (!selectedDateTimeStr.isNullOrEmpty()) {
                            try {
                                val duration = itm.duration.toInt()
                                val dateFormat = SimpleDateFormat("yyyy-M-dd hh:mm a", Locale.ENGLISH)
                                val selectedDateTime = dateFormat.parse(selectedDateTimeStr)
                                val comparisonResult = currentDate.compareTo(selectedDateTime)
                                val finishTime = calculateFinishTime(selectedDateTime, duration)
                                val comparisonResult2 = currentDate.compareTo(finishTime)
                                Log.d("RunningQuestion", "Comparison result 2: $comparisonResult2")
                                if (comparisonResult >= 0 && comparisonResult2 <= 0) {
                                    quizList.add(itm)
                                }
                            } catch (e: ParseException) {
                                Log.e("RunningQuestion", "Date parsing error: ${e.message}")
                            } catch (e: Exception) {
                                Log.e("RunningQuestion", "Unexpected error: ${e.message}")
                            }
                        } else {
                            Log.e("RunningQuestion", "Empty or null date string in Quiz item")
                        }
                    }
                }

                setAdapterMCQ(quizList)

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
                Toast.makeText(
                    this@RunningQuestion,
                    "Error fetching data: ${databaseError.message}", Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    fun calculateFinishTime(selectedDateTime: Date, minutesToAdd: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = selectedDateTime
        calendar.add(Calendar.MINUTE, minutesToAdd)
        return calendar.time
    }




    private fun setAdapterMCQ(quizlist: MutableList<Quiz>) {

        Log.d("Quilist",quizList.toString())
        adapter= QuizAdapter(this,quizlist)
        var QuizRecyclerView=findViewById<RecyclerView>(R.id.quizRecyclerView)
        QuizRecyclerView.layoutManager= GridLayoutManager(this,2)
        quizRecyclerView.adapter=adapter

    }
    private fun setUpRealtimeDatabase(item:String) {
        databaseRef = Firebase.database
        val ref = databaseRef.reference.child("Qizzs").child(item)

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val currentDate = Date()
                for (snap in dataSnapshot.children) {
                    val itm = snap.getValue(ShortQuiz::class.java)
                    if (itm != null) {
                        val selectedDateTimeStr = itm.selectedDateTime
                        if (!selectedDateTimeStr.isNullOrEmpty()) {
                            try {
                                val duration = itm.duration.toInt()

                                Log.e("duration21",duration.toString())
                                val dateFormat = SimpleDateFormat("yyyy-M-dd hh:mm a", Locale.ENGLISH)
                                val selectedDateTime = dateFormat.parse(selectedDateTimeStr)
                                val comparisonResult = currentDate.compareTo(selectedDateTime)
                                val finishTime = calculateFinishTime(selectedDateTime, duration)
                                val comparisonResult2 = currentDate.compareTo(finishTime)
                                Log.d("RunningQuestion", "Comparison result 2: $comparisonResult2")
                                if (comparisonResult >= 0 && comparisonResult2 <= 0) {
                                    shortquizlist.add(itm)
                                    Log.e("shortquizlist",itm.toString())
                                }
                            } catch (e: ParseException) {
                                Log.e("RunningQuestion", "Date parsing error: ${e.message}")
                            } catch (e: Exception) {
                                Log.e("RunningQuestion", "Unexpected error: ${e.message}")
                            }
                        } else {
                            Log.e("RunningQuestion", "Empty or null date string in Quiz item")
                        }
                    }
                }

                setAdapter(shortquizlist)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
                Toast.makeText(this@RunningQuestion,
                    "Error fetching data: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })

    }


    private fun setAdapter(shortquizlist: MutableList<ShortQuiz>) {

        shortadapter= ShortQuizAdapter(this,shortquizlist)
        shortQuizRecyclerView=findViewById<RecyclerView>(R.id.shortQuizRecyclerView)
        shortQuizRecyclerView.layoutManager= GridLayoutManager(this,2)
        shortQuizRecyclerView.adapter=shortadapter

    }
    private fun setUpButtonClickListeners() {
        refreshButton = findViewById(R.id.refreshButton)
        refreshButton.setOnClickListener{
            if (set != null) {
                val list: MutableList<String> = set.toMutableList()
                for(item in list){
                    setUpRealtimeDatabaseforMCQ(item)
                    setUpRealtimeDatabase(item)
                }
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
            // Show MCQ RecyclerView and hide Short Quiz RecyclerView

        }

        btnShortQuestion.setOnClickListener {
            // Show underline for Short Question button and hide underline for MCQ button
            findViewById<View>(R.id.underlineMCQ).visibility = View.INVISIBLE
            findViewById<View>(R.id.underlineShortQuestion).visibility = View.VISIBLE
            quizRecyclerView.visibility = View.GONE
            shortQuizRecyclerView.visibility = View.VISIBLE
            // Set up search functionality for Short Questions
            // Show Short Quiz RecyclerView and hide MCQ RecyclerView

        }

    }
}