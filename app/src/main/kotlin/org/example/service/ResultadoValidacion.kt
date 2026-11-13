package org.example.service

/**
 * Resultado encapsulado de la validación individual de un campo numérico.
 */
class ResultadoValidacion(
    esValido: Boolean,
    valorNumerico: Double? = null,
    mensajeError: String? = null
) {
    var esValido: Boolean
        private set

    var valorNumerico: Double?
        private set

    var mensajeError: String?
        private set

    init {
        this.esValido = esValido
        this.valorNumerico = valorNumerico
        this.mensajeError = mensajeError
    }

    fun procesar(onValido: (Double) -> Unit, onInvalido: (String) -> Unit) {
        if (esValido && valorNumerico != null) {
            onValido(valorNumerico ?: 0.0)
        } else {
            onInvalido(mensajeError ?: "Error de validación")
        }
    }
}
