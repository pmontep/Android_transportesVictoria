package com.pointguatemala.transportesvictoria.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val VictoriaColorScheme = lightColorScheme(
    primary          = VictoriaYellow,
    onPrimary        = VictoriaBlack,
    secondary        = VictoriaBlack,
    onSecondary      = VictoriaWhite,
    background       = VictoriaWhite,
    onBackground     = VictoriaBlack,
    surface          = VictoriaWhite,
    onSurface        = VictoriaBlack,
    surfaceVariant   = VictoriaGray,
    outline          = VictoriaGrayMid
)

@Composable
fun TransportesVictoriaTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = VictoriaColorScheme,
        typography  = Typography,
        content     = content
    )
}
