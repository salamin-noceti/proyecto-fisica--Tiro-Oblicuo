package org.example.ui.simulacion.graficos

import java.awt.Color
import java.awt.image.BufferedImage
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CartesianChartCanvasTest {

    @Test
    fun `inicializacion con parametros por defecto y limites correctos`() {
        val canvas = CartesianChartCanvas(
            titulo = "Test Gráfico",
            etiquetaEjeX = "Tiempo (s)",
            etiquetaEjeY = "Metros (m)"
        )

        assertEquals("Test Gráfico", canvas.titulo)
        assertEquals("Tiempo (s)", canvas.etiquetaEjeX)
        assertEquals("Metros (m)", canvas.etiquetaEjeY)
        assertEquals(0.0, canvas.xMin)
        assertEquals(10.0, canvas.xMax)
        assertEquals(0.0, canvas.yMin)
        assertEquals(10.0, canvas.yMax)
        assertTrue(canvas.mostrarCursorTiempo)
        assertTrue(canvas.obtenerSeries().isEmpty())
    }

    @Test
    fun `configurarLimites ajusta dominio y rango correctamente con proteccion de rango minimo`() {
        val canvas = CartesianChartCanvas()

        canvas.configurarLimites(xMin = 0.0, xMax = 5.0, yMin = -10.0, yMax = 20.0)
        assertEquals(0.0, canvas.xMin)
        assertEquals(5.0, canvas.xMax)
        assertEquals(-10.0, canvas.yMin)
        assertEquals(20.0, canvas.yMax)

        // Comprobación de borde: si xMax <= xMin
        canvas.configurarLimites(xMin = 5.0, xMax = 5.0, yMin = 10.0, yMax = 10.0)
        assertTrue(canvas.xMax > canvas.xMin)
        assertTrue(canvas.yMax > canvas.yMin)
    }

    @Test
    fun `actualizarSeries y limpiar administran correctamente los datos`() {
        val canvas = CartesianChartCanvas()
        val puntos = listOf(PuntoGrafico(0.0, 0.0), PuntoGrafico(1.0, 10.0))
        val serie = SerieCartesiana(
            id = "s1",
            nombre = "Serie 1",
            color = Color.BLUE,
            puntos = puntos,
            valorActual = 10.0,
            unidad = "m"
        )

        canvas.actualizarSeries(listOf(serie))
        assertEquals(1, canvas.obtenerSeries().size)
        assertEquals(2, canvas.obtenerSeries()[0].puntos.size)
        assertEquals(10.0, canvas.obtenerSeries()[0].valorActual)

        canvas.tiempoActual = 1.0
        canvas.limpiar()
        assertEquals(0.0, canvas.tiempoActual)
        assertTrue(canvas.obtenerSeries()[0].puntos.isEmpty())
        assertEquals(null, canvas.obtenerSeries()[0].valorActual)
    }

    @Test
    fun `transformaciones cartesianas de proyeccion aPixelX y aPixelY`() {
        val canvas = CartesianChartCanvas()
        canvas.configurarLimites(xMin = 0.0, xMax = 10.0, yMin = 0.0, yMax = 100.0)

        val plotX = 50
        val plotW = 200
        val plotY = 30
        val plotH = 150

        // X = 0 debe dar plotX
        assertEquals(50.0, canvas.aPixelX(0.0, plotX, plotW), 0.001)
        // X = 5 debe dar plotX + plotW / 2 = 150
        assertEquals(150.0, canvas.aPixelX(5.0, plotX, plotW), 0.001)
        // X = 10 debe dar plotX + plotW = 250
        assertEquals(250.0, canvas.aPixelX(10.0, plotX, plotW), 0.001)

        // Y = 0 (mínimo) debe dar el fondo del gráfico: plotY + plotH = 180
        assertEquals(180.0, canvas.aPixelY(0.0, plotY, plotH), 0.001)
        // Y = 100 (máximo) debe dar la parte superior: plotY = 30
        assertEquals(30.0, canvas.aPixelY(100.0, plotY, plotH), 0.001)
        // Y = 50 (punto medio) debe dar 30 + 75 = 105
        assertEquals(105.0, canvas.aPixelY(50.0, plotY, plotH), 0.001)
    }

    @Test
    fun `renderizado paintComponent ejecuta sin excepciones sobre un buffer grafico`() {
        val canvas = CartesianChartCanvas(titulo = "Prueba Render", etiquetaEjeX = "t (s)", etiquetaEjeY = "y (m)")
        canvas.setSize(400, 300)
        canvas.configurarLimites(0.0, 10.0, -10.0, 50.0)

        val serie = SerieCartesiana(
            id = "test",
            nombre = "Trayectoria",
            color = Color.RED,
            puntos = listOf(PuntoGrafico(0.0, 0.0), PuntoGrafico(2.5, 25.0), PuntoGrafico(5.0, 0.0)),
            valorActual = 25.0,
            unidad = "m"
        )
        canvas.actualizarSeries(listOf(serie))
        canvas.tiempoActual = 2.5

        val imagen = BufferedImage(400, 300, BufferedImage.TYPE_INT_ARGB)
        val g = imagen.createGraphics()
        try {
            canvas.paint(g)
        } finally {
            g.dispose()
        }
    }
}
