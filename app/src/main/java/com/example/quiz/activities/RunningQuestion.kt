package com.example.quiz.activities

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quiz.R
import com.example.quiz.adapters.QuizAdapter
import com.example.quiz.adapters.ShortQuizAdapter
import com.example.quiz.models.Quiz
import com.example.quiz.models.ShortQuiz
import com.google.android.material.button.MaterialButton
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class RunningQuestion : AppCompatActivity() {

    private lateinit var quizRecyclerView: RecyclerView
    private lateinit var shortQuizRecyclerView: RecyclerView
    private lateinit var tvEmpty: TextView

    private val runningMcqMap = mutableMapOf<String, MutableList<Quiz>>()
    private val runningShortMap = mutableMapOf<String, MutableList<ShortQuiz>>()
    private var showingMcq = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.running_question)

        quizRecyclerView = findViewById(R.id.quizRecyclerView)
        shortQuizRecyclerView = findViewById(R.id.shortQuizRecyclerView)
        tvEmpty = findViewById(R.id.tvEmpty)

        setUpTabs()
        loadRunningQuizzes()
    }

    private fun loadRunningQuizzes() {
        val teacherSet = getSharedPreferences("UserData", Context.MODE_PRIVATE)
            .getStringSet("myList", null) ?: return
        for (teacherPhone in teacherSet) {
            loadMcqForTeacher(teacherPhone)
            loadShortForTeacher(teacherPhone)
        }
    }

    private fun loadMcqForTeacher(teacherPhone: String) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.ENGLISH)
        Firebase.database.reference.child("MCQ").child(teacherPhone)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val currentDate = Date()
                    val running = mutableListOf<Quiz>()
                    for (snap in snapshot.children) {
                        val itm = snap.getValue(Quiz::class.java) ?: continue
                        itm.teacherPhone = teacherPhone
                        val dateStr = itm.selectedDateTime ?: continue
                        try {
                            val startTime = dateFormat.parse(dateStr) ?: continue
                            val finishTime = addMinutes(startTime, itm.duration.toInt())
                            if (currentDate >= startTime && currentDate <= finishTime) {
                                running.add(itm)
                            }
                        } catch (e: ParseException) {
                            Log.e("RunningQuestion", "MCQ parse error: ${e.message}")
                        }
                    }
                    runningMcqMap[teacherPhone] = running
                    if (showingMcq) rebuildMcqList()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@RunningQuestion, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun loadShortForTeacher(teacherPhone: String) {
        val dateFormat = SimpleDateFormat("yyyy-M-d hh:mm a", Locale.ENGLISH)
        Firebase.database.reference.child("Qizzs").child(teacherPhone)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val currentDate = Date()
                    val running = mutableListOf<ShortQuiz>()
                    for (snap in snapshot.children) {
                        val itm = snap.getValue(ShortQuiz::class.java) ?: continue
                        itm.teacherPhone = teacherPhone
                        val dateStr = itm.selectedDateTime ?: continue
                        try {
                            val startTime = dateFormat.parse(dateStr) ?: continue
                            val finishTime = addMinutes(startTime, itm.duration.toInt())
                            if (currentDate >= startTime && currentDate <= finishTime) {
                                running.add(itm)
                            }
                        } catch (e: ParseException) {
                            Log.e("RunningQuestion", "Short parse error: ${e.message}")
                        }
                    }
                    runningShortMap[teacherPhone] = running
                    if (!showingMcq) rebuildShortList()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@RunningQuestion, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun addMinutes(date: Date, minutes: Int): Date {
        val cal = Calendar.getInstance()
        cal.time = date
        cal.add(Calendar.MINUTE, minutes)
        return cal.time
    }

    private fun rebuildMcqList() {
        val all = runningMcqMap.values.flatten().toMutableList()
        quizRecyclerView.layoutManager = LinearLayoutManager(this)
        quizRecyclerView.adapter = QuizAdapter(this, all)
        tvEmpty.visibility = if (all.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun rebuildShortList() {
        val all = runningShortMap.values.flatten().toMutableList()
        shortQuizRecyclerView.layoutManager = LinearLayoutManager(this)
        shortQuizRecyclerView.adapter = ShortQuizAdapter(this, all)
        tvEmpty.visibility = if (all.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun setUpTabs() {
        val btnMCQ = findViewById<MaterialButton>(R.id.btnMCQ)
        val btnShort = findViewById<MaterialButton>(R.id.btnShortQuestion)
        val underlineMcq = findViewById<View>(R.id.underlineMCQ)
        val underlineShort = findViewById<View>(R.id.underlineShortQuestion)

        btnMCQ.setOnClickListener {
            showingMcq = true
            underlineMcq.visibility = View.VISIBLE
            underlineShort.visibility = View.INVISIBLE
            quizRecyclerView.visibility = View.VISIBLE
            shortQuizRecyclerView.visibility = View.GONE
            rebuildMcqList()
        }
        btnShort.setOnClickListener {
            showingMcq = false
            underlineMcq.visibility = View.INVISIBLE
            underlineShort.visibility = View.VISIBLE
            quizRecyclerView.visibility = View.GONE
            shortQuizRecyclerView.visibility = View.VISIBLE
            rebuildShortList()
        }
    }
}
