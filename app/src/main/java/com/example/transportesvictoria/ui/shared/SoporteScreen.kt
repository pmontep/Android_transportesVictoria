package com.pointguatemala.transportesvictoria.ui.shared

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pointguatemala.transportesvictoria.R
import com.pointguatemala.transportesvictoria.data.model.SoporteResponse
import com.pointguatemala.transportesvictoria.ui.theme.VictoriaBlack
import com.pointguatemala.transportesvictoria.ui.theme.VictoriaDivider
import com.pointguatemala.transportesvictoria.ui.theme.VictoriaGray
import com.pointguatemala.transportesvictoria.ui.theme.VictoriaYellow

private val WhatsAppGreen = Color(0xFF25D366)

@Composable
fun SoporteScreen(viewModel: SoporteViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {

        // ── Logo ─────────────────────────────────────────────────────────────
        Box(
            modifier         = Modifier
                .fillMaxWidth()
                .background(Color.White),
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

        Spacer(modifier = Modifier.height(28.dp))

        // ── Ícono + título ────────────────────────────────────────────────────
        Box(
            modifier         = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier         = Modifier
                    .size(64.dp)
                    .background(
                        color = VictoriaYellow.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = Icons.Filled.HeadsetMic,
                    contentDescription = null,
                    tint               = VictoriaYellow,
                    modifier           = Modifier.size(34.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text       = "Soporte",
            fontSize   = 20.sp,
            fontWeight = FontWeight.Bold,
            color      = VictoriaBlack,
            modifier   = Modifier.align(Alignment.CenterHorizontally)
        )

        Text(
            text      = "Estamos aquí para ayudarte",
            fontSize  = 13.sp,
            color     = Color(0xFF9E9E9E),
            modifier  = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 4.dp),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(28.dp))

        // ── Contenido dinámico ────────────────────────────────────────────────
        when {
            uiState.isLoading -> {
                Box(
                    modifier         = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = VictoriaYellow)
                }
            }

            uiState.error != null -> {
                Column(
                    modifier          = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text      = uiState.error!!,
                        fontSize  = 13.sp,
                        color     = Color(0xFF9E9E9E),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    TextButton(onClick = { viewModel.cargarSoporte() }) {
                        Icon(
                            imageVector        = Icons.Filled.Refresh,
                            contentDescription = null,
                            tint               = VictoriaYellow,
                            modifier           = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Reintentar", color = VictoriaYellow, fontSize = 13.sp)
                    }
                }
            }

            uiState.soporte != null -> {
                TarjetaContacto(soporte = uiState.soporte!!)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// ── Tarjeta de contacto ───────────────────────────────────────────────────────
@Composable
private fun TarjetaContacto(soporte: SoporteResponse) {
    val context = LocalContext.current
    val tieneInfo = !soporte.email.isNullOrBlank() ||
                    !soporte.telefono.isNullOrBlank() ||
                    !soporte.whatsapp.isNullOrBlank()

    if (!tieneInfo) {
        Text(
            text      = "No hay información de contacto disponible.",
            fontSize  = 13.sp,
            color     = Color(0xFF9E9E9E),
            textAlign = TextAlign.Center,
            modifier  = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        )
        return
    }

    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = VictoriaGray),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text          = "CONTACTO",
                fontSize      = 12.sp,
                fontWeight    = FontWeight.Bold,
                color         = VictoriaBlack,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            if (!soporte.email.isNullOrBlank()) {
                SoporteFila(
                    icono = Icons.Filled.Email,
                    label = "Correo",
                    valor = soporte.email
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${soporte.email}"))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(10.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = VictoriaBlack)
                ) {
                    Icon(
                        imageVector        = Icons.Filled.Email,
                        contentDescription = null,
                        modifier           = Modifier.size(18.dp),
                        tint               = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text       = "Enviar correo",
                        color      = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (!soporte.telefono.isNullOrBlank()) {
                SoporteFila(
                    icono = Icons.Filled.Phone,
                    label = "Teléfono",
                    valor = soporte.telefono
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${soporte.telefono}"))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(10.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = VictoriaBlack)
                ) {
                    Icon(
                        imageVector        = Icons.Filled.Phone,
                        contentDescription = null,
                        modifier           = Modifier.size(18.dp),
                        tint               = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text       = "Llamar",
                        color      = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (!soporte.whatsapp.isNullOrBlank()) {
                SoporteFila(
                    icono = Icons.Filled.Chat,
                    label = "WhatsApp",
                    valor = soporte.whatsapp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        val numero = soporte.whatsapp.filter { it.isDigit() }
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://wa.me/$numero")
                        )
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(10.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen)
                ) {
                    Icon(
                        imageVector        = Icons.Filled.Chat,
                        contentDescription = null,
                        modifier           = Modifier.size(18.dp),
                        tint               = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text       = "Abrir en WhatsApp",
                        color      = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 14.sp
                    )
                }
            }
        }
    }
}

// ── Fila de dato ──────────────────────────────────────────────────────────────
@Composable
private fun SoporteFila(icono: ImageVector, label: String, valor: String) {
    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
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
            Text(
                text     = label,
                fontSize = 11.sp,
                color    = Color(0xFF9E9E9E)
            )
            Text(
                text       = valor,
                fontSize   = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color      = VictoriaBlack
            )
        }
    }
    HorizontalDivider(color = VictoriaDivider, thickness = 0.5.dp)
}
