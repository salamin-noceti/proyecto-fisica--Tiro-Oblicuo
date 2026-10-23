package org.example.ui.simulacion.graficos

import java.awt.BasicStroke
import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.Stroke
import java.awt.geom.Ellipse2D
import java.awt.geom.Line2D
import java.awt.geom.Path2D
import java.awt.geom.Rectangle2D
import java.util.Locale
import javax.swing.JPanel

/**
 * Coordenada numérica de un punto en el plano cartesiano físico.
 *
 * @param x Valor en el eje horizontal (ej. tiempo t en segundos).
 * @param y Valor en el eje vertical (ej. posición en m, velocidad en m/s o aceleración en m/s²).
 */
data class PuntoGrafico(
    val x: Double,
    val y: Double
)

/**
 * Representa una serie de datos cartesianos a ser graficada sobre el lienzo.
 *
 * @param id Identificador único de la serie.
 * @param nombre Nombre descriptivo mostrado en la leyenda.
 * @param color Color de la curva e indicador de telemetría.
 * @param puntos Colección de puntos históricos acumulados hasta el tiempo actual.
 * @param trazo Estilo de trazo (ancho y terminación de línea).
 * @param valorActual Valor numérico instantáneo en el tiempo actual para telemetría.
 * @param unidad Unidad de medida física (m, m/s, m/s²).
 */
data class SerieCartesiana(
    val id: String,
    val nombre: String,
    val color: Color,
    val puntos: List<PuntoGrafico> = emptyList(),
    val trazo: Stroke = BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND),
    val valorActual: Double? = null,
    val unidad: String = ""
)

/**
 * Componente gráfico genérico y reutilizable para el trazado de Gráficos Cartesianos 2D.
 *
 * Cumple estrictamente con:
 * - **DRY (DUP -> 0%):** Unifica todo el dibujo de ejes cartesianos, cuadrícula de referencia,
 *   escalas numéricas, leyendas descriptivas y el marcador/cursor de tiempo actual.
 * - **SRP:** Responsable exclusivamente del renderizado geométrico visual y la proyección
 *   cartesiana (Métrico -> Píxeles). No contiene fórmulas cinemáticas ni gestiona temporizadores.
 * - **POO:** Encapsula su estado y algoritmos de proyección gráfica en métodos de instancia.
 *
 * @param titulo Título principal del gráfico.
 * @param etiquetaEjeX Rótulo del eje horizontal (ej. "Tiempo t (s)").
 * @param etiquetaEjeY Rótulo del eje vertical (ej. "Posición (m)").
 */
