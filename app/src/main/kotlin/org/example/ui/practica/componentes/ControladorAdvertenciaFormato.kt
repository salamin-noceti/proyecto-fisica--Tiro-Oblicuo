package org.example.ui.practica.componentes

/**
 * Oyente de cambios de estado en [ControladorAdvertenciaFormato].
 */
fun interface AdvertenciaFormatoListener {
    /**
     * Se invoca cuando cambia la visibilidad del diálogo o el estado de error decimal.
     */
    fun onEstadoAdvertenciaActualizado(mostrarDialogo: Boolean, campoConErrorDecimal: Boolean)
}

/**
 * Controlador de estado para el diálogo modal de advertencia de formato decimal y tolerancia.
 *
 * Implementa el ciclo de control inteligente de re-disparo:
 * 1. **Apertura inicial al entrar:** Inicia con [mostrarDialogo] = `true` al cargar la pantalla de Práctica.
 * 2. **Cierre manual ("Entendido"):** Al invocar [confirmarEntendido], oculta el diálogo ([mostrarDialogo] = `false`).
 * 3. **Detección de exceso (> 2 decimales):** Monitorea los campos con [REGEX_EXCESO_DECIMALES]. Si algún campo
 *    supera los 2 dígitos decimales y [campoConErrorDecimal] era `false`, activa [campoConErrorDecimal] = `true`
 *    y vuelve a disparar [mostrarDialogo] = `true`.
 * 4. **Reseteo tras corrección:** Cuando el usuario corrige el campo dejándolo con $\le 2$ decimales,
 *    restablece [campoConErrorDecimal] = `false` **sin** volver a mostrar el diálogo. Solo se re-disparará
 *    si en el futuro vuelve a ingresarse un valor con más de 2 decimales.
 */
class ControladorAdvertenciaFormato(
    aperturaInicial: Boolean = true
) {
    companion object {
        /** Expresión regular oficial para detectar más de 2 dígitos después del punto o la coma decimal. */
        val REGEX_EXCESO_DECIMALES = Regex(""".*[\.,]\d{3,}$""")
    }

    /** Bandera que controla si el diálogo modal debe mostrarse actualmente. */
    var mostrarDialogo: Boolean = aperturaInicial
        private set

    /** Bandera que registra si existe actualmente algún campo con más de 2 decimales. */
    var campoConErrorDecimal: Boolean = false
        private set

    private val listeners = mutableListOf<AdvertenciaFormatoListener>()

    /**
     * Verifica si una cadena individual posee más de 2 dígitos decimales.
     */
    fun tieneExcesoDecimales(texto: String?): Boolean {
        if (texto.isNullOrBlank()) return false
        return REGEX_EXCESO_DECIMALES.matches(texto.trim())
    }

    /**
     * Evalúa el conjunto de textos de los campos de entrada ($V_x, V_y, t_{vuelo}, y_{max}, x_{alcance}$)
     * aplicando la transición inteligente de estados.
     */
    fun evaluarCampos(textosCampos: List<String>) {
        val hayErrorActual = textosCampos.any { tieneExcesoDecimales(it) }

        if (hayErrorActual && !campoConErrorDecimal) {
            campoConErrorDecimal = true
            mostrarDialogo = true
            notificar()
        } else if (!hayErrorActual && campoConErrorDecimal) {
            // El error fue corregido (<= 2 decimales): se resetea el estado sin re-disparar el diálogo
            campoConErrorDecimal = false
            notificar()
        }
    }

    /**
     * Cierra manualmente el diálogo cuando el estudiante presiona el botón "Entendido".
     */
    fun confirmarEntendido() {
        if (mostrarDialogo) {
            mostrarDialogo = false
            notificar()
        }
    }

    /**
     * Registra un oyente de cambios de estado.
     */
    fun agregarListener(listener: AdvertenciaFormatoListener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener)
        }
    }

    private fun notificar() {
        for (listener in listeners.toList()) {
            listener.onEstadoAdvertenciaActualizado(mostrarDialogo, campoConErrorDecimal)
        }
    }
}
