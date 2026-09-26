# COMPARACIÓN DE FUNCIONALIDADES iOS → Android
## TransportesVictoria
Fecha: 2026-06-19

---

## RESUMEN EJECUTIVO

| Categoría | Total iOS | Implementado Android | Pendiente |
|-----------|-----------|----------------------|-----------|
| Pantallas principales | 16 | 13 ✅ | 3 ❌ |
| Endpoints HTTP | 20 | 16 ✅ | 4 ❌ |
| Funcionalidades detalladas | — | Con diferencias menores en 6 pantallas | — |

---

## PANTALLAS COMPLETAMENTE IMPLEMENTADAS ✅

### 1. Login
- Email + contraseña con botón ojo (mostrar/ocultar)
- Spinner durante carga, botón deshabilitado
- Manejo de errores 401, 403 y genérico
- Navegación según rol (usuario / conductor / reclutador)
- Link a Registro y a Recuperar contraseña

### 2. Registro
- Nombre, apellidos, correo, teléfono, tipo (Empleado/Entrenamiento)
- Carnet (si Empleado) o DPI (si Entrenamiento), visible según tipo seleccionado
- Empresa (dropdown cargado desde API)
- Términos y condiciones con link clickeable
- Validación de campos, borde rojo en errores
- Mensaje de éxito al registrar

### 3. Viajes Asignados - Conductor
- Lista de viajes con fecha/hora, ruta/estado, botón Ver
- Carga al entrar a la pantalla
- Botón recargar
- Estado vacío "No tienes viajes asignados hoy"
- Spinner de carga

### 4. Detalle Viaje - Conductor
- Barra amarilla con título e ícono bus decorativo
- Card con Ruta, Fecha, Hora, Estado
- Botón Iniciar viaje (activo si "programado")
- Botón Finalizar viaje (activo si "en curso")
- Lista de pasajeros con nombre, dirección, tipo, ID
- Toggle abordar pasajero con cambio optimista y spinner por fila
- Hora de abordaje visible si ya abordado
- Mensaje de error contextual si viaje finalizado

### 5. Usuarios en Entrenamiento - Reclutador
- Buscador con ícono lupa y botón limpiar (X)
- Debounce en búsqueda
- Filtros pills: Inactivos / Activos / Todos
- Contador de registros
- Tabla con nombre, correo, DPI, ruta, chip de estado
- Toggle Activar/Suspender usuario
- Paginación con botón "Cargar más"

### 6. Mis Solicitudes - Usuario
- Botón "Solicitar Viaje"
- Lista con fecha/hora, ruta/estado viaje, botón Ver más
- Botón recargar, spinner inicial, estado vacío
- Chip de estado con color según estado solicitud/viaje

### 7. Crear Solicitud de Viaje
- Selector de ruta (dropdown con dirección y departamento)
- Pre-selección automática si solo hay una ruta
- Horarios disponibles como FilterChips horizontales
- Campo observaciones (opcional)
- Diálogo de éxito al crear
- Diálogo si el usuario ya tiene un viaje en tránsito
- Validación de ruta y hora obligatorios

### 8. Términos y Condiciones
- Carga desde GET /api/terminos
- Texto scrollable
- Spinner durante carga, mensaje si no hay contenido, reintentar si error
- Botón volver al registro

### 9. Soporte
- Carga desde GET /api/soporte
- Muestra correo, teléfono y WhatsApp
- Botón "Abrir en WhatsApp" con intent externo

### 10. Recuperar Contraseña (3 pasos)
- Indicador de pasos animado (Correo → Código → Contraseña)
- Paso 1: Ingresa email, envía código
- Paso 2: Input OTP de 6 cajas visuales, botón reenviar código
- Paso 3: Nueva contraseña + confirmar, toggle visibilidad
- Pantalla de éxito con botón "Iniciar sesión"
- Transición animada entre pasos (slideInHorizontally)

### 11. Navegación por Rol (equivalente a Tab Bar Controllers)
- NavBar inferior con 3 tabs: Home / Soporte / Perfil
- El tab Home navega a la pantalla correcta según rol del usuario autenticado
- La barra no aparece en pantallas de formulario o detalle

---

## PANTALLAS PARCIALMENTE IMPLEMENTADAS ⚠️

### 12. Detalle Solicitud - Usuario
**Implementado:**
- Card "Datos del viaje": Ruta, Estado viaje, Abordó, Hora abordaje, Conductor, Hora inicio/fin, Observaciones
- Card "Información de la solicitud": Fecha/hora de solicitud, Estado
- Chip de estado al inicio con color
- Botón "Cancelar solicitud" con diálogo de confirmación (solo si puede cancelarse)
- Diálogo de éxito al cancelar
- Botón "Volver a mis solicitudes"

