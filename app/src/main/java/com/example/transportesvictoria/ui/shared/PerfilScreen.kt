package com.pointguatemala.transportesvictoria.ui.shared

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pointguatemala.transportesvictoria.R
import com.pointguatemala.transportesvictoria.data.model.User
import com.pointguatemala.transportesvictoria.ui.theme.VictoriaBlack
import com.pointguatemala.transportesvictoria.ui.theme.VictoriaDivider
import com.pointguatemala.transportesvictoria.ui.theme.VictoriaGray
import com.pointguatemala.transportesvictoria.ui.theme.VictoriaYellow

private val ColorRojo = Color(0xFFEF5350)

@Composable
fun PerfilScreen(
    user: User,
    onCambiarPassword: () -> Unit = {},
    onCerrarSesion: () -> Unit = {},
    onVerMapa: (lat: Double, lng: Double, titulo: String, subtitulo: String?) -> Unit = { _, _, _, _ -> }
) {
    val perfilViewModel: PerfilViewModel = viewModel(factory = PerfilViewModel.Factory(user))
    val uiState by perfilViewModel.uiState.collectAsState()
    val displayUser = uiState.user

    val snackbarHostState = remember { SnackbarHostState() }
    var mostrarDialogoCerrarSesion by remember { mutableStateOf(false) }
    var mostrarDialogoEliminar     by remember { mutableStateOf(false) }
    var mostrarDialogoExitoElim    by remember { mutableStateOf(false) }
    var motivoTexto                by remember { mutableStateOf("") }
    val motivoExcedido = motivoTexto.length > 1000

    val iniciales = buildString {
        append(displayUser.name.firstOrNull()?.uppercaseChar() ?: "")
        append(displayUser.lastname.firstOrNull()?.uppercaseChar() ?: "")
    }

    val rol = displayUser.rolPrincipal.trim().lowercase()
    val esUsuario     = rol == "usuario"
    val esReclutador  = rol == "reclutador"
    val puedeEliminar = esUsuario || esReclutador

    // Botón de eliminación oculto si ya se envió o ya había una solicitud activa
    val ocultarBotonEliminar = uiState.eliminacionEnviada || uiState.eliminacionYaSolicitada

    // Abrir diálogo de éxito cuando el VM confirma el 201
    LaunchedEffect(uiState.eliminacionEnviada) {
        if (uiState.eliminacionEnviada) mostrarDialogoExitoElim = true
    }

    // Snackbar para errores genéricos y para el 409
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            perfilViewModel.clearError()
        }
    }
    LaunchedEffect(uiState.eliminacionYaSolicitada) {
        if (uiState.eliminacionYaSolicitada) {
            snackbarHostState.showSnackbar("Ya tienes una solicitud de eliminación activa.")
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(rememberScrollState())
        ) {

            // ── Logo ─────────────────────────────────────────────────────────────
            Box(
                modifier         = Modifier.fillMaxWidth().background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter            = painterResource(id = R.drawable.logo_tv),
                    contentDescription = "Transportes Victoria",
                    contentScale       = ContentScale.Fit,
                    modifier           = Modifier
                        .fillMaxWidth(0.60f)
                        .height(110.dp)
                        .padding(top = 36.dp, bottom = 10.dp)
                )
            }

            // ── Franja amarilla ───────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(VictoriaYellow)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Avatar con iniciales ──────────────────────────────────────────────
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Box(
                    modifier         = Modifier
                        .size(80.dp)
                        .background(color = VictoriaYellow, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text       = iniciales,
                        fontSize   = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color      = VictoriaBlack
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ── Nombre completo ───────────────────────────────────────────────────
            Text(
                text       = "${displayUser.name} ${displayUser.lastname}",
                fontSize   = 18.sp,
                fontWeight = FontWeight.Bold,
                color      = VictoriaBlack,
                modifier   = Modifier.align(Alignment.CenterHorizontally)
            )

            // ── Rol ───────────────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 6.dp)
                    .background(
                        color = VictoriaYellow.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 4.dp)
            ) {
                Text(
                    text       = displayUser.rolPrincipal.replaceFirstChar { it.uppercaseChar() },
                    fontSize   = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = VictoriaBlack
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Botón Cambiar contraseña (debajo del nombre, arriba de las tarjetas) ──
            Button(
                onClick  = onCambiarPassword,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(horizontal = 20.dp),
                shape  = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = VictoriaYellow,
                    contentColor   = VictoriaBlack
                )
            ) {
                Icon(
                    imageVector        = Icons.Filled.Lock,
                    contentDescription = null,
                    modifier           = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text       = "Cambiar contraseña",
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Tarjeta: Información personal ─────────────────────────────────────
            PerfilCard(titulo = "Información personal") {
                PerfilFila(Icons.Filled.Email, "Correo", displayUser.email)
                if (!displayUser.tel.isNullOrBlank()) {
                    PerfilFila(Icons.Filled.Phone, "Teléfono", displayUser.tel)
                }
                if (displayUser.documento != null) {
                    val doc = "${displayUser.documento.tipo ?: ""} ${displayUser.documento.valor ?: ""}".trim()
                    if (doc.isNotBlank()) {
                        PerfilFila(Icons.Filled.Badge, "Documento", doc)
                    }
                }
            }

            // ── Tarjeta: Empresa (si aplica) ──────────────────────────────────────
            displayUser.empresa?.let { empresa ->
                val nombreEmpresa = empresa.nombre ?: empresa.nombreCorto
                if (!nombreEmpresa.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    PerfilCard(titulo = "Empresa") {
                        PerfilFila(Icons.Filled.Apartment, "Nombre", nombreEmpresa)
                        if (!empresa.nombreCorto.isNullOrBlank() && empresa.nombreCorto != empresa.nombre) {
                            PerfilFila(Icons.Filled.Apartment, "Nombre corto", empresa.nombreCorto)
                        }
                    }
                }
            }

            // ── Tarjeta: Dirección principal (si aplica) ──────────────────────────
            displayUser.direccionPrincipal?.let { dir ->
                if (!dir.direccion.isNullOrBlank() || !dir.departamento.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    PerfilCard(titulo = "Dirección principal") {
                        if (!dir.ruta.isNullOrBlank()) {
                            PerfilFila(Icons.Filled.LocationOn, "Ruta", dir.ruta)
                        }
                        if (!dir.direccion.isNullOrBlank()) {
                            PerfilFila(Icons.Filled.LocationOn, "Dirección", dir.direccion)
                        }
                        if (!dir.departamento.isNullOrBlank()) {
                            PerfilFila(Icons.Filled.LocationOn, "Departamento", dir.departamento)
                        }
                        if (esUsuario && dir.lat != null && dir.lng != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(
                                onClick  = { onVerMapa(dir.lat, dir.lng, "Dirección principal", dir.direccion) },
                                modifier = Modifier.fillMaxWidth().height(40.dp),
                                shape    = RoundedCornerShape(8.dp),
                                border   = BorderStroke(1.5.dp, VictoriaBlack),
                                colors   = ButtonDefaults.outlinedButtonColors(
                                    containerColor = Color.White,
                                    contentColor   = VictoriaBlack
                                )
                            ) {
                                Icon(Icons.Filled.Map, null, modifier = Modifier.size(15.dp), tint = VictoriaYellow)
                                Spacer(modifier = Modifier.size(6.dp))
                                Text("Ver en mapa", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = VictoriaBlack)
                            }
                        }
                    }
                }
            }

            // ── Tarjeta: Dirección secundaria (solo rol "usuario") ─────────────────
            if (esUsuario) {
                displayUser.direccionSecundaria?.let { dir2 ->
                    if (!dir2.direccion.isNullOrBlank() || !dir2.departamento.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        PerfilCard(titulo = "Dirección secundaria") {
                            if (!dir2.ruta.isNullOrBlank()) {
                                PerfilFila(Icons.Filled.LocationOn, "Ruta", dir2.ruta)
                            }
                            if (!dir2.direccion.isNullOrBlank()) {
                                PerfilFila(Icons.Filled.LocationOn, "Dirección", dir2.direccion)
                            }
                            if (!dir2.departamento.isNullOrBlank()) {
                                PerfilFila(Icons.Filled.LocationOn, "Departamento", dir2.departamento)
                            }
                            if (dir2.lat != null && dir2.lng != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedButton(
                                    onClick  = { onVerMapa(dir2.lat, dir2.lng, "Dirección secundaria", dir2.direccion) },
                                    modifier = Modifier.fillMaxWidth().height(40.dp),
                                    shape    = RoundedCornerShape(8.dp),
                                    border   = BorderStroke(1.5.dp, VictoriaBlack),
                                    colors   = ButtonDefaults.outlinedButtonColors(
                                        containerColor = Color.White,
                                        contentColor   = VictoriaBlack
                                    )
                                ) {
                                    Icon(Icons.Filled.Map, null, modifier = Modifier.size(15.dp), tint = VictoriaYellow)
                                    Spacer(modifier = Modifier.size(6.dp))
                                    Text("Ver en mapa", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = VictoriaBlack)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Botón Solicitar eliminar datos (usuario y reclutador) ─────────────
            if (puedeEliminar && !ocultarBotonEliminar) {
                OutlinedButton(
                    onClick  = { mostrarDialogoEliminar = true },
                    enabled  = !uiState.isEliminando,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .padding(horizontal = 20.dp),
                    shape  = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.5.dp, ColorRojo),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor   = ColorRojo
                    )
                ) {
                    if (uiState.isEliminando) {
                        CircularProgressIndicator(
                            color       = ColorRojo,
                            modifier    = Modifier.size(20.dp),
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Icon(
                            imageVector        = Icons.Filled.DeleteForever,
                            contentDescription = null,
                            modifier           = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text       = "Solicitar eliminar datos",
                            fontSize   = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            // ── Botón Cerrar sesión ───────────────────────────────────────────────
            Button(
                onClick  = { mostrarDialogoCerrarSesion = true },
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
                Text(
                    text       = "Cerrar sesión",
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        // ── Snackbar ──────────────────────────────────────────────────────────────
        SnackbarHost(
            hostState = snackbarHostState,
            modifier  = Modifier.align(Alignment.BottomCenter)
        ) { data ->
            Snackbar(
                snackbarData   = data,
                containerColor = VictoriaBlack,
                contentColor   = Color.White
            )
        }
    }

    // ── Diálogo confirmación cerrar sesión ────────────────────────────────────
    if (mostrarDialogoCerrarSesion) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoCerrarSesion = false },
            title = {
                Text(text = "Cerrar sesión", fontWeight = FontWeight.Bold, color = VictoriaBlack)
            },
            text = {
                Text(text = "¿Estás seguro que deseas cerrar sesión?", color = VictoriaBlack)
            },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogoCerrarSesion = false
                        onCerrarSesion()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VictoriaBlack,
                        contentColor   = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Cerrar sesión", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoCerrarSesion = false }) {
                    Text("Cancelar", color = VictoriaBlack)
                }
            }
        )
    }

    // ── Diálogo confirmación eliminar datos ───────────────────────────────────
    if (mostrarDialogoEliminar) {
        AlertDialog(
            onDismissRequest = {
                mostrarDialogoEliminar = false
                motivoTexto = ""
            },
            icon = {
                Icon(
                    imageVector        = Icons.Filled.WarningAmber,
                    contentDescription = null,
                    tint               = ColorRojo,
                    modifier           = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text       = "¿Solicitar eliminación de datos?",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp,
                    color      = VictoriaBlack
                )
            },
            text = {
                Column {
                    Text(
                        text      = "Se eliminarán tu dirección, DPI y teléfono, y tu cuenta quedará desactivada. Este proceso lo realiza un administrador y no es instantáneo.",
                        fontSize  = 13.sp,
                        color     = Color(0xFF757575),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value         = motivoTexto,
                        onValueChange = { if (it.length <= 1001) motivoTexto = it },
                        placeholder   = {
                            Text(
                                text     = "Cuéntanos por qué quieres eliminar tus datos (opcional)",
                                fontSize = 12.sp,
                                color    = Color(0xFF9E9E9E)
                            )
                        },
                        minLines      = 3,
                        maxLines      = 5,
                        isError       = motivoExcedido,
                        modifier      = Modifier.fillMaxWidth(),
                        shape         = RoundedCornerShape(10.dp),
                        colors        = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor   = if (motivoExcedido) ColorRojo else VictoriaBlack,
                            unfocusedBorderColor = if (motivoExcedido) ColorRojo else Color(0xFFBDBDBD)
                        )
                    )
                    Text(
                        text      = "${motivoTexto.length}/1000",
                        fontSize  = 11.sp,
                        color     = if (motivoExcedido) ColorRojo else Color(0xFF9E9E9E),
                        textAlign = TextAlign.End,
                        modifier  = Modifier.fillMaxWidth().padding(top = 2.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogoEliminar = false
                        val motivo = motivoTexto.trim().takeIf { it.isNotBlank() }
                        motivoTexto = ""
                        perfilViewModel.solicitarEliminacion(motivo)
                    },
                    enabled = !motivoExcedido,
                    colors  = ButtonDefaults.buttonColors(
                        containerColor         = ColorRojo,
                        contentColor           = Color.White,
                        disabledContainerColor = ColorRojo.copy(alpha = 0.4f),
                        disabledContentColor   = Color.White.copy(alpha = 0.6f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Sí, solicitar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    mostrarDialogoEliminar = false
                    motivoTexto = ""
                }) {
                    Text("Cancelar", color = VictoriaBlack)
                }
            },
            containerColor = Color.White,
            shape          = RoundedCornerShape(20.dp)
        )
    }

    // ── Diálogo éxito eliminación ─────────────────────────────────────────────
    if (mostrarDialogoExitoElim) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoExitoElim = false },
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
                    text       = "Solicitud registrada",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp,
                    color      = VictoriaBlack
                )
            },
            text = {
                Text(
                    text      = "Tu solicitud fue registrada. Un administrador la procesará en los próximos días. Tu sesión sigue activa mientras tanto.",
                    fontSize  = 13.sp,
                    color     = Color(0xFF757575),
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = { mostrarDialogoExitoElim = false },
                    colors  = ButtonDefaults.buttonColors(
                        containerColor = VictoriaBlack,
                        contentColor   = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Entendido", fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color.White,
            shape          = RoundedCornerShape(20.dp)
        )
    }
}

// ── Card de sección ───────────────────────────────────────────────────────────
@Composable
private fun PerfilCard(titulo: String, content: @Composable () -> Unit) {
    Card(
        modifier  = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = VictoriaGray),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text          = titulo,
                fontSize      = 12.sp,
                fontWeight    = FontWeight.Bold,
                color         = VictoriaBlack,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}

// ── Fila de dato ─────────────────────────────────────────────────────────────
@Composable
private fun PerfilFila(icono: ImageVector, label: String, valor: String) {
    Row(
        modifier          = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector        = icono,
            contentDescription = null,
            tint               = Color(0xFF9E9E9E),
            modifier           = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, fontSize = 11.sp, color = Color(0xFF9E9E9E))
            Text(
                text       = valor.ifBlank { "—" },
                fontSize   = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color      = VictoriaBlack
            )
        }
    }
    HorizontalDivider(color = VictoriaDivider, thickness = 0.5.dp)
}
