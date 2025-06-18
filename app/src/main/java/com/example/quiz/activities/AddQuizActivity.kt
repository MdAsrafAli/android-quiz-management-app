package com.example.quiz.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quiz.R
import com.example.quiz.models.ShortQuestion
import com.example.quiz.models.ShortQuiz
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddQuizActivity : AppCompatActivity() {

    private lateinit var questionEditText: EditText
    private lateinit var answerEditText: EditText
    private lateinit var addQuestionButton: Button
    private lateinit var saveQuizButton: Button

    private lateinit var databaseRef: DatabaseReference

    private val questionsMap = mutableMapOf<String, ShortQuestion>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.addquestion)

        questionEditText = findViewById(R.id.questionEditText)
        answerEditText = findViewById(R.id.answerEditText)
        addQuestionButton = findViewById(R.id.addQuestionButton)
        saveQuizButton = findViewById(R.id.saveQuizButton)

        databaseRef = FirebaseDatabase.getInstance().reference

        addQuestionButton.setOnClickListener {
            val questionText = questionEditText.text.toString().trim()
            val answerText = answerEditText.text.toString().trim()

            if (questionText.isNotEmpty() && answerText.isNotEmpty()) {
                val questionId = databaseRef.push().key ?: ""
                val question = ShortQuestion(questionText, answerText)
                questionsMap[questionId] = question
                Toast.makeText(this, "Question added", Toast.LENGTH_SHORT).show()
                questionEditText.text.clear()
                answerEditText.text.clear()
            } else {
                Toast.makeText(this, "Please enter both question and answer", Toast.LENGTH_SHORT).show()
            }
        }

        saveQuizButton.setOnClickListener {
            val questionText = questionEditText.text.toString().trim()
            val answerText = answerEditText.text.toString().trim()

            if (questionText.isNotEmpty() && answerText.isNotEmpty()) {
                val questionId = databaseRef.push().key ?: ""
                val question = ShortQuestion(questionText, answerText)
                questionsMap[questionId] = question
                Toast.makeText(this, "Question added", Toast.LENGTH_SHORT).show()
                questionEditText.text.clear()
                answerEditText.text.clear()
            }
            val sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
            val userID = sharedPreferences.getString("Phone", "").toString()
            val title = intent.getStringExtra("title")
            val date = intent.getStringExtra("selectedDateTime")
            val duration = intent.getStringExtra("duration")?.toLong()!!
            if (title != null) {
                if (title.isNotEmpty() && questionsMap.isNotEmpty()) {
                    val quizId = databaseRef.push().key ?: ""
                    val quiz = ShortQuiz(quizId, title,date,duration, questionsMap)
                    databaseRef.child("Qizzs").child(userID).child(quizId).setValue(quiz)
                    Toast.makeText(this, "Quiz added", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@AddQuizActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Please enter quiz title and add at least one question", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(this, "Title is empty", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
