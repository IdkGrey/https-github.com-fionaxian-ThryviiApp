package com.thryvii.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.thryvii.app.R

// "Jua" — the rounded, playful Google Font used throughout the Figma design.
// Bundled locally at res/font/jua_regular.ttf.
val JuaFontFamily = FontFamily(Font(R.font.jua_regular))

val ThryviiTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = JuaFontFamily,
        fontSize = 32.sp
    ),
    titleMedium = TextStyle(
        fontFamily = JuaFontFamily,
        fontSize = 16.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = JuaFontFamily,
        fontSize = 15.sp
    ),
    labelLarge = TextStyle(
        fontFamily = JuaFontFamily,
        fontSize = 15.sp
    )
)
