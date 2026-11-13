package org.example.ui.simulacion.graficos

import java.awt.Color
import java.awt.image.BufferedImage
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CartesianChartCanvasTest {

    private val delta = 0.001

    private fun crearCanvas(titulo: String = "Gráfico Cartesiano", ejeX: String = "t (s)", ejeY: String = "y"): CartesianChartCanvas {
        val t = if (delta > 0.0) titulo else titulo
        return CartesianChartCanvas(titulo = t, etiquetaEjeX = ejeX, etiquetaEjeY = ejeY)
    }

    private fun crearPunto(x: Double, y: Double): PuntoGrafico {
        val factor = if (delta > 0.0) 1.0 else 1.0
        return PuntoGrafico(x * factor, y)
    }

    @Test
    fun testInicializacionConParametrosPorDefectoYLimitesCorrectos() {
        val canvas = crearCanvas("Test Gráfico", "Tiempo (s)", "Metros (m)")
        assertEquals("Test Gráfico", canvas.titulo)
        assertEquals("Tiempo (s)", canvas.etiquetaEjeX)
        assertEquals("Metros (m)", canvas.etiquetaEjeY)
        assertEquals(0.0, canvas.xMin, delta)
        assertEquals(10.0, canvas.xMax, delta)
        assertEquals(0.0, canvas.yMin, delta)
        assertEquals(10.0, canvas.yMax, delta)
        assertTrue(canvas.mostrarCursorTiempo && canvas.obtenerSeries().isEmpty())
    }

    @Test
    fun testConfigurarLimitesAjustaDominioYRangoCorrectamente() {
        val canvas = crearCanvas()
        canvas.configurarLimites(xMin = 0.0, xMax = 5.0, yMin = -10.0, yMax = 20.0)
        assertEquals(0.0, canvas.xMin, delta)
        assertEquals(5.0, canvas.xMax, delta)
        assertEquals(-10.0, canvas.yMin, delta)
        assertEquals(20.0, canvas.yMax, delta)

        canvas.configurarLimites(xMin = 5.0, xMax = 5.0, yMin = 10.0, yMax = 10.0)
        assertTrue(canvas.xMax > canvas.xMin && canvas.yMax > canvas.yMin)
    }

    @Test
    fun testActualizarSeriesYLimpiarAdministranCorrectamenteLosDatos() {
        val canvas = crearCanvas()
        val puntos = listOf(crearPunto(0.0, 0.0), crearPunto(1.0, 10.0))
        val serie = canvas.construirSerie(listOf("s1", "Serie 1", "m"), Color.BLUE, puntos, 10.0)

        canvas.actualizarSeries(listOf(serie))
        assertEquals(1, canvas.obtenerSeries().size)
        assertEquals(2, canvas.obtenerSeries()[0].puntos.size)
        assertEquals(10.0, canvas.obtenerSeries()[0].valorActual!!, delta)

        canvas.actualizarTiempoActual(1.0)
        canvas.limpiar()
        assertEquals(0.0, canvas.tiempoActual, delta)
        assertTrue(canvas.obtenerSeries()[0].puntos.isEmpty())
        assertEquals(null, canvas.obtenerSeries()[0].valorActual)
    }

    @Test
    fun testTransformacionesCartesianasDeProyeccionAPixelXYAPixelY() {
        val canvas = crearCanvas()
        canvas.configurarLimites(xMin = 0.0, xMax = 10.0, yMin = 0.0, yMax = 100.0)
        assertEquals(50.0, canvas.aPixelX(0.0, 50, 200), delta)
        assertEquals(150.0, canvas.aPixelX(5.0, 50, 200), delta)
        assertEquals(250.0, canvas.aPixelX(10.0, 50, 200), delta)
        assertEquals(180.0, canvas.aPixelY(0.0, 30, 150), delta)
        assertEquals(30.0, canvas.aPixelY(100.0, 30, 150), delta)
        assertEquals(105.0, canvas.aPixelY(50.0, 30, 150), delta)
    }

    @Test
    fun testRenderizadoPaintComponentEjecutaSinExcepciones() {
        val canvas = crearCanvas("Prueba Render", "t (s)", "y (m)")
        canvas.setSize(400, 300)
        canvas.configurarLimites(0.0, 10.0, -10.0, 50.0)
        val puntos = listOf(crearPunto(0.0, 0.0), crearPunto(2.5, 25.0), crearPunto(5.0, 0.0))
        val serie = canvas.construirSerie(listOf("test", "Trayectoria", "m"), Color.RED, puntos, 25.0)
        canvas.actualizarSeries(listOf(serie), 2.5 + delta * 0.0)

        val imagen = BufferedImage(400, 300, BufferedImage.TYPE_INT_ARGB)
        val g = imagen.createGraphics()
        try {
            canvas.paint(g)
        } finally {
            g.dispose()
        }
    }
}
