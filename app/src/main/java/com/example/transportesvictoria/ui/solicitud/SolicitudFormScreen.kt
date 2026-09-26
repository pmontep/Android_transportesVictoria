package com.pointguatemala.transportesvictoria.ui.solicitud

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pointguatemala.transportesvictoria.R
import com.pointguatemala.transportesvictoria.ui.theme.VictoriaBlack
import com.pointguatemala.transportesvictoria.ui.theme.VictoriaDivider
import com.pointguatemala.transportesvictoria.ui.theme.VictoriaGray
import com.pointguatemala.transportesvictoria.ui.theme.VictoriaGrayMid
import com.pointguatemala.transportesvictoria.ui.theme.VictoriaYellow

// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudFormScreen(
    onVolver: () -> Unit,
    onSolicitudCreada: () -> Unit,
    viewModel: SolicitudFormViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Error en snackbar
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    // ── Diálogo: ya tienes un viaje en tránsito ───────────────────────────────
    if (uiState.tieneViajeActivo) {
        AlertDialog(
            onDismissRequest = { viewModel.clearViajeActivo() },
            icon = {
                Icon(
                    imageVector        = Icons.Filled.DirectionsBus,
                    contentDescription = null,
                    tint               = VictoriaYellow,
                    modifier           = Modifier.size(40.dp)
                )
            },
            title = {
                Text(
                    text       = "Viaje en tránsito",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp,
                    color      = VictoriaBlack
                )
            },
            text = {
                Text(
                    text     = uiState.mensajeViajeActivo,
                    fontSize = 13.sp,
                    color    = Color(0xFF757575)
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.clearViajeActivo() },
                    colors  = ButtonDefaults.buttonColors(
                        containerColor = VictoriaYellow,
                        contentColor   = VictoriaBlack
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Entendido", fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color.White,
            shape          = RoundedCornerShape(20.dp)
        )
    }

    // ── Diálogo de éxito al crear ─────────────────────────────────────────────
    if (uiState.solicitudCreada) {
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
                    text       = "¡Solicitud creada!",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp,
                    color      = VictoriaBlack
                )
            },
            text = {
                Text(
                    text     = "Tu solicitud de viaje se ha creado exitosamente.",
                    fontSize = 13.sp,
                    color    = Color(0xFF757575)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.onSolicitudCreadaConsumed()
                        onSolicitudCreada()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VictoriaYellow,
                        contentColor   = VictoriaBlack
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

            // ── Logo + botón volver ───────────────────────────────────────────
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
                    onClick  = onVolver,
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

            // ── Título ────────────────────────────────────────────────────────
            Text(
                text       = "Nueva solicitud de viaje",
                color      = VictoriaBlack,
                fontSize   = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier   = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ── Contenido del formulario ──────────────────────────────────────
            Card(
                modifier  = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape     = RoundedCornerShape(16.dp),
                colors    = CardDefaults.cardColors(containerColor = VictoriaGray),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    when {
                        // ── Cargando rutas ────────────────────────────────────
                        uiState.isLoadingRutas -> {
                            Box(
                                modifier            = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp),
                                contentAlignment    = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator(
                                        color       = VictoriaYellow,
                                        modifier    = Modifier.size(36.dp),
                                        strokeWidth = 3.dp
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text     = "Cargando rutas disponibles…",
                                        color    = VictoriaGrayMid,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }

                        // ── Error al cargar rutas ─────────────────────────────
                        uiState.errorRutas != null -> {
                            Box(
                                modifier         = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text     = uiState.errorRutas ?: "No se pudieron cargar las rutas",
                                        color    = Color(0xFFEF5350),
                                        fontSize = 13.sp
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Button(
                                        onClick = { viewModel.cargarRutas() },
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

                        // ── Sin rutas asignadas ───────────────────────────────
                        uiState.rutas.isEmpty() -> {
                            Box(
                                modifier         = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text     = "No tienes rutas asignadas.",
                                    color    = VictoriaGrayMid,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        // ── Formulario ────────────────────────────────────────
                        else -> {
                            FormularioContenido(
                                uiState   = uiState,
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Botón enviar (solo visible si hay rutas) ──────────────────────
            if (!uiState.isLoadingRutas && uiState.errorRutas == null && uiState.rutas.isNotEmpty()) {
                Button(
                    onClick  = { viewModel.crearSolicitud() },
                    enabled  = !uiState.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .padding(horizontal = 20.dp),
                    shape  = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor         = VictoriaYellow,
                        contentColor           = VictoriaBlack,
                        disabledContainerColor = VictoriaYellow.copy(alpha = 0.5f),
                        disabledContentColor   = VictoriaBlack.copy(alpha = 0.4f)
                    )
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            color       = VictoriaBlack,
                            modifier    = Modifier.size(22.dp),
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Text(
                            text       = "Enviar solicitud",
                            fontSize   = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            // ── Botón cancelar ────────────────────────────────────────────────
            Button(
                onClick  = onVolver,
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
                    text       = "Cancelar",
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

// ── Contenido del formulario (rutas + horarios + observaciones) ───────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormularioContenido(
    uiState: SolicitudFormUiState,
    viewModel: SolicitudFormViewModel
) {
    var rutaExpandida by remember { mutableStateOf(false) }

    // ── Selector de ruta ──────────────────────────────────────────────────────
    FormLabel(texto = "Ruta", requerido = true)
    Spacer(modifier = Modifier.height(6.dp))

    ExposedDropdownMenuBox(
        expanded          = rutaExpandida,
        onExpandedChange  = { rutaExpandida = it }
    ) {
        OutlinedTextField(
            value         = uiState.rutaSeleccionada?.nombre ?: "",
            onValueChange = {},
            readOnly      = true,
            placeholder   = {
                Text("Selecciona una ruta", color = VictoriaGrayMid, fontSize = 13.sp)
            },
            trailingIcon  = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = rutaExpandida)
            },
            isError        = uiState.rutaError,
            supportingText = if (uiState.rutaError) {
                { Text("Selecciona una ruta", color = Color(0xFFEF5350), fontSize = 11.sp) }
            } else null,
            modifier = Modifier
                .menuAnchor(type = MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth(),
            shape  = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor      = VictoriaYellow,
                unfocusedBorderColor    = VictoriaDivider,
                focusedContainerColor   = Color.White,
                unfocusedContainerColor = Color.White,
                errorContainerColor     = Color.White
            )
        )

        ExposedDropdownMenu(
            expanded          = rutaExpandida,
            onDismissRequest  = { rutaExpandida = false },
            containerColor    = Color.White
        ) {
            uiState.rutas.forEach { ruta ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(
                                text       = ruta.nombre,
                                color      = VictoriaBlack,
                                fontSize   = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            if (!ruta.direccion.isNullOrBlank()) {
                                Text(
                                    text     = "${ruta.direccion} · ${ruta.departamento ?: ""}".trimEnd(' ', '·'),
                                    color    = VictoriaGrayMid,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    },
                    onClick = {
                        viewModel.seleccionarRuta(ruta)
                        rutaExpandida = false
                    }
                )
                HorizontalDivider(color = VictoriaDivider, thickness = 0.5.dp)
            }
        }
    }

    // ── Horarios de la ruta seleccionada ──────────────────────────────────────
    uiState.rutaSeleccionada?.let { ruta ->
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = VictoriaDivider, thickness = 0.5.dp)
        Spacer(modifier = Modifier.height(16.dp))

        FormLabel(texto = "Horario disponible", requerido = true)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier            = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ruta.horarios.forEach { hora ->
                val seleccionada = uiState.horaSeleccionada == hora
                FilterChip(
                    selected = seleccionada,
                    onClick  = { viewModel.seleccionarHora(hora) },
                    label    = {
                        Text(
                            text       = hora,
                            fontSize   = 13.sp,
                            fontWeight = if (seleccionada) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor  = VictoriaYellow,
                        selectedLabelColor      = VictoriaBlack,
                        containerColor          = Color.White,
                        labelColor              = VictoriaGrayMid
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled              = true,
                        selected             = seleccionada,
                        borderColor          = VictoriaDivider,
                        selectedBorderColor  = VictoriaYellow,
                        borderWidth          = 1.dp,
                        selectedBorderWidth  = 1.5.dp
                    )
                )
            }
        }

        if (uiState.horaError) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text     = "Selecciona un horario",
                color    = Color(0xFFEF5350),
                fontSize = 11.sp
            )
        }
    }

    // ── Observaciones ─────────────────────────────────────────────────────────
    Spacer(modifier = Modifier.height(16.dp))
    HorizontalDivider(color = VictoriaDivider, thickness = 0.5.dp)
    Spacer(modifier = Modifier.height(16.dp))

    FormLabel(texto = "Observaciones", requerido = false)
    Spacer(modifier = Modifier.height(6.dp))

    OutlinedTextField(
        value         = uiState.observaciones,
        onValueChange = viewModel::setObservaciones,
        placeholder   = {
            Text("Opcional", color = VictoriaGrayMid, fontSize = 13.sp)
        },
        minLines = 2,
        maxLines = 4,
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(10.dp),
        colors   = OutlinedTextFieldDefaults.colors(
            focusedBorderColor      = VictoriaYellow,
            unfocusedBorderColor    = VictoriaDivider,
            focusedContainerColor   = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}

// ── Etiqueta de campo ─────────────────────────────────────────────────────────
@Composable
private fun FormLabel(texto: String, requerido: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text       = texto,
            color      = VictoriaBlack,
            fontSize   = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
        if (requerido) {
            Spacer(modifier = Modifier.width(3.dp))
            Text(text = "*", color = Color(0xFFEF5350), fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}