class CartesianChartCanvas(
    var titulo: String = "Gráfico Cartesiano",
    var etiquetaEjeX: String = "t (s)",
    var etiquetaEjeY: String = "y"
) : JPanel() {

    /** Límite inferior del dominio horizontal (X). */
    var xMin: Double = 0.0

    /** Límite superior del dominio horizontal (X). */
    var xMax: Double = 10.0

    /** Límite inferior del rango vertical (Y). */
    var yMin: Double = 0.0

    /** Límite superior del rango vertical (Y). */
    var yMax: Double = 10.0

    /** Instante de tiempo actual simulado para el marcador/cursor vertical. */
    var tiempoActual: Double = 0.0

    /** Indica si se debe dibujar la línea vertical indicadora del tiempo actual. */
    var mostrarCursorTiempo: Boolean = true

    /** Series de datos activas en el gráfico. */
    private var series: List<SerieCartesiana> = emptyList()

    // Márgenes internos para etiquetas, escalas y encabezado
    private val margenIzquierdo: Int = 62
    private val margenDerecho: Int = 24
    private val margenSuperior: Int = 38
    private val margenInferior: Int = 36

    // Colores de la paleta moderna coherente con FlatLaf y la UI de la aplicación
    private val colorFondo = Color(255, 255, 255)
    private val colorFondoPlot = Color(255, 255, 255)
    private val colorEjes = Color(71, 85, 105)
    private val colorEjeCero = Color(51, 65, 85)
    private val colorGrilla = Color(241, 245, 249)
    private val colorTextoEje = Color(100, 116, 139)
    private val colorTitulo = Color(15, 23, 42)
    private val colorCursorTiempo = Color(239, 68, 68, 200)

    // Trazos reutilizables
    private val trazoGrilla: Stroke = BasicStroke(
        1.0f,
        BasicStroke.CAP_BUTT,
        BasicStroke.JOIN_MITER,
        10.0f,
        floatArrayOf(4.0f, 4.0f),
        0.0f
    )
    private val trazoEje: Stroke = BasicStroke(1.2f)
    private val trazoEjeCero: Stroke = BasicStroke(1.6f)
    private val trazoCursor: Stroke = BasicStroke(
        1.5f,
        BasicStroke.CAP_BUTT,
        BasicStroke.JOIN_MITER,
        10.0f,
        floatArrayOf(5.0f, 4.0f),
        0.0f
    )

    // Tipografías
    private val fuenteTitulo = Font(Font.SANS_SERIF, Font.BOLD, 12)
    private val fuenteLeyenda = Font(Font.SANS_SERIF, Font.BOLD, 10)
    private val fuenteEjes = Font(Font.SANS_SERIF, Font.PLAIN, 10)
    private val fuenteRotulo = Font(Font.SANS_SERIF, Font.BOLD, 10)

    init {
        preferredSize = Dimension(380, 240)
        minimumSize = Dimension(220, 160)
        background = colorFondo
        isOpaque = true
    }

    /**
     * Retorna una copia de las series registradas actualmente.
     */
    fun obtenerSeries(): List<SerieCartesiana> = series

    /**
     * Actualiza las series a graficar y redibuja el componente.
     */
    fun actualizarSeries(nuevasSeries: List<SerieCartesiana>) {
        this.series = nuevasSeries
        repaint()
    }

    /**
     * Limpia los puntos de todas las series registradas y restablece el tiempo a cero.
     */
    fun limpiar() {
        tiempoActual = 0.0
        this.series = series.map { it.copy(puntos = emptyList(), valorActual = null) }
        repaint()
    }

    /**
     * Configura conjuntamente los límites del dominio y rango cartesiano.
     */
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
            // Activar antialiasing para trazos fluidos y textos nítidos
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)

            val anchoTotal = width
            val altoTotal = height

            val plotX = margenIzquierdo
            val plotY = margenSuperior
            val plotW = (anchoTotal - margenIzquierdo - margenDerecho).coerceAtLeast(20)
            val plotH = (altoTotal - margenSuperior - margenInferior).coerceAtLeast(20)

            // 1. Fondo del área de trazado
            g2.color = colorFondoPlot
            g2.fillRect(plotX, plotY, plotW, plotH)

            // 2. Cuadrícula de referencia y marcas numéricas
            dibujarCuadriculaYEjes(g2, plotX, plotY, plotW, plotH)

            // 3. Trazado de las curvas de las series
            dibujarSeries(g2, plotX, plotY, plotW, plotH)

            // 4. Cursor del tiempo actual
            if (mostrarCursorTiempo) {
                dibujarCursorTiempo(g2, plotX, plotY, plotW, plotH)
            }

            // 5. Encabezado (Título del gráfico y leyenda dinámica con valores en vivo)
            dibujarEncabezadoYLeyenda(g2, anchoTotal)

        } finally {
            g2.dispose()
        }
    }

    /**
     * Dibuja las líneas de la cuadrícula, los valores numéricos en los ejes y la línea de cero.
     */
    private fun dibujarCuadriculaYEjes(g2: Graphics2D, plotX: Int, plotY: Int, plotW: Int, plotH: Int) {
        val divisionesX = 5
        val divisionesY = 5

        val rangoX = (xMax - xMin).coerceAtLeast(1e-5)
        val rangoY = (yMax - yMin).coerceAtLeast(1e-5)

        // Líneas verticales de la grilla y marcas del eje X
        g2.font = fuenteEjes
        for (i in 0..divisionesX) {
            val valorX = xMin + (rangoX * i / divisionesX)
            val px = aPixelX(valorX, plotX, plotW)

            // Línea de grilla vertical
            g2.color = colorGrilla
            g2.stroke = trazoGrilla
            g2.draw(Line2D.Double(px, plotY.toDouble(), px, (plotY + plotH).toDouble()))

            // Etiqueta numérica
            g2.color = colorTextoEje
            val texto = formatear(valorX)
            val fm = g2.fontMetrics
            val textoW = fm.stringWidth(texto)
            g2.drawString(texto, (px - textoW / 2).toFloat(), (plotY + plotH + 16).toFloat())
        }

        // Rótulo del eje X
        g2.font = fuenteRotulo
        g2.color = colorEjes
        val fmRotuloX = g2.fontMetrics
        val anchoRotuloX = fmRotuloX.stringWidth(etiquetaEjeX)
        g2.drawString(etiquetaEjeX, (plotX + plotW - anchoRotuloX).toFloat(), (plotY + plotH + 30).toFloat())

        // Líneas horizontales de la grilla y marcas del eje Y
        g2.font = fuenteEjes
        for (i in 0..divisionesY) {
            val valorY = yMin + (rangoY * i / divisionesY)
            val py = aPixelY(valorY, plotY, plotH)

            // Línea de grilla horizontal
            g2.color = colorGrilla
            g2.stroke = trazoGrilla
            g2.draw(Line2D.Double(plotX.toDouble(), py, (plotX + plotW).toDouble(), py))

            // Etiqueta numérica
            g2.color = colorTextoEje
            val texto = formatear(valorY)
            val fm = g2.fontMetrics
            val textoW = fm.stringWidth(texto)
            g2.drawString(texto, (plotX - textoW - 8).toFloat(), (py + fm.ascent / 2 - 1).toFloat())
        }

        // Rótulo del eje Y
        g2.font = fuenteRotulo
        g2.color = colorEjes
        g2.drawString(etiquetaEjeY, plotX.toFloat(), (plotY - 8).toFloat())

        // Eje cero horizontal (y = 0 m, m/s o m/s²) si se encuentra dentro del rango visible
        if (yMin <= 0.0 && yMax >= 0.0) {
            val pyCero = aPixelY(0.0, plotY, plotH)
            g2.color = colorEjeCero
            g2.stroke = trazoEjeCero
            g2.draw(Line2D.Double(plotX.toDouble(), pyCero, (plotX + plotW).toDouble(), pyCero))
        }

        // Marco contenedor del área de dibujo
        g2.color = Color(203, 213, 225)
        g2.stroke = trazoEje
        g2.draw(Rectangle2D.Double(plotX.toDouble(), plotY.toDouble(), plotW.toDouble(), plotH.toDouble()))
    }

    /**
     * Dibuja las trayectorias de todas las series de datos registradas.
     */
    private fun dibujarSeries(g2: Graphics2D, plotX: Int, plotY: Int, plotW: Int, plotH: Int) {
        val clipOriginal = g2.clip
        // Evitar que las líneas se dibujen fuera del área del gráfico
        g2.setClip(plotX, plotY, plotW, plotH)

        for (serie in series) {
            if (serie.puntos.isEmpty()) continue

            g2.color = serie.color
            g2.stroke = serie.trazo

            val path = Path2D.Double()
            var iniciado = false

            for (punto in serie.puntos) {
                val px = aPixelX(punto.x, plotX, plotW)
                val py = aPixelY(punto.y, plotY, plotH)

                if (!iniciado) {
                    path.moveTo(px, py)
                    iniciado = true
                } else {
                    path.lineTo(px, py)
                }
            }
            g2.draw(path)

            // Si hay un único punto o para resaltar el último trazado
            val ultimoPunto = serie.puntos.last()
            val pxUltimo = aPixelX(ultimoPunto.x, plotX, plotW)
            val pyUltimo = aPixelY(ultimoPunto.y, plotY, plotH)

            val radio = 3.5
            g2.fill(Ellipse2D.Double(pxUltimo - radio, pyUltimo - radio, radio * 2, radio * 2))
        }

        g2.clip = clipOriginal
    }

    /**
     * Dibuja la línea vertical indicadora del tiempo actual sincronizada con el bucle temporal.
     */
    private fun dibujarCursorTiempo(g2: Graphics2D, plotX: Int, plotY: Int, plotW: Int, plotH: Int) {
        if (tiempoActual < xMin || tiempoActual > xMax) return

        val px = aPixelX(tiempoActual, plotX, plotW)

        val clipOriginal = g2.clip
        g2.setClip(plotX - 1, plotY, plotW + 2, plotH)

        // Línea vertical punteada
        g2.color = colorCursorTiempo
        g2.stroke = trazoCursor
        g2.draw(Line2D.Double(px, plotY.toDouble(), px, (plotY + plotH).toDouble()))

        // Resaltar el valor actual de cada serie sobre la línea de tiempo
        for (serie in series) {
            val valor = serie.valorActual ?: continue
            val py = aPixelY(valor, plotY, plotH)

            // Halo exterior
            g2.color = Color(serie.color.red, serie.color.green, serie.color.blue, 70)
            g2.fill(Ellipse2D.Double(px - 5.5, py - 5.5, 11.0, 11.0))

            // Punto interior con borde blanco
            g2.color = serie.color
            g2.fill(Ellipse2D.Double(px - 3.5, py - 3.5, 7.0, 7.0))
            g2.color = Color.WHITE
            g2.stroke = BasicStroke(1.2f)
            g2.draw(Ellipse2D.Double(px - 3.5, py - 3.5, 7.0, 7.0))
        }

        g2.clip = clipOriginal
    }

    /**
     * Dibuja el título del gráfico y las leyendas descriptivas con los valores instantáneos.
     */
    private fun dibujarEncabezadoYLeyenda(g2: Graphics2D, anchoTotal: Int) {
        // Título del gráfico a la izquierda
        g2.font = fuenteTitulo
        g2.color = colorTitulo
        g2.drawString(titulo, margenIzquierdo.toFloat(), (margenSuperior - 18).toFloat())

        // Leyendas a la derecha o debajo del título
        var posLegX = anchoTotal - margenDerecho
        g2.font = fuenteLeyenda

        val fm = g2.fontMetrics
        // Renderizar de derecha a izquierda
        for (serie in series.reversed()) {
            val valorTexto = if (serie.valorActual != null) {
                "${formatear(serie.valorActual)} ${serie.unidad}".trim()
            } else {
                "—"
            }
            val texto = "${serie.nombre}: $valorTexto"
            val textoW = fm.stringWidth(texto)
            val espacioTotal = textoW + 16

            posLegX -= espacioTotal

            if (posLegX >= margenIzquierdo + 80) {
                // Indicador circular de color
                g2.color = serie.color
                g2.fill(Ellipse2D.Double(posLegX.toDouble(), (margenSuperior - 24).toDouble(), 7.0, 7.0))

                // Texto descriptivo con valor
                g2.color = Color(30, 41, 59)
                g2.drawString(texto, (posLegX + 11).toFloat(), (margenSuperior - 18).toFloat())

                posLegX -= 10
            }
        }
    }

    /**
     * Convierte una coordenada matemática X al espacio de píxeles del lienzo.
     */
    fun aPixelX(x: Double, plotX: Int, plotW: Int): Double {
        val rango = (xMax - xMin).coerceAtLeast(1e-5)
        val proporcion = ((x - xMin) / rango).coerceIn(-0.1, 1.1)
        return plotX + proporcion * plotW
    }

    /**
     * Convierte una coordenada matemática Y al espacio de píxeles del lienzo (invirtiendo el eje Y).
     */
    fun aPixelY(y: Double, plotY: Int, plotH: Int): Double {
        val rango = (yMax - yMin).coerceAtLeast(1e-5)
        val proporcion = ((y - yMin) / rango).coerceIn(-0.1, 1.1)
        return (plotY + plotH) - proporcion * plotH
    }

    private fun formatear(valor: Double): String = String.format(Locale.US, "%.1f", valor)
}
