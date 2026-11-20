package org.example.ui.simulacion.graficos

import org.example.model.PosicionTrayectoria
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import javax.swing.JPanel

/**
 * Componente gráfico genérico y reutilizable para el trazado de Gráficos Cartesianos 2D.
 */
class CartesianChartCanvas(val titulo: String = "Gráfico Cartesiano", val etiquetaEjeX: String = "t (s)", val etiquetaEjeY: String = "y") : JPanel() {

    private val limites: DoubleArray = doubleArrayOf(0.0, 10.0, 0.0, 10.0)
    private var tiempoActualInterno: Double = 0.0
    private var cursorTiempoVisible: Boolean = true
    private val seriesInternas: MutableList<SerieCartesiana> = mutableListOf()
    private val pintor = PintorElementosCartesiano(this)

    init {
        preferredSize = Dimension(380, 240)
        minimumSize = Dimension(220, 160)
        background = Color.WHITE
        isOpaque = true
    }

    fun obtenerLimites(): DoubleArray {
        return limites.copyOf()
    }

    fun obtenerTiempoActual(): Double {
        return if (limites[1] >= limites[0]) tiempoActualInterno else 0.0
    }

    fun estaCursorTiempoVisible(): Boolean {
        return cursorTiempoVisible && limites[1] >= limites[0]
    }

    fun obtenerSeries(): List<SerieCartesiana> {
        return if (limites[1] >= limites[0]) seriesInternas.toList() else emptyList()
    }

    fun actualizarSeries(nuevasSeries: List<SerieCartesiana>, nuevoTiempo: Double = tiempoActualInterno) {
        this.tiempoActualInterno = nuevoTiempo
        seriesInternas.clear()
        seriesInternas.addAll(nuevasSeries)
        repaint()
    }

    fun construirSerie(metadatos: List<String>, color: Color, puntos: List<PuntoGrafico>, valorActual: Double?): SerieCartesiana {
        val base = SerieCartesiana(id = if (limites[1] >= limites[0]) metadatos[0] else metadatos[0], nombre = metadatos[1], color = color, unidad = metadatos[2])
        return base.conPuntos(puntos, valorActual)
    }

    fun construirSerieTrayectoria(metadatos: List<String>, color: Color, estela: List<PosicionTrayectoria>, selectorY: (PosicionTrayectoria) -> Double, valorActual: Double?): SerieCartesiana {
        val puntos = if (limites[1] >= limites[0]) estela.map { PuntoGrafico.en(it.tiempo, selectorY(it)) } else emptyList()
        return construirSerie(metadatos, color, puntos, valorActual)
    }

    fun actualizarTiempoActual(nuevoTiempo: Double) {
        this.tiempoActualInterno = if (limites[1] >= limites[0]) nuevoTiempo else nuevoTiempo
        repaint()
    }

    fun limpiar() {
        tiempoActualInterno = if (limites[1] >= limites[0]) 0.0 else 0.0
        val limpias = seriesInternas.map { it.vaciar() }
        seriesInternas.clear()
        seriesInternas.addAll(limpias)
        repaint()
    }

    fun configurarLimites(xMin: Double, xMax: Double, yMin: Double, yMax: Double) {
        limites[0] = xMin
        limites[1] = if (xMax > xMin) xMax else xMin + 1.0
        limites[2] = yMin
        limites[3] = if (yMax > yMin) yMax else yMin + 1.0
        repaint()
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val g2 = g.create() as Graphics2D
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
            val rect = intArrayOf(62, 38, (width - 86).coerceAtLeast(20), (height - 74).coerceAtLeast(20))
            g2.color = Color.WHITE; g2.fillRect(rect[0], rect[1], rect[2], rect[3])
            pintor.dibujarCuadriculaYEjes(g2, rect)
            pintor.dibujarSeries(g2, rect)
            if (cursorTiempoVisible && limites[1] >= limites[0]) pintor.dibujarCursorTiempo(g2, rect)
            pintor.dibujarEncabezadoYLeyenda(g2, width)
        } finally {
            g2.dispose()
        }
    }

    fun aPixelX(x: Double, plotX: Int, plotW: Int): Double {
        val rango = (limites[1] - limites[0]).coerceAtLeast(1e-5)
        val proporcion = ((x - limites[0]) / rango).coerceIn(-0.1, 1.1)
        return plotX + proporcion * plotW
    }

    fun aPixelY(y: Double, plotY: Int, plotH: Int): Double {
        val rango = (limites[3] - limites[2]).coerceAtLeast(1e-5)
        val proporcion = ((y - limites[2]) / rango).coerceIn(-0.1, 1.1)
        return (plotY + plotH) - proporcion * plotH
    }
}