**FALTANTE:**
- ❌ Card "Tu calificación": si el viaje está completado, debe mostrar las estrellas dadas y comentario (si ya calificó) o el botón "Calificar viaje" (si no ha calificado)
- ❌ La acción "Calificar viaje" que abre el modal de calificación

### 13. Perfil
**Implementado:**
- Avatar con iniciales
- Nombre completo + badge de rol
- Card "Información personal": correo, teléfono, documento
- Card "Empresa"
- Card "Dirección principal" (ruta, dirección, departamento)
- Botón "Cerrar sesión"

**FALTANTE:**
- ❌ Botón "Cambiar contraseña" (navega a CambiarContraseñaPerfilScreen)
- ❌ Card "Dirección secundaria" (no se muestra aunque exista en el modelo)
- ❌ Llamada a GET /api/me al cargar (solo usa datos del SessionManager; perfil puede estar desactualizado)
- ❌ Diálogo de confirmación al cerrar sesión ("¿Estás seguro?")
- ❌ Botón "Ver en mapa" en las cards de dirección
- ❌ POST /api/logoutapi no se llama al cerrar sesión (solo limpia SessionManager local)

---

## PANTALLAS NO IMPLEMENTADAS ❌

### 14. Calificar Viaje
**Descripción iOS:**
- Modal (pageSheet) con 5 estrellas seleccionables
- Campo de comentario opcional (max 500 caracteres) con contador en tiempo real
- Botón "Enviar calificación" y "Cancelar"
- PATCH /api/viajes/mis-solicitudes/{idL}/calificar con `{ calificacion, comentario_calificacion? }`
- Al completar: cierra modal y actualiza UI del detalle

**Usado en:** DetalleSolicitudScreen cuando viaje está completado y no ha sido calificado.

### 15. Mapa de Ubicación
**Descripción iOS:**
- MapView con zoom cercano, anotación amarilla en las coordenadas del pasajero/dirección
- Título dinámico según contexto
- Parámetros: lat, lng, título, dirección, mostrarBotonAbrirMaps

**Usado en:**
- Botón "Ver en mapa" en cada pasajero del DetalleViajeScreen (conductor)
- Botón "Ver en mapa" en las cards de dirección del PerfilScreen

### 16. Cambiar Contraseña desde Perfil
**Descripción iOS:**
- Card con 3 campos: contraseña actual, nueva contraseña, confirmar nueva
- Validaciones: actual no vacía, nueva 8+ chars, confirmación igual, nueva ≠ actual
- POST /api/perfil/password con `{ current_password, password, password_confirmation }`
- Maneja error_code WRONG_PASSWORD
- Al éxito: regresa al perfil

---

## DIFERENCIAS FUNCIONALES ESPECÍFICAS

### Conductor - Detalle de Viaje
| Funcionalidad | iOS | Android |
|---|---|---|
| Diálogo confirmar Iniciar viaje | ✅ Sí, con mensaje | ❌ Acción directa |
| Diálogo confirmar Finalizar viaje | ✅ Sí, con mensaje | ❌ Acción directa |
| Correo del pasajero en celda | ✅ Visible | ❌ No se muestra |
| Teléfono del pasajero en celda | ✅ Visible | ❌ No se muestra |
| Botón "Ver en mapa" del pasajero | ✅ Sí | ❌ No existe |
| Total pasajeros en card de info | ✅ Sí | ❌ No se muestra |

### Reclutador
| Funcionalidad | iOS | Android |
|---|---|---|
| Diálogo confirmar toggle usuario | ✅ "¿Activar/Desactivar?" | ❌ Acción directa |
| Debounce búsqueda | 300ms | 600ms |

