package com.example.quiz.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quiz.R
import com.example.quiz.adapters.TitlesAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ShowResultsActivity : AppCompatActivity() {

    private lateinit var titlesRecyclerView: RecyclerView
    private lateinit var titlesAdapter: TitlesAdapter
    private val titlesList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_results)

        titlesRecyclerView = findViewById(R.id.titlesRecyclerView)
        titlesRecyclerView.layoutManager = LinearLayoutManager(this)

        fetchTitles()
    }

    private fun fetchTitles() {
        val database = FirebaseDatabase.getInstance()
        val resultsRef = database.getReference("results")

        resultsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                titlesList.clear()
                for (titleSnapshot in snapshot.children) {
                    val title = titleSnapshot.key
                    if (title != null) {
                        titlesList.add(title)
                    }
                }
                titlesAdapter = TitlesAdapter(titlesList) { title ->
                    val intent = Intent(this@ShowResultsActivity, ResultsDetailActivity::class.java)
                    intent.putExtra("TitleOfQuiz", title)
                    startActivity(intent)
                }
                titlesRecyclerView.adapter = titlesAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ShowResultsActivity, "Failed to fetch titles: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}