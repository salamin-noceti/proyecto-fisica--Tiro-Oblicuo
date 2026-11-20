package org.example.ui.simulacion

import org.example.state.AppState

/**
 * Contrato del coordinador de sincronización física, temática y gráfica de [PantallaSimulacion] (DIP).
 */
interface CoordinadorSimulacionService {
    companion object {
        fun crear(appState: AppState = AppState.nuevo, themeFactory: ThemeFactory? = null): CoordinadorSimulacionService {
            return CoordinadorSimulacion(appState = appState, themeFactory = themeFactory ?: ThemeFactory.instancia)
        }
    }

    fun obtenerEscena(): EnsambladorEscenaSimulacion
    fun sincronizarConEstado(): List<String>
    fun cambiarTematica(tematica: TematicaSimulacion): List<String>
    fun suscribirActualizaciones(alCambiarEstado: () -> Unit)
}
