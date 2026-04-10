package com.example.quiz.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quiz.R
import com.example.quiz.models.Quiz
import com.example.quiz.models.ShortQuiz

data class TeacherSection(
    val teacherPhone: String,
    val teacherName: String,
    val quizzes: List<Quiz>
)

data class TeacherShortSection(
    val teacherPhone: String,
    val teacherName: String,
    val quizzes: List<ShortQuiz>
)

class TeacherSectionAdapter(
    private val context: Context,
    private val sections: List<TeacherSection>
) : RecyclerView.Adapter<TeacherSectionAdapter.SectionViewHolder>() {

    inner class SectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTeacherName: TextView = itemView.findViewById(R.id.tvTeacherName)
        val rvQuizzes: RecyclerView = itemView.findViewById(R.id.rvTeacherQuizzes)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.teacher_section_item, parent, false)
        return SectionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        val section = sections[position]
        holder.tvTeacherName.text = "Teacher: ${section.teacherName}"
        holder.rvQuizzes.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        holder.rvQuizzes.adapter = QuizAdapter(context, section.quizzes)
    }

    override fun getItemCount() = sections.size
}

class TeacherShortSectionAdapter(
    private val context: Context,
    private val sections: List<TeacherShortSection>
) : RecyclerView.Adapter<TeacherShortSectionAdapter.SectionViewHolder>() {

    inner class SectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTeacherName: TextView = itemView.findViewById(R.id.tvTeacherName)
        val rvQuizzes: RecyclerView = itemView.findViewById(R.id.rvTeacherQuizzes)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.teacher_section_item, parent, false)
        return SectionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        val section = sections[position]
        holder.tvTeacherName.text = "Teacher: ${section.teacherName}"
        holder.rvQuizzes.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        holder.rvQuizzes.adapter = ShortQuizAdapter(context, section.quizzes.toMutableList())
    }

    override fun getItemCount() = sections.size
}
