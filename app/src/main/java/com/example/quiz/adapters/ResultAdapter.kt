package com.example.quiz.adapters

import android.graphics.Color
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
        holder.bind(questions[position], position + 1)
    }

    override fun getItemCount() = questions.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val questionTextView: TextView = itemView.findViewById(R.id.questionTextView)
        private val correctAnswerTextView: TextView = itemView.findViewById(R.id.correctAnswerTextView)
        private val userAnswerTextView: TextView = itemView.findViewById(R.id.userAnswerTextView)
        private val statusStripe: View = itemView.findViewById(R.id.statusStripe)
        private val tvResultBadge: TextView = itemView.findViewById(R.id.tvResultBadge)

        fun bind(question: ShortQuestion, number: Int) {
            val isCorrect = question.userAnswer?.trim()?.lowercase() ==
                    question.correctanswer?.trim()?.lowercase()

            questionTextView.text = "$number. ${question.text}"
            correctAnswerTextView.text = "✓ Correct: ${question.correctanswer}"

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
