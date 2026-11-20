package org.example.ui.autocorrecion

import org.example.model.ReporteAutocorreccion
import org.example.state.AppState

/**
 * Abstracción del coordinador de evaluación y estado para [PantallaAutocorreccion] (DIP).
 */
interface CoordinadorAutocorreccionService {
    companion object {
        val porDefecto: CoordinadorAutocorreccionService
            get() = CoordinadorAutocorreccion.nuevo

        fun crear(appState: AppState): CoordinadorAutocorreccionService {
            return CoordinadorAutocorreccion(appState = appState)
        }
    }

    fun obtenerReporteActual(): ReporteAutocorreccion
    fun evaluarEstadoActual(): ReporteAutocorreccion
    fun suscribirCambios(accion: () -> Unit)
}
