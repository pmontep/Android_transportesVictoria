package com.pointguatemala.transportesvictoria.ui.dashboard

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pointguatemala.transportesvictoria.R
import com.pointguatemala.transportesvictoria.data.model.SolicitudViaje
import com.pointguatemala.transportesvictoria.data.model.User
import com.pointguatemala.transportesvictoria.ui.theme.VictoriaBlack
import com.pointguatemala.transportesvictoria.ui.theme.VictoriaGray
import com.pointguatemala.transportesvictoria.ui.theme.VictoriaGrayMid
import com.pointguatemala.transportesvictoria.ui.theme.VictoriaYellow

// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuarioScreen(
    user: User,
    viewModel: UsuarioViewModel = viewModel(),
    recargar: Boolean = false,
    onRecargarDone: () -> Unit = {},
    onSolicitar: () -> Unit = {},
    onVerDetalle: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val isAnimatingRefresh = uiState.isLoading || uiState.isRefreshing
    val infiniteTransition = rememberInfiniteTransition(label = "refresh")
    val refreshRotation by infiniteTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = 360f,
        animationSpec = infiniteRepeatable(
            animation  = tween(durationMillis = 700, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "refreshRotation"
    )

    // Recargar lista cuando se vuelve del formulario con una solicitud nueva
    LaunchedEffect(recargar) {
        if (recargar) {
            viewModel.cargarSolicitudes()
            onRecargarDone()
        }
    }

    // Cerrar diálogo al completar calificación exitosa
    LaunchedEffect(uiState.calificacionExitosa) {
        if (uiState.calificacionExitosa) {
            viewModel.onCalificacionExitosaConsumed()
        }
    }

    // Mostrar errores en snackbar
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    // Diálogo de error en carga de solicitudes
    if (uiState.errorCargaSolicitudes) {
        AlertDialog(
            onDismissRequest = { /* Requiere confirmación explícita */ },
            icon = {
                Icon(
                    imageVector        = Icons.Filled.WarningAmber,
                    contentDescription = null,
                    tint               = Color(0xFFFFA726),
                    modifier           = Modifier.size(40.dp)
                )
            },
            title = {
                Text(
                    text       = "No se pudieron cargar tus solicitudes",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp,
                    color      = VictoriaBlack
                )
            },
            text = {
                Text(
                    text      = "Desliza para reintentar o vuelve a entrar.",
                    fontSize  = 13.sp,
                    color     = Color(0xFF757575),
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearErrorCargaSolicitudes()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VictoriaBlack,
                        contentColor   = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Aceptar", fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color.White,
            shape          = RoundedCornerShape(20.dp)
        )
    }

    // Diálogo de calificación
    if (uiState.solicitudACalificar != null) {
        CalificarViajeDialog(
            isLoading  = uiState.isCalificating,
            onEnviar   = { estrellas, comentario -> viewModel.calificarViaje(estrellas, comentario) },
            onCancelar = { viewModel.cerrarCalificar() }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {

            // ── Logo superior ─────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_tv),
                    contentDescription = "Transportes Victoria",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth(0.70f)
                        .height(130.dp)
                        .padding(top = 36.dp, bottom = 12.dp)
                )
            }

            // ── Franja amarilla ───────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(VictoriaYellow)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ── Botón Solicitar Viaje ─────────────────────────────────────────
            Button(
                onClick = onSolicitar,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = VictoriaYellow,
                    contentColor   = VictoriaBlack
                )
            ) {
                Text(
                    text = "Solicitar Viaje",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.3.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Título + botón recargar ───────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Mis solicitudes de viaje",
                    color = VictoriaBlack,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.4.sp
                )
                IconButton(
                    onClick = { viewModel.cargarSolicitudes() },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector        = Icons.Filled.Refresh,
                        contentDescription = "Recargar",
                        tint               = VictoriaBlack,
                        modifier           = Modifier
                            .size(20.dp)
                            .rotate(if (isAnimatingRefresh) refreshRotation else 0f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── Encabezado de columnas ────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(VictoriaGray)
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Fecha / Hora",
                    color = Color(0xFF757575),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(2f)
                )
                Text(
                    text = "Ruta / Estado viaje",
                    color = Color(0xFF757575),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(2.5f)
                )
                Text(
                    text = "Detalle",
                    color = Color(0xFF757575),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(2.5f)
                )
            }

            // ── Contenido principal ───────────────────────────────────────────
            PullToRefreshBox(
                isRefreshing = uiState.isRefreshing,
                onRefresh    = { viewModel.cargarSolicitudes(fromRefresh = true) },
                modifier     = Modifier.fillMaxSize()
            ) {

                when {
                    // Cargando
                    uiState.isLoading -> {
                        CircularProgressIndicator(
                            color = VictoriaYellow,
                            modifier = Modifier
                                .size(48.dp)
                                .align(Alignment.Center)
                        )
                    }

                    // Lista vacía
                    uiState.solicitudes.isEmpty() && !uiState.isLoading -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No tienes solicitudes de viaje",
                                color = VictoriaGrayMid,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { viewModel.cargarSolicitudes() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = VictoriaYellow,
                                    contentColor   = VictoriaBlack
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Reintentar", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Lista de solicitudes
                    else -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item { Spacer(modifier = Modifier.height(4.dp)) }

                            items(
                                items = uiState.solicitudes,
                                key   = { it.idL ?: it.id.toString() }
                            ) { solicitud ->
                                FilaSolicitud(
                                    solicitud    = solicitud,
                                    onVerDetalle = {
                                        onVerDetalle(solicitud.idL ?: solicitud.id.toString())
                                    },
                                    onCalificar  = { viewModel.abrirCalificar(solicitud) }
                                )
                            }

                            item { Spacer(modifier = Modifier.height(16.dp)) }
                        }
                    }
                }
            }
        }

        // ── Snackbar de errores ───────────────────────────────────────────────
        SnackbarHost(
            hostState = snackbarHostState,
            modifier  = Modifier.align(Alignment.BottomCenter)
        ) { data ->
            Snackbar(
                snackbarData   = data,
                containerColor = VictoriaBlack,
                contentColor   = Color.White,
                actionColor    = VictoriaYellow
            )
        }
    }
}

