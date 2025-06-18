package com.example.quiz.activities
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.example.quiz.R
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {

    private lateinit var txtEmail: TextView
    private lateinit var txtUserID: TextView
    private lateinit var txtRole: TextView
    private lateinit var txtName: TextView
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        txtEmail = findViewById(R.id.txtEmail)
        txtUserID = findViewById(R.id.txtUserID)
        txtRole = findViewById(R.id.txtRole)
        txtName = findViewById(R.id.txtName)
        val sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val userID = sharedPreferences.getString("Phone", "").toString()
        val email = sharedPreferences.getString("email", "").toString()
        val role = sharedPreferences.getString("Role", "").toString()
        val Name = sharedPreferences.getString("Name", "").toString()
        txtEmail.text = "Mail : ${email}"
        txtUserID.text = "Your Number: ${userID}"
        txtRole.text = "Your role : ${role}"
        txtName.text = "Name: ${Name}"

        val btnLogout: Button = findViewById(R.id.btnLogout)
        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
            with(sharedPreferences.edit()) {
                putString("email", "")
                putString("Name", "")
                putString("Role", "")
                putString("Phone", "")
                putBoolean("IsLogin", false)
                apply()
            }
            val intent = Intent(this, VerifyOTP::class.java)
            startActivity(intent)
            finish()
        }
    }
}