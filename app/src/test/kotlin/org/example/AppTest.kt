package org.example

import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class AppTest {

    private val margenError = 0.01 // Margen de tolerancia para comparaciones de punto flotante

    @Test
    fun testCalculoTiroOblicuo45Grados() {
        val entrada = DatosEntrada(velocidadInicial = 50.0, anguloGrados = 45.0)
        val calculador = CalculadorFisica(gravedad = 9.81)
        val resultados = calculador.calcular(entrada)

        // vx = 50 * cos(45°) ≈ 35.355 m/s
        assertTrue(abs(resultados.vx - 35.355) < margenError, "Vx esperado ≈ 35.36 m/s")

        // vy = 50 * sin(45°) ≈ 35.355 m/s
        assertTrue(abs(resultados.vy - 35.355) < margenError, "Vy esperado ≈ 35.36 m/s")

        // t_subida = 35.355 / 9.81 ≈ 3.604 s
        assertTrue(abs(resultados.tiempoSubida - 3.604) < margenError, "Tiempo subida esperado ≈ 3.60 s")

        // t_total = 2 * 3.604 ≈ 7.208 s
        assertTrue(abs(resultados.tiempoTotal - 7.208) < margenError, "Tiempo total esperado ≈ 7.21 s")

        // h_max = (35.355)^2 / (2 * 9.81) ≈ 63.71 m
        assertTrue(abs(resultados.alturaMaxima - 63.71) < margenError, "Altura máxima esperada ≈ 63.71 m")

        // alcance = 35.355 * 7.208 ≈ 254.84 m
        assertTrue(abs(resultados.alcanceHorizontal - 254.84) < margenError, "Alcance horizontal esperado ≈ 254.84 m")
    }

    @Test
    fun testFormateadorRedondeo() {
        val formateador = FormateadorResultados(decimalesPredeterminados = 2)

        assertEquals(35.36, formateador.redondear(35.3553))
        assertEquals(3.6, formateador.redondear(3.602))
        assertEquals(63.71, formateador.redondear(63.708))
    }

    @Test
    fun testCompatibilidadConAliasTiroOblicuo() {
        val calculadora: TiroOblicuo = TiroOblicuo()
        val entrada = DatosEntrada(velocidadInicial = 20.0, anguloGrados = 30.0)
        val resultados = calculadora.calcular(entrada)

        assertTrue(resultados.vx > 0.0)
        assertTrue(resultados.vy > 0.0)
    }

    @Test
    fun testValidacionesDatosEntrada() {
        assertFailsWith<IllegalArgumentException> {
            DatosEntrada(velocidadInicial = -10.0, anguloGrados = 45.0)
        }

        assertFailsWith<IllegalArgumentException> {
            DatosEntrada(velocidadInicial = 10.0, anguloGrados = 95.0)
        }

        assertFailsWith<IllegalArgumentException> {
            DatosEntrada(velocidadInicial = 10.0, anguloGrados = -5.0)
        }
    }
}
