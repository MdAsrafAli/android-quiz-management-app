package com.example.quiz.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quiz.R
import com.example.quiz.models.ShortQuestion

class ResultAdapter(private val questions: List<ShortQuestion>) :
    RecyclerView.Adapter<ResultAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_question_answer, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val question = questions[position]
        Log.e("CorrectAnswer", questions[position].toString())
        holder.bind(question)
    }

    override fun getItemCount(): Int {

        return questions.size

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val questionTextView: TextView = itemView.findViewById(R.id.questionTextView)
        private val userAnswerTextView: TextView = itemView.findViewById(R.id.userAnswerTextView)
        private val correctAnswerTextView: TextView = itemView.findViewById(R.id.correctAnswerTextView)

        fun bind(question: ShortQuestion) {
            questionTextView.text = question.text
            userAnswerTextView.text = "Your Answer: ${question.userAnswer}"
            correctAnswerTextView.text = "Correct Answer: ${question.correctanswer}"
            Log.e("Your Answer",question.userAnswer.toString())

        }
    }
}
