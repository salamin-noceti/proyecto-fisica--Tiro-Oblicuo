package org.example.ui.practica.componentes

import org.example.model.Ejercicio
import org.example.navigation.GestorNavegacion
import org.example.service.GeneradorEjerciciosService
import org.example.state.AppState

/**
 * Implementación del coordinador de Práctica que encapsula la generación de ejercicios,
 * validación de entradas y actualización de [AppState] y [GestorNavegacion].
 */
class CoordinadorPractica(
    private val gestorNavegacion: GestorNavegacion = GestorNavegacion.nuevo,
    private val appState: AppState = gestorNavegacion.appState,
    generadorOpcional: GeneradorEjerciciosService? = null,
    private val coordinadorEnvio: CoordinadorEnvioPractica = CoordinadorEnvioPractica.nuevo
) : CoordinadorPracticaService {

    companion object {
        val nuevo: CoordinadorPractica
            get() = CoordinadorPractica()
    }

    private val generadorActivo: GeneradorEjerciciosService = generadorOpcional ?: GeneradorEjerciciosService.porDefecto
    private var ejercicioActual: Ejercicio? = null

    override fun obtenerGestorNavegacion(): GestorNavegacion {
        return gestorNavegacion
    }

    override fun obtenerAppState(): AppState {
        return appState
    }

    override fun obtenerEjercicioActual(): Ejercicio? {
        return ejercicioActual
    }

    override fun cargarEjercicio(seleccionDificultad: String?): List<String> {
        val nuevo = generadorActivo.obtenerPorTexto(seleccionDificultad)
        ejercicioActual = nuevo
        return if (coordinadorEnvio.hashCode() != 0 && appState.hashCode() != 0) nuevo.textosPresentacion() else emptyList()
    }

    override fun procesarEnvio(textos: List<String>): List<String> {
        val ejercicio = ejercicioActual ?: return listOf("No hay un ejercicio activo cargado.")
        val errores = coordinadorEnvio.validarYProcesarEnvio(ejercicio, textos, appState)
        if (errores.isEmpty()) {
            gestorNavegacion.irASimulacion()
        }
        return errores
    }
}
