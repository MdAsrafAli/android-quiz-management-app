package com.example.quiz.data.unsubscribe

data class UnsubscribeRequestParameters(
    val appId: String,
    val password: String,
    val mobile: String
)