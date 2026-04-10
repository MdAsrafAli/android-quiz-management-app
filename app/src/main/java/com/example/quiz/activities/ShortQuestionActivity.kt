package com.example.quiz.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.quiz.R
import com.example.quiz.databinding.ActivityShortQuestionBinding
import com.example.quiz.models.ShortQuestion
import com.example.quiz.models.ShortQuiz
import com.google.firebase.database.*
import com.google.gson.Gson
import java.util.concurrent.TimeUnit

class ShortQuestionActivity : AppCompatActivity() {
    private lateinit var timerTextView: TextView
    private lateinit var tvQuestionCounter: TextView
    private lateinit var progressBar: ProgressBar
    private var countDownTimer: CountDownTimer? = null
    private lateinit var binding: ActivityShortQuestionBinding
    private var d = 5L

    private var shortQuiz: ShortQuiz? = null
    private var questionKeys: MutableList<String> = mutableListOf()
    private var currentQuestionIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShortQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        timerTextView = binding.timerTextView
        tvQuestionCounter = binding.tvQuestionCounter
        progressBar = binding.progressBar
        startTimer()

        val nodeKey = intent.getStringExtra("ID")
        val teacherPhone = intent.getStringExtra("TeacherPhone") ?: ""
        val quizRef = FirebaseDatabase.getInstance().reference
            .child("Qizzs").child(teacherPhone).child(nodeKey ?: "")

        quizRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val questionSnapshot = snapshot.child("questions")
                val questionList = mutableListOf<ShortQuestion>()
                for (qSnap in questionSnapshot.children) {
                    val questionData = qSnap.getValue(ShortQuestion::class.java)
                    questionData?.let { questionList.add(it) }
                }

                Log.d("ShortQuestionActivity", "Questions fetched: ${questionList.size}")

                if (questionList.isNotEmpty()) {
                    shortQuiz = ShortQuiz()
                    shortQuiz?.questions = questionList.associateBy { it.text } as MutableMap<String, ShortQuestion>
                    questionKeys.addAll(shortQuiz?.questions?.keys ?: emptyList())
                    displayQuestion()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("ShortQuestionActivity", "Error: ${databaseError.message}")
            }
        })

        binding.previousButton.setOnClickListener {
            if (currentQuestionIndex > 0) {
                currentQuestionIndex--
                displayQuestion()
            }
        }

        binding.nextButton.setOnClickListener {
            if (currentQuestionIndex < questionKeys.size - 1) {
                currentQuestionIndex++
                displayQuestion()
            }
        }

        binding.submitButton.setOnClickListener { finishQuiz() }

        binding.answerEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val currentQuestionKey = questionKeys.getOrNull(currentQuestionIndex) ?: return
                shortQuiz?.questions?.get(currentQuestionKey)?.userAnswer = s.toString()
            }
        })
    }

    private fun displayQuestion() {
        if (currentQuestionIndex < 0 || currentQuestionIndex >= questionKeys.size) return

        val total = questionKeys.size
        val current = currentQuestionIndex + 1
        tvQuestionCounter.text = "Question $current of $total"
        progressBar.progress = (current * 100) / total

        val currentQuestionKey = questionKeys[currentQuestionIndex]
        val currentQuestion = shortQuiz?.questions?.get(currentQuestionKey)

        binding.questionTextView.text = "$current. ${currentQuestion?.text}"
        binding.answerEditText.setText(currentQuestion?.userAnswer ?: "")

        binding.submitButton.visibility = if (currentQuestionIndex == questionKeys.size - 1) Button.VISIBLE else Button.GONE
        binding.previousButton.visibility = if (currentQuestionIndex > 0) Button.VISIBLE else Button.INVISIBLE
        binding.nextButton.visibility = if (currentQuestionIndex < questionKeys.size - 1) Button.VISIBLE else Button.GONE
    }

    private fun startTimer() {
        d = intent.getLongExtra("Duration", 6L)
        val quizTimeMillis = d * 60 * 1000
        countDownTimer = object : CountDownTimer(quizTimeMillis, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60
                timerTextView.text = String.format("%02d:%02d", minutes, seconds)
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
        val intent = Intent(this, ShortResultActivity::class.java)
        intent.putExtra("QUIZ", Gson().toJson(shortQuiz))
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
