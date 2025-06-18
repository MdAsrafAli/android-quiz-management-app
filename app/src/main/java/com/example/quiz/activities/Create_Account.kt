package com.example.quiz.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.quiz.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Create_Account : AppCompatActivity() {

    private lateinit var email: String
    private lateinit var password: String
    private lateinit var phoneNumber: String

    private lateinit var nameEditText: EditText
    private lateinit var ID: EditText
    private lateinit var roleSelectionRadioGroup: RadioGroup

    private lateinit var database: FirebaseDatabase
    private lateinit var usersRef: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        // Retrieve email and password from intent extras
        email = intent.getStringExtra("email") ?: ""
        password = intent.getStringExtra("password") ?: ""

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        usersRef = database.reference.child("users")

        val btnSignUp: Button = findViewById(R.id.btn1SignUp)
        btnSignUp.setOnClickListener {
            createFirebaseAuthUser()
        }
    }

    private fun createFirebaseAuthUser() {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Authentication successful, proceed with user registration in the database
                    Toast.makeText(this, "Authentication successful", Toast.LENGTH_SHORT).show()
                    signUpUser()
                } else {
                    Toast.makeText(this, "Sign up failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    Log.e("FirebaseAuthError", "Error creating FirebaseAuth user", task.exception)
                }
            }
    }

    private fun signUpUser() {
        nameEditText = findViewById(R.id.etName)
        ID = findViewById(R.id.ID)
        roleSelectionRadioGroup = findViewById(R.id.roleSelectionRadioGroup)
        phoneNumber = ID.text.toString()
        val name = nameEditText.text.toString()
        val selectedRole = findViewById<RadioButton>(roleSelectionRadioGroup.checkedRadioButtonId).text.toString()

        if (phoneNumber.isEmpty() || name.isEmpty() || selectedRole.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val userData = HashMap<String, Any>()
        userData["name"] = name
        userData["email"] = email
        userData["role"] = selectedRole

        val sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString("Phone", phoneNumber)
        editor.putString("email", email)
        editor.putString("Role", selectedRole)
        editor.putString("Name", name)
        editor.apply()

        usersRef.child(phoneNumber).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(this@Create_Account, "Phone number already exists", Toast.LENGTH_SHORT).show()
                } else {
                    usersRef.child(phoneNumber).setValue(userData)
                        .addOnSuccessListener {
                            Toast.makeText(this@Create_Account, "Registration Successful", Toast.LENGTH_SHORT).show()
                            navigateToMainActivity()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this@Create_Account, "Registration Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                            Log.e("FirebaseError", "Error adding user data to database", e)
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Create_Account, "Database Error: ${error.message}", Toast.LENGTH_SHORT).show()
                Log.e("FirebaseError", "Database error onCancelled", error.toException())
            }
        })
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setMessage("Are you sure you want to exit?")
            .setPositiveButton("Yes") { _, _ ->
                // Update SharedPreferences to indicate the user is not logged in
                val sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
                with(sharedPreferences.edit()) {
                    putBoolean("IsLogin", false)
                    apply()
                }
                finishAffinity()
            }
            .setNegativeButton("No", null)
            .show()

    }

}
