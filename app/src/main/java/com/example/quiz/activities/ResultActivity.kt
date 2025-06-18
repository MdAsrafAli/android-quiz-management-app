package com.example.quiz.activities

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.Button
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
    lateinit var quiz: Quiz
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.answer_activity)
        setUpViews()
        GoHome()

    }
    private fun setUpViews() {
        val quizData = intent.getStringExtra("QUIZ")
        quiz = Gson().fromJson<Quiz>(quizData, Quiz::class.java)
        calculateScore()
        setAnswerView()
    }
    private fun setAnswerView() {
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val adapter = AnswerAdapter(quiz.questions.values.toList())
        recyclerView.adapter = adapter
    }


    private fun calculateScore() {
        var score = 0
        for (entry in quiz.questions.entries) {
            val question = entry.value
            if (question.answer == question.userAnswer) {
                score += 10
            }
        }
        val txtScore: TextView=findViewById(R.id.resultScoreTextView)
        txtScore.text = "Your Score : $score"
        val sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val userID = sharedPreferences.getString("Phone", "").toString()
        val name = sharedPreferences.getString("Name", "").toString()
        val title = intent.getStringExtra("TitleOfQuiz")
        saveResultToFirebase(title.toString(), userID, name, score)
    }
    private fun saveResultToFirebase(title: String, userID: String, name: String, score :Int) {
        val database = FirebaseDatabase.getInstance()
        val resultsRef = database.getReference("results").child(title)

        // Check if userID already exists under the title node
        resultsRef.child(userID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // userID already exists, do not store the result
                    Toast.makeText(this@ResultActivity, "Result already exists for this user", Toast.LENGTH_SHORT).show()
                } else {
                    // userID does not exist, store the result
                    val resultData = mapOf(
                        "userID" to userID,
                        "name" to name,
                        "score" to score
                        // Add other result data here if needed
                    )
                    resultsRef.child(userID).setValue(resultData)
                        .addOnSuccessListener {
                            Toast.makeText(this@ResultActivity, "Result stored successfully", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this@ResultActivity, "Failed to store result: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ResultActivity, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun GoHome(){
        var btnHome: Button =findViewById(R.id.homeButton)
        btnHome.setOnClickListener{
            intent= Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        // Ensure that the activity is finished when it is destroyed
        finish()
    }
    override fun onBackPressed() {
        // Build an AlertDialog for confirmation
        AlertDialog.Builder(this)
            .setMessage("Go home screen?")
            .setPositiveButton("Yes") { _, _ ->
                // User confirmed, navigate to MainActivity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // Finish the current activity
            }
            .setNegativeButton("No", null) // Do nothing if user cancels
            .show()
    }

}