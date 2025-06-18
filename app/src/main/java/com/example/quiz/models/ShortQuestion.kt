package com.example.quiz.models

import com.google.firebase.database.DataSnapshot
data class ShortQuestion(
    val text: String ?= "",
    val correctanswer: String? = "",
    var userAnswer: String ?= ""
)