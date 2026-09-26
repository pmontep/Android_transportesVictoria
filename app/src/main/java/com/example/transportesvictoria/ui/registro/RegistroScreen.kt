package com.pointguatemala.transportesvictoria.ui.registro

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pointguatemala.transportesvictoria.R
import com.pointguatemala.transportesvictoria.ui.theme.VictoriaBlack
import com.pointguatemala.transportesvictoria.ui.theme.VictoriaYellow

private val FieldBorder = Color(0xFFCCCCCC)
private val FieldBg     = Color(0xFFFAFAFA)
private val HintText    = Color(0xFFAAAAAA)
private val ErrorRed    = Color(0xFFE53935)

private val DEPARTAMENTOS_GUATEMALA = listOf(
    "Alta Verapaz", "Baja Verapaz", "Chimaltenango", "Chiquimula",
    "El Progreso", "Escuintla", "Guatemala", "Huehuetenango",
    "Izabal", "Jalapa", "Jutiapa", "Petén",
    "Quetzaltenango", "Quiché", "Retalhuleu", "Sacatepéquez",
    "San Marcos", "Santa Rosa", "Sololá", "Suchitepéquez",
    "Totonicapán", "Zacapa"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    viewModel: RegistroViewModel = viewModel(),
    onVolver: () -> Unit = {},
    onRegistroExitoso: () -> Unit = {},
    onVerTerminos: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current
    var dropdownExpanded by remember { mutableStateOf(false) }
    var dropdownDepartamentoExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    // Diálogo de éxito
    if (uiState.registroExitoso) {
        AlertDialog(
            onDismissRequest = {},
            title = {
                Text(text = "¡Registro exitoso!", fontWeight = FontWeight.Bold, color = VictoriaBlack)
            },
            text = {
                Text(
                    text = uiState.mensajeExito.ifBlank {
                        "Tu cuenta está pendiente de activación. Recibirás tu contraseña por correo una vez que un reclutador active tu cuenta."
                    },
                    fontSize = 14.sp,
                    color = Color(0xFF555555)
                )
            },
            confirmButton = {
                Button(
                    onClick = onRegistroExitoso,
                    colors = ButtonDefaults.buttonColors(containerColor = VictoriaYellow, contentColor = VictoriaBlack),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Ir al inicio de sesión", fontWeight = FontWeight.Bold)
                }
            }
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
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ── Cabecera: logo + franja amarilla ──────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                // Botón volver
                IconButton(
                    onClick  = onVolver,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 8.dp, top = 36.dp)
                ) {
                    Icon(
                        imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint               = VictoriaBlack
                    )
                }
                Image(
                    painter            = painterResource(id = R.drawable.logo_tv),
                    contentDescription = "Transportes Victoria",
                    contentScale       = ContentScale.Fit,
                    modifier           = Modifier
                        .fillMaxWidth(0.78f)
                        .height(150.dp)
                        .padding(top = 40.dp, bottom = 20.dp)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(VictoriaYellow)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Tarjeta del formulario ────────────────────────────────────────
            Card(
                modifier  = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp),
                shape     = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                colors    = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text       = "Crear cuenta",
                        fontSize   = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color      = VictoriaBlack,
                        textAlign  = TextAlign.Center
                    )

                    Text(
                        text     = "Completa tus datos para registrarte",
                        fontSize = 13.sp,
                        color    = Color(0xFF9E9E9E),
                        modifier = Modifier.padding(top = 4.dp, bottom = 20.dp),
                        textAlign = TextAlign.Center
                    )

                    // ── Nombre ────────────────────────────────────────────────
                    CampoTexto(
                        valor       = uiState.nombre,
                        onCambio    = viewModel::onNombreChange,
                        placeholder = "Nombre",
                        isError     = uiState.nombreError,
                        errorMsg    = "Campo requerido",
                        shakeKey    = uiState.submitCount,
                        keyType     = KeyboardType.Text,
                        caps        = KeyboardCapitalization.Words,
                        imeAction   = ImeAction.Next,
                        onNext      = { focusManager.moveFocus(FocusDirection.Down) }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // ── Apellido ──────────────────────────────────────────────
                    CampoTexto(
                        valor       = uiState.apellido,
                        onCambio    = viewModel::onApellidoChange,
                        placeholder = "Apellido",
                        isError     = uiState.apellidoError,
                        errorMsg    = "Campo requerido",
                        shakeKey    = uiState.submitCount,
                        keyType     = KeyboardType.Text,
                        caps        = KeyboardCapitalization.Words,
                        imeAction   = ImeAction.Next,
                        onNext      = { focusManager.moveFocus(FocusDirection.Down) }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // ── Correo ────────────────────────────────────────────────
                    CampoTexto(
                        valor       = uiState.email,
                        onCambio    = viewModel::onEmailChange,
                        placeholder = "Correo electrónico",
                        isError     = uiState.emailError,
                        errorMsg    = "Ingresa un correo válido",
                        shakeKey    = uiState.submitCount,
                        keyType     = KeyboardType.Email,
                        imeAction   = ImeAction.Next,
                        onNext      = { focusManager.moveFocus(FocusDirection.Down) }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // ── Teléfono ──────────────────────────────────────────────
                    CampoTexto(
                        valor       = uiState.telefono,
                        onCambio    = viewModel::onTelefonoChange,
                        placeholder = "Teléfono",
                        isError     = uiState.telefonoError,
                        errorMsg    = "Campo requerido",
                        shakeKey    = uiState.submitCount,
                        keyType     = KeyboardType.Phone,
                        imeAction   = ImeAction.Next,
                        onNext      = { focusManager.moveFocus(FocusDirection.Down) }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // ── Tipo de usuario ───────────────────────────────────────
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text     = "Tipo de usuario",
                            fontSize = 12.sp,
                            color    = Color(0xFF9E9E9E),
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            FilterChip(
                                selected = uiState.tipoUsuario == "1",
                                onClick  = { viewModel.onTipoUsuarioChange("1") },
                                label    = { Text("Empleado", fontSize = 13.sp) },
                                colors   = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor     = VictoriaYellow,
                                    selectedLabelColor         = VictoriaBlack,
                                    containerColor             = Color(0xFFF5F5F5),
                                    labelColor                 = Color(0xFF555555)
                                )
                            )
                            FilterChip(
                                selected = uiState.tipoUsuario == "0",
                                onClick  = { viewModel.onTipoUsuarioChange("0") },
                                label    = { Text("En entrenamiento", fontSize = 13.sp) },
                                colors   = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor     = VictoriaYellow,
                                    selectedLabelColor         = VictoriaBlack,
                                    containerColor             = Color(0xFFF5F5F5),
                                    labelColor                 = Color(0xFF555555)
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // ── Carnet / DPI según tipo ───────────────────────────────
                    if (uiState.tipoUsuario == "1") {
                        CampoTexto(
                            valor       = uiState.carnet,
                            onCambio    = viewModel::onCarnetChange,
                            placeholder = "Número de carnet",
                            isError     = uiState.documentoError,
                            errorMsg    = "Campo requerido",
                            shakeKey    = uiState.submitCount,
                            keyType     = KeyboardType.Text,
                            imeAction   = ImeAction.Next,
                            onNext      = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    } else {
                        CampoTexto(
                            valor       = uiState.dpi,
                            onCambio    = viewModel::onDpiChange,
                            placeholder = "DPI",
                            isError     = uiState.documentoError,
                            errorMsg    = "Campo requerido",
                            shakeKey    = uiState.submitCount,
                            keyType     = KeyboardType.Number,
                            imeAction   = ImeAction.Next,
                            onNext      = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // ── Empresa (dropdown) ────────────────────────────────────
                    val empresaOffsetX = rememberShakeOffset(uiState.submitCount, uiState.empresaError)
                    Column(modifier = Modifier.fillMaxWidth().offset(x = empresaOffsetX.dp)) {
                        ExposedDropdownMenuBox(
                            expanded          = dropdownExpanded,
                            onExpandedChange  = { if (!uiState.isLoadingEmpresas) dropdownExpanded = it }
                        ) {
                            OutlinedTextField(
                                value         = uiState.empresaSeleccionada?.nombreCorto ?: "",
                                onValueChange = {},
                                readOnly      = true,
                                placeholder   = {
                                    Text(
                                        text = if (uiState.isLoadingEmpresas) "Cargando empresas…" else "Selecciona tu empresa",
                                        color = HintText,
                                        fontSize = 14.sp
                                    )
                                },
                                trailingIcon  = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded)
                                },
                                modifier      = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                shape         = RoundedCornerShape(10.dp),
                                isError       = uiState.empresaError,
                                colors        = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor      = if (uiState.empresaError) ErrorRed else VictoriaYellow,
                                    unfocusedBorderColor    = if (uiState.empresaError) ErrorRed else FieldBorder,
                                    focusedContainerColor   = FieldBg,
                                    unfocusedContainerColor = FieldBg,
                                    cursorColor             = VictoriaBlack
                                )
                            )
                            ExposedDropdownMenu(
                                expanded         = dropdownExpanded,
                                onDismissRequest = { dropdownExpanded = false }
                            ) {
                                uiState.empresas.forEach { empresa ->
                                    DropdownMenuItem(
                                        text    = { Text(empresa.nombreCorto, fontSize = 14.sp) },
                                        onClick = {
                                            viewModel.onEmpresaSeleccionada(empresa)
                                            dropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                        if (uiState.empresaError) {
                            Text(
                                text     = "Selecciona una empresa",
                                color    = ErrorRed,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // ── Departamento (picker) ──────────────────────────────────
                    val deptoOffsetX = rememberShakeOffset(uiState.submitCount, uiState.departamentoError)
                    Column(modifier = Modifier.fillMaxWidth().offset(x = deptoOffsetX.dp)) {
                        ExposedDropdownMenuBox(
                            expanded         = dropdownDepartamentoExpanded,
                            onExpandedChange = { dropdownDepartamentoExpanded = it }
                        ) {
                            OutlinedTextField(
                                value         = uiState.departamento,
                                onValueChange = {},
                                readOnly      = true,
                                placeholder   = {
                                    Text(
                                        text     = "Departamento",
                                        color    = HintText,
                                        fontSize = 14.sp
                                    )
                                },
                                trailingIcon  = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownDepartamentoExpanded)
                                },
                                modifier      = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                shape         = RoundedCornerShape(10.dp),
                                isError       = uiState.departamentoError,
                                colors        = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor      = if (uiState.departamentoError) ErrorRed else VictoriaYellow,
                                    unfocusedBorderColor    = if (uiState.departamentoError) ErrorRed else FieldBorder,
                                    focusedContainerColor   = FieldBg,
                                    unfocusedContainerColor = FieldBg,
                                    cursorColor             = VictoriaBlack
                                )
                            )
                            ExposedDropdownMenu(
                                expanded         = dropdownDepartamentoExpanded,
                                onDismissRequest = { dropdownDepartamentoExpanded = false }
                            ) {
                                DEPARTAMENTOS_GUATEMALA.forEach { dep ->
                                    DropdownMenuItem(
                                        text    = { Text(dep, fontSize = 14.sp) },
                                        onClick = {
                                            viewModel.onDepartamentoChange(dep)
                                            dropdownDepartamentoExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                        if (uiState.departamentoError) {
                            Text(
                                text     = "Selecciona un departamento",
                                color    = ErrorRed,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // ── Dirección ─────────────────────────────────────────────
                    CampoTexto(
                        valor       = uiState.direccion,
                        onCambio    = viewModel::onDireccionChange,
                        placeholder = "Dirección de residencia",
                        isError     = uiState.direccionError,
                        errorMsg    = "Campo requerido",
                        shakeKey    = uiState.submitCount,
                        keyType     = KeyboardType.Text,
                        caps        = KeyboardCapitalization.Sentences,
                        imeAction   = ImeAction.Done,
                        onNext      = { focusManager.clearFocus() }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // ── Términos y condiciones ────────────────────────────────
                    val termsOffsetX = rememberShakeOffset(uiState.submitCount, uiState.termsError)
                    Column(modifier = Modifier.fillMaxWidth().offset(x = termsOffsetX.dp)) {
                    Row(
                        modifier          = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked         = uiState.acceptedTerms,
                            onCheckedChange = viewModel::onTermsChange,
                            colors          = CheckboxDefaults.colors(
                                checkedColor   = VictoriaYellow,
                                checkmarkColor = VictoriaBlack
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        val baseColor = if (uiState.termsError) ErrorRed else Color(0xFF555555)
                        val textoTerminos = buildAnnotatedString {
                            withStyle(SpanStyle(color = baseColor, fontSize = 13.sp)) {
                                append("Acepto los ")
                            }
                            pushStringAnnotation(tag = "TERMINOS", annotation = "TERMINOS")
                            withStyle(
                                SpanStyle(
                                    color          = VictoriaYellow,
                                    fontSize       = 13.sp,
                                    fontWeight     = FontWeight.SemiBold,
                                    textDecoration = TextDecoration.Underline
                                )
                            ) {
                                append("términos y condiciones")
                            }
                            pop()
                        }
                        ClickableText(
                            text  = textoTerminos,
                            onClick = { offset ->
                                textoTerminos.getStringAnnotations("TERMINOS", offset, offset)
                                    .firstOrNull()
                                    ?.let { onVerTerminos() }
                                    ?: viewModel.onTermsChange(!uiState.acceptedTerms)
                            }
                        )
                    }
                    if (uiState.termsError) {
                        Text(
                            text     = "Debes aceptar los términos para continuar",
                            color    = ErrorRed,
                            fontSize = 11.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 4.dp)
                        )
                    }
                    } // end terms Column

                    Spacer(modifier = Modifier.height(20.dp))

                    // ── Botón Crear cuenta ────────────────────────────────────
                    Button(
                        onClick  = {
                            focusManager.clearFocus()
                            viewModel.registrar()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape    = RoundedCornerShape(10.dp),
                        enabled  = !uiState.isLoading,
                        colors   = ButtonDefaults.buttonColors(
                            containerColor         = VictoriaYellow,
                            contentColor           = VictoriaBlack,
                            disabledContainerColor = Color(0xFFFFDD80),
                            disabledContentColor   = Color(0xFF888888)
                        )
                    ) {
                        AnimatedVisibility(visible = uiState.isLoading, enter = fadeIn(), exit = fadeOut()) {
                            CircularProgressIndicator(
                                color       = VictoriaBlack,
                                modifier    = Modifier.size(22.dp),
                                strokeWidth = 2.5.dp
                            )
                        }
                        AnimatedVisibility(visible = !uiState.isLoading, enter = fadeIn(), exit = fadeOut()) {
                            Text(
                                text          = "Crear cuenta",
                                fontSize      = 16.sp,
                                fontWeight    = FontWeight.Bold,
                                letterSpacing = 0.3.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // ── Link volver al login ──────────────────────────────────
                    TextButton(onClick = onVolver) {
                        Text(
                            text      = "¿Ya tienes cuenta? Inicia sesión",
                            fontSize  = 14.sp,
                            color     = VictoriaBlack,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }

        // ── Snackbar errores ──────────────────────────────────────────────────
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

// ── Animación shake ───────────────────────────────────────────────────────────
@Composable
private fun rememberShakeOffset(shakeKey: Int, isError: Boolean): Float {
    val offset = remember { Animatable(0f) }
    LaunchedEffect(shakeKey) {
        if (shakeKey > 0 && isError) {
            offset.animateTo(
                targetValue   = 0f,
                animationSpec = keyframes {
                    durationMillis = 400
                     0f at   0
                    -8f at  50
                     8f at 100
                    -6f at 150
                     6f at 200
                    -4f at 250
                     4f at 300
                     0f at 400
                }
            )
        }
    }
    return offset.value
}

// ── Campo de texto reutilizable ───────────────────────────────────────────────
@Composable
private fun CampoTexto(
    valor:       String,
    onCambio:    (String) -> Unit,
    placeholder: String,
    isError:     Boolean = false,
    errorMsg:    String  = "",
    shakeKey:    Int     = 0,
    keyType:     KeyboardType = KeyboardType.Text,
    caps:        KeyboardCapitalization = KeyboardCapitalization.None,
    imeAction:   ImeAction = ImeAction.Next,
    onNext:      () -> Unit = {}
) {
    val offsetX = rememberShakeOffset(shakeKey, isError)
    Column(modifier = Modifier.fillMaxWidth().offset(x = offsetX.dp)) {
        OutlinedTextField(
            value         = valor,
            onValueChange = onCambio,
            placeholder   = { Text(text = placeholder, color = HintText, fontSize = 14.sp) },
            singleLine    = true,
            isError       = isError,
            modifier      = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType    = keyType,
                imeAction       = imeAction,
                capitalization  = caps
            ),
            keyboardActions = KeyboardActions(
                onNext = { onNext() },
                onDone = { onNext() }
            ),
            shape  = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor      = if (isError) ErrorRed else VictoriaYellow,
                unfocusedBorderColor    = if (isError) ErrorRed else FieldBorder,
                focusedContainerColor   = FieldBg,
                unfocusedContainerColor = FieldBg,
                cursorColor             = VictoriaBlack
            )
        )
        if (isError && errorMsg.isNotBlank()) {
            Text(
                text     = errorMsg,
                color    = ErrorRed,
                fontSize = 11.sp,
                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
            )
        }
    }
}
