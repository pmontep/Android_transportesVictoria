package com.pointguatemala.transportesvictoria

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pointguatemala.transportesvictoria.data.model.ConductorViaje
import com.pointguatemala.transportesvictoria.data.model.LoginResponse
import com.pointguatemala.transportesvictoria.data.repository.AuthRepository
import com.pointguatemala.transportesvictoria.data.repository.PerfilRepository
import com.pointguatemala.transportesvictoria.data.session.SessionManager
import com.pointguatemala.transportesvictoria.ui.dashboard.ConductorDetalleScreen
import com.pointguatemala.transportesvictoria.ui.dashboard.ConductorScreen
import com.pointguatemala.transportesvictoria.ui.dashboard.DetalleSolicitudScreen
import com.pointguatemala.transportesvictoria.ui.dashboard.ReclutadorScreen
import com.pointguatemala.transportesvictoria.ui.dashboard.UsuarioScreen
import com.pointguatemala.transportesvictoria.ui.login.LoginScreen
import com.pointguatemala.transportesvictoria.ui.login.LoginViewModel
import com.pointguatemala.transportesvictoria.ui.login.RecuperarPasswordScreen
import com.pointguatemala.transportesvictoria.ui.registro.RegistroScreen
import com.pointguatemala.transportesvictoria.ui.registro.TerminosScreen
import com.pointguatemala.transportesvictoria.ui.shared.CambiarPasswordScreen
import com.pointguatemala.transportesvictoria.ui.shared.MapaScreen
import com.pointguatemala.transportesvictoria.ui.shared.NavBarInferior
import com.pointguatemala.transportesvictoria.ui.shared.PerfilScreen
import com.pointguatemala.transportesvictoria.ui.shared.SoporteScreen
import com.pointguatemala.transportesvictoria.ui.shared.TabNav
import com.pointguatemala.transportesvictoria.ui.solicitud.SolicitudFormScreen
import com.pointguatemala.transportesvictoria.ui.theme.TransportesVictoriaTheme
import com.pointguatemala.transportesvictoria.ui.theme.VictoriaYellow
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TransportesVictoriaTheme {
                AppNavigation()
            }
        }
    }
}

sealed class Pantalla {
    object Splash                                          : Pantalla()
    object Login                                           : Pantalla()
    object Registro                                        : Pantalla()
    object Terminos                                        : Pantalla()
    object Usuario                                         : Pantalla()
    object Conductor                                       : Pantalla()
    object Reclutador                                      : Pantalla()
    object SolicitarViaje                                  : Pantalla()
    data class DetalleSolicitud(val id: String)            : Pantalla()
    data class ConductorDetalle(val viaje: ConductorViaje) : Pantalla()
    object Soporte                                         : Pantalla()
    object Perfil                                          : Pantalla()
    object RecuperarPassword                               : Pantalla()
    object CambiarPassword                                 : Pantalla()
    data class Mapa(
        val lat: Double,
        val lng: Double,
        val titulo: String,
        val subtitulo: String? = null
    ) : Pantalla()
}

