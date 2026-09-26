package com.pointguatemala.transportesvictoria.ui.login

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pointguatemala.transportesvictoria.ui.theme.VictoriaBlack
import com.pointguatemala.transportesvictoria.ui.theme.VictoriaYellow

private val FieldBorder = Color(0xFFCCCCCC)
private val FieldBg     = Color(0xFFFAFAFA)
private val HintText    = Color(0xFFAAAAAA)
private val SubText     = Color(0xFF777777)

@Composable
fun RecuperarPasswordScreen(
    viewModel: RecuperarPasswordViewModel = viewModel(),
    onVolver: () -> Unit,
    onExitoso: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    LaunchedEffect(uiState.paso) {
        if (uiState.paso == PasoRecuperar.EXITOSO) {
            // pantalla de éxito la mostramos en la UI, el usuario debe presionar el botón
        }
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
            // ── Barra superior con flecha volver ──────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 52.dp, start = 8.dp, end = 16.dp, bottom = 8.dp)
            ) {
                IconButton(
                    onClick = {
                        if (uiState.paso == PasoRecuperar.EMAIL || uiState.paso == PasoRecuperar.EXITOSO) {
                            onVolver()
                        } else {
                            // volver al paso anterior
                            viewModel.onOtpChange("")
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = VictoriaBlack
                    )
                }
                Text(
                    text = "Recuperar contraseña",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = VictoriaBlack,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── Indicador de pasos ────────────────────────────────────────────
            if (uiState.paso != PasoRecuperar.EXITOSO) {
                StepIndicator(pasoActual = uiState.paso)
                Spacer(modifier = Modifier.height(24.dp))
            }

            // ── Tarjeta con contenido del paso ────────────────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                AnimatedContent(
                    targetState = uiState.paso,
                    transitionSpec = {
                        (slideInHorizontally { it } + fadeIn()) togetherWith
                        (slideOutHorizontally { -it } + fadeOut())
                    },
                    label = "paso"
                ) { paso ->
                    when (paso) {
                        PasoRecuperar.EMAIL -> PasoEmail(
                            email     = uiState.email,
                            isLoading = uiState.isLoading,
                            onChange  = viewModel::onEmailChange,
                            onEnviar  = viewModel::enviarCodigo
                        )
                        PasoRecuperar.OTP -> PasoOtp(
                            email     = uiState.email,
                            otp       = uiState.otp,
                            isLoading = uiState.isLoading,
                            onChange  = viewModel::onOtpChange,
                            onVerificar  = viewModel::verificarCodigo,
                            onReenviar   = viewModel::reenviarCodigo
                        )
                        PasoRecuperar.NUEVA_PASSWORD -> PasoNuevaPassword(
                            nuevaPassword     = uiState.nuevaPassword,
                            confirmarPassword = uiState.confirmarPassword,
                            isLoading         = uiState.isLoading,
                            onNuevaChange     = viewModel::onNuevaPasswordChange,
                            onConfirmarChange = viewModel::onConfirmarPasswordChange,
                            onRestablecer     = viewModel::resetPassword
                        )
                        PasoRecuperar.EXITOSO -> PasoExitoso(onIniciarSesion = onExitoso)
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

// ─────────────────────────────────────────────────────────────────────────────
// Indicador de pasos (1 → 2 → 3)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun StepIndicator(pasoActual: PasoRecuperar) {
    val pasos = listOf("Correo", "Código", "Contraseña")
    val pasoIndex = when (pasoActual) {
        PasoRecuperar.EMAIL          -> 0
        PasoRecuperar.OTP            -> 1
        PasoRecuperar.NUEVA_PASSWORD -> 2
        PasoRecuperar.EXITOSO        -> 2
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        pasos.forEachIndexed { index, label ->
            val isActive   = index == pasoIndex
            val isComplete = index < pasoIndex

            // Círculo numerado
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isActive   -> VictoriaYellow
                            isComplete -> VictoriaYellow.copy(alpha = 0.6f)
                            else       -> Color(0xFFEEEEEE)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = (index + 1).toString(),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isActive || isComplete) VictoriaBlack else Color(0xFFAAAAAA)
                )
            }

            // Label
            Text(
                text = label,
                fontSize = 11.sp,
                color = if (isActive) VictoriaBlack else Color(0xFFAAAAAA),
                fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                modifier = Modifier.padding(start = 4.dp)
            )

            // Línea conectora
            if (index < pasos.size - 1) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .padding(horizontal = 4.dp)
                        .background(
                            if (index < pasoIndex) VictoriaYellow.copy(alpha = 0.6f)
                            else Color(0xFFEEEEEE)
                        )
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Paso 1: Email
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun PasoEmail(
    email: String,
    isLoading: Boolean,
    onChange: (String) -> Unit,
    onEnviar: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Ingresa tu correo",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = VictoriaBlack
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Te enviaremos un código de 6 dígitos para restablecer tu contraseña.",
            fontSize = 13.sp,
            color = SubText,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = onChange,
            placeholder = { Text("Correo electrónico", color = HintText, fontSize = 14.sp) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction    = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
                onEnviar()
            }),
            shape = RoundedCornerShape(10.dp),
            enabled = !isLoading,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor      = VictoriaYellow,
                unfocusedBorderColor    = FieldBorder,
                focusedContainerColor   = FieldBg,
                unfocusedContainerColor = FieldBg,
                cursorColor             = VictoriaBlack
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { focusManager.clearFocus(); onEnviar() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor         = VictoriaYellow,
                contentColor           = VictoriaBlack,
                disabledContainerColor = Color(0xFFFFDD80),
                disabledContentColor   = Color(0xFF888888)
            ),
            enabled = !isLoading
        ) {
            AnimatedVisibility(visible = isLoading, enter = fadeIn(), exit = fadeOut()) {
                CircularProgressIndicator(
                    color       = VictoriaBlack,
                    modifier    = Modifier.size(22.dp),
                    strokeWidth = 2.5.dp
                )
            }
            AnimatedVisibility(visible = !isLoading, enter = fadeIn(), exit = fadeOut()) {
                Text("Enviar código", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Paso 2: OTP de 6 dígitos
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun PasoOtp(
    email: String,
    otp: String,
    isLoading: Boolean,
    onChange: (String) -> Unit,
    onVerificar: () -> Unit,
    onReenviar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Ingresa el código",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = VictoriaBlack
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Enviamos un código de 6 dígitos a\n$email",
            fontSize = 13.sp,
            color = SubText,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(28.dp))

        OtpBoxInput(
            value     = otp,
            onChange  = onChange,
            enabled   = !isLoading
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onVerificar,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor         = VictoriaYellow,
                contentColor           = VictoriaBlack,
                disabledContainerColor = Color(0xFFFFDD80),
                disabledContentColor   = Color(0xFF888888)
            ),
            enabled = !isLoading && otp.length == 6
        ) {
            AnimatedVisibility(visible = isLoading, enter = fadeIn(), exit = fadeOut()) {
                CircularProgressIndicator(
                    color       = VictoriaBlack,
                    modifier    = Modifier.size(22.dp),
                    strokeWidth = 2.5.dp
                )
            }
            AnimatedVisibility(visible = !isLoading, enter = fadeIn(), exit = fadeOut()) {
                Text("Verificar código", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "¿No recibiste el código? Reenviar",
            fontSize = 13.sp,
            color = SubText,
            textAlign = TextAlign.Center,
            modifier = Modifier.clickable(enabled = !isLoading) { onReenviar() }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// 6 cajas de OTP
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun OtpBoxInput(value: String, onChange: (String) -> Unit, enabled: Boolean) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        runCatching { focusRequester.requestFocus() }
    }

    BasicTextField(
        value = value,
        onValueChange = { new ->
            val digits = new.filter { it.isDigit() }.take(6)
            onChange(digits)
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword,
            imeAction    = ImeAction.Done
        ),
        modifier = Modifier
            .focusRequester(focusRequester)
            .fillMaxWidth(),
        enabled = enabled,
        decorationBox = { innerTextField ->
            Box {
                // Campo invisible que captura el teclado
                Box(modifier = Modifier.size(1.dp)) { innerTextField() }

                // Cajas visuales
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                ) {
                    repeat(6) { index ->
                        val char      = value.getOrNull(index)?.toString() ?: ""
                        val isCurrent = index == value.length && enabled

                        Box(
                            modifier = Modifier
                                .size(44.dp, 54.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(FieldBg)
                                .border(
                                    width = if (isCurrent) 2.dp else 1.dp,
                                    color = if (isCurrent) VictoriaYellow else FieldBorder,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable(enabled = enabled) {
                                    runCatching { focusRequester.requestFocus() }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text       = char,
                                fontSize   = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color      = VictoriaBlack
                            )
                        }
                    }
                }
            }
        }
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Paso 3: Nueva contraseña
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun PasoNuevaPassword(
    nuevaPassword: String,
    confirmarPassword: String,
    isLoading: Boolean,
    onNuevaChange: (String) -> Unit,
    onConfirmarChange: (String) -> Unit,
    onRestablecer: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    var nuevaVisible     by remember { mutableStateOf(false) }
    var confirmarVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Nueva contraseña",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = VictoriaBlack
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Elige una contraseña segura de al menos 8 caracteres.",
            fontSize = 13.sp,
            color = SubText,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = nuevaPassword,
            onValueChange = onNuevaChange,
            placeholder = { Text("Nueva contraseña", color = HintText, fontSize = 14.sp) },
            trailingIcon = {
                IconButton(onClick = { nuevaVisible = !nuevaVisible }) {
                    Icon(
                        imageVector = if (nuevaVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = null,
                        tint = Color(0xFF9E9E9E)
                    )
                }
            },
            visualTransformation = if (nuevaVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction    = ImeAction.Next
            ),
            shape = RoundedCornerShape(10.dp),
            enabled = !isLoading,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor      = VictoriaYellow,
                unfocusedBorderColor    = FieldBorder,
                focusedContainerColor   = FieldBg,
                unfocusedContainerColor = FieldBg,
                cursorColor             = VictoriaBlack
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = confirmarPassword,
            onValueChange = onConfirmarChange,
            placeholder = { Text("Confirmar contraseña", color = HintText, fontSize = 14.sp) },
            trailingIcon = {
                IconButton(onClick = { confirmarVisible = !confirmarVisible }) {
                    Icon(
                        imageVector = if (confirmarVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = null,
                        tint = Color(0xFF9E9E9E)
                    )
                }
            },
            visualTransformation = if (confirmarVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction    = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
                onRestablecer()
            }),
            shape = RoundedCornerShape(10.dp),
            enabled = !isLoading,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor      = VictoriaYellow,
                unfocusedBorderColor    = FieldBorder,
                focusedContainerColor   = FieldBg,
                unfocusedContainerColor = FieldBg,
                cursorColor             = VictoriaBlack
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { focusManager.clearFocus(); onRestablecer() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor         = VictoriaYellow,
                contentColor           = VictoriaBlack,
                disabledContainerColor = Color(0xFFFFDD80),
                disabledContentColor   = Color(0xFF888888)
            ),
            enabled = !isLoading
        ) {
            AnimatedVisibility(visible = isLoading, enter = fadeIn(), exit = fadeOut()) {
                CircularProgressIndicator(
                    color       = VictoriaBlack,
                    modifier    = Modifier.size(22.dp),
                    strokeWidth = 2.5.dp
                )
            }
            AnimatedVisibility(visible = !isLoading, enter = fadeIn(), exit = fadeOut()) {
                Text("Restablecer contraseña", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Pantalla de éxito
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun PasoExitoso(onIniciarSesion: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = null,
            tint = VictoriaYellow,
            modifier = Modifier.size(72.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "¡Contraseña actualizada!",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = VictoriaBlack,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tu contraseña fue restablecida exitosamente. Ya puedes iniciar sesión.",
            fontSize = 14.sp,
            color = SubText,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(28.dp))
        Button(
            onClick = onIniciarSesion,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = VictoriaYellow,
                contentColor   = VictoriaBlack
            )
        ) {
            Text("Iniciar sesión", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}
