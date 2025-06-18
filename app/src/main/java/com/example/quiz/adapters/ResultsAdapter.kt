package com.example.quiz.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quiz.R
import com.example.quiz.models.QuizResult

class ResultsAdapter(private val resultsList: List<QuizResult>) : RecyclerView.Adapter<ResultsAdapter.ResultViewHolder>() {

    class ResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.userName)
        val userScore: TextView = itemView.findViewById(R.id.userScore)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.result_item, parent, false)
        return ResultViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        val result = resultsList[position]
        holder.userName.text = result.name
        holder.userScore.text = result.score.toString()
    }

    override fun getItemCount(): Int {
        return resultsList.size
    }
}
