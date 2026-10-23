package org.example.service

import org.example.model.IngresoDatos
import org.example.model.PosicionTrayectoria
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

    /**
     * Calcula la posición y velocidades instantáneas del proyectil en el instante [tiempo].
     */
    fun calcularPosicion(datos: IngresoDatos, tiempo: Double): PosicionTrayectoria

    /**
     * Calcula una secuencia de puntos de la trayectoria completa desde t=0 hasta t=tiempoVuelo.
     */
    fun calcularTrayectoria(datos: IngresoDatos, pasos: Int = 100): List<PosicionTrayectoria>
}
