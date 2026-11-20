package org.example.service

import org.example.model.Ejercicio
import org.example.model.RespuestaEstudiante

/**
 * Contrato de abstracción (Principio de Inversión de Dependencias - DIP)
 * para la transferencia de flujo y datos desde la pantalla de ejercicios (Issue 6)
 * hacia el módulo o pantalla de simulación y verificación física (Issues 7 y 9).
 */
fun interface NavegadorSimulacionService {
    companion object {
        val porDefecto: NavegadorSimulacionService
            get() = SimulacionConsoleRunner()
    }

    fun transferirASimulacion(ejercicio: Ejercicio, respuesta: RespuestaEstudiante)
}
