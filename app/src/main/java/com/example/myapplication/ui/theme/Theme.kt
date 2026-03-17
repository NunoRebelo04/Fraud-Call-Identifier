package com.example.myapplication.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val AppLightColors = lightColors(
    primary = Gray700,
    primaryVariant = Gray800,
    secondary = Gray500,
    background = Gray100,
    surface = White,
    onPrimary = White,
    onSecondary = White,
    onBackground = Gray900,
    onSurface = Gray900,
    error = RedSpam,
    onError = White
)

private val AppDarkColors = darkColors(
    primary = Gray300,
    primaryVariant = Gray400,
    secondary = Gray500,
    background = Gray900,
    surface = Gray800,
    onPrimary = Gray900,
    onSecondary = White,
    onBackground = White,
    onSurface = White,
    error = RedSpam,
    onError = White
)

@Composable
fun FraudCallTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = if (darkTheme) AppDarkColors else AppLightColors,
        content = content
    )
}