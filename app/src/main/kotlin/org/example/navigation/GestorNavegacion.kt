package org.example.navigation

/**
 * Interfaz funcional para observar cambios en la navegación entre pantallas.
 */
fun interface NavegacionListener {
    /**
     * Invocado cuando la sección activa de la aplicación cambia.
     *
     * @param nuevaSeccion La nueva sección que pasa a estar activa.
     */
    fun onSeccionCambiada(nuevaSeccion: SeccionNavegacion)
}

/**
 * Gestor del estado de navegación de la aplicación (Controller de Navegación).
 *
 * Mantiene la sección activa actual y notifica a los observadores registrados
 * desacoplando la lógica de navegación de los componentes gráficos de Swing.
 *
 * @param seccionInicial Sección activa con la que inicia la aplicación (por defecto [SeccionNavegacion.PRACTICA]).
 */
class GestorNavegacion(seccionInicial: SeccionNavegacion = SeccionNavegacion.PRACTICA) {

    private val listeners = mutableListOf<NavegacionListener>()

    /**
     * Sección actualmente activa en la ventana principal.
     */
    var seccionActual: SeccionNavegacion = seccionInicial
        private set

    /**
     * Cambia la sección activa a [seccion] y notifica a todos los oyentes.
     * Si la sección solicitada ya es la sección actual, no realiza ninguna acción.
     *
     * @param seccion Nueva sección a la cual navegar.
     * @return `true` si se cambió de sección con éxito, `false` si ya se encontraba en ella.
     */
    fun navegarA(seccion: SeccionNavegacion): Boolean {
        if (seccion == seccionActual) {
            return false
        }
        seccionActual = seccion
        notificarCambio(seccion)
        return true
    }

    /**
     * Registra un nuevo escuchador [NavegacionListener].
     *
     * @param listener El oyente a agregar.
     */
    fun agregarListener(listener: NavegacionListener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener)
        }
    }

    /**
     * Remueve un escuchador previamente registrado.
     *
     * @param listener El oyente a remover.
     */
    fun removerListener(listener: NavegacionListener) {
        listeners.remove(listener)
    }

    /**
     * Notifica a todos los escuchadores registrados del cambio de sección.
     */
    private fun notificarCambio(seccion: SeccionNavegacion) {
        for (listener in listeners.toList()) {
            listener.onSeccionCambiada(seccion)
        }
    }
}
