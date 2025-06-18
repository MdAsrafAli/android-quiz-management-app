package com.example.quiz.adapters

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
import java.util.Date

class ShortQuizAdapter(val context: Context, val quizzes: MutableList<ShortQuiz>) : RecyclerView.Adapter<ShortQuizAdapter.QuizViewHolder>() {

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
        holder.itemView.setOnClickListener{
            Toast.makeText(context,quizzes[position].title,Toast.LENGTH_SHORT).show()
            val intent= Intent(context, ShortQuestionActivity::class.java)
            Log.e("Title",quizzes[position].id.toString())
            intent.putExtra("ID",quizzes[position].id)
            intent.putExtra("POS",position)
            intent.putExtra("Duration",quizzes[position].duration)
            Log.e("DurationAdapter",quizzes[position].duration.toString())
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        Log.d("ShortQuizAdapter", "Quizzes list size: ${quizzes.size}")
        return quizzes.size
    }
}