package com.example.quiz.models

data class Quiz(
    var id: String ?="",
    var title: String ?="",
    val selectedDateTime: String ?="",
    val duration: Long=1L,
    var questions:MutableMap<String, Question> = mutableMapOf()
)