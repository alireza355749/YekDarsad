package com.example.yekdarsad.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.yekdarsad.R


val VazirFont = FontFamily(

    Font(
        R.font.vazirmatn_regular,
        FontWeight.Normal
    ),

    Font(
        R.font.vazirmatn_medium,
        FontWeight.Medium
    ),

    Font(
        R.font.vazirmatn_bold,
        FontWeight.Bold
    )

)



val Typography = Typography(

    bodyLarge = TextStyle(

        fontFamily = VazirFont,

        fontWeight = FontWeight.Normal,

        fontSize = 16.sp,

        lineHeight = 24.sp

    ),


    bodyMedium = TextStyle(

        fontFamily = VazirFont,

        fontWeight = FontWeight.Normal,

        fontSize = 14.sp

    ),


    titleLarge = TextStyle(

        fontFamily = VazirFont,

        fontWeight = FontWeight.Bold,

        fontSize = 22.sp

    )

)