### Soporte
| Funcionalidad | iOS | Android |
|---|---|---|
| Abrir correo (mailto:) | ✅ Sí | ❌ Solo muestra el dato |
| Abrir teléfono (tel://) | ✅ Sí | ❌ Solo muestra el dato |
| Abrir WhatsApp | ✅ Sí | ✅ Sí |

### Mis Solicitudes - Celda
| Funcionalidad | iOS | Android |
|---|---|---|
| Muestra estrellas de calificación | ✅ Si está calificada | ❌ No se muestra |

### Registro
| Funcionalidad | iOS | Android |
|---|---|---|
| Picker Departamento (22 opciones) | ✅ UIPickerView | ❌ Campo de texto libre |
| Animación shake en errores | ✅ Sí | ❌ Solo borde rojo |

### Listas en general
| Funcionalidad | iOS | Android |
|---|---|---|
| Pull-to-refresh | ✅ Todas las listas | ❌ Solo botón refresh |
| Animación rotación en botón refresh | ✅ Sí | ❌ Sin animación |
| Deduplicación de solicitudes por idL | ✅ Sí | ❌ No implementado |

---

## ENDPOINTS HTTP

### Implementados ✅
| Endpoint | Uso |
|---|---|
| POST /api/loginapi | Login |
| POST /api/forgot-password | Recuperar contraseña paso 1 |
| POST /api/verify-otp | Recuperar contraseña paso 2 |
| POST /api/reset-password | Recuperar contraseña paso 3 |
| POST /api/loginRegister | Registro |
| GET /api/empresas | Carga empresas en registro |
| GET /api/terminos | Términos y condiciones |
| GET /api/soporte | Datos de soporte |
| GET /api/viajes/mis-solicitudes | Lista solicitudes usuario |
| GET /api/viajes/mis-solicitudes/{idL} | Detalle solicitud |
| GET /api/viajes/mis-rutas | Rutas del usuario (incluye horarios) |
| POST /api/viajes/solicitar | Crear solicitud |
| DELETE /api/viajes/mis-solicitudes/{idL} | Cancelar solicitud |
| GET /api/conductor/viajes | Viajes del conductor |
| PATCH /api/conductor/viajes/{idL}/iniciar | Iniciar viaje |
| PATCH /api/conductor/viajes/{idL}/finalizar | Finalizar viaje |
| PATCH /api/conductor/solicitud/{idL}/abordar | Abordar pasajero |
| GET /api/reclutador/usuarios | Lista usuarios reclutador |
| PATCH /api/reclutador/usuarios/{id}/toggle-status | Toggle estado usuario |

### No implementados ❌
| Endpoint | Uso faltante |
|---|---|
| POST /api/logoutapi | No se llama al cerrar sesión (solo limpia local) |
| GET /api/me | No se llama en Perfil (datos pueden ser viejos) |
| POST /api/perfil/password | Cambiar contraseña desde perfil |
| PATCH /api/viajes/mis-solicitudes/{idL}/calificar | Calificar viaje |

### Diferente a iOS
| Endpoint iOS | Comportamiento Android |
|---|---|
| GET /api/viajes/horarios?ruta={nombre} | No se usa; los horarios vienen incluidos en `/api/viajes/mis-rutas` |

---

## PRIORIDADES SUGERIDAS PARA COMPLETAR

### Alta prioridad (funcionalidades visibles al usuario)
1. ~~**Calificar viaje**~~ ✅ COMPLETADO (2026-06-19)
2. ~~**Cambiar contraseña desde perfil**~~ ✅ COMPLETADO (2026-06-19)
3. ~~**Card de calificación en DetalleSolicitud**~~ ✅ COMPLETADO (2026-06-19)
4. ~~**Diálogos de confirmación en Conductor**~~ ✅ COMPLETADO (2026-06-19)

### Media prioridad (mejora UX)
5. ~~**Mapa de ubicación**~~ ✅ COMPLETADO (2026-06-19) — OSMDroid, sin API key
6. ~~**GET /api/me en Perfil**~~ ✅ COMPLETADO (2026-06-19)
7. ~~**POST /api/logoutapi**~~ ✅ COMPLETADO (2026-06-19)
8. ~~**Picker de departamentos**~~ ✅ COMPLETADO (2026-06-19)
9. ~~**Botones abrir correo/teléfono en Soporte**~~ ✅ COMPLETADO (2026-06-19)
10. ~~**Dirección secundaria en Perfil**~~ ✅ COMPLETADO (2026-06-19)
11. ~~**Botón "Ver en mapa" en Perfil**~~ ✅ COMPLETADO (2026-06-19)

### Baja prioridad (detalles y polish)
12. **Calificación en celda de Mis Solicitudes**
13. **Pull-to-refresh** en todas las listas
14. **Animación de rotación** en botón refresh
15. **Diálogo confirmación cerrar sesión** en Perfil
16. **Diálogo confirmación toggle usuario** en Reclutador
17. **Animación shake** en campos con error (Registro)
18. **Correo y teléfono del pasajero** en DetalleViaje (Conductor)
19. **Deduplicación de solicitudes** por idL en Mis Solicitudes
