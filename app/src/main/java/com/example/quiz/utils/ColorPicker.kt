package com.example.quiz.utils

object ColorPicker {
    var colors=arrayOf(
        "#3EB9DF",
        "#3685BC",
        "#D36280",
        "#E44F55",
        "#FA8056",
        "#818BCA",
        "#7D695F",
        "#51BAB3",
        "#4FB66C",
        "#E3AD17",
        "#627991",
        "#EF8EAD",
        "#B5BFC6",
        "#627991",

        )
    var currentcolorindex=0
    fun getcolor():String{
        currentcolorindex = (currentcolorindex +1)% colors.size
        return colors[currentcolorindex]
    }
}