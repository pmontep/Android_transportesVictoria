package com.pointguatemala.transportesvictoria.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.pointguatemala.transportesvictoria.ui.theme.VictoriaBlack
import com.pointguatemala.transportesvictoria.ui.theme.VictoriaGray
import com.pointguatemala.transportesvictoria.ui.theme.VictoriaGrayMid
import com.pointguatemala.transportesvictoria.ui.theme.VictoriaYellow

private const val MAX_COMENTARIO = 500

@Composable
fun CalificarViajeDialog(
    isLoading: Boolean,
    onEnviar: (calificacion: Int, comentario: String?) -> Unit,
    onCancelar: () -> Unit
) {
    var estrellas  by remember { mutableIntStateOf(0) }
    var comentario by remember { mutableStateOf("") }

    Dialog(onDismissRequest = { if (!isLoading) onCancelar() }) {
        Surface(
            shape             = RoundedCornerShape(20.dp),
            color             = Color.White,
            tonalElevation    = 4.dp
        ) {
            Column(
                modifier            = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text       = "Calificar viaje",
                    fontSize   = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color      = VictoriaBlack
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text      = "¿Cómo fue tu experiencia?",
                    fontSize  = 13.sp,
                    color     = VictoriaGrayMid,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                // ── 5 estrellas ───────────────────────────────────────────────
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    repeat(5) { index ->
                        val llena = index < estrellas
                        Icon(
                            imageVector        = if (llena) Icons.Filled.Star else Icons.Outlined.StarOutline,
                            contentDescription = "Estrella ${index + 1}",
                            tint               = if (llena) VictoriaYellow else VictoriaGrayMid,
                            modifier           = Modifier
                                .size(40.dp)
                                .clickable(enabled = !isLoading) { estrellas = index + 1 }
                        )
                    }
                }

                if (estrellas > 0) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text      = etiquetaEstrellas(estrellas),
                        fontSize  = 12.sp,
                        color     = VictoriaYellow,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ── Campo de comentario ───────────────────────────────────────
                OutlinedTextField(
                    value         = comentario,
                    onValueChange = { if (it.length <= MAX_COMENTARIO) comentario = it },
                    placeholder   = { Text("Comentario (opcional)", color = VictoriaGrayMid, fontSize = 13.sp) },
                    minLines      = 3,
                    maxLines      = 5,
                    modifier      = Modifier.fillMaxWidth(),
                    enabled       = !isLoading,
                    shape         = RoundedCornerShape(10.dp),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor      = VictoriaYellow,
                        unfocusedBorderColor    = Color(0xFFCCCCCC),
                        focusedContainerColor   = VictoriaGray,
                        unfocusedContainerColor = VictoriaGray,
                        cursorColor             = VictoriaBlack
                    )
                )

                Text(
                    text      = "${comentario.length} / $MAX_COMENTARIO",
                    fontSize  = 11.sp,
                    color     = VictoriaGrayMid,
                    modifier  = Modifier
                        .align(Alignment.End)
                        .padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // ── Botón Enviar ──────────────────────────────────────────────
                Button(
                    onClick  = {
                        if (estrellas > 0) {
                            onEnviar(estrellas, comentario.takeIf { it.isNotBlank() })
                        }
                    },
                    enabled  = estrellas > 0 && !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape  = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor         = VictoriaYellow,
                        contentColor           = VictoriaBlack,
                        disabledContainerColor = VictoriaYellow.copy(alpha = 0.4f),
                        disabledContentColor   = VictoriaBlack.copy(alpha = 0.4f)
                    )
                ) {
                    AnimatedVisibility(visible = isLoading, enter = fadeIn(), exit = fadeOut()) {
                        CircularProgressIndicator(
                            color       = VictoriaBlack,
                            modifier    = Modifier.size(22.dp),
                            strokeWidth = 2.5.dp
                        )
                    }
                    AnimatedVisibility(visible = !isLoading, enter = fadeIn(), exit = fadeOut()) {
                        Text("Enviar calificación", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // ── Botón Cancelar ────────────────────────────────────────────
                TextButton(
                    onClick  = onCancelar,
                    enabled  = !isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text     = "Cancelar",
                        color    = VictoriaGrayMid,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

private fun etiquetaEstrellas(n: Int) = when (n) {
    1    -> "Muy malo"
    2    -> "Malo"
    3    -> "Regular"
    4    -> "Bueno"
    5    -> "Excelente"
    else -> ""
}
