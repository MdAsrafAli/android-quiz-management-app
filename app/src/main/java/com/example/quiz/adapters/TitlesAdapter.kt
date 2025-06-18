package com.example.quiz.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quiz.R

class TitlesAdapter(
    private val titlesList: List<String>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<TitlesAdapter.TitleViewHolder>() {

    class TitleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TitleViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.title_item, parent, false)
        return TitleViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TitleViewHolder, position: Int) {
        val title = titlesList[position]
        holder.titleTextView.text = title
        holder.itemView.setOnClickListener { onItemClick(title) }
    }

    override fun getItemCount(): Int {
        return titlesList.size
    }
}
