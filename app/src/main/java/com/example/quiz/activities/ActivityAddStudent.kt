package com.example.quiz.activities

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quiz.R
import com.example.quiz.adapters.RequestAdapters
import com.example.quiz.models.UserData
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class ActivityAddStudent : AppCompatActivity() {
    private lateinit var databaseRef: FirebaseDatabase
    private lateinit var studentName: String
    private lateinit var studentemail: String
    private lateinit var studentPhone: String
    private var user = mutableListOf<UserData>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_student)
        recyclerView = findViewById(R.id.recyclerView)
        emptyTextView = findViewById(R.id.emptytxt)

        getRequests()
    }

    private fun getRequests() {
        val sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val teacherID = sharedPreferences.getString("Phone", "").toString()

        databaseRef = FirebaseDatabase.getInstance()
        val requestsRef = databaseRef.reference.child("requests").child(teacherID)
        val numbers = mutableListOf<String>()
        requestsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                numbers.clear()
                for (requestSnapshot in dataSnapshot.children) {
                    val phoneNumber = requestSnapshot.value.toString()
                    if (phoneNumber.isNotEmpty()) {
                        numbers.add(phoneNumber)
                    }
                }
                showReq(numbers)
                Log.e("Numbers", numbers.toString())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(
                    this@ActivityAddStudent,
                    "Error fetching data: ${databaseError.message}", Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun showReq(numbers: MutableList<String>) {
        user.clear()
        for (number in numbers) {
            val ref = databaseRef.reference.child("users").child(number)
            ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val name = dataSnapshot.child("name").getValue(String::class.java).toString()
                        val email = dataSnapshot.child("email").getValue(String::class.java).toString()
                        val phone = number
                        val userData = UserData(name, email, phone)
                        user.add(userData)
                        setAdapter(user)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(this@ActivityAddStudent,
                        "Error fetching data: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun setAdapter(user: MutableList<UserData>) {
        val sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val userID = sharedPreferences.getString("Phone", "").toString()
        val adapter = RequestAdapters(user, userID, emptyTextView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        adapter.updateEmptyView()
    }
}
