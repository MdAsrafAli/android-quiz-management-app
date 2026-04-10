package com.example.quiz.adapters

import android.graphics.Color
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
        holder.bind(questions[position], position + 1)
    }

    override fun getItemCount() = questions.size

    inner class AnswerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val questionTextView: TextView = itemView.findViewById(R.id.questionTextView)
        private val correctAnswerTextView: TextView = itemView.findViewById(R.id.correctAnswerTextView)
        private val userAnswerTextView: TextView = itemView.findViewById(R.id.userAnswerTextView)
        private val statusStripe: View = itemView.findViewById(R.id.statusStripe)
        private val tvResultBadge: TextView = itemView.findViewById(R.id.tvResultBadge)

        fun bind(question: Question, number: Int) {
            val isCorrect = question.answer == question.userAnswer
            questionTextView.text = "$number. ${question.description}"
            correctAnswerTextView.text = "✓ Correct: ${question.answer}"

            if (isCorrect) {
                userAnswerTextView.text = "Your answer: ${question.userAnswer}"
                userAnswerTextView.setTextColor(Color.parseColor("#2E7D32"))
                statusStripe.setBackgroundColor(Color.parseColor("#4CAF50"))
                tvResultBadge.text = "✓"
                tvResultBadge.setTextColor(Color.parseColor("#2E7D32"))
            } else {
                val userAns = question.userAnswer?.takeIf { it.isNotEmpty() } ?: "(no answer)"
                userAnswerTextView.text = "Your answer: $userAns"
                userAnswerTextView.setTextColor(Color.parseColor("#C62828"))
                statusStripe.setBackgroundColor(Color.parseColor("#F44336"))
                tvResultBadge.text = "✗"
                tvResultBadge.setTextColor(Color.parseColor("#C62828"))
            }
        }
    }
}
