package com.example.yekdarsad.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable


private val DarkColorScheme = darkColorScheme(

    primary = GreenPrimary,
    secondary = CreamBackground,
    tertiary = GreenLight

)


private val LightColorScheme = lightColorScheme(

    primary = GreenPrimary,
    secondary = GreenLight,
    tertiary = GreenDark,

    background = CreamBackground,
    surface = CardCream,

    onPrimary = CreamBackground,
    onSecondary = TextDark,
    onBackground = TextDark,
    onSurface = TextDark

)



@Composable
fun YekDarsadTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {


    MaterialTheme(

        colorScheme = if (darkTheme)
            DarkColorScheme
        else
            LightColorScheme,

        typography = Typography,

        content = content

    )

}