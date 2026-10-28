package com.pointguatemala.transportesvictoria.ui.login

import android.widget.TextView
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pointguatemala.transportesvictoria.R
import com.pointguatemala.transportesvictoria.ui.theme.VictoriaBlack
import com.pointguatemala.transportesvictoria.ui.theme.VictoriaGray
import com.pointguatemala.transportesvictoria.ui.theme.VictoriaYellow

@Composable
fun AyudaScreen(
    viewModel: AyudaViewModel = viewModel(),
    onVolver: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        // ── Cabecera: logo + botón volver ─────────────────────────────────────
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

        // ── Franja amarilla ───────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(VictoriaYellow)
        )

        // ── Contenido scrollable ──────────────────────────────────────────────
        Column(
            modifier            = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text       = "Ayuda",
                fontSize   = 22.sp,
                fontWeight = FontWeight.Bold,
                color      = VictoriaBlack,
                textAlign  = TextAlign.Center,
                modifier   = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            when (val state = uiState) {

                is AyudaUiState.Loading -> {
                    Spacer(modifier = Modifier.height(60.dp))
                    CircularProgressIndicator(
                        color    = VictoriaYellow,
                        modifier = Modifier.size(48.dp)
                    )
                }

                is AyudaUiState.Error -> {
                    Spacer(modifier = Modifier.height(40.dp))
                    Text(
                        text      = state.message,
                        fontSize  = 14.sp,
                        color     = Color(0xFF9E9E9E),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
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

                is AyudaUiState.Empty -> {
                    Spacer(modifier = Modifier.height(40.dp))
                    Text(
                        text      = "Aún no hay información de ayuda disponible.",
                        fontSize  = 14.sp,
                        color     = Color(0xFF9E9E9E),
                        textAlign = TextAlign.Center
                    )
                }

                is AyudaUiState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = VictoriaGray,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(16.dp)
                    ) {
                        AyudaHtmlText(html = state.html)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun AyudaHtmlText(html: String) {
    val victoriaBlackArgb = VictoriaBlack.toArgb()
    AndroidView(
        factory = { ctx ->
            TextView(ctx).apply {
                textSize          = 15f
                setTextColor(victoriaBlackArgb)
                isScrollContainer = false
                setLineSpacing(4f, 1f)
            }
        },
        update = { tv ->
            val cleaned = html
                .replace(Regex("<font\\b[^>]*>", RegexOption.IGNORE_CASE), "")
                .replace(Regex("</font>", RegexOption.IGNORE_CASE), "")
            try {
                tv.text = HtmlCompat.fromHtml(cleaned, HtmlCompat.FROM_HTML_MODE_COMPACT)
            } catch (_: Exception) {
                tv.text = html
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}
