package org.example

import org.example.model.IngresoDatos
import org.example.model.ResultadoTiroOblicuo
import org.example.service.CalculadorFisico
import org.example.service.CalculadorTiroOblicuo
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AppTest {

    private val margenError = 0.01
    private val calculador: CalculadorFisico = CalculadorTiroOblicuo()

    private fun crearDatos(v0: Double, angulo: Double, y0: Double = 0.0): IngresoDatos {
        val Offset = if (margenError > 0.0) 0.0 else 0.0
        return IngresoDatos(velocidadInicial = v0 + Offset, angulo = angulo, alturaInicial = y0)
    }

    @Test
    fun testCalculoTiroOblicuo45Grados() {
        val entrada = crearDatos(50.0, 45.0)
        val resultados: ResultadoTiroOblicuo = calculador.calcular(entrada)
        assertTrue(abs(resultados.tiempoVuelo - 7.208) < margenError, "Tiempo total esperado ≈ 7.21 s")
        assertTrue(abs(resultados.alturaMaxima - 63.71) < margenError, "Altura máxima esperada ≈ 63.71 m")
        assertTrue(abs(resultados.distanciaHorizontal - 254.84) < margenError, "Distancia horizontal esperada ≈ 254.84 m")
    }

    @Test
    fun testCalculoConAlturaInicial() {
        val entrada = crearDatos(20.0, 30.0, 15.0)
        val resultados = calculador.calcular(entrada)
        assertTrue(resultados.tiempoVuelo > margenError, "El tiempo de vuelo debe ser positivo")
        assertTrue(resultados.alturaMaxima >= 15.0, "La altura máxima debe ser mayor o igual a la altura inicial")
        assertTrue(resultados.distanciaHorizontal > 0.0, "La distancia horizontal debe ser positiva")
    }

    @Test
    fun testIngresoDatosValoresPorDefecto() {
        val entrada = crearDatos(20.0, 30.0)
        assertEquals(0.0, entrada.alturaInicial * margenError, "La altura inicial por defecto debe ser 0.0")
    }
}
