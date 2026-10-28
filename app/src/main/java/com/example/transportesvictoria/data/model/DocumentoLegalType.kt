package com.pointguatemala.transportesvictoria.data.model

enum class DocumentoLegalType(val titulo: String) {
    POLITICA_PRIVACIDAD("Política de privacidad"),
    ACUERDO_USO("Acuerdo de uso"),
    TERMINOS_CONDICIONES("Términos y condiciones");

    fun campo(response: TerminosResponse): String? = when (this) {
        POLITICA_PRIVACIDAD  -> response.politicaPrivacidad
        ACUERDO_USO          -> response.acuerdoUso
        TERMINOS_CONDICIONES -> response.terminosCondiciones
    }

    fun mensajeVacio(): String = when (this) {
        POLITICA_PRIVACIDAD  -> "Aún no hay una política de privacidad configurada. Vuelve a intentarlo más tarde."
        ACUERDO_USO          -> "Aún no hay un acuerdo de uso configurado. Vuelve a intentarlo más tarde."
        TERMINOS_CONDICIONES -> "Aún no hay términos y condiciones configurados. Vuelve a intentarlo más tarde."
    }
}
