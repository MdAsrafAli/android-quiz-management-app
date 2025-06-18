package com.example.quiz.activities

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quiz.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AddCourse : AppCompatActivity() {
    private lateinit var databaseref: FirebaseDatabase
    private lateinit var reqRef: DatabaseReference
    private lateinit var teacherIdEditText: EditText
    private lateinit var addCourseButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_course)

        getId()
    }

    private fun getId() {
        teacherIdEditText = findViewById(R.id.teacherIdEditText)
        addCourseButton = findViewById(R.id.addCourseButton)
        addCourseButton.setOnClickListener {
            val teacherId = teacherIdEditText.text.toString().trim()
            if (teacherId.isEmpty()) {
                Toast.makeText(this@AddCourse, "Please enter a teacher ID", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val databaseRef = Firebase.database
            val usersRef = databaseRef.reference.child("users")

            usersRef.child(teacherId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Phone number exists
                        val teacherName = snapshot.child("name").getValue(String::class.java)
                        if (teacherName != null) {
                            val sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
                            val userID = sharedPreferences.getString("Phone", "").toString()

                            databaseRef.reference.child("requests").child(teacherId).push()
                                .setValue(userID)
                                .addOnSuccessListener {
                                    Log.d("AddedStudent", teacherId)
                                }
                                .addOnFailureListener {
                                    // Handle failure
                                    Log.e("NotAdded", teacherId)
                                }

                            databaseRef.reference.child("requests").child(userID).push()
                                .setValue(teacherId)
                                .addOnSuccessListener {
                                    Log.d("AddedStudent", teacherId)
                                }
                                .addOnFailureListener {
                                    // Handle failure
                                    Log.e("NotAdded", teacherId)
                                }

                            Toast.makeText(this@AddCourse, "Request sent to teacher $teacherName", Toast.LENGTH_SHORT).show()

                            // Build and show the dialog
                            val dialogBuilder = AlertDialog.Builder(this@AddCourse)
                            dialogBuilder.setTitle("Request sent to teacher:")
                            dialogBuilder.setMessage(teacherName)
                            dialogBuilder.setPositiveButton("OK") { dialog, _ ->
                                dialog.dismiss()
                                val intent = Intent(this@AddCourse, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            val dialog = dialogBuilder.create()
                            dialog.show()
                        } else {
                            Toast.makeText(this@AddCourse, "Teacher name not found!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@AddCourse, "Teacher not found!", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@AddCourse, "Error fetching data: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
