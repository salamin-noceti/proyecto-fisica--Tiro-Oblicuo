package org.example.navigation

/**
 * Interfaz funcional para observar cambios en la navegación entre pantallas.
 */
fun interface NavegacionListener {
    /**
     * Invocado cuando la sección activa de la aplicación cambia.
     */
    fun onSeccionCambiada(nuevaSeccion: SeccionNavegacion)
}
