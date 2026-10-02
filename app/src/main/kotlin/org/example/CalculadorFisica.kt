package org.example

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

typealias TiroOblicuo = CalculadorFisica

class CalculadorFisica(
    val gravedad: Double = 9.81
) {
    init {
        require(gravedad > 0.0) {
            "La aceleración de la gravedad debe ser positiva: $gravedad m/s²"
        }
    }

    fun calcular(entrada: DatosEntrada): ResultadosTiro {
        
        val anguloRadianes = entrada.anguloGrados * (PI / 180.0)
        val vx = entrada.velocidadInicial * cos(anguloRadianes)
        val vy = entrada.velocidadInicial * sin(anguloRadianes)
        val tiempoSubida = vy / gravedad
        val tiempoTotal = 2.0 * tiempoSubida
        val alturaMaxima = (vy * vy) / (2.0 * gravedad)
        val alcanceHorizontal = vx * tiempoTotal

        return ResultadosTiro(
            vx = vx,
            vy = vy,
            tiempoSubida = tiempoSubida,
            tiempoTotal = tiempoTotal,
            alturaMaxima = alturaMaxima,
            alcanceHorizontal = alcanceHorizontal
        )
    }
}
