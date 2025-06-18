// AnswerAdapter.kt
package com.example.quiz.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quiz.R
import com.example.quiz.models.Question

class AnswerAdapter(private val questions: List<Question>) :
    RecyclerView.Adapter<AnswerAdapter.AnswerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_question_answer, parent, false)
        return AnswerViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnswerViewHolder, position: Int) {
        val question = questions[position]
        holder.bind(question)
    }

    override fun getItemCount(): Int {
        return questions.size
    }

    inner class AnswerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val questionTextView: TextView = itemView.findViewById(R.id.questionTextView)
        private val correctAnswerTextView: TextView = itemView.findViewById(R.id.correctAnswerTextView)
        private val userAnswerTextView: TextView = itemView.findViewById(R.id.userAnswerTextView)

        fun bind(question: Question) {
            questionTextView.text = "Question: ${question.description}"
            correctAnswerTextView.text = "Correct Answer: ${question.answer}"
            userAnswerTextView.text = "Your Answer: ${question.userAnswer}"
        }
    }
}
