package com.example.quiz.models

data class ShortQuiz(
    var id: String? = "",
    var title: String? = "",
    val selectedDateTime: String ?="",
    val duration : Long= 1L ,
    var questions: MutableMap<String, ShortQuestion> = mutableMapOf()
)