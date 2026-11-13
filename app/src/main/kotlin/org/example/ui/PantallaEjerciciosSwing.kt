package org.example.ui

import org.example.service.GeneradorEjerciciosService
import org.example.service.NavegadorSimulacionService
import org.example.service.SimulacionConsoleRunner
import org.example.service.ValidadorEntradaService

/**
 * Implementación gráfica en Swing para la Pantalla de Ejercicios (Issue 6).
 */
class PantallaEjerciciosSwing(
    generadorService: GeneradorEjerciciosService? = null,
    navegadorSimulacion: NavegadorSimulacionService = SimulacionConsoleRunner(),
    validador: ValidadorEntradaService? = null
) : PantallaPractica(
    generadorService = generadorService,
    navegadorSimulacion = navegadorSimulacion,
    validador = validador
) {
    private val activa: Boolean = true

    fun estaActiva(): Boolean {
        return activa
    }
}
