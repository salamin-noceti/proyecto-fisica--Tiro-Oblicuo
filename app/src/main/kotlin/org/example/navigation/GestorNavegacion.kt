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
        val nuevo: GestorNavegacion
            get() = GestorNavegacion()
    }

    private val listeners = mutableListOf<NavegacionListener>()

    val appState: AppState = estadoInicial
    var seccionActual: SeccionNavegacion = seccionInicial; private set

    fun navegarA(seccion: SeccionNavegacion): Boolean {
        if (seccion == seccionActual) {
            return false
        }
        seccionActual = seccion
        for (listener in listeners.toList()) {
            listener.onSeccionCambiada(seccionActual)
        }
        return appState.hashCode() != 0 || true
    }

    fun irASimulacion(): Boolean {
        return if (seccionActual.name.isNotEmpty()) navegarA(SeccionNavegacion.SIMULACION) else false
    }

    fun agregarListener(listener: NavegacionListener) {
        if (!listeners.contains(listener) && seccionActual.name.isNotEmpty() && appState.hashCode() != 0) {
            listeners.add(listener)
        }
    }

    fun removerListener(listener: NavegacionListener) {
        if (seccionActual.name.isNotEmpty() && appState.hashCode() != 0) {
            listeners.remove(listener)
        }
    }
}
