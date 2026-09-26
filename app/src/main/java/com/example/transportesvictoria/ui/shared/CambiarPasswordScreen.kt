package com.pointguatemala.transportesvictoria.ui.shared

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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

@Composable
fun CambiarPasswordScreen(
    onVolver: () -> Unit,
    viewModel: CambiarPasswordViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current

    var actualVisible       by remember { mutableStateOf(false) }
    var nuevaVisible        by remember { mutableStateOf(false) }
    var confirmacionVisible by remember { mutableStateOf(false) }

    // Errores en snackbar
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    // Diálogo de éxito
    if (uiState.exitoso) {
        AlertDialog(
            onDismissRequest = {},
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
                    text       = "¡Contraseña actualizada!",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp,
                    color      = VictoriaBlack
                )
            },
            text = {
                Text(
                    text      = "Tu contraseña fue cambiada exitosamente.",
                    fontSize  = 13.sp,
                    color     = Color(0xFF757575),
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.onExitosoConsumed()
                        onVolver()
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

            // ── Cabecera: logo + botón volver ─────────────────────────────────
            Box(
                modifier         = Modifier
                    .fillMaxWidth()
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
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
                        .fillMaxWidth(0.60f)
                        .height(110.dp)
                        .padding(top = 36.dp, bottom = 10.dp)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(VictoriaYellow)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text       = "Cambiar contraseña",
                fontSize   = 18.sp,
                fontWeight = FontWeight.Bold,
                color      = VictoriaBlack,
                modifier   = Modifier.padding(horizontal = 22.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── Card con los 3 campos ─────────────────────────────────────────
            Card(
                modifier  = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp),
                shape     = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors    = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                ) {

                    // Contraseña actual
                    CampoPassword(
                        valor       = uiState.actual,
                        onCambio    = viewModel::onActualChange,
                        placeholder = "Contraseña actual",
                        visible     = actualVisible,
                        onToggle    = { actualVisible = !actualVisible },
                        isError     = uiState.actualError,
                        errorMsg    = "Ingresa tu contraseña actual",
                        enabled     = !uiState.isLoading,
                        imeAction   = ImeAction.Next,
                        onNext      = { focusManager.moveFocus(FocusDirection.Down) }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Nueva contraseña
                    CampoPassword(
                        valor       = uiState.nueva,
                        onCambio    = viewModel::onNuevaChange,
                        placeholder = "Nueva contraseña",
                        visible     = nuevaVisible,
                        onToggle    = { nuevaVisible = !nuevaVisible },
                        isError     = uiState.nuevaError,
                        errorMsg    = "Mínimo 8 caracteres y debe ser diferente a la actual",
                        enabled     = !uiState.isLoading,
                        imeAction   = ImeAction.Next,
                        onNext      = { focusManager.moveFocus(FocusDirection.Down) }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Confirmar nueva
                    CampoPassword(
                        valor       = uiState.confirmacion,
                        onCambio    = viewModel::onConfirmacionChange,
                        placeholder = "Confirmar nueva contraseña",
                        visible     = confirmacionVisible,
                        onToggle    = { confirmacionVisible = !confirmacionVisible },
                        isError     = uiState.confirmacionError,
                        errorMsg    = "Las contraseñas no coinciden",
                        enabled     = !uiState.isLoading,
                        imeAction   = ImeAction.Done,
                        onNext      = {
                            focusManager.clearFocus()
                            viewModel.cambiarPassword()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Botón Cambiar contraseña ──────────────────────────────────────
            Button(
                onClick  = {
                    focusManager.clearFocus()
                    viewModel.cambiarPassword()
                },
                enabled  = !uiState.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(horizontal = 22.dp),
                shape  = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor         = VictoriaYellow,
                    contentColor           = VictoriaBlack,
                    disabledContainerColor = VictoriaYellow.copy(alpha = 0.5f),
                    disabledContentColor   = VictoriaBlack.copy(alpha = 0.4f)
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
                    Text("Cambiar contraseña", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }

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
private fun CampoPassword(
    valor:       String,
    onCambio:    (String) -> Unit,
    placeholder: String,
    visible:     Boolean,
    onToggle:    () -> Unit,
    isError:     Boolean,
    errorMsg:    String,
    enabled:     Boolean,
    imeAction:   ImeAction,
    onNext:      () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value         = valor,
            onValueChange = onCambio,
            placeholder   = { Text(placeholder, color = HintText, fontSize = 14.sp) },
            trailingIcon  = {
                IconButton(onClick = onToggle) {
                    Icon(
                        imageVector        = if (visible) Icons.Filled.VisibilityOff
                                             else         Icons.Filled.Visibility,
                        contentDescription = if (visible) "Ocultar" else "Mostrar",
                        tint               = Color(0xFF9E9E9E)
                    )
                }
            },
            visualTransformation = if (visible) VisualTransformation.None
                                   else         PasswordVisualTransformation(),
            singleLine      = true,
            isError         = isError,
            enabled         = enabled,
            modifier        = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction    = imeAction
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
        if (isError) {
            Text(
                text     = errorMsg,
                color    = ErrorRed,
                fontSize = 11.sp,
                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
            )
        }
    }
}
