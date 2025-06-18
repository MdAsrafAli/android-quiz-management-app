package com.example.quiz.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quiz.R
import com.example.quiz.models.Question
import com.example.quiz.models.Quiz
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddMCQ : AppCompatActivity() {

    private lateinit var questionEditText: EditText
    private lateinit var option1: EditText
    private lateinit var option2: EditText
    private lateinit var option3: EditText
    private lateinit var option4: EditText
    private lateinit var addQuestionButton: Button
    private lateinit var saveQuizButton: Button
    private lateinit var answer:Array<String>
    private lateinit var databaseRef: DatabaseReference
    private lateinit var spinnerAnswer: Spinner
    private lateinit var selectedAnser : String
    private lateinit var answerText : String
    private val questionsMap = mutableMapOf<String, Question>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_mcq)

        questionEditText = findViewById(R.id.questionEditText)
        spinnerAnswer = findViewById(R.id.spinnerAnswer)
        addQuestionButton = findViewById(R.id.addQuestionButton)
        saveQuizButton = findViewById(R.id.saveQuizButton)
        option1 = findViewById(R.id.option1EditText)
        option2 = findViewById(R.id.option2EditText)
        option3 = findViewById(R.id.option3EditText)
        option4 = findViewById(R.id.option4EditText)
        answer = arrayOf("A", "B" ,"C" ,"D")
        val answerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, answer)
        spinnerAnswer.adapter = answerAdapter
        databaseRef = FirebaseDatabase.getInstance().reference

        spinnerAnswer.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedAnser = answer[position]
                Log.e("selectedAnswer", selectedAnser)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
        addQuestionButton.setOnClickListener {

            val questionText = questionEditText.text.toString().trim()
            var answerspinner = selectedAnser
            val option1Text ="${"A."}${option1.text.toString().trim()}"
            val option2Text = "${"B."}${option2.text.toString().trim()}"
            val option3Text = "${"C."}${option3.text.toString().trim()}"
            val option4Text = "${"D."}${option4.text.toString().trim()}"
            if(answerspinner == "A"){
                answerText = option1Text
            }else if(answerspinner == "B"){
                answerText = option2Text
            }else if(answerspinner == "C"){
                answerText = option3Text
            }else{
                answerText = option4Text
            }
            if (questionText.isNotEmpty() && option1Text.isNotEmpty() &&
                option2Text.isNotEmpty() && option3Text.isNotEmpty()&& option4Text.isNotEmpty()) {
                val questionId = databaseRef.push().key ?: ""
                val question = Question(questionText,option1Text ,option2Text ,option3Text ,option4Text ,answerText)
                questionsMap[questionId] = question
                Log.d("questionsMap",questionsMap.toString())
                Toast.makeText(this, "Question added", Toast.LENGTH_SHORT).show()
                questionEditText.text.clear()
                option1.text.clear()
                option2.text.clear()
                option3.text.clear()
                option4.text.clear()

            } else {
                Toast.makeText(this, "Please enter question, answer and options",
                    Toast.LENGTH_SHORT).show()
            }
        }

        saveQuizButton.setOnClickListener {
            val questionText = questionEditText.text.toString().trim()
            var answerspinner = selectedAnser
            val option1Text ="${"A."}${option1.text.toString().trim()}"
            val option2Text = "${"B."}${option2.text.toString().trim()}"
            val option3Text = "${"C."}${option3.text.toString().trim()}"
            val option4Text = "${"D."}${option4.text.toString().trim()}"
            if(answerspinner == "A"){
                answerText = option1Text
            }else if(answerspinner == "B"){
                answerText = option2Text
            }else if(answerspinner == "C"){
                answerText = option3Text
            }else{
                answerText = option4Text
            }
            if (questionText.isNotEmpty() && option1Text.isNotEmpty() &&
                option2Text.isNotEmpty() && option3Text.isNotEmpty()&& option4Text.isNotEmpty()) {
                val questionId = databaseRef.push().key ?: ""
                val question = Question(questionText,option1Text ,option2Text ,option3Text ,option4Text ,answerText)
                questionsMap[questionId] = question
                Log.d("questionsMap",questionsMap.toString())
            }
            val sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
            val userID = sharedPreferences.getString("Phone", "").toString()
            val title = intent.getStringExtra("title")
            val date = intent.getStringExtra("selectedDateTime")
            val duration = intent.getStringExtra("duration")?.toLong()!!
            if (title != null) {
                if (title.isNotEmpty() && questionsMap.isNotEmpty()) {
                    val quizId = databaseRef.push().key ?: ""
                    val quiz = Quiz(quizId, title,date, duration, questionsMap)
                    databaseRef.child("MCQ").child(userID).child(quizId).setValue(quiz)
                    Toast.makeText(this, "Quiz added", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@AddMCQ, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Please add at least one question",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
