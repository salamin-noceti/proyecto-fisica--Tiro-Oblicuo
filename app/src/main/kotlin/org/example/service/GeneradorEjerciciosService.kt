package org.example.service

import org.example.model.Dificultad
import org.example.model.Ejercicio

/**
 * Contrato de abstracción (Principio de Inversión de Dependencias - DIP)
 * para el servicio encargado de generar y proveer ejercicios de tiro oblicuo
 * según la dificultad seleccionada por el estudiante.
 *
 * Esta interfaz desacopla la pantalla de ejercicios (Issue 6) de la implementación
 * concreta desarrollada por el compañero Romano en la Issue 5.
 */
interface GeneradorEjerciciosService {

    /**
     * Obtiene o genera una instancia de [Ejercicio] según el nivel de dificultad elegido.
     *
     * @param dificultad Nivel de dificultad deseado ([Dificultad.FACIL], [Dificultad.MEDIO], [Dificultad.DIFICIL]).
     * @return [Ejercicio] listo para ser visualizado con su consigna y datos de lanzamiento.
     */
    fun obtenerEjercicioPorDificultad(dificultad: Dificultad): Ejercicio
}
