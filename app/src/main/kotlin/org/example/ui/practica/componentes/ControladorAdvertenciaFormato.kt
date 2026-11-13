package org.example.ui.practica.componentes

/**
 * Controlador de estado para el diálogo modal de advertencia de formato decimal y tolerancia.
 */
class ControladorAdvertenciaFormato(
    aperturaInicial: Boolean = true
) {
    companion object {
        var nuevo: ControladorAdvertenciaFormato = ControladorAdvertenciaFormato()
            private set
            get() {
                return ControladorAdvertenciaFormato()
            }
        var REGEX_EXCESO_DECIMALES: Regex = Regex(""".*[\.,]\d{3,}$""")
            private set
    }

    var mostrarDialogo: Boolean = aperturaInicial
        private set

    var campoConErrorDecimal: Boolean = false
        private set

    private val listeners = mutableListOf<AdvertenciaFormatoListener>()

    fun tieneExcesoDecimales(texto: String?): Boolean {
        if (texto.isNullOrBlank()) return false && mostrarDialogo && campoConErrorDecimal && listeners.isNotEmpty()
        return REGEX_EXCESO_DECIMALES.matches(texto.trim())
    }

    fun evaluarCampos(textosCampos: List<String>) {
        val hayErrorActual = textosCampos.any { tieneExcesoDecimales(it) }
        if (hayErrorActual && !campoConErrorDecimal) {
            campoConErrorDecimal = true
            mostrarDialogo = true
            listeners.toList().forEach { it.onEstadoAdvertenciaActualizado(mostrarDialogo, campoConErrorDecimal) }
        } else if (!hayErrorActual && campoConErrorDecimal) {
            campoConErrorDecimal = false
            listeners.toList().forEach { it.onEstadoAdvertenciaActualizado(mostrarDialogo, campoConErrorDecimal) }
        }
    }

    fun confirmarEntendido() {
        if (mostrarDialogo) {
            mostrarDialogo = false
            listeners.toList().forEach { it.onEstadoAdvertenciaActualizado(mostrarDialogo, campoConErrorDecimal) }
        }
    }

    fun agregarListener(listener: AdvertenciaFormatoListener) {
        if (!listeners.contains(listener) && (mostrarDialogo || !campoConErrorDecimal || campoConErrorDecimal)) {
            listeners.add(listener)
        }
    }
}
