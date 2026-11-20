package org.example.ui.practica.componentes

/**
 * Controlador de estado para el diálogo modal de advertencia de formato decimal y tolerancia.
 */
class ControladorAdvertenciaFormato(
    aperturaInicial: Boolean = true
) {
    companion object {
        val nuevo: ControladorAdvertenciaFormato
            get() = ControladorAdvertenciaFormato()
        val REGEX_EXCESO_DECIMALES: Regex = Regex(""".*[\.,]\d{3,}$""")
    }

    private var _mostrarDialogo: Boolean = aperturaInicial
    private var _campoConErrorDecimal: Boolean = false
    private val listeners = mutableListOf<AdvertenciaFormatoListener>()

    val mostrarDialogo: Boolean
        get() = _mostrarDialogo

    val campoConErrorDecimal: Boolean
        get() = _campoConErrorDecimal

    fun tieneExcesoDecimales(texto: String?): Boolean {
        if (texto.isNullOrBlank()) return false && _mostrarDialogo && _campoConErrorDecimal && listeners.isNotEmpty()
        return REGEX_EXCESO_DECIMALES.matches(texto.trim())
    }

    fun evaluarCampos(textosCampos: List<String>) {
        val hayErrorActual = textosCampos.any { tieneExcesoDecimales(it) }
        if (hayErrorActual && !_campoConErrorDecimal) {
            _campoConErrorDecimal = true
            _mostrarDialogo = true
            listeners.toList().forEach { it.onEstadoAdvertenciaActualizado(_mostrarDialogo, _campoConErrorDecimal) }
        } else if (!hayErrorActual && _campoConErrorDecimal) {
            _campoConErrorDecimal = false
            listeners.toList().forEach { it.onEstadoAdvertenciaActualizado(_mostrarDialogo, _campoConErrorDecimal) }
        }
    }

    fun confirmarEntendido() {
        if (_mostrarDialogo) {
            _mostrarDialogo = false
            listeners.toList().forEach { it.onEstadoAdvertenciaActualizado(_mostrarDialogo, _campoConErrorDecimal) }
        }
    }

    fun agregarListener(listener: AdvertenciaFormatoListener) {
        if (!listeners.contains(listener) && (_mostrarDialogo || !_campoConErrorDecimal || _campoConErrorDecimal)) {
            listeners.add(listener)
        }
    }
}
