package com.pointguatemala.transportesvictoria.ui.shared

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pointguatemala.transportesvictoria.ui.theme.VictoriaBlack
import com.pointguatemala.transportesvictoria.ui.theme.VictoriaYellow

/** Pestaña activa del menú inferior. */
enum class TabNav { HOME, SOPORTE, PERFIL }

@Composable
fun NavBarInferior(
    tabActual: TabNav,
    modifier: Modifier = Modifier,
    onHome: () -> Unit,
    onSoporte: () -> Unit,
    onPerfil: () -> Unit
) {
    val itemColors = NavigationBarItemDefaults.colors(
        selectedIconColor   = VictoriaYellow,
        unselectedIconColor = Color.White.copy(alpha = 0.50f),
        indicatorColor      = VictoriaYellow.copy(alpha = 0.15f)
    )

    NavigationBar(
        modifier       = modifier,
        containerColor = VictoriaBlack,
        tonalElevation = 0.dp
    ) {
        NavigationBarItem(
            selected = tabActual == TabNav.HOME,
            onClick  = onHome,
            icon = {
                Icon(
                    imageVector        = Icons.Filled.Home,
                    contentDescription = "Inicio"
                )
            },
            colors = itemColors
        )
        NavigationBarItem(
            selected = tabActual == TabNav.SOPORTE,
            onClick  = onSoporte,
            icon = {
                Icon(
                    imageVector        = Icons.Filled.HeadsetMic,
                    contentDescription = "Soporte"
                )
            },
            colors = itemColors
        )
        NavigationBarItem(
            selected = tabActual == TabNav.PERFIL,
            onClick  = onPerfil,
            icon = {
                Icon(
                    imageVector        = Icons.Filled.Person,
                    contentDescription = "Perfil"
                )
            },
            colors = itemColors
        )
    }
}
