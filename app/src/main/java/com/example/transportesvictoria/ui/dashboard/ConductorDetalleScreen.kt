package com.pointguatemala.transportesvictoria.ui.dashboard

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pointguatemala.transportesvictoria.data.model.ConductorViaje
import com.pointguatemala.transportesvictoria.data.model.PasajeroViaje
import com.pointguatemala.transportesvictoria.ui.theme.VictoriaBlack
import com.pointguatemala.transportesvictoria.ui.theme.VictoriaGray
import com.pointguatemala.transportesvictoria.ui.theme.VictoriaYellow

@Composable
fun ConductorDetalleScreen(
    viaje: ConductorViaje,
    onBack: () -> Unit = {},
    onVerMapa: (lat: Double, lng: Double, titulo: String, subtitulo: String?) -> Unit = { _, _, _, _ -> },
    viewModel: ConductorDetalleViewModel = viewModel(
        key     = viaje.id,
        factory = ConductorDetalleViewModel.Factory(viaje)
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val viajeActual = uiState.viaje ?: viaje

    var mostrarDialogoIniciar   by remember { mutableStateOf(false) }
    var mostrarDialogoFinalizar by remember { mutableStateOf(false) }

    if (mostrarDialogoIniciar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoIniciar = false },
            icon = {
                Icon(
                    imageVector        = Icons.Filled.DirectionsBus,
                    contentDescription = null,
                    tint               = VictoriaYellow,
                    modifier           = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text       = "¿Iniciar viaje?",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp,
                    color      = VictoriaBlack
                )
            },
            text = {
                Text(
                    text     = "El viaje comenzará ahora y los pasajeros serán notificados.",
                    fontSize = 13.sp,
                    color    = Color(0xFF757575)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogoIniciar = false
                        viewModel.iniciarViaje()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VictoriaYellow,
                        contentColor   = VictoriaBlack
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Iniciar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoIniciar = false }) {
                    Text("Cancelar", color = VictoriaBlack, fontWeight = FontWeight.SemiBold)
                }
            },
            containerColor = Color.White,
            shape          = RoundedCornerShape(20.dp)
        )
    }

    if (mostrarDialogoFinalizar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoFinalizar = false },
            icon = {
                Icon(
                    imageVector        = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint               = VictoriaYellow,
                    modifier           = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text       = "¿Finalizar viaje?",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp,
                    color      = VictoriaBlack
                )
            },
            text = {
                Text(
                    text     = "El viaje se marcará como completado. Esta acción no se puede deshacer.",
                    fontSize = 13.sp,
                    color    = Color(0xFF757575)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogoFinalizar = false
                        viewModel.finalizarViaje()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VictoriaBlack,
                        contentColor   = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Finalizar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoFinalizar = false }) {
                    Text("Cancelar", color = VictoriaBlack, fontWeight = FontWeight.SemiBold)
                }
            },
            containerColor = Color.White,
            shape          = RoundedCornerShape(20.dp)
        )
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    val estadoActual = viajeActual.estadoViaje?.lowercase()?.trim() ?: ""
    val esProgramado = estadoActual == "programado"
    val eEnCurso     = estadoActual == "en curso"
    val esCompletado = estadoActual == "completado" || estadoActual == "cancelado"

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            // ── Barra superior ─────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(VictoriaYellow)
                    .padding(top = 40.dp, bottom = 16.dp, start = 8.dp, end = 20.dp)
            ) {
                IconButton(
                    onClick  = onBack,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint               = VictoriaBlack,
                        modifier           = Modifier.size(24.dp)
                    )
                }
                Text(
                    text       = "Detalle del viaje",
                    color      = VictoriaBlack,
                    fontSize   = 17.sp,
                    fontWeight = FontWeight.Bold,
                    modifier   = Modifier.align(Alignment.Center)
                )
                Icon(
                    imageVector        = Icons.Filled.DirectionsBus,
                    contentDescription = null,
                    tint               = VictoriaBlack.copy(alpha = 0.18f),
                    modifier           = Modifier
                        .size(36.dp)
                        .align(Alignment.CenterEnd)
                )
            }

            // ── Franja negra ───────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(VictoriaBlack)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ── Caja gris con info del viaje ───────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .background(Color(0xFFF0F0F0), RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                InfoRow(label = "Ruta",  value = viajeActual.nombreRuta  ?: "—")
                HorizontalDivider(color = Color(0xFFDDDDDD), modifier = Modifier.padding(vertical = 8.dp))
                InfoRow(label = "Fecha", value = viajeActual.fechaViaje  ?: "—")
                HorizontalDivider(color = Color(0xFFDDDDDD), modifier = Modifier.padding(vertical = 8.dp))
                InfoRow(
                    label = "Hora",
                    value = viajeActual.horaViajeInicio?.let { "${it}:00 hrs" } ?: "—"
                )
                HorizontalDivider(color = Color(0xFFDDDDDD), modifier = Modifier.padding(vertical = 8.dp))
                InfoRow(
                    label = "Estado",
                    value = etiquetaEstado(viajeActual.estadoViaje),
                    valueColor = colorEstado(viajeActual.estadoViaje)
                )
                val totalPax = viajeActual.totalPasajeros ?: viajeActual.pasajeros?.size ?: 0
                HorizontalDivider(color = Color(0xFFDDDDDD), modifier = Modifier.padding(vertical = 8.dp))
                InfoRow(label = "Pasajeros", value = "$totalPax")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Acciones del conductor ─────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {

                // Botón: Iniciar viaje
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick  = { mostrarDialogoIniciar = true },
                        enabled  = esProgramado && !uiState.isLoadingInicio,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape    = RoundedCornerShape(12.dp),
                        colors   = ButtonDefaults.buttonColors(
                            containerColor         = VictoriaYellow,
                            contentColor           = VictoriaBlack,
                            disabledContainerColor = VictoriaGray,
                            disabledContentColor   = Color(0xFF9E9E9E)
                        )
                    ) {
                        if (uiState.isLoadingInicio) {
                            CircularProgressIndicator(
                                color    = VictoriaBlack,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Iniciar viaje", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }

                    if (!viajeActual.fechaHoraInicio.isNullOrBlank()) {
                        Column(horizontalAlignment = Alignment.Start) {
                            Text(
                                text     = "Inicio:",
                                color    = Color(0xFF757575),
                                fontSize = 10.sp
                            )
                            Text(
                                text       = viajeActual.fechaHoraInicio,
                                color      = VictoriaBlack,
                                fontSize   = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Botón: Marcar viaje como completado
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick  = { mostrarDialogoFinalizar = true },
                        enabled  = eEnCurso && !uiState.isLoadingFin,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape    = RoundedCornerShape(12.dp),
                        colors   = ButtonDefaults.buttonColors(
                            containerColor         = VictoriaBlack,
                            contentColor           = Color.White,
                            disabledContainerColor = VictoriaGray,
                            disabledContentColor   = Color(0xFF9E9E9E)
                        )
                    ) {
                        if (uiState.isLoadingFin) {
                            CircularProgressIndicator(
                                color    = Color.White,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Viaje completado", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }

                    if (!viajeActual.fechaHoraFin.isNullOrBlank()) {
                        Column(horizontalAlignment = Alignment.Start) {
                            Text(
                                text     = "Fin:",
                                color    = Color(0xFF757575),
                                fontSize = 10.sp
                            )
                            Text(
                                text       = viajeActual.fechaHoraFin,
                                color      = VictoriaBlack,
                                fontSize   = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Tabla de pasajeros ─────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text       = "Pasajeros (${viajeActual.pasajeros?.size ?: 0})",
                    color      = VictoriaBlack,
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.4.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Encabezado tabla pasajeros
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(VictoriaGray)
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text     = "Nombre / Dirección",
                    color    = Color(0xFF757575),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(3f)
                )
                Text(
                    text     = "ID",
                    color    = Color(0xFF757575),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(2f)
                )
                Text(
                    text     = "Abordó",
                    color    = Color(0xFF757575),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
            }

            if (viajeActual.pasajeros.isNullOrEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text  = "Sin pasajeros asignados",
                        color = Color(0xFF9E9E9E),
                        fontSize = 13.sp
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Spacer(modifier = Modifier.height(6.dp))
                    viajeActual.pasajeros?.forEach { pasajero ->
                        FilaPasajero(
                            pasajero          = pasajero,
                            isLoadingAbordaje = uiState.loadingAbordaje.contains(pasajero.solicitudId),
                            esCompletado      = esCompletado,
                            onToggleAbordaje  = { viewModel.toggleAbordaje(pasajero.solicitudId) },
                            onVerMapa         = onVerMapa
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        // ── Snackbar ───────────────────────────────────────────────────────────
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

@Composable
private fun InfoRow(
    label: String,
    value: String,
    valueColor: Color = VictoriaBlack
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text     = label,
            color    = Color(0xFF757575),
            fontSize = 13.sp,
            modifier = Modifier.weight(1f)
        )
        Text(
            text       = value,
            color      = valueColor,
            fontSize   = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier  = Modifier.weight(2f),
            overflow  = TextOverflow.Ellipsis,
            maxLines  = 1
        )
    }
}

@Composable
private fun FilaPasajero(
    pasajero: PasajeroViaje,
    isLoadingAbordaje: Boolean,
    esCompletado: Boolean,
    onToggleAbordaje: () -> Unit,
    onVerMapa: (lat: Double, lng: Double, titulo: String, subtitulo: String?) -> Unit = { _, _, _, _ -> }
) {
    val u = pasajero.usuario
    val esEmpleado = u?.tipoUsuario == "1"
    val idLabel    = if (esEmpleado) "Carnet: ${u?.codigoCarnet ?: "—"}"
                     else             "DPI: ${u?.dpi ?: "—"}"
    val tipoLabel  = if (esEmpleado) "Empleado" else "En entrenamiento"

    val abordado        = pasajero.abordado ?: false
    val botonBgColor    = if (abordado) Color(0xFF4CAF50) else Color(0xFFE0E0E0)
    val botonIconColor  = if (abordado) Color.White       else Color(0xFF757575)

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // ── Columna 1: Nombre + Dirección + Contacto ───────────────────
                Column(modifier = Modifier.weight(3f)) {
                    Text(
                        text       = "${u?.name ?: "—"} ${u?.lastname ?: ""}".trim(),
                        color      = VictoriaBlack,
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines   = 1,
                        overflow   = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text     = u?.direccion1 ?: "—",
                        color    = Color(0xFF9E9E9E),
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text     = tipoLabel,
                        color    = Color(0xFF757575),
                        fontSize = 10.sp
                    )
                    if (!u?.email.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text     = u!!.email!!,
                            color    = Color(0xFF9E9E9E),
                            fontSize = 10.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    if (!u?.tel.isNullOrBlank()) {
                        Text(
                            text     = u!!.tel!!,
                            color    = Color(0xFF9E9E9E),
                            fontSize = 10.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // ── Columna 2: Tipo de ID ──────────────────────────────────────
                Column(modifier = Modifier.weight(2f)) {
                    Text(
                        text     = idLabel,
                        color    = VictoriaBlack,
                        fontSize = 11.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (!pasajero.horaAbordaje.isNullOrBlank() && abordado) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text     = pasajero.horaAbordaje,
                            color    = Color(0xFF9E9E9E),
                            fontSize = 9.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                // ── Columna 3: Botón abordar ───────────────────────────────────
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoadingAbordaje) {
                        CircularProgressIndicator(
                            color    = VictoriaYellow,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        IconButton(
                            onClick  = onToggleAbordaje,
                            enabled  = !esCompletado,
                            modifier = Modifier
                                .size(36.dp)
                                .background(botonBgColor, CircleShape)
                        ) {
                            Icon(
                                imageVector = if (abordado) Icons.Filled.HowToReg
                                              else          Icons.Filled.PersonAdd,
                                contentDescription = if (abordado) "Desmarcar abordaje" else "Marcar abordaje",
                                tint     = botonIconColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // ── Botón "Ver en mapa" (solo si el pasajero tiene coordenadas) ──
            if (u?.lat != null && u.lng != null) {
                HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 0.5.dp)
                TextButton(
                    onClick  = {
                        val nombre = "${u.name ?: ""} ${u.lastname ?: ""}".trim()
                        onVerMapa(u.lat, u.lng, nombre, u.direccion1)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector        = Icons.Filled.Map,
                        contentDescription = null,
                        tint               = VictoriaYellow,
                        modifier           = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text       = "Ver en mapa",
                        fontSize   = 12.sp,
                        color      = VictoriaYellow,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
