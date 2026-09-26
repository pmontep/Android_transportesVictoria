package com.pointguatemala.transportesvictoria.ui.registro

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pointguatemala.transportesvictoria.R
import com.pointguatemala.transportesvictoria.ui.theme.VictoriaBlack
import com.pointguatemala.transportesvictoria.ui.theme.VictoriaGray
import com.pointguatemala.transportesvictoria.ui.theme.VictoriaYellow

@Composable
fun TerminosScreen(
    viewModel: TerminosViewModel = viewModel(),
    onVolver: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        // ── Cabecera fija: logo + botón volver ────────────────────────────────
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

        // ── Título ────────────────────────────────────────────────────────────
        Text(
            text       = "Términos y condiciones",
            fontSize   = 18.sp,
            fontWeight = FontWeight.Bold,
            color      = VictoriaBlack,
            modifier   = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 20.dp, bottom = 4.dp)
        )

        Text(
            text     = "Lee el acuerdo de uso antes de registrarte",
            fontSize = 13.sp,
            color    = Color(0xFF9E9E9E),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp),
            textAlign = TextAlign.Center
        )

        // ── Contenido ─────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 20.dp)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        color    = VictoriaYellow,
                        modifier = Modifier
                            .size(48.dp)
                            .align(Alignment.Center)
                    )
                }

                uiState.error != null -> {
                    Column(
                        modifier            = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text      = uiState.error!!,
                            fontSize  = 13.sp,
                            color     = Color(0xFF9E9E9E),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.cargar() },
                            colors  = ButtonDefaults.buttonColors(
                                containerColor = VictoriaYellow,
                                contentColor   = VictoriaBlack
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector        = Icons.Filled.Refresh,
                                contentDescription = null,
                                modifier           = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Reintentar", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                uiState.texto.isNullOrBlank() -> {
                    Text(
                        text      = "El administrador aún no ha configurado los términos y condiciones.",
                        fontSize  = 14.sp,
                        color     = Color(0xFF9E9E9E),
                        textAlign = TextAlign.Center,
                        modifier  = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                color = VictoriaGray,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text      = uiState.texto!!,
                            fontSize  = 14.sp,
                            color     = VictoriaBlack,
                            lineHeight = 22.sp
                        )
                    }
                }
            }
        }

        // ── Botón volver al registro ──────────────────────────────────────────
        Button(
            onClick  = onVolver,
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
            Text(
                text       = "Volver al registro",
                fontSize   = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
