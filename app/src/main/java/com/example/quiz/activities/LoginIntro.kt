package com.example.quiz.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.quiz.R
import com.google.firebase.auth.FirebaseAuth
import java.lang.Exception

class LoginIntro : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loginintro)
        val auth=FirebaseAuth.getInstance()
        if(auth.currentUser!=null){
            Toast.makeText(this,"User already logged in!",Toast.LENGTH_SHORT).show()
            redirect("MAIN")
        }
        val btnGetStarted: Button = findViewById(R.id.btnGetStarted)
        btnGetStarted.setOnClickListener(){
            redirect("LOGIN")
        }
    }
    private fun redirect(name:String){
        val intent = when(name){
            "LOGIN" -> Intent(this, LoginActivity::class.java)
            "MAIN" -> Intent(this, MainActivity::class.java)
            else -> throw Exception("No valid path exists")
        }
        startActivity(intent)
        finish() // Finish the current activity after starting the new one
    }

}