package org.example.ui.simulacion.graficos

import org.example.model.IngresoDatos
import org.example.model.PosicionTrayectoria
import org.example.model.ResultadoTiroOblicuo
import org.example.service.CalculadorTiroOblicuo
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class RenderizadorCinematicoTest {

    private val datosEjemplo = IngresoDatos(velocidadInicial = 20.0, angulo = 45.0, alturaInicial = 5.0)
    private val calculador = CalculadorTiroOblicuo()
    private val resultadoTeorico = calculador.calcular(datosEjemplo)

    @Test
    fun `RenderizadorPosicion mapea correctamente x e y y configura limites`() {
        val renderizador = RenderizadorPosicion()
        renderizador.configurarLimites(datosEjemplo, resultadoTeorico)

        assertEquals("Posición vs Tiempo", renderizador.canvas.titulo)
        assertEquals("Posición (m)", renderizador.canvas.etiquetaEjeY)
        assertTrue(renderizador.canvas.xMax >= resultadoTeorico.tiempoVuelo)
        assertTrue(renderizador.canvas.yMax >= resultadoTeorico.distanciaHorizontal)

        val puntosEstela = listOf(
            calculador.calcularPosicion(datosEjemplo, 0.0),
            calculador.calcularPosicion(datosEjemplo, resultadoTeorico.tiempoVuelo / 2),
            calculador.calcularPosicion(datosEjemplo, resultadoTeorico.tiempoVuelo)
        )

        renderizador.actualizar(
            tiempoActual = resultadoTeorico.tiempoVuelo,
            puntosEstela = puntosEstela,
            resultadoTeorico = resultadoTeorico,
            datos = datosEjemplo
        )

        val series = renderizador.canvas.obtenerSeries()
        assertEquals(2, series.size)

        val serieX = series.find { it.id == "x_t" }
        val serieY = series.find { it.id == "y_t" }

        assertNotNull(serieX)
        assertNotNull(serieY)

        // x(t) crece monótonamente
        assertTrue(serieX.puntos[0].y <= serieX.puntos[1].y)
        assertTrue(serieX.puntos[1].y <= serieX.puntos[2].y)

        // y(t) en t=0 es y0, sube a la mitad y baja a 0 al final
        assertEquals(datosEjemplo.alturaInicial, serieY.puntos[0].y, 0.01)
        assertTrue(serieY.puntos[1].y > datosEjemplo.alturaInicial)
        assertEquals(0.0, serieY.puntos[2].y, 0.01)

        renderizador.reiniciar()
        assertTrue(renderizador.canvas.obtenerSeries().all { it.puntos.isEmpty() })
    }

    @Test
    fun `RenderizadorVelocidad muestra vx constante y vy decreciente cruzando cero`() {
        val renderizador = RenderizadorVelocidad()
        renderizador.configurarLimites(datosEjemplo, resultadoTeorico)

        assertEquals("Velocidad vs Tiempo", renderizador.canvas.titulo)
        assertEquals("Velocidad (m/s)", renderizador.canvas.etiquetaEjeY)
        assertTrue(renderizador.canvas.yMin < 0.0, "El eje Y debe permitir valores negativos de velocidad vertical")

        val puntosEstela = calculador.calcularTrayectoria(datosEjemplo, pasos = 10)

        renderizador.actualizar(
            tiempoActual = resultadoTeorico.tiempoVuelo,
            puntosEstela = puntosEstela,
            resultadoTeorico = resultadoTeorico,
            datos = datosEjemplo
        )

        val series = renderizador.canvas.obtenerSeries()
        assertEquals(2, series.size)

        val serieVx = series.find { it.id == "vx_t" }
        val serieVy = series.find { it.id == "vy_t" }

        assertNotNull(serieVx)
        assertNotNull(serieVy)

        // vx(t) es constante en todos los puntos
        for (punto in serieVx.puntos) {
            assertEquals(resultadoTeorico.velocidadX, punto.y, 0.001)
        }

        // vy(t) decrece desde Vy0 hasta un valor negativo
        assertTrue(serieVy.puntos.first().y > 0.0)
        assertTrue(serieVy.puntos.last().y < 0.0)
    }

    @Test
    fun `RenderizadorAceleracion muestra ax en cero y ay en menos gravedad constante`() {
        val renderizador = RenderizadorAceleracion()
        renderizador.configurarLimites(datosEjemplo, resultadoTeorico)

        assertEquals("Aceleración vs Tiempo", renderizador.canvas.titulo)
        assertEquals("Aceleración (m/s²)", renderizador.canvas.etiquetaEjeY)
        assertTrue(renderizador.canvas.yMin <= -12.0)
        assertTrue(renderizador.canvas.yMax >= 2.0)

        val puntosEstela = listOf(
            calculador.calcularPosicion(datosEjemplo, 0.0),
            calculador.calcularPosicion(datosEjemplo, 1.0)
        )

        renderizador.actualizar(
            tiempoActual = 1.0,
            puntosEstela = puntosEstela,
            resultadoTeorico = resultadoTeorico,
            datos = datosEjemplo
        )

        val series = renderizador.canvas.obtenerSeries()
        assertEquals(2, series.size)

        val serieAx = series.find { it.id == "ax_t" }
        val serieAy = series.find { it.id == "ay_t" }

        assertNotNull(serieAx)
        assertNotNull(serieAy)

        // ax = 0.0
        assertTrue(serieAx.puntos.all { it.y == 0.0 })
        assertEquals(0.0, serieAx.valorActual)

        // ay = -9.81
        assertTrue(serieAy.puntos.all { it.y == -CalculadorTiroOblicuo.GRAVEDAD })
        assertEquals(-CalculadorTiroOblicuo.GRAVEDAD, serieAy.valorActual)
    }
}
