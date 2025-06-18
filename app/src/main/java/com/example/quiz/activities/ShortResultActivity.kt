package com.example.quiz.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quiz.R
import com.example.quiz.adapters.AnswerAdapter
import com.example.quiz.adapters.ResultAdapter
import com.example.quiz.models.Quiz
import com.example.quiz.models.ShortQuestion
import com.example.quiz.models.ShortQuiz
import com.google.gson.Gson

class ShortResultActivity : AppCompatActivity() {
    lateinit var quiz: ShortQuiz
    lateinit var resultScoreTextView: TextView // Moved initialization here

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.answer_activity)
        resultScoreTextView = findViewById(R.id.resultScoreTextView) // Initialize here
        setUpViews()
        GoHome()
        resultScoreTextView.text = "Your Result : "
    }

    private fun setUpViews() {
        val quizData = intent.getStringExtra("QUIZ")
        if(quizData != null)
        {
            quiz = Gson().fromJson<ShortQuiz>(quizData, ShortQuiz::class.java)

            setAnswerView()

        }else
        {
            Log.e("quizData",quizData.toString())
        }

    }

    private fun setAnswerView() {
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val adapter = ResultAdapter(quiz.questions.values.toList())
        recyclerView.adapter = adapter
    }
    override fun onPause() {
        super.onPause()
        finish()
    }


    private fun GoHome(){
        val btnHome: Button = findViewById(R.id.homeButton)
        btnHome.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
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
