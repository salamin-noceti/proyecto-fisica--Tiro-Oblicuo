package org.example.service

import org.example.model.Ejercicio
import org.example.model.RespuestaEstudiante

/**
 * Contrato de abstracción (Principio de Inversión de Dependencias - DIP)
 * para la transferencia de flujo y datos desde la pantalla de ejercicios (Issue 6)
 * hacia el módulo o pantalla de simulación y verificación física (Issues 7 y 9).
 */
interface NavegadorSimulacionService {

    /**
     * Transfiere el control, el ejercicio resuelto y los valores numéricos
     * calculados por el estudiante hacia el módulo de simulación.
     *
     * @param ejercicio Ejercicio que contiene el enunciado y las condiciones físicas iniciales.
     * @param respuesta Valores numéricos (alcance, altura máxima y tiempo de vuelo) cargados por el alumno.
     */
    fun transferirASimulacion(ejercicio: Ejercicio, respuesta: RespuestaEstudiante)
}
