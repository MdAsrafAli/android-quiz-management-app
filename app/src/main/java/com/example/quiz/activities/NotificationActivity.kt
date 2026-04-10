package com.example.quiz.activities

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quiz.R
import com.example.quiz.adapters.QuizAdapterForNotify
import com.example.quiz.adapters.ShortQuizAdapterForNotify
import com.example.quiz.models.Quiz
import com.example.quiz.models.ShortQuiz
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificationActivity : AppCompatActivity() {

    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var quizRecyclerView: RecyclerView
    private lateinit var shortQuizRecyclerView: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var searchEditText: TextInputEditText

    private val upcomingMcqMap = mutableMapOf<String, MutableList<Quiz>>()
    private val upcomingShortMap = mutableMapOf<String, MutableList<ShortQuiz>>()
    private var showingMcq = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notification)

        quizRecyclerView = findViewById(R.id.quizRecyclerView)
        shortQuizRecyclerView = findViewById(R.id.shortQuizRecyclerView)
        tvEmpty = findViewById(R.id.tvEmpty)
        searchEditText = findViewById(R.id.searchEditText)

        setUpDrawerLayout()
        setUpTabs()
        setUpSearch()
        loadUpcomingQuizzes()
    }

    private fun loadUpcomingQuizzes() {
        val teacherSet = getSharedPreferences("UserData", Context.MODE_PRIVATE)
            .getStringSet("myList", null) ?: return
        for (teacherPhone in teacherSet) {
            loadMcqForTeacher(teacherPhone)
            loadShortForTeacher(teacherPhone)
        }
    }

    private fun loadMcqForTeacher(teacherPhone: String) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault())
        Firebase.database.reference.child("MCQ").child(teacherPhone)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val currentDate = Date()
                    val upcoming = mutableListOf<Quiz>()
                    for (snap in snapshot.children) {
                        val itm = snap.getValue(Quiz::class.java) ?: continue
                        itm.teacherPhone = teacherPhone
                        try {
                            val dt = dateFormat.parse(itm.selectedDateTime ?: "") ?: continue
                            if (currentDate.before(dt)) upcoming.add(itm)
                        } catch (e: ParseException) {
                            Log.e("NotificationActivity", "Parse error: ${e.message}")
                        }
                    }
                    upcomingMcqMap[teacherPhone] = upcoming
                    if (showingMcq) rebuildMcqList()
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@NotificationActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun loadShortForTeacher(teacherPhone: String) {
        val dateFormat = SimpleDateFormat("yyyy-M-d hh:mm a", Locale.getDefault())
        Firebase.database.reference.child("Qizzs").child(teacherPhone)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val currentDate = Date()
                    val upcoming = mutableListOf<ShortQuiz>()
                    for (snap in snapshot.children) {
                        val itm = snap.getValue(ShortQuiz::class.java) ?: continue
                        itm.teacherPhone = teacherPhone
                        try {
                            val dt = dateFormat.parse(itm.selectedDateTime ?: "") ?: continue
                            if (currentDate.before(dt)) upcoming.add(itm)
                        } catch (e: ParseException) {
                            Log.e("NotificationActivity", "Parse error: ${e.message}")
                        }
                    }
                    upcomingShortMap[teacherPhone] = upcoming
                    if (!showingMcq) rebuildShortList()
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@NotificationActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun rebuildMcqList(filter: String = searchEditText.text.toString()) {
        val all = upcomingMcqMap.values.flatten()
        val filtered = if (filter.isEmpty()) all
                       else all.filter { it.title?.contains(filter, true) == true }
        quizRecyclerView.layoutManager = LinearLayoutManager(this)
        quizRecyclerView.adapter = QuizAdapterForNotify(this, filtered)
        tvEmpty.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun rebuildShortList(filter: String = searchEditText.text.toString()) {
        val all = upcomingShortMap.values.flatten()
        val filtered = if (filter.isEmpty()) all
                       else all.filter { it.title?.contains(filter, true) == true }
        shortQuizRecyclerView.layoutManager = LinearLayoutManager(this)
        shortQuizRecyclerView.adapter = ShortQuizAdapterForNotify(this, filtered.toMutableList())
        tvEmpty.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun setUpTabs() {
        val btnMCQ = findViewById<MaterialButton>(R.id.btnMCQ)
        val btnShort = findViewById<MaterialButton>(R.id.btnShortQuestion)
        val underlineMcq = findViewById<View>(R.id.underlineMCQ)
        val underlineShort = findViewById<View>(R.id.underlineShortQuestion)

        btnMCQ.setOnClickListener {
            showingMcq = true
            underlineMcq.visibility = View.VISIBLE
            underlineShort.visibility = View.INVISIBLE
            quizRecyclerView.visibility = View.VISIBLE
            shortQuizRecyclerView.visibility = View.GONE
            rebuildMcqList()
        }
        btnShort.setOnClickListener {
            showingMcq = false
            underlineMcq.visibility = View.INVISIBLE
            underlineShort.visibility = View.VISIBLE
            quizRecyclerView.visibility = View.GONE
            shortQuizRecyclerView.visibility = View.VISIBLE
            rebuildShortList()
        }
    }

    private fun setUpSearch() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val q = s.toString()
                if (showingMcq) rebuildMcqList(q) else rebuildShortList(q)
            }
        })
    }

    private fun setUpDrawerLayout() {
        val appBar = findViewById<MaterialToolbar>(R.id.appBar)
        val mainDrawer = findViewById<DrawerLayout>(R.id.mainDrawer)
        setSupportActionBar(appBar)
        actionBarDrawerToggle = ActionBarDrawerToggle(this, mainDrawer, R.string.app_name, R.string.app_name)
        actionBarDrawerToggle.syncState()
        findViewById<NavigationView>(R.id.navigationView)
            .setNavigationItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.btnProfile -> {
                        startActivity(android.content.Intent(this, ProfileActivity::class.java))
                        mainDrawer.closeDrawers()
                        true
                    }
                    else -> false
                }
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) return true
        return super.onOptionsItemSelected(item)
    }
}
