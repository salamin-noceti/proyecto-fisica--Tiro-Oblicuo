// Implementa los cálculos físicos del tiro oblicuo usando las fórmulas de cinemática.
// Implementa la interfaz CalculadorFisico (DIP de SOLID).
package org.example.service

import org.example.model.IngresoDatos
import org.example.model.ResultadoTiroOblicuo
import kotlin.math.*

class CalculadorTiroOblicuo : CalculadorFisico {

    // Aceleración de la gravedad en m/s² (cerca de la superficie terrestre)
    private val gravedad: Double = 9.81

    override fun calcular(datos: IngresoDatos): ResultadoTiroOblicuo {
        // Convertimos el ángulo de grados a radianes (así lo entiende Kotlin)
        val anguloRadianes = Math.toRadians(datos.anguloGrados)

        // Componentes de la velocidad inicial
        // vx = v₀ · cos(θ)  →  velocidad horizontal (constante, no hay aire)
        val vx = datos.velocidadInicial * cos(anguloRadianes)

        // vy = v₀ · sin(θ)  →  velocidad vertical inicial
        val vyInicial = datos.velocidadInicial * sin(anguloRadianes)

        // Tiempo que tarda en subir hasta la altura máxima (donde vy = 0)
        // t_subida = vy / g
        val tiempoSubida = vyInicial / gravedad

        // Tiempo total de vuelo = 2 × tiempo de subida (simplificado para altura inicial = 0)
        val tiempoVuelo = 2.0 * tiempoSubida

        // Alcance horizontal = vx × tiempoTotal
        val alcanceHorizontal = vx * tiempoVuelo

        // Altura máxima = vy² / (2·g)
        val alturaMaxima = (vyInicial * vyInicial) / (2.0 * gravedad)

        return ResultadoTiroOblicuo(
            alcanceHorizontal = alcanceHorizontal,
            alturaMaxima = alturaMaxima,
            tiempoVuelo = tiempoVuelo,
            vx = vx,
            vy = vyInicial
        )
    }
}
