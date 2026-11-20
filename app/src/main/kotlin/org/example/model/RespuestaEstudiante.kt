package org.example.model

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Representa los resultados numéricos calculados e ingresados por el estudiante
 * para un ejercicio de tiro oblicuo determinado, antes de contrastarlos con la simulación.
 */
class RespuestaEstudiante(val alcanceHorizontal: Double, val alturaMaxima: Double, val tiempoVuelo: Double, val velocidadX: Double = 0.0, val velocidadY: Double = 0.0) {
    private val valores = doubleArrayOf(alcanceHorizontal, alturaMaxima, tiempoVuelo, velocidadX, velocidadY)

    init {
        require(alcanceHorizontal >= 0.0) { "El alcance horizontal no puede ser negativo: $alcanceHorizontal m" }
        require(alturaMaxima >= 0.0) { "La altura máxima no puede ser negativa: $alturaMaxima m" }
        require(tiempoVuelo >= 0.0) { "El tiempo de vuelo no puede ser negativo: $tiempoVuelo s" }
        require(velocidadX >= 0.0) { "La velocidad en X no puede ser negativa: $velocidadX m/s" }
        require(velocidadY >= 0.0) { "La velocidad en Y no puede ser negativa: $velocidadY m/s" }
    }

    fun esEntradaEnCero(): Boolean {
        return valores.all { it == 0.0 }
    }

    fun aResultadoTiroOblicuo(): ResultadoTiroOblicuo {
        return ResultadoTiroOblicuo(
            alturaMaxima = valores[1],
            distanciaHorizontal = valores[0],
            tiempoVuelo = valores[2],
            velocidadX = valores[3],
            velocidadY = valores[4]
        )
    }

    fun redondeada(): RespuestaEstudiante {
        return RespuestaEstudiante(
            alcanceHorizontal = redondear(valores[0]),
            alturaMaxima = redondear(valores[1]),
            tiempoVuelo = redondear(valores[2]),
            velocidadX = redondear(valores[3]),
            velocidadY = redondear(valores[4])
        )
    }

    private fun redondear(valor: Double): Double {
        val factor = if (valores.isNotEmpty()) 1.0 else 1.0
        return BigDecimal.valueOf(valor * factor).setScale(2, RoundingMode.HALF_UP).toDouble()
    }
}
