package com.example.quiz.activities

import com.example.quiz.R
import com.example.quiz.adapters.ResultsAdapter
import com.example.quiz.models.QuizResult
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ResultsDetailActivity : AppCompatActivity() {

    private lateinit var resultsRecyclerView: RecyclerView
    private lateinit var tvEmpty: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results_detail)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        resultsRecyclerView = findViewById(R.id.resultsRecyclerView)
        tvEmpty = findViewById(R.id.tvEmpty)
        resultsRecyclerView.layoutManager = LinearLayoutManager(this)

        val title = intent.getStringExtra("TitleOfQuiz")
        if (title != null) {
            toolbar.title = title
            fetchResults(title)
        } else {
            Toast.makeText(this, "Quiz title missing", Toast.LENGTH_SHORT).show()
            showEmpty()
        }
    }

    private fun fetchResults(title: String) {
        FirebaseDatabase.getInstance().getReference("results").child(title)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val resultsList = mutableListOf<QuizResult>()
                    for (resultSnapshot in snapshot.children) {
                        val result = resultSnapshot.getValue(QuizResult::class.java)
                        if (result != null) resultsList.add(result)
                    }
                    resultsList.sortByDescending { it.score }

                    if (resultsList.isEmpty()) {
                        showEmpty()
                    } else {
                        tvEmpty.visibility = View.GONE
                        resultsRecyclerView.visibility = View.VISIBLE
                        resultsRecyclerView.adapter = ResultsAdapter(resultsList)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ResultsDetailActivity, "Failed: ${error.message}", Toast.LENGTH_SHORT).show()
                    showEmpty()
                }
            })
    }

    private fun showEmpty() {
        resultsRecyclerView.visibility = View.GONE
        tvEmpty.visibility = View.VISIBLE
    }
}
