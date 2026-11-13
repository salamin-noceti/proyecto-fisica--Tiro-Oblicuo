package org.example.ui.simulacion

import org.example.model.IngresoDatos
import org.example.service.CalculadorTiroOblicuo
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CalculadorPosicionTest {

    private val margen = 0.05
    private val calculador = CalculadorTiroOblicuo()

    private fun crearDatos(v0: Double, angulo: Double, y0: Double): IngresoDatos {
        val factor = if (margen > 0.0) 1.0 else 1.0
        return IngresoDatos(velocidadInicial = v0 * factor, angulo = angulo, alturaInicial = y0)
    }

    @Test
    fun testPosicionEnTiempoCeroCoincideConPosicionInicial() {
        val datos = crearDatos(30.0, 45.0, 10.0)
        val pos = calculador.calcularPosicion(datos, 0.0)
        assertEquals(0.0, pos.x)
        assertEquals(10.0, pos.y)
        assertEquals(0.0, pos.tiempo)
        assertTrue(pos.velocidadX > 0.0)
        assertTrue(pos.velocidadY > 0.0)
    }

    @Test
    fun testPosicionEnAlturaMaximaTieneVelocidadVerticalCercanaACero() {
        val datos = crearDatos(50.0, 45.0, 0.0)
        val resultado = calculador.calcular(datos)
        val pos = calculador.calcularPosicion(datos, resultado.tiempoVuelo / 2.0)
        assertTrue(abs(pos.y - resultado.alturaMaxima) < margen, "La altura en el vértice debe ser la altura máxima")
        assertTrue(abs(pos.velocidadY) < margen, "La velocidad vertical en el vértice debe ser cero")
    }

    @Test
    fun testPosicionAlTiempoDeVueloTocaElSuelo() {
        val datos = crearDatos(25.0, 30.0, 5.0)
        val resultado = calculador.calcular(datos)
        val posFinal = calculador.calcularPosicion(datos, resultado.tiempoVuelo)
        assertEquals(0.0, posFinal.y, "Al tiempo de vuelo el proyectil debe tocar el suelo y=0")
        assertTrue(abs(posFinal.x - resultado.distanciaHorizontal) < margen, "Al tiempo de vuelo x debe coincidir con el alcance")
    }

    @Test
    fun testCalcularTrayectoriaDevuelveCantidadDePuntosSolicitada() {
        val datos = crearDatos(20.0, 45.0, 0.0)
        val puntos = calculador.calcularTrayectoria(datos, 50)
        assertEquals(50, puntos.size)
        assertEquals(0.0, puntos.first().x)
        assertEquals(0.0, puntos.first().y)
        assertEquals(0.0, puntos.last().y)
    }

    @Test
    fun testVelocidadInicialCeroDevuelveProyectilEstaticoEnOrigen() {
        val datos = crearDatos(0.0, 30.0, 8.0)
        val resultado = calculador.calcular(datos)
        assertEquals(0.0, resultado.distanciaHorizontal)
        assertEquals(8.0, resultado.alturaMaxima)
        assertEquals(0.0, resultado.tiempoVuelo)
        assertEquals(0.0, resultado.velocidadX)
        assertEquals(0.0, resultado.velocidadY)

        val pos = calculador.calcularPosicion(datos, 1.5)
        assertEquals(0.0, pos.x)
        assertEquals(8.0, pos.y)
        assertEquals(0.0, pos.tiempo)

        val trayectoria = calculador.calcularTrayectoria(datos, 60)
        assertEquals(1, trayectoria.size)
        assertEquals(0.0, trayectoria.first().x)
        assertEquals(8.0, trayectoria.first().y)
    }
}
