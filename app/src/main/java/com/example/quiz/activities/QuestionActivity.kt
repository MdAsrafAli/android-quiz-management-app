package com.example.quiz.activities

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quiz.R
import com.example.quiz.adapters.OptionAdapters
import com.example.quiz.databinding.ActivityQuestionBinding
import com.example.quiz.models.Question
import com.example.quiz.models.Quiz
import com.google.firebase.database.*
import com.google.gson.Gson
import java.util.concurrent.TimeUnit

class QuestionActivity : AppCompatActivity() {
    private lateinit var timerTextView: TextView
    private var countDownTimer: CountDownTimer? = null
    private lateinit var binding: ActivityQuestionBinding
    private var d = 5L

    private var databaseRef: DatabaseReference? = null

    private var quiz: Quiz? = null
    private var questionKeys: MutableList<String> = mutableListOf()
    private var currentQuestionIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        timerTextView = findViewById(R.id.timer)
        startTimer()

        val nodeKey = intent.getStringExtra("ID")
        val rootRef = FirebaseDatabase.getInstance().reference.child("MCQ")

        rootRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (teacherSnapshot in snapshot.children) {
                    val teacherId = teacherSnapshot.key
                    for (quizSnapshot in teacherSnapshot.children) {
                        val quizId = quizSnapshot.key
                        if (quizId == nodeKey) {
                            val questionSnapshot = quizSnapshot.child("questions")
                            val questionList = mutableListOf<Question>()
                            for (qSnap in questionSnapshot.children) {
                                val questionData = qSnap.getValue(Question::class.java)
                                questionData?.let { questionList.add(it) }
                            }

                            if (questionList.isNotEmpty()) {
                                quiz = Quiz()
                                quiz?.questions = questionList.associateBy { it.description } as MutableMap<String, Question>
                                questionKeys.addAll(quiz?.questions?.keys ?: emptyList())
                                displayQuestion()
                                Log.e("QuizFound", "Found in teacher: $teacherId")
                            }
                            return  // Exit after finding the quiz
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("QuestionActivity", "Error fetching data: ${error.message}")
            }
        })


        binding.btnPrevious.setOnClickListener {
            if (currentQuestionIndex > 0) {
                currentQuestionIndex--
                displayQuestion()
            }
        }

        binding.btnNext.setOnClickListener {
            if (currentQuestionIndex < questionKeys.size - 1) {
                currentQuestionIndex++
                displayQuestion()
            } else {
                binding.btnNext.visibility = Button.GONE
                binding.btnPrevious.visibility = Button.GONE
                binding.btnSubmit.visibility = Button.VISIBLE
            }
        }

        binding.btnSubmit.setOnClickListener {
            finishQuiz()
        }

        // Fetch duration and start the timer


    }

    private fun displayQuestion() {
        if (currentQuestionIndex < 0 || currentQuestionIndex >= questionKeys.size) {
            return
        }

        val currentQuestionNumber = currentQuestionIndex + 1
        val currentQuestionKey = questionKeys[currentQuestionIndex]
        val currentQuestion = quiz?.questions?.get(currentQuestionKey)

        val questionTextWithNumber = "$currentQuestionNumber. ${currentQuestion?.description}"

        binding.description.text = questionTextWithNumber

        binding.btnSubmit.visibility = if (currentQuestionIndex == questionKeys.size - 1) {
            Button.VISIBLE
        } else {
            Button.GONE
        }

        binding.btnPrevious.visibility = if (currentQuestionIndex > 0) {
            Button.VISIBLE
        } else {
            Button.GONE
        }

        binding.btnNext.visibility = if (currentQuestionIndex < questionKeys.size - 1) {
            Button.VISIBLE
        } else {
            Button.GONE
        }

        currentQuestion?.let {
            val description: TextView = findViewById(R.id.description)
            val optionList: RecyclerView = findViewById(R.id.optionList)
            description.text = it.description
            val optionAdapter = OptionAdapters(this, it)
            optionList.layoutManager = LinearLayoutManager(this)
            optionList.adapter = optionAdapter
            optionList.setHasFixedSize(true)
        }
    }


    private fun startTimer() {
        d=intent.getLongExtra("Duration", 2L)
        val quizTimeMillis = d * 60 * 1000 // Convert minutes to milliseconds
        val countDownInterval = 1000L

        countDownTimer = object : CountDownTimer(quizTimeMillis, countDownInterval) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60
                val timeRemainingFormatted = String.format("%02d:%02d", minutes, seconds)
                timerTextView.text = timeRemainingFormatted
            }

            override fun onFinish() {
                finishQuiz()
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }

    private fun finishQuiz() {
        val title= intent.getStringExtra("TitleOfQuiz")
        val intent = Intent(this, ResultActivity::class.java)

        val json = Gson().toJson(quiz)
        intent.putExtra("QUIZ", json)
        intent.putExtra("TitleOfQuiz", title)

        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setMessage("Are you sure you want to exit?")
            .setPositiveButton("Yes") { _, _ ->
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            .setNegativeButton("No", null)
            .show()
    }
}
