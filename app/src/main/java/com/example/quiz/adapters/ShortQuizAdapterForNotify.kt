package com.example.quiz.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.quiz.utils.ColorPicker
import com.example.quiz.utils.IconPicker
import com.example.quiz.R
import com.example.quiz.activities.ShortQuestionActivity
import com.example.quiz.models.Quiz
import com.example.quiz.models.ShortQuiz
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs

class ShortQuizAdapterForNotify(val context: Context, val quizzes: MutableList<ShortQuiz>) : RecyclerView.Adapter<ShortQuizAdapterForNotify.QuizViewHolder>() {

    inner class QuizViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewTitle: TextView = itemView.findViewById(R.id.quizTitle)
        var iconView: ImageView = itemView.findViewById(R.id.quizIcon)
        var cardContainer: CardView = itemView.findViewById(R.id.cardContainer)
        var date:TextView=itemView.findViewById(R.id.date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.quiz_item, parent, false)
        return QuizViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
        holder.textViewTitle.text = quizzes[position].title
        holder.date.text = quizzes[position].selectedDateTime
        holder.cardContainer.setCardBackgroundColor(Color.parseColor(ColorPicker.getcolor()))
        holder.iconView.setImageResource(IconPicker.geticon())
        val timeFormat = "yyyy-MM-dd hh:mm a"
        holder.itemView.setOnClickListener{
            val remainingTime = quizzes[position].selectedDateTime?.let { it1 ->
                calculateRemainingTime(it1, timeFormat)
            }
            val dialogBuilder = AlertDialog.Builder(holder.itemView.context)
            dialogBuilder.setTitle("Remaining Time:")
            dialogBuilder.setMessage(remainingTime)
            dialogBuilder.setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            val dialog = dialogBuilder.create()
            dialog.show()
        }
    }
    fun calculateRemainingTime(givenTime: String, timeFormat: String): String {
        val dateFormat = SimpleDateFormat(timeFormat, Locale.getDefault())
        val currentTime = Calendar.getInstance().time
        val remainingMilliseconds = dateFormat.parse(givenTime)?.time?.minus(currentTime.time) ?: 0

        val days = remainingMilliseconds / (1000 * 60 * 60 * 24)
        val hours = (remainingMilliseconds % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)
        val minutes = (remainingMilliseconds % (1000 * 60 * 60)) / (1000 * 60)
        val seconds = (remainingMilliseconds % (1000 * 60)) / 1000

        return String.format("%02d:%02d:%02d:%02d", abs(days), abs(hours), abs(minutes), abs(seconds))
    }

    override fun getItemCount(): Int {
        Log.d("ShortQuizAdapter", "Quizzes list size: ${quizzes.size}")
        return quizzes.size
    }
}