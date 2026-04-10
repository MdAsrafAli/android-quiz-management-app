package com.example.quiz.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quiz.R
import com.example.quiz.adapters.TitlesAdapter
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ShowResultsActivity : AppCompatActivity() {

    private lateinit var titlesRecyclerView: RecyclerView
    private lateinit var tvEmpty: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_results)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        titlesRecyclerView = findViewById(R.id.titlesRecyclerView)
        tvEmpty = findViewById(R.id.tvEmpty)
        titlesRecyclerView.layoutManager = LinearLayoutManager(this)

        val teacherPhone = getSharedPreferences("UserData", Context.MODE_PRIVATE)
            .getString("Phone", "") ?: ""
        fetchTeacherQuizTitles(teacherPhone)
    }

    /**
     * Collects the teacher's own quiz titles from MCQ + Qizzs nodes,
     * then shows only those titles that also have entries under results/.
     */
    private fun fetchTeacherQuizTitles(teacherPhone: String) {
        if (teacherPhone.isEmpty()) { showEmpty(); return }

        val db = FirebaseDatabase.getInstance().reference
        val teacherTitles = mutableSetOf<String>()

        db.child("MCQ").child(teacherPhone)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (quizSnap in snapshot.children) {
                        quizSnap.child("title").getValue(String::class.java)
                            ?.takeIf { it.isNotEmpty() }?.let { teacherTitles.add(it) }
                    }
                    db.child("Qizzs").child(teacherPhone)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snap2: DataSnapshot) {
                                for (quizSnap in snap2.children) {
                                    quizSnap.child("title").getValue(String::class.java)
                                        ?.takeIf { it.isNotEmpty() }?.let { teacherTitles.add(it) }
                                }
                                fetchResultsForTitles(teacherTitles)
                            }
                            override fun onCancelled(error: DatabaseError) { fetchResultsForTitles(teacherTitles) }
                        })
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ShowResultsActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    showEmpty()
                }
            })
    }

    private fun fetchResultsForTitles(teacherTitles: Set<String>) {
        FirebaseDatabase.getInstance().reference.child("results")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val titlesList = mutableListOf<String>()
                    for (snap in snapshot.children) {
                        val title = snap.key ?: continue
                        if (title in teacherTitles) titlesList.add(title)
                    }
                    if (titlesList.isEmpty()) {
                        showEmpty()
                    } else {
                        tvEmpty.visibility = View.GONE
                        titlesRecyclerView.visibility = View.VISIBLE
                        titlesRecyclerView.adapter = TitlesAdapter(titlesList) { title ->
                            startActivity(
                                Intent(this@ShowResultsActivity, ResultsDetailActivity::class.java)
                                    .putExtra("TitleOfQuiz", title)
                            )
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ShowResultsActivity, "Failed: ${error.message}", Toast.LENGTH_SHORT).show()
                    showEmpty()
                }
            })
    }

    private fun showEmpty() {
        titlesRecyclerView.visibility = View.GONE
        tvEmpty.visibility = View.VISIBLE
    }
}
