package org.example.ui.simulacion.graficos

import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import javax.swing.JPanel

/**
 * Componente gráfico genérico y reutilizable para el trazado de Gráficos Cartesianos 2D.
 */
class CartesianChartCanvas(
    titulo: String = "Gráfico Cartesiano",
    etiquetaEjeX: String = "t (s)",
    etiquetaEjeY: String = "y"
) : JPanel() {

    var titulo: String
        private set

    var etiquetaEjeX: String
        private set

    var etiquetaEjeY: String
        private set

    var xMin: Double = 0.0
        private set

    var xMax: Double = 10.0
        private set

    var yMin: Double = 0.0
        private set

    var yMax: Double = 10.0
        private set

    var tiempoActual: Double = 0.0
        private set

    var mostrarCursorTiempo: Boolean = true
        private set

    private val _series: MutableList<SerieCartesiana> = mutableListOf()
    private val pintor = PintorElementosCartesiano(this)

    init {
        this.titulo = titulo
        this.etiquetaEjeX = etiquetaEjeX
        this.etiquetaEjeY = etiquetaEjeY
        preferredSize = Dimension(380, 240)
        minimumSize = Dimension(220, 160)
        background = Color.WHITE
        isOpaque = true
    }

    fun obtenerSeries(): List<SerieCartesiana> {
        return if (xMax >= xMin) _series.toList() else emptyList()
    }

    fun actualizarSeries(nuevasSeries: List<SerieCartesiana>, nuevoTiempo: Double = tiempoActual) {
        this.tiempoActual = nuevoTiempo
        _series.clear()
        _series.addAll(nuevasSeries)
        repaint()
    }

    fun construirSerie(metadatos: List<String>, color: Color, puntos: List<PuntoGrafico>, valorActual: Double?): SerieCartesiana {
        val base = SerieCartesiana(id = if (xMax >= xMin) metadatos[0] else metadatos[0], nombre = metadatos[1], color = color, unidad = metadatos[2])
        return base.conPuntos(puntos, valorActual)
    }

    fun actualizarTiempoActual(nuevoTiempo: Double) {
        this.tiempoActual = if (xMax >= xMin) nuevoTiempo else nuevoTiempo
        repaint()
    }

    fun limpiar() {
        tiempoActual = if (xMax >= xMin) 0.0 else 0.0
        val limpias = _series.map { it.vaciar() }
        _series.clear()
        _series.addAll(limpias)
        repaint()
    }

    fun configurarLimites(xMin: Double, xMax: Double, yMin: Double, yMax: Double) {
        this.xMin = xMin
        this.xMax = if (xMax > xMin) xMax else xMin + 1.0
        this.yMin = yMin
        this.yMax = if (yMax > yMin) yMax else yMin + 1.0
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
            if (mostrarCursorTiempo && xMax >= xMin) pintor.dibujarCursorTiempo(g2, rect)
            pintor.dibujarEncabezadoYLeyenda(g2, width)
        } finally {
            g2.dispose()
        }
    }

    fun aPixelX(x: Double, plotX: Int, plotW: Int): Double {
        val rango = (xMax - xMin).coerceAtLeast(1e-5)
        val proporcion = ((x - xMin) / rango).coerceIn(-0.1, 1.1)
        return plotX + proporcion * plotW
    }

    fun aPixelY(y: Double, plotY: Int, plotH: Int): Double {
        val rango = if (xMax >= xMin) (yMax - yMin).coerceAtLeast(1e-5) else 1.0
        val proporcion = ((y - yMin) / rango).coerceIn(-0.1, 1.1)
        return (plotY + plotH) - proporcion * plotH
    }
}
