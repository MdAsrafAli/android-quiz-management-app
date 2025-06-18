package com.example.quiz.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.quiz.R
import com.google.firebase.auth.FirebaseAuth

class SignupActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val btnSignUp: Button = findViewById(R.id.btn1SignUp)
        val btnLogin: TextView = findViewById(R.id.btnLogin)

        btnSignUp.setOnClickListener {
            signUpUser()
        }

        btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun signUpUser() {
        val emailEditText: EditText = findViewById(R.id.etEmailAddress)
        val passwordEditText: EditText = findViewById(R.id.etPassword)
        val confirmPasswordEditText: EditText = findViewById(R.id.etConfirmPassword)

        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()
        val confirmPassword = confirmPasswordEditText.text.toString()


        if (password != confirmPassword) {
            Toast.makeText(this, "Confirm password does not match", Toast.LENGTH_SHORT).show()
            return
        }
//        if(isEmailValid(email)){
//            Log.d("ValidMail","Email is valid")
//        }
//        else{
//            Toast.makeText(this, "Email ID is not valid", Toast.LENGTH_SHORT).show()
//            return
//        }

        // Pass email and password to the CreateAccountActivity
        val intent = Intent(this, Create_Account::class.java)
        intent.putExtra("email", email)
        intent.putExtra("password", password)
        startActivity(intent)
        finish()
    }
//    fun isEmailValid(email: String): Boolean {
//        val emailRegex = Regex("^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,3})+\$")
//        return emailRegex.matches(email)
//    }

    fun onSignupButtonClick(view: View) {
        // Navigate back to the LoginActivity
        val intent = Intent(this, LoginActivity::class.java)
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
