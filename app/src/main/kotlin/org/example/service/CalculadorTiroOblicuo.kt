package org.example.service

import org.example.model.IngresoDatos
import org.example.model.PosicionTrayectoria
import org.example.model.ResultadoTiroOblicuo
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Implementación concreta de [CalculadorFisico] que aplica
 * las fórmulas del tiro oblicuo (movimiento parabólico).
 *
 * La aceleración de la gravedad está fijada en [GRAVEDAD] y no se pide por
 * consola: es una constante física del modelo, no un dato del ejercicio.
 */
class CalculadorTiroOblicuo : CalculadorFisico {

    override fun calcular(datos: IngresoDatos): ResultadoTiroOblicuo {
        // Las funciones trigonométricas trabajan en radianes, convertimos el ángulo
        val anguloRad = Math.toRadians(datos.angulo)

        // Componentes de la velocidad inicial (descomponer en x e y)
        val velocidadX = datos.velocidadInicial * cos(anguloRad)
        val velocidadY = datos.velocidadInicial * sin(anguloRad)

        // Tiempo de vuelo: se resuelve y(t) = y0 + Vy*t - g*t²/2 = 0 y se toma
        // la raíz positiva. Con y0 = 0 se reduce a t = 2*Vy / g
        val tiempoVuelo =
            (velocidadY + sqrt(velocidadY * velocidadY + 2 * GRAVEDAD * datos.alturaInicial)) / GRAVEDAD

        // Altura máxima: el pico de la parábola, usando h = Vy² / (2 * g) sobre y0
        val alturaMaxima = datos.alturaInicial + (velocidadY * velocidadY) / (2 * GRAVEDAD)

        // Alcance horizontal: velocidad en x por el tiempo total de vuelo
        val distanciaHorizontal = velocidadX * tiempoVuelo

        return ResultadoTiroOblicuo(
            alturaMaxima = alturaMaxima,
            distanciaHorizontal = distanciaHorizontal,
            tiempoVuelo = tiempoVuelo,
            velocidadX = velocidadX,
            velocidadY = velocidadY
        )
    }

    override fun calcularPosicion(datos: IngresoDatos, tiempo: Double): PosicionTrayectoria {
        val anguloRad = Math.toRadians(datos.angulo)
        val vx = datos.velocidadInicial * cos(anguloRad)
        val vy0 = datos.velocidadInicial * sin(anguloRad)

        val tiempoVuelo =
            (vy0 + sqrt(vy0 * vy0 + 2 * GRAVEDAD * datos.alturaInicial)) / GRAVEDAD

        val tEfectivo = tiempo.coerceIn(0.0, tiempoVuelo.coerceAtLeast(0.0))
        val x = vx * tEfectivo
        val y = if (tEfectivo >= tiempoVuelo) {
            0.0
        } else {
            (datos.alturaInicial + vy0 * tEfectivo - 0.5 * GRAVEDAD * tEfectivo * tEfectivo).coerceAtLeast(0.0)
        }
        val vy = vy0 - GRAVEDAD * tEfectivo

        return PosicionTrayectoria(
            x = x,
            y = y,
            tiempo = tEfectivo,
            velocidadX = vx,
            velocidadY = vy
        )
    }

    override fun calcularTrayectoria(datos: IngresoDatos, pasos: Int): List<PosicionTrayectoria> {
        val resultado = calcular(datos)
        val n = pasos.coerceAtLeast(2)
        val dt = if (resultado.tiempoVuelo > 0.0) resultado.tiempoVuelo / (n - 1) else 0.0
        return (0 until n).map { i ->
            val t = (i * dt).coerceAtMost(resultado.tiempoVuelo)
            calcularPosicion(datos, t)
        }
    }

    companion object {
        /** Aceleración de la gravedad en la superficie terrestre (m/s²). */
        const val GRAVEDAD: Double = 9.81
    }
}
