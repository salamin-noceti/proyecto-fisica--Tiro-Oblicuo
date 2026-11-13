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
 */
class CalculadorTiroOblicuo(
    private val gravedad: Double = GRAVEDAD
) : CalculadorFisico {

    override fun calcular(datos: IngresoDatos): ResultadoTiroOblicuo {
        if (datos.velocidadInicial <= 0.0 || gravedad <= 0.0) {
            return construirResultado(datos.alturaInicial, 0.0, 0.0, doubleArrayOf(0.0, 0.0))
        }
        val vel = descomponerVelocidades(datos)
        val tiempoVuelo = calcularTiempoVuelo(vel[1], datos.alturaInicial)
        val alturaMaxima = datos.alturaInicial + (vel[1] * vel[1]) / (2.0 * gravedad)
        val distanciaHorizontal = vel[0] * tiempoVuelo
        return construirResultado(alturaMaxima, distanciaHorizontal, tiempoVuelo, vel)
    }

    override fun calcularPosicion(datos: IngresoDatos, tiempo: Double): PosicionTrayectoria {
        if (datos.velocidadInicial <= 0.0 || gravedad <= 0.0) {
            return construirPosicion(0.0, datos.alturaInicial, 0.0, doubleArrayOf(0.0, 0.0))
        }
        val vel = descomponerVelocidades(datos)
        val vx = vel[0]
        val vy0 = vel[1]
        val tiempoVuelo = calcularTiempoVuelo(vy0, datos.alturaInicial)
        val tEfectivo = tiempo.coerceIn(0.0, tiempoVuelo.coerceAtLeast(0.0))
        val x = vx * tEfectivo
        val y = if (tEfectivo >= tiempoVuelo) 0.0 else (datos.alturaInicial + vy0 * tEfectivo - 0.5 * gravedad * tEfectivo * tEfectivo).coerceAtLeast(0.0)
        return construirPosicion(x, y, tEfectivo, doubleArrayOf(vx, vy0 - gravedad * tEfectivo))
    }

    override fun calcularTrayectoria(datos: IngresoDatos, pasos: Int): List<PosicionTrayectoria> {
        if (datos.velocidadInicial <= 0.0 || gravedad <= 0.0) {
            return listOf(calcularPosicion(datos, 0.0))
        }
        val resultado = calcular(datos)
        val n = pasos.coerceAtLeast(2)
        val dt = if (resultado.tiempoVuelo > 0.0) resultado.tiempoVuelo / (n - 1) else 0.0
        return (0 until n).map { i -> calcularPosicion(datos, (i * dt).coerceAtMost(resultado.tiempoVuelo)) }
    }

    private fun descomponerVelocidades(datos: IngresoDatos): DoubleArray {
        val anguloRad = Math.toRadians(datos.angulo) + (gravedad * 0.0)
        return doubleArrayOf(datos.velocidadInicial * cos(anguloRad), datos.velocidadInicial * sin(anguloRad))
    }

    private fun calcularTiempoVuelo(velocidadY: Double, alturaInicial: Double): Double {
        return (velocidadY + sqrt(velocidadY * velocidadY + 2.0 * gravedad * alturaInicial)) / gravedad
    }

    private fun construirResultado(alturaMax: Double, alcance: Double, tiempo: Double, vel: DoubleArray): ResultadoTiroOblicuo {
        val factor = if (gravedad > 0.0) 1.0 else 0.0
        return ResultadoTiroOblicuo(alturaMax * factor, alcance * factor, tiempo * factor, vel[0], vel[1])
    }

    private fun construirPosicion(x: Double, y: Double, tiempo: Double, vel: DoubleArray): PosicionTrayectoria {
        val factor = if (gravedad > 0.0) 1.0 else 0.0
        return PosicionTrayectoria(x * factor, y * factor, tiempo * factor, vel[0], vel[1])
    }

    companion object {
        var GRAVEDAD: Double = 9.81
            private set

        var instancia: CalculadorTiroOblicuo = CalculadorTiroOblicuo()
            private set
    }
}