// ── Fila de solicitud de viaje ────────────────────────────────────────────────
@Composable
private fun FilaSolicitud(
    solicitud: SolicitudViaje,
    onVerDetalle: () -> Unit,
    onCalificar: () -> Unit
) {
    val estadosCancelados = setOf("cancelado", "cancelada", "inactiva", "rechazado")
    val esCancelada   = solicitud.estadoSolicitud?.lowercase()?.trim() in estadosCancelados
    val estadoMostrar = if (esCancelada) solicitud.estadoSolicitud else solicitud.estadoViaje
    val estadoColor   = colorEstado(estadoMostrar)

    val viajeCompletado = solicitud.estadoViaje?.trim()?.lowercase().let {
        it == "completado" || it == "finalizado"
    }
    val mostrarCalificar = viajeCompletado && !esCancelada

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // ── Columna 1: Fecha + Hora ────────────────────────────────────────
            Column(modifier = Modifier.weight(2f)) {
                Text(
                    text       = solicitud.fechaSolicitud ?: "—",
                    color      = VictoriaBlack,
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text     = solicitud.horaSolicitud?.take(5) ?: "—",
                    color    = Color(0xFF9E9E9E),
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // ── Columna 2: Ruta + estado ───────────────────────────────────────
            Column(modifier = Modifier.weight(2.5f)) {
                Text(
                    text       = solicitud.rutaSolicitada ?: "Sin ruta",
                    color      = VictoriaBlack,
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .background(
                            color = estadoColor.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text       = etiquetaEstado(estadoMostrar),
                        color      = estadoColor,
                        fontSize   = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            // ── Columna 3: Detalle — layout vertical centrado ─────────────────
            // Orden: [··· icono arriba] → [★ tag Calificar] → [Ver más abajo]
            Column(
                modifier            = Modifier.weight(2.5f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // 1. Icono tres puntos horizontales (···)
                Icon(
                    imageVector        = Icons.Filled.MoreHoriz,
                    contentDescription = "Ver detalles",
                    tint               = VictoriaBlack,
                    modifier           = Modifier
                        .size(24.dp)
                        .clickable(onClick = onVerDetalle)
                )

                // 2. Tag Calificar (entre el icono y "Ver más")
                if (mostrarCalificar) {
                    Spacer(modifier = Modifier.height(5.dp))
                    if (solicitud.calificacion != null) {
                        // Ya calificado → tag gris con ★ + número
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFFE0E0E0))
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector        = Icons.Filled.Star,
                                    contentDescription = null,
                                    tint               = VictoriaBlack,
                                    modifier           = Modifier.size(9.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text       = "${solicitud.calificacion}",
                                    color      = VictoriaBlack,
                                    fontSize   = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else {
                        // Sin calificar → tag amarillo con ★ + "Calificar" (clicable)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(VictoriaYellow)
                                .clickable(onClick = onCalificar)
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector        = Icons.Filled.Star,
                                    contentDescription = null,
                                    tint               = VictoriaBlack,
                                    modifier           = Modifier.size(9.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text       = "Calificar",
                                    color      = VictoriaBlack,
                                    fontSize   = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // 3. Texto "Ver más"
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text     = "Ver más",
                    color    = VictoriaGrayMid,
                    fontSize = 10.sp,
                    modifier = Modifier.clickable(onClick = onVerDetalle)
                )
            }
        }
    }
}
