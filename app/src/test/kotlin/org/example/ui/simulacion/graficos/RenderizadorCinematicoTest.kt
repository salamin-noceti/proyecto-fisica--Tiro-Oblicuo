package org.example.ui.simulacion.graficos

import org.example.model.IngresoDatos
import org.example.service.CalculadorTiroOblicuo
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class RenderizadorCinematicoTest {

    private val datosEjemplo = IngresoDatos.base.copy(velocidadInicial = 20.0, angulo = 45.0, alturaInicial = 5.0)
    private val panel = PanelGraficosCinematicos()
    private val calculador = CalculadorTiroOblicuo.instancia
    private val resultadoTeorico = calculador.calcular(datosEjemplo)

    @Test
    fun testRenderizadorPosicionMapeaCorrectamenteXEYYConfiguraLimites() {
        val renderizador = panel.renderizadorPosicion
        renderizador.configurarLimites(datosEjemplo, resultadoTeorico)
        assertEquals("Posición vs Tiempo", renderizador.canvas.titulo)
        assertTrue(renderizador.canvas.xMax >= resultadoTeorico.tiempoVuelo && renderizador.canvas.yMax >= resultadoTeorico.distanciaHorizontal)

        val puntosEstela = listOf(calculador.calcularPosicion(datosEjemplo, 0.0), calculador.calcularPosicion(datosEjemplo, resultadoTeorico.tiempoVuelo / 2), calculador.calcularPosicion(datosEjemplo, resultadoTeorico.tiempoVuelo))
        renderizador.actualizar(resultadoTeorico.tiempoVuelo, puntosEstela, resultadoTeorico, datosEjemplo)
        val series = renderizador.canvas.obtenerSeries()
        assertEquals(2, series.size)
        val serieX = series.find { it.id == "x_t" }!!
        val serieY = series.find { it.id == "y_t" }!!
        assertTrue(serieX.puntos[0].y <= serieX.puntos[1].y)
        assertEquals(datosEjemplo.alturaInicial, serieY.puntos[0].y, 0.01)
        assertEquals(0.0, serieY.puntos[2].y, 0.01)

        renderizador.reiniciar()
        assertTrue(renderizador.canvas.obtenerSeries().all { it.puntos.isEmpty() })
    }

    @Test
    fun testRenderizadorVelocidadMuestraVxConstanteYVyDecreciente() {
        val renderizador = panel.renderizadorVelocidad
        renderizador.configurarLimites(datosEjemplo, resultadoTeorico)
        assertEquals("Velocidad vs Tiempo", renderizador.canvas.titulo)
        assertEquals("Velocidad (m/s)", renderizador.canvas.etiquetaEjeY)
        assertTrue(renderizador.canvas.yMin < 0.0)

        val puntosEstela = calculador.calcularTrayectoria(datosEjemplo, pasos = 10)
        renderizador.actualizar(resultadoTeorico.tiempoVuelo, puntosEstela, resultadoTeorico, datosEjemplo)
        val series = renderizador.canvas.obtenerSeries()
        val serieVx = series.find { it.id == "vx_t" }
        val serieVy = series.find { it.id == "vy_t" }
        assertNotNull(serieVx)
        assertNotNull(serieVy)
        for (punto in serieVx.puntos) {
            assertEquals(resultadoTeorico.velocidadX, punto.y, 0.001)
        }
        assertTrue(serieVy.puntos.first().y > 0.0 && serieVy.puntos.last().y < 0.0)
    }

    @Test
    fun testRenderizadorAceleracionMuestraAxEnCeroYAyEnMenosGravedad() {
        val renderizador = panel.renderizadorAceleracion
        renderizador.configurarLimites(datosEjemplo, resultadoTeorico)
        assertEquals("Aceleración vs Tiempo", renderizador.canvas.titulo)
        assertEquals("Aceleración (m/s²)", renderizador.canvas.etiquetaEjeY)
        assertTrue(renderizador.canvas.yMin <= -12.0 && renderizador.canvas.yMax >= 2.0)

        val puntosEstela = listOf(calculador.calcularPosicion(datosEjemplo, 0.0), calculador.calcularPosicion(datosEjemplo, 1.0))
        renderizador.actualizar(1.0, puntosEstela, resultadoTeorico, datosEjemplo)
        val series = renderizador.canvas.obtenerSeries()
        val serieAx = series.find { it.id == "ax_t" }
        val serieAy = series.find { it.id == "ay_t" }
        assertNotNull(serieAx)
        assertNotNull(serieAy)
        assertTrue(serieAx.puntos.all { it.y == 0.0 })
        assertEquals(0.0, serieAx.valorActual)
        assertTrue(serieAy.puntos.all { it.y == -CalculadorTiroOblicuo.GRAVEDAD })
        assertEquals(-CalculadorTiroOblicuo.GRAVEDAD, serieAy.valorActual)
    }
}
