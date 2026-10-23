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

    @Test
    fun `posicion en tiempo cero coincide con posicion inicial`() {
        val datos = IngresoDatos(velocidadInicial = 30.0, angulo = 45.0, alturaInicial = 10.0)
        val pos = calculador.calcularPosicion(datos, 0.0)

        assertEquals(0.0, pos.x)
        assertEquals(10.0, pos.y)
        assertEquals(0.0, pos.tiempo)
        assertTrue(pos.velocidadX > 0.0)
        assertTrue(pos.velocidadY > 0.0)
    }

    @Test
    fun `posicion en altura maxima tiene velocidad vertical cercana a cero`() {
        val datos = IngresoDatos(velocidadInicial = 50.0, angulo = 45.0, alturaInicial = 0.0)
        val resultado = calculador.calcular(datos)

        // En un tiro con alturaInicial = 0, el tiempo al vértice es la mitad del tiempo de vuelo
        val tiempoVertice = resultado.tiempoVuelo / 2.0
        val pos = calculador.calcularPosicion(datos, tiempoVertice)

        assertTrue(abs(pos.y - resultado.alturaMaxima) < margen, "La altura en el vértice debe ser la altura máxima")
        assertTrue(abs(pos.velocidadY) < margen, "La velocidad vertical en el vértice debe ser cero")
    }

    @Test
    fun `posicion al tiempo de vuelo toca el suelo`() {
        val datos = IngresoDatos(velocidadInicial = 25.0, angulo = 30.0, alturaInicial = 5.0)
        val resultado = calculador.calcular(datos)

        val posFinal = calculador.calcularPosicion(datos, resultado.tiempoVuelo)

        assertEquals(0.0, posFinal.y, "Al tiempo de vuelo el proyectil debe tocar el suelo y=0")
        assertTrue(abs(posFinal.x - resultado.distanciaHorizontal) < margen, "Al tiempo de vuelo x debe coincidir con el alcance")
    }

    @Test
    fun `calcularTrayectoria devuelve la cantidad de puntos solicitada cubriendo el vuelo`() {
        val datos = IngresoDatos(velocidadInicial = 20.0, angulo = 45.0, alturaInicial = 0.0)
        val puntos = calculador.calcularTrayectoria(datos, 50)

        assertEquals(50, puntos.size)
        assertEquals(0.0, puntos.first().x)
        assertEquals(0.0, puntos.first().y)
        assertEquals(0.0, puntos.last().y)
    }
}
