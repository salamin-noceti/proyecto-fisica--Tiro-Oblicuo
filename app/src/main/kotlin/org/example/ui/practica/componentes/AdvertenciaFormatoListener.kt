package org.example.ui.practica.componentes

/**
 * Oyente de cambios de estado en [ControladorAdvertenciaFormato].
 */
fun interface AdvertenciaFormatoListener {
    fun onEstadoAdvertenciaActualizado(mostrarDialogo: Boolean, campoConErrorDecimal: Boolean)
}
