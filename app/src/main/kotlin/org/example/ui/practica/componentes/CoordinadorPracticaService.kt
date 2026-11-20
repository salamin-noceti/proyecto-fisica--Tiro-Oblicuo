package org.example.ui.practica.componentes

import org.example.model.Ejercicio
import org.example.navigation.GestorNavegacion
import org.example.service.GeneradorEjerciciosService
import org.example.service.NavegadorSimulacionService
import org.example.service.ValidadorEntradaService
import org.example.state.AppState

/**
 * Contrato del coordinador de lógica de negocio y estado para `PantallaPractica` (DIP).
 */
interface CoordinadorPracticaService {
    companion object {
        val porDefecto: CoordinadorPracticaService
            get() = CoordinadorPractica.nuevo

        fun paraSwing(generador: GeneradorEjerciciosService?, navegador: NavegadorSimulacionService, validador: ValidadorEntradaService?): CoordinadorPracticaService {
            return CoordinadorPractica(generadorOpcional = generador, coordinadorEnvio = CoordinadorEnvioPractica(validador, null, navegador))
        }
    }

    fun obtenerGestorNavegacion(): GestorNavegacion
    fun obtenerAppState(): AppState
    fun obtenerEjercicioActual(): Ejercicio?
    fun cargarEjercicio(seleccionDificultad: String?): List<String>
    fun procesarEnvio(textos: List<String>): List<String>
}
