package com.example.quiz.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
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
    private lateinit var tvQuestionCounter: TextView
    private lateinit var progressBar: ProgressBar
    private var countDownTimer: CountDownTimer? = null
    private lateinit var binding: ActivityQuestionBinding
    private var d = 5L

    private var quiz: Quiz? = null
    private var questionKeys: MutableList<String> = mutableListOf()
    private var currentQuestionIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        timerTextView = findViewById(R.id.timer)
        tvQuestionCounter = findViewById(R.id.tvQuestionCounter)
        progressBar = findViewById(R.id.progressBar)
        startTimer()

        val nodeKey = intent.getStringExtra("ID")
        val teacherPhone = intent.getStringExtra("TeacherPhone") ?: ""
        val quizRef = FirebaseDatabase.getInstance().reference
            .child("MCQ").child(teacherPhone).child(nodeKey ?: "")

        quizRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val questionSnapshot = snapshot.child("questions")
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
                    Log.e("QuizFound", "Loaded from teacher: $teacherPhone")
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
            }
        }

        binding.btnSubmit.setOnClickListener {
            finishQuiz()
        }
    }

    private fun displayQuestion() {
        if (currentQuestionIndex < 0 || currentQuestionIndex >= questionKeys.size) return

        val total = questionKeys.size
        val current = currentQuestionIndex + 1
        tvQuestionCounter.text = "Question $current of $total"
        progressBar.progress = (current * 100) / total

        val currentQuestionKey = questionKeys[currentQuestionIndex]
        val currentQuestion = quiz?.questions?.get(currentQuestionKey)

        binding.description.text = "${current}. ${currentQuestion?.description}"

        binding.btnSubmit.visibility = if (currentQuestionIndex == questionKeys.size - 1) Button.VISIBLE else Button.GONE
        binding.btnPrevious.visibility = if (currentQuestionIndex > 0) Button.VISIBLE else Button.INVISIBLE
        binding.btnNext.visibility = if (currentQuestionIndex < questionKeys.size - 1) Button.VISIBLE else Button.GONE

        currentQuestion?.let {
            val optionList: RecyclerView = findViewById(R.id.optionList)
            val optionAdapter = OptionAdapters(this, it)
            optionList.layoutManager = LinearLayoutManager(this)
            optionList.adapter = optionAdapter
            optionList.setHasFixedSize(true)
        }
    }

    private fun startTimer() {
        d = intent.getLongExtra("Duration", 2L)
        val quizTimeMillis = d * 60 * 1000
        countDownTimer = object : CountDownTimer(quizTimeMillis, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60
                timerTextView.text = String.format("%02d:%02d", minutes, seconds)
                // Warn visually when time is low
                when {
                    millisUntilFinished < 30_000 -> timerTextView.setTextColor(Color.parseColor("#D32F2F"))
                    millisUntilFinished < 60_000 -> timerTextView.setTextColor(Color.parseColor("#F57C00"))
                    else -> timerTextView.setTextColor(Color.parseColor("#0D47A1"))
                }
            }
            override fun onFinish() { finishQuiz() }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }

    private fun finishQuiz() {
        val title = intent.getStringExtra("TitleOfQuiz")
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra("QUIZ", Gson().toJson(quiz))
        intent.putExtra("TitleOfQuiz", title)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setMessage("Are you sure you want to exit?")
            .setPositiveButton("Yes") { _, _ ->
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .setNegativeButton("No", null)
            .show()
    }
}
