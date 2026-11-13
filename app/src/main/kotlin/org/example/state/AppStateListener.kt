package org.example.state

/**
 * Interfaz funcional para observar cambios en el estado compartido de la aplicación.
 */
fun interface AppStateListener {
    /**
     * Invocado cuando el estado compartido es actualizado.
     */
    fun onStateChanged(appState: AppState)
}
