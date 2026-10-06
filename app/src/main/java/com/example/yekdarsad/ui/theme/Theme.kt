package com.example.yekdarsad.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(

    primary = PrimaryGreen,

    secondary = PrimaryGreenLight,

    tertiary = ProgressColor,

    background = AppBackground,

    surface = CardBackground,

    onPrimary = CardBackground,

    onSecondary = TextPrimary,

    onBackground = TextPrimary,

    onSurface = TextPrimary

)


private val DarkColorScheme = darkColorScheme(

    primary = DarkPrimaryGreen,

    secondary = DarkPrimaryGreenLight,

    tertiary = DarkProgressColor,

    background = DarkAppBackground,

    surface = DarkCardBackground,

    surfaceVariant = DarkCardSecondary,

    onPrimary = DarkAppBackground,

    onSecondary = DarkTextPrimary,

    onBackground = DarkTextPrimary,

    onSurface = DarkTextPrimary

)


@Composable
fun YekDarsadTheme(

    darkTheme: Boolean = false,

    content: @Composable () -> Unit

) {

    MaterialTheme(

        colorScheme =
            if (darkTheme)
                DarkColorScheme
            else
                LightColorScheme,

        typography =
            Typography,

        content =
            content

    )

}