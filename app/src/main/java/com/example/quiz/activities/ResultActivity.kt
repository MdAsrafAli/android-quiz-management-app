package com.example.quiz.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.quiz.R
import com.example.quiz.adapters.AnswerAdapter
import com.example.quiz.models.Quiz
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson

class ResultActivity : AppCompatActivity() {
    private lateinit var quiz: Quiz

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.answer_activity)
        setUpViews()
        findViewById<com.google.android.material.button.MaterialButton>(R.id.homeButton)
            .setOnClickListener {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
    }

    private fun setUpViews() {
        val quizData = intent.getStringExtra("QUIZ")
        quiz = Gson().fromJson(quizData, Quiz::class.java)
        showScore()
        setAnswerView()
    }

    private fun showScore() {
        var correct = 0
        val total = quiz.questions.size
        for (q in quiz.questions.values) {
            if (q.answer == q.userAnswer) correct++
        }
        val points = correct * 10

        findViewById<TextView>(R.id.resultScoreTextView).text = "$correct / $total correct"
        findViewById<TextView>(R.id.tvPoints).text = "$points points"

        val sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val userID = sharedPreferences.getString("Phone", "").toString()
        val name = sharedPreferences.getString("Name", "").toString()
        val title = intent.getStringExtra("TitleOfQuiz")
        saveResultToFirebase(title.toString(), userID, name, points)
    }

    private fun setAnswerView() {
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.adapter = AnswerAdapter(quiz.questions.values.toList())
    }

    private fun saveResultToFirebase(title: String, userID: String, name: String, score: Int) {
        val resultsRef = FirebaseDatabase.getInstance().getReference("results").child(title)
        resultsRef.child(userID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    val resultData = mapOf("userID" to userID, "name" to name, "score" to score)
                    resultsRef.child(userID).setValue(resultData)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ResultActivity, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setMessage("Go to home screen?")
            .setPositiveButton("Yes") { _, _ ->
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .setNegativeButton("No", null)
            .show()
    }
}
