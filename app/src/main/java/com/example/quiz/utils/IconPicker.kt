package com.example.quiz.utils

import com.example.quiz.R

object IconPicker {
    var icons=arrayOf(
        R.drawable.icon_1,
        R.drawable.icon_2,
        R.drawable.icon_3,
        R.drawable.icon_4,
        R.drawable.icon_5,
        R.drawable.icon_6,
        R.drawable.icon_8,

        )
    var currenticonindex=0
    fun geticon(): Int {
        currenticonindex = (currenticonindex +1)% icons.size
        return icons[currenticonindex]
    }
}