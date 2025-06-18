package com.example.quiz.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quiz.R
import java.util.*
import kotlin.properties.Delegates

class DecideActivity : AppCompatActivity() {


    private lateinit var buttonProceed:Button
    private lateinit var buttonDate: Button
    private lateinit var title: EditText
    private lateinit var selectedHour:String
    private lateinit var selectedMinute:String
    private lateinit var selectedAMPM:String
    private lateinit var selectedqType:String


    private lateinit var spinnerHours: Spinner
    private lateinit var spinnerMinutes: Spinner
    private lateinit var AMPM: Spinner
    private lateinit var spinnerQType: Spinner
    private lateinit var spinnerDuration: Spinner
    private lateinit var hours: Array<String>
    private lateinit var minutes: Array<String>
    private lateinit var ampm: Array<String>
    private lateinit var qType: Array<String>
    private lateinit var durations: Array<Int>

    private var selectedDate: String? = null
    private var selectedTime: Int = 5
    private var mcq by Delegates.notNull<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.decide_activity)
        setUpSpinners()
        setonclickListeners()


    }

    private fun setUpSpinners() {
        spinnerHours= findViewById(R.id.spinnerHours)
        spinnerMinutes = findViewById(R.id.spinnerMinutes)
        AMPM= findViewById(R.id.AM)
        spinnerQType = findViewById(R.id.spinnerQType)
        spinnerDuration = findViewById(R.id.spinnerDuration)


        // Set up hours spinner
        hours = arrayOf("00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12")
        val hoursAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, hours)
        spinnerHours.adapter = hoursAdapter

        // Set up minutes spinner
        minutes = arrayOf("00", "05", "10", "15", "20", "25", "30", "35",  "40", "45", "50", "55")
        val minutesAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, minutes)
        spinnerMinutes.adapter = minutesAdapter
        ampm = arrayOf("AM", "PM")
        val ampmAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, ampm)
        AMPM.adapter = ampmAdapter
        qType = arrayOf("MCQ", "Short Question")
        val QTypeadapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, qType)
        spinnerQType.adapter = QTypeadapter
        durations = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25,
            26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52,
            53, 54, 55, 56, 57, 58, 59, 60)
        val durationAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, durations)
        spinnerDuration.adapter = durationAdapter

    }

    private fun setonclickListeners(){

        buttonDate = findViewById(R.id.buttonDate)
        buttonProceed= findViewById(R.id.buttonProceed)

        mcq=true

        // Set click listener for date button
        buttonDate.setOnClickListener {
            showDatePicker()
        }


        // Set click listener for proceed button

        buttonProceed.setOnClickListener {
            title = findViewById(R.id.title)
            val selectedDateTime = "$selectedDate $selectedHour:$selectedMinute $selectedAMPM"
            val Title = title.text.toString().trim()
            Log.e("Title", Title)

            if (selectedDate != null && Title.isNotEmpty()) {
                // Proceed to the appropriate activity based on the selected date and time
                val intent = if (selectedqType != "MCQ") {
                    Intent(this, AddQuizActivity::class.java)
                } else {
                    Intent(this, AddMCQ::class.java)
                }
                // Pass selected date and time to the next activity if needed
                intent.putExtra("selectedDateTime", selectedDateTime)
                intent.putExtra("DateTime", selectedDateTime)
                intent.putExtra("title", Title)
                intent.putExtra("duration", selectedTime.toString())

                startActivity(intent)
            } else {
                Toast.makeText(this, "Please select Title, date and time", Toast.LENGTH_SHORT).show()
            }
        }



        spinnerHours.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedHour = hours[position]
                Log.e("selectedHour", selectedHour)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

// Set up listener for minutes spinner
        spinnerMinutes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedMinute = minutes[position]
                Log.e("selectedMinute", selectedMinute)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

// Set up listener for AM/PM spinner
        AMPM.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedAMPM = ampm[position]
                Log.e("selectedAMPM", selectedAMPM)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
        spinnerQType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedqType = qType[position]
                Log.e("selectedType", selectedqType)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
        spinnerDuration.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedTime = durations[position]
                Log.e("selectedTime", selectedTime.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }


    }



    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, day ->
                selectedDate = "$year-${month + 1}-$day"
                buttonDate.text = selectedDate
            },
            year,
            month,
            dayOfMonth
        )

        datePickerDialog.show()
    }
}
