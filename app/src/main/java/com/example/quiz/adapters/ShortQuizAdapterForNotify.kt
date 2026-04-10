package com.example.quiz.adapters

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.quiz.utils.ColorPicker
import com.example.quiz.utils.IconPicker
import com.example.quiz.R
import com.example.quiz.models.ShortQuiz
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ShortQuizAdapterForNotify(val context: Context, val quizzes: MutableList<ShortQuiz>) :
    RecyclerView.Adapter<ShortQuizAdapterForNotify.QuizViewHolder>() {

    inner class QuizViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewTitle: TextView = itemView.findViewById(R.id.quizTitle)
        var iconView: ImageView = itemView.findViewById(R.id.quizIcon)
        var cardContainer: CardView = itemView.findViewById(R.id.cardContainer)
        var date: TextView = itemView.findViewById(R.id.date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.quiz_item, parent, false)
        return QuizViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
        val quiz = quizzes[position]
        holder.textViewTitle.text = quiz.title
        holder.date.text = quiz.selectedDateTime
        holder.cardContainer.setCardBackgroundColor(Color.parseColor(ColorPicker.getcolor()))
        holder.iconView.setImageResource(IconPicker.geticon())

        holder.itemView.setOnClickListener {
            val msg = quiz.selectedDateTime?.let { dt ->
                formatRemainingTime(dt, "yyyy-M-d hh:mm a")
            } ?: "Unknown start time"

            AlertDialog.Builder(context)
                .setTitle("Starts in")
                .setMessage(msg)
                .setPositiveButton("OK") { d, _ -> d.dismiss() }
                .show()
        }
    }

    private fun formatRemainingTime(givenTime: String, timeFormat: String): String {
        return try {
            val dateFormat = SimpleDateFormat(timeFormat, Locale.getDefault())
            val target = dateFormat.parse(givenTime) ?: return "Unknown"
            val diffMs = target.time - Calendar.getInstance().time.time
            if (diffMs <= 0) return "Starting now"

            val days = diffMs / (1000 * 60 * 60 * 24)
            val hours = (diffMs % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)
            val minutes = (diffMs % (1000 * 60 * 60)) / (1000 * 60)

            buildString {
                if (days > 0) append("${days}d ")
                if (hours > 0) append("${hours}h ")
                append("${minutes}m")
            }.trim()
        } catch (e: Exception) {
            "Unknown"
        }
    }

    override fun getItemCount() = quizzes.size
}
