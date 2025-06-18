package com.example.quiz.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import com.example.quiz.R
import com.example.quiz.retrifit.MyApiService
import com.example.quiz.retrifit.ServiceBuilder
import com.example.quiz.data.otpRequest.ApiResponse
import com.example.quiz.data.otpRequest.RequestParameters
import com.example.quiz.data.otpVerify.OtpVerifyRespone
import com.example.quiz.data.otpVerify.VerifyParameters
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VerifyOTP : ComponentActivity() {
    private var referenceNo: String? = null
    private lateinit var mobileNumber: String
    private lateinit var editTextMobileNumber:EditText
    private lateinit var submitButton:Button
    private lateinit var editTextOtp:EditText
    private lateinit var verifyButton:Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.verify_otp)

        val sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val userStatus = sharedPreferences.getBoolean("IsLogin", false)
        if(userStatus){
            Log.d("NextActivity",userStatus.toString())
            //activity
            val intent = Intent(this, LoginIntro::class.java)
            startActivity(intent)
            finish()
        }
         editTextMobileNumber = findViewById<EditText>(R.id.editTextMobileNumber)
         submitButton = findViewById<Button>(R.id.submitButton)
         editTextOtp = findViewById<EditText>(R.id.editTextOtp)
        verifyButton = findViewById<Button>(R.id.verifyButton)

        editTextOtp.visibility= View.GONE
        verifyButton.visibility=View.GONE

        submitButton.setOnClickListener {
            mobileNumber = "88${editTextMobileNumber.text.toString()}"

            if (mobileNumber.isNotEmpty()) {
                requestOtp(mobileNumber)
            } else {
                Toast.makeText(this, "Please enter a mobile number", Toast.LENGTH_SHORT).show()
            }
        }

        verifyButton.setOnClickListener {
            val otp = editTextOtp.text.toString()
            if (otp.isNotEmpty() && referenceNo != null) {
                verifyOtp(otp, referenceNo!!)
            } else {
                Toast.makeText(this, "Please enter the OTP", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun requestOtp(mobile: String) {
        val requestParameters = RequestParameters(
            appId = "APP_118964",
            password = "634b7bb619e789ee9f7aff680b340c96",
            mobile = mobile
        )

        val destinationService = ServiceBuilder.buildService(MyApiService::class.java)
        val requestCall = destinationService.requestOtp(requestParameters)

        requestCall.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null) {
                        referenceNo = apiResponse.referenceNo

                        if(apiResponse.statusDetail=="user already registered"){
                            val sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
                            with(sharedPreferences.edit()) {
                                putBoolean("IsLogin", true)
                                apply()
                            }
                            Toast.makeText(this@VerifyOTP, "Your number is already verified!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@VerifyOTP, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }else{
                            editTextOtp.visibility= View.VISIBLE
                            verifyButton.visibility=View.VISIBLE
                            editTextMobileNumber.visibility= View.GONE
                            submitButton.visibility=View.GONE

                            Log.d("MainActivity", "OTP sent successfully: $apiResponse")
                            Toast.makeText(this@VerifyOTP, "OTP sent successfully!", Toast.LENGTH_SHORT).show()

                        }

                    } else {
                        Log.e("MainActivity", "Failed to send OTP: Response is null")
                    }
                } else {
                    Log.e("MainActivity", "Failed to send OTP: ${response.errorBody()?.string()}")
                    Toast.makeText(this@VerifyOTP, "Failed to send OTP", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Log.e("MainActivity", "Network error: ${t.message}")
                Toast.makeText(this@VerifyOTP, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun verifyOtp(otp: String, referenceNo: String) {
        val verifyParameters = VerifyParameters(
            appId = "APP_118964",
            password = "634b7bb619e789ee9f7aff680b340c96",
            referenceNo = referenceNo,
            otp = otp
        )

        val destinationService = ServiceBuilder.buildService(MyApiService::class.java)
        val requestCall = destinationService.verifyOtp(verifyParameters)

        requestCall.enqueue(object : Callback<OtpVerifyRespone> {
            override fun onResponse(call: Call<OtpVerifyRespone>, response: Response<OtpVerifyRespone>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null) {
                        if (apiResponse.statusDetail == "Invalid OTP") {
                            Toast.makeText(this@VerifyOTP, "Invalid OTP", Toast.LENGTH_SHORT).show()
                            Log.d("MainActivity", "Invalid OTP: $apiResponse")
                        } else {
                            Toast.makeText(this@VerifyOTP, "OTP verified successfully!", Toast.LENGTH_SHORT).show()
                            Log.d("MainActivity", "OTP verified successfully: $apiResponse")
                            val sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
                            with(sharedPreferences.edit()) {
                                putBoolean("IsLogin", true)
                                apply()
                            }
                            val intent = Intent(this@VerifyOTP, LoginIntro::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        Log.e("MainActivity", "Failed to verify OTP: Response is null")
                        Toast.makeText(this@VerifyOTP, "Failed to verify OTP", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("MainActivity", "Failed to verify OTP: ${response.errorBody()?.string()}")
                    Toast.makeText(this@VerifyOTP, "Invalid OTP", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<OtpVerifyRespone>, t: Throwable) {
                Log.e("MainActivity", "Network error: ${t.message}")
                Toast.makeText(this@VerifyOTP, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
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
