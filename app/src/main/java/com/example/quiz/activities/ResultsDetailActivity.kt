package com.example.quiz.activities

import com.example.quiz.R
import com.example.quiz.adapters.ResultsAdapter
import com.example.quiz.models.QuizResult
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ResultsDetailActivity : AppCompatActivity() {

    private lateinit var resultsRecyclerView: RecyclerView
    private lateinit var resultsAdapter: ResultsAdapter
    private val resultsList = mutableListOf<QuizResult>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results_detail)

        resultsRecyclerView = findViewById(R.id.resultsRecyclerView)
        resultsRecyclerView.layoutManager = LinearLayoutManager(this)

        val title = intent.getStringExtra("TitleOfQuiz")
        if (title != null) {
            fetchResults(title)
        } else {
            Toast.makeText(this, "Title of quiz is missing", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchResults(title: String) {
        val database = FirebaseDatabase.getInstance()
        val resultsRef = database.getReference("results").child(title)

        resultsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                resultsList.clear()
                for (resultSnapshot in snapshot.children) {
                    val result = resultSnapshot.getValue(QuizResult::class.java)
                    if (result != null) {
                        resultsList.add(result)
                    }
                }
                // Sort resultsList by score in descending order
                resultsList.sortByDescending { it.score }
                resultsAdapter = ResultsAdapter(resultsList)
                resultsRecyclerView.adapter = resultsAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ResultsDetailActivity, "Failed to fetch results: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
