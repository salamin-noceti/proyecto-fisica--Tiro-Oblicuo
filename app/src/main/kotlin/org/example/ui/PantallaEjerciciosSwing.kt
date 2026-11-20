package org.example.ui

import org.example.service.GeneradorEjerciciosService
import org.example.service.NavegadorSimulacionService
import org.example.service.ValidadorEntradaService
import org.example.ui.practica.componentes.CoordinadorPracticaService

/**
 * Implementación gráfica en Swing para la Pantalla de Ejercicios (Issue 6).
 */
class PantallaEjerciciosSwing(
    generadorService: GeneradorEjerciciosService? = null,
    navegadorSimulacion: NavegadorSimulacionService = NavegadorSimulacionService.porDefecto,
    validador: ValidadorEntradaService? = null
) : PantallaPractica(
    coordinador = CoordinadorPracticaService.paraSwing(generadorService, navegadorSimulacion, validador)
) {
    private val activa: Boolean = true

    fun estaActiva(): Boolean {
        return activa
    }
}
