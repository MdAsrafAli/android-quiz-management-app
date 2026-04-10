package com.example.quiz.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quiz.R
import com.example.quiz.models.QuizResult

class ResultsAdapter(private val resultsList: List<QuizResult>) :
    RecyclerView.Adapter<ResultsAdapter.ResultViewHolder>() {

    class ResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvRank: TextView = itemView.findViewById(R.id.tvRank)
        val userName: TextView = itemView.findViewById(R.id.userName)
        val userScore: TextView = itemView.findViewById(R.id.userScore)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.result_item, parent, false)
        return ResultViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ResultViewHolder, position: Int) {
        val result = resultsList[position]
        val rank = position + 1
        holder.tvRank.text = "#$rank"
        holder.userName.text = result.name
        holder.userScore.text = "Score: ${result.score}"

        // Gold / silver / bronze colors for top 3
        val rankColor = when (rank) {
            1 -> Color.parseColor("#F9A825") // gold
            2 -> Color.parseColor("#90A4AE") // silver
            3 -> Color.parseColor("#BF8970") // bronze
            else -> Color.parseColor("#1565C0")
        }
        holder.tvRank.backgroundTintList = android.content.res.ColorStateList.valueOf(rankColor)
    }

    override fun getItemCount() = resultsList.size
}
