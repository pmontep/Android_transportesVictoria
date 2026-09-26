package com.pointguatemala.transportesvictoria.ui.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.outlined.StarOutline
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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.rememberBottomSheetScaffoldState
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.pointguatemala.transportesvictoria.BuildConfig
import com.pointguatemala.transportesvictoria.R
import com.pointguatemala.transportesvictoria.data.model.SolicitudViaje
import com.pointguatemala.transportesvictoria.ui.theme.VictoriaBlack
import com.pointguatemala.transportesvictoria.ui.theme.VictoriaDivider
import com.pointguatemala.transportesvictoria.ui.theme.VictoriaGray
import com.pointguatemala.transportesvictoria.ui.theme.VictoriaGrayMid
import com.pointguatemala.transportesvictoria.ui.theme.VictoriaYellow

private val ColorRojo = Color(0xFFEF5350)

// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun DetalleSolicitudScreen(
    solicitudIdL: String,
    onBack: () -> Unit,
    onCancelada: () -> Unit = {},
    viewModel: DetalleSolicitudViewModel = viewModel(
        key     = "detalle_$solicitudIdL",
        factory = DetalleSolicitudViewModel.Factory(solicitudIdL)
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var mostrarDialogoCancelar  by remember { mutableStateOf(false) }
    var mostrarDialogoCalificar by remember { mutableStateOf(false) }

    // Cerrar diálogo de calificación al terminar exitosamente
    LaunchedEffect(uiState.calificacionExitosa) {
        if (uiState.calificacionExitosa) {
            mostrarDialogoCalificar = false
            viewModel.onCalificacionExitosaConsumed()
        }
    }

    if (mostrarDialogoCalificar) {
        CalificarViajeDialog(
            isLoading = uiState.isCalificating,
            onEnviar  = { estrellas, comentario ->
                viewModel.calificarViaje(estrellas, comentario)
            },
            onCancelar = { mostrarDialogoCalificar = false }
        )
    }

    // Errores en snackbar
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    // ── Diálogo de éxito al cancelar ─────────────────────────────────────────
    if (uiState.solicitudCancelada) {
        AlertDialog(
            onDismissRequest = { /* Requiere confirmación explícita */ },
            icon = {
                Icon(
                    imageVector        = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint               = Color(0xFF4CAF50),
                    modifier           = Modifier.size(40.dp)
                )
            },
            title = {
                Text(
                    text       = "Solicitud cancelada",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp,
                    color      = VictoriaBlack
                )
            },
            text = {
                Text(
                    text      = "Tu solicitud de viaje ha sido cancelada exitosamente.",
                    fontSize  = 13.sp,
                    color     = Color(0xFF757575),
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.onCanceladaConsumed()
                        onCancelada()
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

    // ── Diálogo de confirmación ───────────────────────────────────────────────
    if (mostrarDialogoCancelar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoCancelar = false },
            icon = {
                Icon(
                    imageVector        = Icons.Filled.WarningAmber,
                    contentDescription = null,
                    tint               = ColorRojo,
                    modifier           = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text       = "¿Cancelar solicitud?",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp,
                    color      = VictoriaBlack
                )
            },
            text = {
                Text(
                    text      = "Esta acción no se puede deshacer. La solicitud quedará cancelada y no podrá ser recuperada.",
                    fontSize  = 13.sp,
                    color     = VictoriaGrayMid,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogoCancelar = false
                        viewModel.cancelarSolicitud()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ColorRojo,
                        contentColor   = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Sí, cancelar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoCancelar = false }) {
                    Text("Mantener", color = VictoriaBlack, fontWeight = FontWeight.SemiBold)
                }
            },
            containerColor = Color.White,
            shape          = RoundedCornerShape(20.dp)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            // ── Logo + barra de navegación ────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                Image(
                    painter            = painterResource(id = R.drawable.logo_tv),
                    contentDescription = "Transportes Victoria",
                    contentScale       = ContentScale.Fit,
                    modifier           = Modifier
                        .fillMaxWidth(0.60f)
                        .height(110.dp)
                        .padding(top = 32.dp, bottom = 10.dp)
                        .align(Alignment.Center)
                )
                IconButton(
                    onClick  = onBack,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 28.dp, start = 8.dp)
                ) {
                    Icon(
                        imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint               = VictoriaBlack,
                        modifier           = Modifier.size(26.dp)
                    )
                }
            }

            // ── Franja amarilla ───────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(VictoriaYellow)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ── Título + botón recargar ───────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text       = "Detalle de solicitud de viaje",
                    color      = VictoriaBlack,
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(
                    onClick  = { viewModel.cargarDetalle() },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector        = Icons.Filled.Refresh,
                        contentDescription = "Recargar",
                        tint               = VictoriaBlack,
                        modifier           = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Contenido ─────────────────────────────────────────────────────
            when {
                uiState.isLoading -> {
                    Box(
                        modifier         = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = VictoriaYellow)
                    }
                }

                uiState.solicitud == null -> {
                    Box(
                        modifier         = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text     = "No se pudo cargar la solicitud",
                                color    = VictoriaGrayMid,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { viewModel.cargarDetalle() },
                                colors  = ButtonDefaults.buttonColors(
                                    containerColor = VictoriaYellow,
                                    contentColor   = VictoriaBlack
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Reintentar", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                else -> {
                    uiState.solicitud?.let { DetalleContenido(it) }
                }
            }

            // ── Card de calificación (solo si viaje completado) ───────────────
            uiState.solicitud?.let { sol ->
                val viajeCompletado = sol.estadoViaje?.trim()?.lowercase().let {
                    it == "completado" || it == "finalizado"
                }
                if (viajeCompletado) {
                    Spacer(modifier = Modifier.height(16.dp))
                    CardCalificacion(
                        calificacion           = sol.calificacion,
                        comentarioCalificacion = sol.comentarioCalificacion,
                        onCalificar            = { mostrarDialogoCalificar = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Botón cancelar solicitud (solo si aún se puede cancelar) ──────
            val puedeCancel = uiState.solicitud?.let { puedeCancelar(it) } == true

            if (puedeCancel) {
                Button(
                    onClick  = { mostrarDialogoCancelar = true },
                    enabled  = !uiState.isCanceling,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .padding(horizontal = 20.dp),
                    shape  = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor         = ColorRojo,
                        contentColor           = Color.White,
                        disabledContainerColor = ColorRojo.copy(alpha = 0.5f),
                        disabledContentColor   = Color.White.copy(alpha = 0.6f)
                    )
                ) {
                    if (uiState.isCanceling) {
                        CircularProgressIndicator(
                            color       = Color.White,
                            modifier    = Modifier.size(22.dp),
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Text(
                            text       = "Cancelar solicitud",
                            fontSize   = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            // ── Botón Volver ──────────────────────────────────────────────────
            Button(
                onClick  = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(horizontal = 20.dp),
                shape  = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = VictoriaBlack,
                    contentColor   = Color.White
                )
            ) {
                Icon(
                    imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    modifier           = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text       = "Volver a mis solicitudes",
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        // ── Snackbar ──────────────────────────────────────────────────────────
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

// ── Determina si la solicitud aún puede ser cancelada ────────────────────────
private fun puedeCancelar(solicitud: SolicitudViaje): Boolean {
    val estado = solicitud.estadoSolicitud?.trim()?.lowercase()
    // No se puede cancelar si ya está en estado final
    val estadosFinales = setOf("cancelado", "cancelada", "inactiva", "rechazado")
    if (estado in estadosFinales) return false

    // No se puede cancelar si el viaje ya está en curso o finalizado
    val estadoViaje = solicitud.estadoViaje?.trim()?.lowercase()
    val viajeActivo = setOf("en curso", "en camino", "en_camino", "completado", "finalizado")
    if (estadoViaje in viajeActivo) return false

    return true
}

// ── Contenido de detalle ──────────────────────────────────────────────────────
@Composable
private fun DetalleContenido(solicitud: SolicitudViaje) {

    val esCancelada = solicitud.estadoSolicitud?.trim()?.lowercase().let {
        it == "cancelado" || it == "cancelada" || it == "inactiva"
    }
    val colorSolicitud = colorEstado(solicitud.estadoSolicitud)

    Column(modifier = Modifier.padding(horizontal = 20.dp)) {

        // ── Chip: estado de la solicitud ──────────────────────────────────────
        Box(
            modifier = Modifier
                .background(
                    color = colorSolicitud.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 14.dp, vertical = 5.dp)
        ) {
            Text(
                text       = "Solicitud: ${etiquetaEstado(solicitud.estadoSolicitud)}",
                color      = colorSolicitud,
                fontSize   = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Tarjeta: Datos del viaje (solo si NO está cancelada) ──────────────
        if (!esCancelada) {
            SeccionCard(titulo = "Datos del viaje") {
                DetalleItem("Ruta", solicitud.rutaSolicitada ?: "—")

                if (!solicitud.conductorNombre.isNullOrBlank()) {
                    ConductorRow(
                        nombre = solicitud.conductorNombre,
                        photoPath = solicitud.profilePhotoPath
                    )
                } else {
                    DetalleItem("Conductor", "Por asignar")
                }

                if (!solicitud.estadoViaje.isNullOrBlank()) {
                    DetalleItem("Estado del viaje", etiquetaEstado(solicitud.estadoViaje))
                }

                DetalleItem("Abordó", if (solicitud.abordado == "1" || solicitud.abordado == 1.toString()) "Sí" else "No")

                if (!solicitud.horaAbordaje.isNullOrBlank()) {
                    DetalleItem("Hora de abordaje", solicitud.horaAbordaje.take(5))
                }

                if (!solicitud.fechaHoraInicio.isNullOrBlank()) {
                    DetalleItem("Hora de inicio", extraerHora(solicitud.fechaHoraInicio))
                }

                val horaFin = when {
                    !solicitud.fechaHoraFin.isNullOrBlank() -> extraerHora(solicitud.fechaHoraFin)
                    !solicitud.horaViajeFin.isNullOrBlank()  -> solicitud.horaViajeFin.take(5)
                    else                                     -> null
                }
                if (horaFin != null) DetalleItem("Hora de fin", horaFin)

                if (!solicitud.observaciones.isNullOrBlank()) {
                    DetalleItem("Observaciones", solicitud.observaciones)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // ── Tarjeta: Información de la solicitud ──────────────────────────────
        SeccionCard(titulo = "Información de la solicitud") {
            val fechaHoraSolicitud = buildString {
                if (!solicitud.fechaSolicitud.isNullOrBlank()) append(solicitud.fechaSolicitud)
                if (!solicitud.horaSolicitud.isNullOrBlank()) {
                    if (isNotEmpty()) append("  ")
                    append(solicitud.horaSolicitud.take(5))
                }
            }.ifBlank { "—" }

            DetalleItem("Solicitado el", fechaHoraSolicitud)

            if (!solicitud.updatedAt.isNullOrBlank() && solicitud.updatedAt != solicitud.createdAt) {
                DetalleItem("Actualizado el", formatearFechaCorta(solicitud.updatedAt))
            }

            DetalleItem("Estado", etiquetaEstado(solicitud.estadoSolicitud))
        }
    }
}

// ── Helpers de formato ────────────────────────────────────────────────────────

private fun extraerHora(fechaHora: String): String {
    val timePart = when {
        fechaHora.contains("T") -> fechaHora.substringAfter("T").take(5)
        fechaHora.contains(" ") -> fechaHora.substringAfter(" ").take(5)
        else                    -> fechaHora.take(5)
    }
    return if (timePart.matches(Regex("\\d{2}:\\d{2}"))) timePart else fechaHora.take(5)
}

private fun formatearFechaCorta(fechaHora: String): String =
    fechaHora.replace("T", " ").take(16).ifBlank { fechaHora }

// ── Card de sección ───────────────────────────────────────────────────────────
@Composable
private fun SeccionCard(titulo: String, content: @Composable () -> Unit) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = VictoriaGray),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text          = titulo,
                color         = VictoriaBlack,
                fontSize      = 12.sp,
                fontWeight    = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}

// ── Card de calificación ──────────────────────────────────────────────────────
@Composable
private fun CardCalificacion(
    calificacion: Int?,
    comentarioCalificacion: String?,
    onCalificar: () -> Unit
) {
    SeccionCard(titulo = "Tu calificación") {
        if (calificacion != null) {
            // Ya calificó: mostrar estrellas + comentario
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier          = Modifier.padding(vertical = 4.dp)
            ) {
                repeat(5) { index ->
                    Icon(
                        imageVector        = if (index < calificacion) Icons.Filled.Star
                                             else Icons.Outlined.StarOutline,
                        contentDescription = null,
                        tint               = if (index < calificacion) VictoriaYellow
                                             else VictoriaGrayMid,
                        modifier           = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text       = "$calificacion / 5",
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = VictoriaBlack
                )
            }
            if (!comentarioCalificacion.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text     = comentarioCalificacion,
                    fontSize = 13.sp,
                    color    = VictoriaGrayMid
                )
            }
        } else {
            // No ha calificado: mostrar botón
            Button(
                onClick  = onCalificar,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape  = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = VictoriaYellow,
                    contentColor   = VictoriaBlack
                )
            ) {
                Icon(
                    imageVector        = Icons.Filled.Star,
                    contentDescription = null,
                    modifier           = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Calificar viaje", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

// ── Fila del conductor con foto circular ──────────────────────────────────────
@Composable
private fun ConductorRow(nombre: String, photoPath: String?) {
    var mostrarFotoAmpliada = remember { mutableStateOf(false) }

    val photoUrl = when {
        photoPath.isNullOrBlank() -> {
            android.util.Log.d("ConductorRow", "photoPath es null o vacío")
            null
        }
        photoPath.startsWith("http") -> {
            android.util.Log.d("ConductorRow", "photoPath es URL absoluta: $photoPath")
            photoPath
        }
        else -> {
            val url = "${BuildConfig.BASE_URL}storage/$photoPath"
            android.util.Log.d("ConductorRow", "photoPath relativo: $photoPath → URL: $url")
            url
        }
    }

    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(
            text     = "Conductor",
            color    = Color(0xFF757575),
            fontSize = 13.sp,
            modifier = Modifier.weight(1f)
        )

        Row(
            modifier          = Modifier
                .weight(1.5f)
                .padding(start = 8.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(VictoriaGrayMid)
                    .clickable(enabled = !photoUrl.isNullOrBlank()) {
                        mostrarFotoAmpliada.value = true
                    }
            ) {
                if (!photoUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = photoUrl,
                        contentDescription = "Foto del conductor",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        onSuccess = {
                            android.util.Log.d("ConductorRow", "✓ Imagen cargada exitosamente: $photoUrl")
                        },
                        onError = { state ->
                            android.util.Log.e("ConductorRow", "✗ Error cargando imagen: $photoUrl - ${state.result.throwable}")
                        }
                    )
                } else {
                    Icon(
                        imageVector        = Icons.Filled.Person,
                        contentDescription = "Sin foto",
                        tint               = Color.White,
                        modifier           = Modifier
                            .size(18.dp)
                            .align(Alignment.Center)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text       = nombre,
                color      = VictoriaBlack,
                fontSize   = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
    HorizontalDivider(color = VictoriaDivider, thickness = 0.5.dp)

    // Modal para ampliar foto
    if (mostrarFotoAmpliada.value && !photoUrl.isNullOrBlank()) {
        AlertDialog(
            onDismissRequest = { mostrarFotoAmpliada.value = false },
            title = {
                Text(
                    text = nombre,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = photoUrl,
                        contentDescription = "Foto ampliada del conductor",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(250.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { mostrarFotoAmpliada.value = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VictoriaBlack,
                        contentColor = Color.White
                    )
                ) {
                    Text("Cerrar")
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

// ── Fila de dato individual ───────────────────────────────────────────────────
@Composable
private fun DetalleItem(label: String, value: String) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.Top
    ) {
        Text(
            text     = label,
            color    = Color(0xFF757575),
            fontSize = 13.sp,
            modifier = Modifier.weight(1f)
        )
        Text(
            text       = value.ifBlank { "—" },
            color      = VictoriaBlack,
            fontSize   = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier   = Modifier
                .weight(1.5f)
                .padding(start = 8.dp)
        )
    }
    HorizontalDivider(color = VictoriaDivider, thickness = 0.5.dp)
}
