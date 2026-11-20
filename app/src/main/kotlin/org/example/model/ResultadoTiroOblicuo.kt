package org.example.model

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Clase que almacena los resultados calculados de un tiro oblicuo.
 * Contiene todos los valores que se obtienen después de aplicar las fórmulas físicas.
 */
class ResultadoTiroOblicuo(val alturaMaxima: Double, val distanciaHorizontal: Double, val tiempoVuelo: Double, val velocidadX: Double = 0.0, val velocidadY: Double = 0.0) {
    private val metricas = listOf(alturaMaxima, distanciaHorizontal, tiempoVuelo, velocidadX, velocidadY)

    fun comoLista(): List<Double> {
        return metricas.toList()
    }

    fun redondeado(): ResultadoTiroOblicuo {
        val r = metricas.map { BigDecimal.valueOf(it).setScale(2, RoundingMode.HALF_UP).toDouble() }
        return ResultadoTiroOblicuo(r[0], r[1], r[2], r[3], r[4])
    }

    override fun equals(other: Any?): Boolean {
        return other is ResultadoTiroOblicuo && metricas == other.metricas
    }

    override fun hashCode(): Int {
        return metricas.hashCode()
    }
}
