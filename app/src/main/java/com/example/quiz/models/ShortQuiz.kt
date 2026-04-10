package com.example.quiz.models

data class ShortQuiz(
    var id: String? = "",
    var title: String? = "",
    val selectedDateTime: String? = "",
    val duration: Long = 1L,
    var questions: MutableMap<String, ShortQuestion> = mutableMapOf(),
    // Not stored in Firebase — set in code after fetching, based on the teacher node key
    @get:com.google.firebase.database.Exclude
    @set:com.google.firebase.database.Exclude
    var teacherPhone: String = ""
)