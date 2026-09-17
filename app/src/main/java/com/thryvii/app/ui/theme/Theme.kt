package com.thryvii.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val ThryviiColorScheme = lightColorScheme(
    primary = ButtonRed,
    background = BackgroundGreen,
    surface = CardCream,
    onPrimary = ButtonTextCream,
    onBackground = TextBlack,
    onSurface = TextBlack
)

@Composable
fun ThryviiTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ThryviiColorScheme,
        typography = ThryviiTypography,
        content = content
    )
}