@Composable
fun AppNavigation() {
    val loginViewModel: LoginViewModel = viewModel()
    val scope = rememberCoroutineScope()
    var loginResponse by remember { mutableStateOf<LoginResponse?>(null) }
    var pantallaActual by remember { mutableStateOf<Pantalla>(Pantalla.Splash) }
    var mapaPantallaOrigen by remember { mutableStateOf<Pantalla>(Pantalla.Login) }
    var recargarUsuario by remember { mutableStateOf(false) }
    var recargarConductor by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current

    val onVerMapa: (Double, Double, String, String?) -> Unit = { lat, lng, titulo, sub ->
        mapaPantallaOrigen = pantallaActual
        pantallaActual = Pantalla.Mapa(lat, lng, titulo, sub)
    }

    fun pantallaParaRol(rol: String): Pantalla = when (rol.trim().lowercase()) {
        "conductor"  -> Pantalla.Conductor
        "reclutador" -> Pantalla.Reclutador
        else         -> Pantalla.Usuario
    }

    fun pantallaHome(): Pantalla = pantallaParaRol(
        loginResponse?.user?.rolPrincipal ?: ""
    )

    fun onLoginSuccess(response: LoginResponse) {
        loginResponse = response
        pantallaActual = pantallaParaRol(response.user.rolPrincipal)
    }

    // ── Startup + chequeo de 72h en cada foreground ───────────────────────────
    // Primera entrada (RESUMED): valida el token con /api/me.
    //   200 → chequea 72h, refresca si hace falta, va al dashboard.
    //   401 → limpia sesión, va al login.
    // Entradas siguientes (app vuelve de background): solo chequea 72h.
    LaunchedEffect(lifecycleOwner) {
        var isFirstResume = true
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            if (isFirstResume) {
                isFirstResume = false
                if (SessionManager.isLoggedIn) {
                    val result = PerfilRepository().getMe()
                    result.fold(
                        onSuccess = { user ->
                            if (SessionManager.shouldRefresh()) {
                                AuthRepository().refreshToken()
                            }
                            loginResponse = LoginResponse(
                                user      = user,
                                token     = "",
                                tokenType = "Bearer",
                                issuedAt  = null
                            )
                            pantallaActual = pantallaParaRol(user.rolPrincipal)
                        },
                        onFailure = {
                            SessionManager.clear()
                            pantallaActual = Pantalla.Login
                        }
                    )
                } else {
                    pantallaActual = Pantalla.Login
                }
            } else {
                // La app vuelve de background: solo rotar token si toca
                if (SessionManager.isLoggedIn) {
                    AuthRepository().refreshToken()
                }
            }
        }
    }

    // ── Logout disparado por un 401 en cualquier request (interceptor de OkHttp) ─
    LaunchedEffect(Unit) {
        SessionManager.logoutEvent.collect {
            loginResponse = null
            loginViewModel.resetLoginResponse()
            pantallaActual = Pantalla.Login
        }
    }

    // Pantallas donde NO se muestra la barra inferior
    val mostrarNavBar = pantallaActual !is Pantalla.Splash &&
                        pantallaActual !is Pantalla.Login &&
                        pantallaActual !is Pantalla.Registro &&
                        pantallaActual !is Pantalla.Terminos &&
                        pantallaActual !is Pantalla.RecuperarPassword &&
                        pantallaActual !is Pantalla.CambiarPassword &&
                        pantallaActual !is Pantalla.SolicitarViaje &&
                        pantallaActual !is Pantalla.DetalleSolicitud &&
                        pantallaActual !is Pantalla.ConductorDetalle &&
                        pantallaActual !is Pantalla.Mapa

    val tabActual = when (pantallaActual) {
        is Pantalla.Soporte -> TabNav.SOPORTE
        is Pantalla.Perfil  -> TabNav.PERFIL
        else                -> TabNav.HOME
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(if (mostrarNavBar) Modifier.padding(bottom = 80.dp) else Modifier)
        ) {
            val pantalla = pantallaActual

            when (pantalla) {

                // ── Splash (cargando sesión) ──────────────────────────────────
                is Pantalla.Splash -> {
                    Box(
                        modifier         = Modifier.fillMaxSize().background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Image(
                                painter            = painterResource(id = R.drawable.logo_tv),
                                contentDescription = "Transportes Victoria",
                                contentScale       = ContentScale.Fit,
                                modifier           = Modifier
                                    .fillMaxWidth(0.60f)
                                    .height(110.dp)
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            CircularProgressIndicator(
                                color    = VictoriaYellow,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }

                // ── Login ─────────────────────────────────────────────────────
                is Pantalla.Login -> {
                    LoginScreen(
                        onLoginSuccess      = ::onLoginSuccess,
                        onRegistro          = { pantallaActual = Pantalla.Registro },
                        onRecuperarPassword = { pantallaActual = Pantalla.RecuperarPassword }
                    )
                }

                // ── Recuperar contraseña ──────────────────────────────────────
                is Pantalla.RecuperarPassword -> {
                    RecuperarPasswordScreen(
                        onVolver  = { pantallaActual = Pantalla.Login },
                        onExitoso = { pantallaActual = Pantalla.Login }
                    )
                }

                // ── Registro ──────────────────────────────────────────────────
                is Pantalla.Registro -> {
                    RegistroScreen(
                        onVolver          = { pantallaActual = Pantalla.Login },
                        onRegistroExitoso = { pantallaActual = Pantalla.Login },
                        onVerTerminos     = { pantallaActual = Pantalla.Terminos }
                    )
                }

                // ── Términos y condiciones ────────────────────────────────────
                is Pantalla.Terminos -> {
                    TerminosScreen(
                        onVolver = { pantallaActual = Pantalla.Registro }
                    )
                }

                // ── Dashboard usuario ─────────────────────────────────────────
                is Pantalla.Usuario -> {
                    loginResponse?.user?.let { user ->
                        UsuarioScreen(
                            user           = user,
                            recargar       = recargarUsuario,
                            onRecargarDone = { recargarUsuario = false },
                            onSolicitar    = { pantallaActual = Pantalla.SolicitarViaje },
                            onVerDetalle   = { idL -> pantallaActual = Pantalla.DetalleSolicitud(idL) }
                        )
                    }
                }

                // ── Formulario nueva solicitud ────────────────────────────────
                is Pantalla.SolicitarViaje -> {
                    SolicitudFormScreen(
                        onVolver          = { pantallaActual = Pantalla.Usuario },
                        onSolicitudCreada = {
                            recargarUsuario = true
                            pantallaActual  = Pantalla.Usuario
                        }
                    )
                }

                // ── Detalle de solicitud ──────────────────────────────────────
                is Pantalla.DetalleSolicitud -> {
                    DetalleSolicitudScreen(
                        solicitudIdL = pantalla.id,
                        onBack       = { pantallaActual = Pantalla.Usuario },
                        onCancelada  = {
                            recargarUsuario = true
                            pantallaActual  = Pantalla.Usuario
                        }
                    )
                }

                // ── Dashboard conductor ───────────────────────────────────────
                is Pantalla.Conductor -> {
                    loginResponse?.user?.let { user ->
                        ConductorScreen(
                            user           = user,
                            recargar       = recargarConductor,
                            onRecargarDone = { recargarConductor = false },
                            onVerDetalle   = { viaje -> pantallaActual = Pantalla.ConductorDetalle(viaje) }
                        )
                    }
                }

                // ── Detalle de viaje conductor ────────────────────────────────
                is Pantalla.ConductorDetalle -> {
                    ConductorDetalleScreen(
                        viaje     = pantalla.viaje,
                        onBack    = {
                            recargarConductor = true
                            pantallaActual    = Pantalla.Conductor
                        },
                        onVerMapa = onVerMapa
                    )
                }

                // ── Dashboard reclutador ──────────────────────────────────────
                is Pantalla.Reclutador -> {
                    loginResponse?.user?.let { user ->
                        ReclutadorScreen(user = user)
                    }
                }

                // ── Soporte (todos los roles) ─────────────────────────────────
                is Pantalla.Soporte -> {
                    SoporteScreen()
                }

                // ── Perfil (todos los roles) ──────────────────────────────────
                is Pantalla.Perfil -> {
                    loginResponse?.user?.let { user ->
                        PerfilScreen(
                            user              = user,
                            onCambiarPassword = { pantallaActual = Pantalla.CambiarPassword },
                            onVerMapa         = onVerMapa,
                            onCerrarSesion    = {
                                scope.launch { PerfilRepository().logout() }
                                SessionManager.clear()
                                loginViewModel.resetLoginResponse()
                                loginResponse  = null
                                pantallaActual = Pantalla.Login
                            }
                        )
                    }
                }

                // ── Cambiar contraseña ────────────────────────────────────────
                is Pantalla.CambiarPassword -> {
                    CambiarPasswordScreen(
                        onVolver = { pantallaActual = Pantalla.Perfil }
                    )
                }

                // ── Mapa de ubicación ─────────────────────────────────────────
                is Pantalla.Mapa -> {
                    MapaScreen(
                        lat       = pantalla.lat,
                        lng       = pantalla.lng,
                        titulo    = pantalla.titulo,
                        subtitulo = pantalla.subtitulo,
                        onBack    = { pantallaActual = mapaPantallaOrigen }
                    )
                }
            }
        }

        // ── Barra de navegación inferior ──────────────────────────────────────
        if (mostrarNavBar) {
            NavBarInferior(
                tabActual = tabActual,
                modifier  = Modifier.align(Alignment.BottomCenter),
                onHome    = { pantallaActual = pantallaHome() },
                onSoporte = { pantallaActual = Pantalla.Soporte },
                onPerfil  = { pantallaActual = Pantalla.Perfil }
            )
        }
    }
}
