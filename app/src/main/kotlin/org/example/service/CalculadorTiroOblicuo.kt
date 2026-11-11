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
        if (datos.velocidadInicial <= 0.0) {
            return resultadoEstatico(datos.alturaInicial)
        }

        val (velocidadX, velocidadY) = descomponerVelocidades(datos)
        val tiempoVuelo = calcularTiempoVuelo(velocidadY, datos.alturaInicial)
        val alturaMaxima = calcularAlturaMaxima(velocidadY, datos.alturaInicial)
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
        if (datos.velocidadInicial <= 0.0) {
            return posicionEstatica(datos.alturaInicial)
        }

        val (vx, vy0) = descomponerVelocidades(datos)
        val tiempoVuelo = calcularTiempoVuelo(vy0, datos.alturaInicial)
        val tEfectivo = tiempo.coerceIn(0.0, tiempoVuelo.coerceAtLeast(0.0))

        val x = vx * tEfectivo
        val y = calcularAlturaInstantanea(datos.alturaInicial, vy0, tEfectivo, tiempoVuelo)
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
        if (datos.velocidadInicial <= 0.0) {
            return listOf(posicionEstatica(datos.alturaInicial))
        }

        val resultado = calcular(datos)
        val n = pasos.coerceAtLeast(2)
        val dt = if (resultado.tiempoVuelo > 0.0) resultado.tiempoVuelo / (n - 1) else 0.0
        return (0 until n).map { i ->
            val t = (i * dt).coerceAtMost(resultado.tiempoVuelo)
            calcularPosicion(datos, t)
        }
    }

    private fun descomponerVelocidades(datos: IngresoDatos): Pair<Double, Double> {
        val anguloRad = Math.toRadians(datos.angulo)
        val vx = datos.velocidadInicial * cos(anguloRad)
        val vy = datos.velocidadInicial * sin(anguloRad)
        return Pair(vx, vy)
    }

    private fun calcularTiempoVuelo(velocidadY: Double, alturaInicial: Double): Double =
        (velocidadY + sqrt(velocidadY * velocidadY + 2 * GRAVEDAD * alturaInicial)) / GRAVEDAD

    private fun calcularAlturaMaxima(velocidadY: Double, alturaInicial: Double): Double =
        alturaInicial + (velocidadY * velocidadY) / (2 * GRAVEDAD)

    private fun calcularAlturaInstantanea(
        alturaInicial: Double,
        vy0: Double,
        tEfectivo: Double,
        tiempoVuelo: Double
    ): Double {
        if (tEfectivo >= tiempoVuelo) return 0.0
        return (alturaInicial + vy0 * tEfectivo - 0.5 * GRAVEDAD * tEfectivo * tEfectivo).coerceAtLeast(0.0)
    }

    private fun resultadoEstatico(alturaInicial: Double): ResultadoTiroOblicuo =
        ResultadoTiroOblicuo(
            alturaMaxima = alturaInicial,
            distanciaHorizontal = 0.0,
            tiempoVuelo = 0.0,
            velocidadX = 0.0,
            velocidadY = 0.0
        )

    private fun posicionEstatica(alturaInicial: Double): PosicionTrayectoria =
        PosicionTrayectoria(
            x = 0.0,
            y = alturaInicial,
            tiempo = 0.0,
            velocidadX = 0.0,
            velocidadY = 0.0
        )

    companion object {
        /** Aceleración de la gravedad en la superficie terrestre (m/s²). */
        const val GRAVEDAD: Double = 9.81
    }
}
