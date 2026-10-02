package org.example.service

import org.example.model.IngresoDatos
import org.example.model.ResultadoTiroOblicuo

/**
 * Interfaz que define cómo calcular los resultados de un tiro oblicuo.
 * Separa el "qué" (firma del método) del "cómo" (la implementación física).
 */
interface CalculadorFisico {

    /**
     * Calcula los resultados del tiro oblicuo a partir de los datos de ingreso.
     */
    fun calcular(datos: IngresoDatos): ResultadoTiroOblicuo
}