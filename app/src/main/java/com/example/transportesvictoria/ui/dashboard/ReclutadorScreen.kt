package com.pointguatemala.transportesvictoria.ui.dashboard

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pointguatemala.transportesvictoria.R
import com.pointguatemala.transportesvictoria.data.model.ReclutadorUsuario
import com.pointguatemala.transportesvictoria.data.model.User
import com.pointguatemala.transportesvictoria.ui.theme.VictoriaBlack
import com.pointguatemala.transportesvictoria.ui.theme.VictoriaGray
import com.pointguatemala.transportesvictoria.ui.theme.VictoriaGrayMid
import com.pointguatemala.transportesvictoria.ui.theme.VictoriaYellow
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReclutadorScreen(
    user: User,
    viewModel: ReclutadorViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current

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

    // Campo de búsqueda local con debounce de 300 ms
    var draftQuery by remember { mutableStateOf("") }
    LaunchedEffect(draftQuery) {
        delay(300)
        if (draftQuery != uiState.query) {
            viewModel.cargarUsuarios(query = draftQuery)
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {

            // ── Logo superior ──────────────────────────────────────────────────
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

            // ── Franja amarilla ────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(VictoriaYellow)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ── Buscador ───────────────────────────────────────────────────────
            OutlinedTextField(
                value = draftQuery,
                onValueChange = { draftQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                placeholder = {
                    Text("Buscar por nombre, apellido o correo", fontSize = 13.sp, color = VictoriaGrayMid)
                },
                leadingIcon = {
                    Icon(Icons.Filled.Search, contentDescription = null, tint = VictoriaGrayMid, modifier = Modifier.size(20.dp))
                },
                trailingIcon = {
                    if (draftQuery.isNotEmpty()) {
                        IconButton(onClick = {
                            draftQuery = ""
                            viewModel.cargarUsuarios(query = "")
                            focusManager.clearFocus()
                        }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Limpiar", tint = VictoriaGrayMid, modifier = Modifier.size(18.dp))
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    viewModel.cargarUsuarios(query = draftQuery)
                    focusManager.clearFocus()
                }),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = VictoriaYellow,
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    cursorColor          = VictoriaBlack
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ── Filtros de estado + botón recargar ────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FiltroChip(
                    label    = "Inactivos",
                    selected = uiState.statusFilter == "0",
                    onClick  = { viewModel.cargarUsuarios(query = draftQuery, status = "0") }
                )
                FiltroChip(
                    label    = "Activos",
                    selected = uiState.statusFilter == "1",
                    onClick  = { viewModel.cargarUsuarios(query = draftQuery, status = "1") }
                )
                FiltroChip(
                    label    = "Todos",
                    selected = uiState.statusFilter == "",
                    onClick  = { viewModel.cargarUsuarios(query = draftQuery, status = "") }
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick  = { viewModel.cargarUsuarios() },
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

            Spacer(modifier = Modifier.height(10.dp))

            // ── Título + contador ──────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text       = "Usuarios en entrenamiento",
                    color      = VictoriaBlack,
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.4.sp,
                    modifier   = Modifier.weight(1f)
                )
                if (uiState.total > 0) {
                    Text(
                        text     = "${uiState.total} registros",
                        color    = VictoriaGrayMid,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── Encabezado de tabla ────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(VictoriaGray)
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Nombre / Correo",  color = Color(0xFF757575), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(3f))
                Text("DPI / Ruta",       color = Color(0xFF757575), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(2.5f))
                Text("Acción",           color = Color(0xFF757575), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center, modifier = Modifier.weight(1.8f))
            }

            // ── Contenido principal ────────────────────────────────────────────
            PullToRefreshBox(
                isRefreshing = uiState.isRefreshing,
                onRefresh    = { viewModel.cargarUsuarios(fromRefresh = true) },
                modifier     = Modifier.fillMaxSize()
            ) {

                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(
                            color    = VictoriaYellow,
                            modifier = Modifier.size(48.dp).align(Alignment.Center)
                        )
                    }

                    uiState.usuarios.isEmpty() && !uiState.isLoading -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text  = "No se encontraron usuarios",
                                color = VictoriaGrayMid,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { viewModel.cargarUsuarios() },
                                colors  = ButtonDefaults.buttonColors(containerColor = VictoriaYellow, contentColor = VictoriaBlack),
                                shape   = RoundedCornerShape(10.dp)
                            ) {
                                Text("Reintentar", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item { Spacer(modifier = Modifier.height(4.dp)) }

                            items(
                                items = uiState.usuarios,
                                key   = { it.id }
                            ) { usuario ->
                                FilaUsuarioReclutador(
                                    usuario     = usuario,
                                    isToggling  = uiState.togglingIds.contains(usuario.id),
                                    onToggle    = { viewModel.toggleStatus(usuario.id) }
                                )
                            }

                            // Botón "Cargar más"
                            if (uiState.currentPage < uiState.lastPage) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (uiState.isLoadingMore) {
                                            CircularProgressIndicator(color = VictoriaYellow, modifier = Modifier.size(32.dp), strokeWidth = 3.dp)
                                        } else {
                                            Button(
                                                onClick = { viewModel.cargarMas() },
                                                colors  = ButtonDefaults.buttonColors(containerColor = VictoriaGray, contentColor = VictoriaBlack),
                                                shape   = RoundedCornerShape(10.dp)
                                            ) {
                                                Text("Cargar más", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                            }
                                        }
                                    }
                                }
                            }

                            item { Spacer(modifier = Modifier.height(16.dp)) }
                        }
                    }
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

// ── Chip de filtro ────────────────────────────────────────────────────────────
@Composable
private fun FiltroChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Button(
        onClick  = onClick,
        shape    = RoundedCornerShape(20.dp),
        colors   = ButtonDefaults.buttonColors(
            containerColor = if (selected) VictoriaYellow else VictoriaGray,
            contentColor   = if (selected) VictoriaBlack  else Color(0xFF757575)
        ),
        modifier = Modifier.height(32.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 0.dp)
    ) {
        Text(text = label, fontSize = 12.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
    }
}

// ── Fila de usuario ───────────────────────────────────────────────────────────
@Composable
private fun FilaUsuarioReclutador(
    usuario: ReclutadorUsuario,
    isToggling: Boolean,
    onToggle: () -> Unit
) {
    val activo      = usuario.status == "1"
    val statusColor = if (activo) Color(0xFF4CAF50) else Color(0xFFEF5350)
    val statusLabel = if (activo) "Activo" else "Inactivo"
    var mostrarDialogo by remember { mutableStateOf(false) }

    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = {
                Text(
                    text       = if (activo) "¿Suspender usuario?" else "¿Activar usuario?",
                    fontWeight = FontWeight.Bold,
                    color      = VictoriaBlack
                )
            },
            text = {
                val nombre = "${usuario.name ?: ""} ${usuario.lastname ?: ""}".trim()
                Text(
                    text  = if (activo) "Se suspenderá el acceso de $nombre." else "Se activará el acceso de $nombre.",
                    color = VictoriaBlack
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogo = false
                        onToggle()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (activo) Color(0xFFEF5350) else VictoriaYellow,
                        contentColor   = if (activo) Color.White else VictoriaBlack
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text       = if (activo) "Suspender" else "Activar",
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogo = false }) {
                    Text("Cancelar", color = VictoriaBlack)
                }
            }
        )
    }

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // ── Col 1: Nombre + Email ──────────────────────────────────
                Column(modifier = Modifier.weight(3f)) {
                    Text(
                        text       = "${usuario.name ?: "—"} ${usuario.lastname ?: ""}".trim(),
                        color      = VictoriaBlack,
                        fontSize   = 13.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines   = 1,
                        overflow   = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text     = usuario.email ?: "—",
                        color    = Color(0xFF9E9E9E),
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    // Chip de estado
                    Box(
                        modifier = Modifier
                            .background(statusColor.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(text = statusLabel, color = statusColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // ── Col 2: DPI + Ruta ─────────────────────────────────────
                Column(modifier = Modifier.weight(2.5f)) {
                    Text(
                        text     = "DPI: ${usuario.dpi?.takeIf { it.isNotBlank() } ?: "—"}",
                        color    = VictoriaBlack,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text     = usuario.ruta?.takeIf { it.isNotBlank() } ?: "Sin ruta",
                        color    = Color(0xFF757575),
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (!usuario.direccion1.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text     = usuario.direccion1,
                            color    = Color(0xFFBDBDBD),
                            fontSize = 10.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // ── Col 3: Botón toggle ────────────────────────────────────
                Box(
                    modifier = Modifier.weight(1.8f),
                    contentAlignment = Alignment.Center
                ) {
                    if (isToggling) {
                        CircularProgressIndicator(
                            color       = VictoriaYellow,
                            modifier    = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Button(
                            onClick  = { mostrarDialogo = true },
                            shape    = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors   = ButtonDefaults.buttonColors(
                                containerColor = if (activo) Color(0xFFEF5350) else VictoriaYellow,
                                contentColor   = if (activo) Color.White else VictoriaBlack
                            ),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text       = if (activo) "Suspender" else "Activar",
                                fontSize   = 11.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines   = 1
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 0.5.dp)
        }
    }
}
