package com.pointguatemala.transportesvictoria.ui.shared

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView
import com.pointguatemala.transportesvictoria.ui.theme.VictoriaBlack
import com.pointguatemala.transportesvictoria.ui.theme.VictoriaYellow

@Composable
fun MapaScreen(
    lat: Double,
    lng: Double,
    titulo: String,
    subtitulo: String? = null,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    var mostrarDialogo = remember { mutableStateOf(false) }

    // Configurar OSMDroid con User-Agent válido
    val appVersion = com.pointguatemala.transportesvictoria.BuildConfig.VERSION_NAME
    val androidVersion = android.os.Build.VERSION.RELEASE
    val deviceModel = android.os.Build.MODEL
    val deviceManufacturer = android.os.Build.MANUFACTURER

    val userAgent = buildString {
        append("TransportesVictoria/$appVersion ")
        append("(+https://transportesvictoria.com.gt/) ")
        append("Android/$androidVersion; ")
        append("$deviceManufacturer $deviceModel; ")
        append("Mobile")
    }

    Configuration.getInstance().userAgentValue = userAgent
    android.util.Log.d("MapaScreen", "OSMDroid User-Agent configurado: $userAgent")

    Box(
        modifier = Modifier.fillMaxSize().background(Color.White)
    ) {
        // === CONTENEDOR PRINCIPAL ===
        Column(modifier = Modifier.fillMaxSize()) {
            // Espacio para el header fijo
            Spacer(modifier = Modifier.height(96.dp))

            // === MAPA (OSMDroid MapView) ===
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.LightGray)
            ) {
            AndroidView(
                factory = { ctx ->
                    MapView(ctx).apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)
                        isHorizontalMapRepetitionEnabled = false
                        isVerticalMapRepetitionEnabled = false

                        // IMPORTANTE: OSMDroid muestra automáticamente la atribución
                        // pero nos aseguramos de que esté habilitada
                        isVerticalMapRepetitionEnabled = false

                        controller.setZoom(17.0)
                        controller.setCenter(org.osmdroid.util.GeoPoint(lat, lng))

                        // Agregar marcador
                        val mapOverlays = overlays
                        val marker = org.osmdroid.views.overlay.Marker(this).apply {
                            position = org.osmdroid.util.GeoPoint(lat, lng)
                            title = titulo
                            setAnchor(
                                org.osmdroid.views.overlay.Marker.ANCHOR_CENTER,
                                org.osmdroid.views.overlay.Marker.ANCHOR_CENTER
                            )
                        }
                        mapOverlays.add(marker)

                        android.util.Log.d("MapaScreen", "✓ Mapa OSMDroid creado: lat=$lat, lng=$lng")
                        android.util.Log.d("MapaScreen", "✓ Atribución OSM visible: © OpenStreetMap contributors")
                        android.util.Log.d("MapaScreen", "✓ Tile Source: MAPNIK (OpenStreetMap)")
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

            // === CRÉDITO VISIBLE DE OPENSTREETMAP ===
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "© OpenStreetMap contributors",
                    fontSize = 10.sp,
                    color = Color(0xFF666666),
                    modifier = Modifier.align(Alignment.CenterStart)
                )
            }

            // === BOTÓN ===
            Button(
                onClick = { mostrarDialogo.value = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = VictoriaYellow,
                    contentColor = VictoriaBlack
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.Navigation,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "Abrir en...",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }

        // === HEADER (FIJO EN LA PARTE SUPERIOR) ===
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(VictoriaYellow)
                .padding(horizontal = 12.dp, vertical = 12.dp)
                .height(72.dp)
                .align(Alignment.TopCenter),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    android.util.Log.d("MapaScreen", "Botón atrás presionado")
                    onBack()
                },
                modifier = Modifier
                    .size(56.dp)
                    .padding(4.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = VictoriaBlack,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = titulo,
                color = VictoriaBlack,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
        }
    }

    // Diálogo "Abrir en..."
    if (mostrarDialogo.value) {
        AbrirUbicacionDialog(
            lat = lat,
            lng = lng,
            context = context,
            onDismiss = { mostrarDialogo.value = false }
        )
    }
}

@Composable
private fun AbrirUbicacionDialog(
    lat: Double,
    lng: Double,
    context: android.content.Context,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Abrir ubicación en",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = VictoriaBlack
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                OpcionAbrirUbicacion(
                    nombre = "Google Maps",
                    onClick = {
                        abrirGoogleMaps(context, lat, lng)
                        onDismiss()
                    }
                )
                OpcionAbrirUbicacion(
                    nombre = "Waze",
                    onClick = {
                        abrirWaze(context, lat, lng)
                        onDismiss()
                    }
                )
                OpcionAbrirUbicacion(
                    nombre = "Otras apps",
                    onClick = {
                        abrirOtrasApps(context, lat, lng)
                        onDismiss()
                    }
                )
            }
        },
        confirmButton = {},
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = VictoriaBlack,
                    contentColor = Color.White
                )
            ) {
                Text("Cancelar")
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
private fun OpcionAbrirUbicacion(
    nombre: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.LightGray,
            contentColor = VictoriaBlack
        )
    ) {
        Text(
            text = nombre,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun abrirGoogleMaps(context: android.content.Context, lat: Double, lng: Double) {
    val uri = Uri.parse("geo:$lat,$lng?q=$lat,$lng")
    val intent = Intent(Intent.ACTION_VIEW, uri).apply {
        setPackage("com.google.android.apps.maps")
    }

    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    } else {
        val webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=$lat,$lng")
        context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
    }
}

private fun abrirWaze(context: android.content.Context, lat: Double, lng: Double) {
    val uri = Uri.parse("waze://?ll=$lat,$lng&navigate=yes")
    val intent = Intent(Intent.ACTION_VIEW, uri)

    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    } else {
        val webUri = Uri.parse("https://waze.com/ul?ll=$lat,$lng&navigate=yes")
        context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
    }
}

private fun abrirOtrasApps(context: android.content.Context, lat: Double, lng: Double) {
    val uri = Uri.parse("geo:$lat,$lng")
    val intent = Intent(Intent.ACTION_VIEW, uri)
    context.startActivity(intent)
}
