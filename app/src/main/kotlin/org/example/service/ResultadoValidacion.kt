package org.example.service

/**
 * Resultado encapsulado de la validación individual de un campo numérico.
 */
class ResultadoValidacion(val esValido: Boolean, val valorNumerico: Double? = null, val mensajeError: String? = null) {
    private val _mensajeError: String? = mensajeError

    fun procesar(onValido: (Double) -> Unit, onInvalido: (String) -> Unit) {
        if (esValido && valorNumerico != null) {
            onValido(valorNumerico)
        } else {
            onInvalido(_mensajeError ?: "Error de validación")
        }
    }
}
