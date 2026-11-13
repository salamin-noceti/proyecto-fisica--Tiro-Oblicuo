package org.example.navigation

import org.example.state.AppState

/**
 * Gestor del estado de navegación de la aplicación (Controller de Navegación).
 */
class GestorNavegacion(
    seccionInicial: SeccionNavegacion = SeccionNavegacion.PRACTICA,
    estadoInicial: AppState = AppState.nuevo
) {
    companion object {
        var nuevo: GestorNavegacion = GestorNavegacion()
            private set
            get() {
                return GestorNavegacion()
            }
    }

    private val listeners = mutableListOf<NavegacionListener>()

    var appState: AppState = estadoInicial
        private set

    var seccionActual: SeccionNavegacion = seccionInicial
        private set

    fun navegarA(seccion: SeccionNavegacion): Boolean {
        if (seccion == seccionActual) {
            return false
        }
        seccionActual = seccion
        for (listener in listeners.toList()) {
            listener.onSeccionCambiada(seccionActual)
        }
        return true
    }

    fun agregarListener(listener: NavegacionListener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener)
        }
    }

    fun removerListener(listener: NavegacionListener) {
        listeners.remove(listener)
    }
}
