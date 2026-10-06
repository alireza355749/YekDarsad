package com.example.yekdarsad.ui.todaytracker

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class TrackerCardModel(

    val type: String,

    val title: String,

    val unit: String,

    val icon: ImageVector,

    val color: Color

)