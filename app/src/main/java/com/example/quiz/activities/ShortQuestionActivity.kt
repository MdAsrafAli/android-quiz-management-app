package com.example.quiz.activities

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
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
    private var countDownTimer: CountDownTimer? = null
    private lateinit var binding: ActivityShortQuestionBinding
    var quizzes: MutableList<ShortQuiz>? = null
    var questions: MutableMap<String, ShortQuestion>? = null
    private var d = 5L

    private var databaseRef: DatabaseReference? = null

    private var shortQuiz: ShortQuiz? = null
    private var questionKeys: MutableList<String> = mutableListOf()
    private var currentQuestionIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShortQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        timerTextView = findViewById(R.id.timerTextView)
        startTimer()

        val pos = intent.getIntExtra("POS", 0)
        val nodeKey = intent.getStringExtra("ID")
        val rootRef = FirebaseDatabase.getInstance().reference.child("Qizzs")

        rootRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (teacherSnapshot in snapshot.children) {
                    val teacherId = teacherSnapshot.key
                    for (quizSnapshot in teacherSnapshot.children) {
                        val quizId = quizSnapshot.key
                        if (quizId == nodeKey) {
                            val questionSnapshot = quizSnapshot.child("questions")
                            val questionList = mutableListOf<ShortQuestion>()
                            for (qSnap in questionSnapshot.children) {
                                val questionData = qSnap.getValue(ShortQuestion::class.java)
                                questionData?.let { questionList.add(it) }
                            }

                            Log.d("ShortQuestionActivity", "Questions fetched: $questionList")

                            if (questionList.isNotEmpty()) {
                                shortQuiz = ShortQuiz()
                                shortQuiz?.questions = questionList.associateBy { it.text } as MutableMap<String, ShortQuestion>
                                questionKeys.addAll(shortQuiz?.questions?.keys ?: emptyList())
                                displayQuestion()
                            }

                            return  // Exit after finding the correct quiz
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("ShortQuestionActivity", "Error fetching questions: ${databaseError.message}")
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
            } else {
                binding.nextButton.visibility = Button.GONE
                binding.previousButton.visibility = Button.GONE
                binding.submitButton.visibility = Button.VISIBLE
            }
        }

        binding.submitButton.setOnClickListener {
            finishQuiz()
        }

        binding.answerEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val currentQuestionKey = questionKeys[currentQuestionIndex]
                val currentQuestion = shortQuiz?.questions?.get(currentQuestionKey)
                currentQuestion?.userAnswer = s.toString()
            }
        })


    }

    private fun displayQuestion() {
        if (currentQuestionIndex < 0 || currentQuestionIndex >= questionKeys.size) {
            return
        }

        val currentQuestionNumber = currentQuestionIndex + 1
        val currentQuestionKey = questionKeys[currentQuestionIndex]
        val currentQuestion = shortQuiz?.questions?.get(currentQuestionKey)

        val questionTextWithNumber = "$currentQuestionNumber. ${currentQuestion?.text}"
        binding.questionTextView.text = questionTextWithNumber
        binding.answerEditText.setText(currentQuestion?.userAnswer ?: "")

        binding.submitButton.visibility = if (currentQuestionIndex == questionKeys.size - 1) {
            Button.VISIBLE
        } else {
            Button.GONE
        }

        binding.previousButton.visibility = if (currentQuestionIndex > 0) {
            Button.VISIBLE
        } else {
            Button.GONE
        }

        binding.nextButton.visibility = if (currentQuestionIndex < questionKeys.size - 1) {
            Button.VISIBLE
        } else {
            Button.GONE
        }
    }


    private fun startTimer() {
        d=intent.getLongExtra("Duration", 6L)
        Log.e("duration321", d.toString())
        val quizTimeMillis = d * 60 * 1000 // Convert minutes to milliseconds
        val countDownInterval = 1000L // 1 second

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
        val intent = Intent(this, ShortResultActivity::class.java)
        val json = Gson().toJson(shortQuiz)
        intent.putExtra("QUIZ", json)
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
