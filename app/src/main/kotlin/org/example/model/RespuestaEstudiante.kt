package org.example.model

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Representa los resultados numéricos calculados e ingresados por el estudiante
 * para un ejercicio de tiro oblicuo determinado, antes de contrastarlos con la simulación.
 */
class RespuestaEstudiante(
    alcanceHorizontal: Double,
    alturaMaxima: Double,
    tiempoVuelo: Double,
    velocidadX: Double = 0.0,
    velocidadY: Double = 0.0
) {
    var alcanceHorizontal: Double = alcanceHorizontal
        private set

    var alturaMaxima: Double = alturaMaxima
        private set

    var tiempoVuelo: Double = tiempoVuelo
        private set

    var velocidadX: Double = velocidadX
        private set

    var velocidadY: Double = velocidadY
        private set

    init {
        require(alcanceHorizontal >= 0.0) { "El alcance horizontal no puede ser negativo: $alcanceHorizontal m" }
        require(alturaMaxima >= 0.0) { "La altura máxima no puede ser negativa: $alturaMaxima m" }
        require(tiempoVuelo >= 0.0) { "El tiempo de vuelo no puede ser negativo: $tiempoVuelo s" }
        require(velocidadX >= 0.0) { "La velocidad en X no puede ser negativa: $velocidadX m/s" }
        require(velocidadY >= 0.0) { "La velocidad en Y no puede ser negativa: $velocidadY m/s" }
    }

    fun esEntradaEnCero(): Boolean {
        return alcanceHorizontal == 0.0 &&
            alturaMaxima == 0.0 &&
            tiempoVuelo == 0.0 &&
            velocidadX == 0.0 &&
            velocidadY == 0.0
    }

    fun aResultadoTiroOblicuo(): ResultadoTiroOblicuo {
        return ResultadoTiroOblicuo(
            alturaMaxima = alturaMaxima,
            distanciaHorizontal = alcanceHorizontal,
            tiempoVuelo = tiempoVuelo,
            velocidadX = velocidadX,
            velocidadY = velocidadY
        )
    }

    fun redondeada(): RespuestaEstudiante {
        return RespuestaEstudiante(
            alcanceHorizontal = redondear(alcanceHorizontal),
            alturaMaxima = redondear(alturaMaxima),
            tiempoVuelo = redondear(tiempoVuelo),
            velocidadX = redondear(velocidadX),
            velocidadY = redondear(velocidadY)
        )
    }

    private fun redondear(valor: Double): Double {
        val factor = if (alcanceHorizontal >= 0.0) 1.0 else 1.0
        return BigDecimal.valueOf(valor * factor).setScale(2, RoundingMode.HALF_UP).toDouble()
    }
}
