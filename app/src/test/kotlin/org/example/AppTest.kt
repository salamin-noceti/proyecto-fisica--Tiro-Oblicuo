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

    private val margenError = 0.01 // Margen de tolerancia para comparaciones de punto flotante

    @Test
    fun testCalculoTiroOblicuo45Grados() {
        val entrada = IngresoDatos(velocidadInicial = 50.0, angulo = 45.0)
        val calculador: CalculadorFisico = CalculadorTiroOblicuo()
        val resultados: ResultadoTiroOblicuo = calculador.calcular(entrada)

        // t_total = 2 * (50 * sin(45°)) / 9.81 ≈ 7.208 s
        assertTrue(abs(resultados.tiempoVuelo - 7.208) < margenError, "Tiempo total esperado ≈ 7.21 s")

        // h_max = (50 * sin(45°))^2 / (2 * 9.81) ≈ 63.71 m
        assertTrue(abs(resultados.alturaMaxima - 63.71) < margenError, "Altura máxima esperada ≈ 63.71 m")

        // alcance = (50 * cos(45°)) * 7.208 ≈ 254.84 m
        assertTrue(abs(resultados.distanciaHorizontal - 254.84) < margenError, "Distancia horizontal esperada ≈ 254.84 m")
    }

    @Test
    fun testCalculoConAlturaInicial() {
        val entrada = IngresoDatos(velocidadInicial = 20.0, angulo = 30.0, alturaInicial = 15.0)
        val calculador: CalculadorFisico = CalculadorTiroOblicuo()
        val resultados = calculador.calcular(entrada)

        assertTrue(resultados.tiempoVuelo > 0.0, "El tiempo de vuelo debe ser positivo")
        assertTrue(resultados.alturaMaxima >= 15.0, "La altura máxima debe ser mayor o igual a la altura inicial")
        assertTrue(resultados.distanciaHorizontal > 0.0, "La distancia horizontal debe ser positiva")
    }

    @Test
    fun testIngresoDatosValoresPorDefecto() {
        val entrada = IngresoDatos(velocidadInicial = 20.0, angulo = 30.0)
        assertEquals(0.0, entrada.alturaInicial, "La altura inicial por defecto debe ser 0.0")
    }
}
