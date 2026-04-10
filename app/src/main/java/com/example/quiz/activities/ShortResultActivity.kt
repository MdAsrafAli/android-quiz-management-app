package com.example.quiz.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.quiz.R
import com.example.quiz.adapters.ResultAdapter
import com.example.quiz.models.ShortQuiz
import com.google.gson.Gson

class ShortResultActivity : AppCompatActivity() {
    private lateinit var quiz: ShortQuiz

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
        if (quizData == null) {
            Log.e("ShortResultActivity", "No quiz data received")
            finish()
            return
        }
        quiz = Gson().fromJson(quizData, ShortQuiz::class.java)
        showScore()
        setAnswerView()
    }

    private fun showScore() {
        var correct = 0
        val total = quiz.questions.size
        for (q in quiz.questions.values) {
            if (q.userAnswer?.trim()?.lowercase() == q.correctanswer?.trim()?.lowercase()) {
                correct++
            }
        }
        val pct = if (total > 0) (correct * 100) / total else 0
        findViewById<TextView>(R.id.resultScoreTextView).text = "$correct / $total correct"
        findViewById<TextView>(R.id.tvPoints).text = "$pct% accuracy"
    }

    private fun setAnswerView() {
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.adapter = ResultAdapter(quiz.questions.values.toList())
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
