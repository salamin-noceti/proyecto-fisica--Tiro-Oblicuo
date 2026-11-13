package org.example.ui.simulacion.graficos

import java.awt.BasicStroke
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.Stroke
import java.awt.geom.Ellipse2D
import java.awt.geom.Line2D
import java.awt.geom.Path2D
import java.awt.geom.Rectangle2D
import java.util.Locale

/**
 * Pintor dedicado a los elementos geométricos (ejes, grilla, curvas, cursor y leyenda)
 * de [CartesianChartCanvas].
 */
class PintorElementosCartesiano(
    private val canvas: CartesianChartCanvas
) {
    private val trazoGrilla: Stroke = BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, floatArrayOf(4.0f, 4.0f), 0.0f)
    private val trazoCursor: Stroke = BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, floatArrayOf(5.0f, 4.0f), 0.0f)

    fun dibujarCuadriculaYEjes(g2: Graphics2D, rect: IntArray) {
        dibujarGrillaVertical(g2, rect)
        dibujarGrillaHorizontal(g2, rect)
        if (canvas.yMin <= 0.0 && canvas.yMax >= 0.0) {
            val pyCero = canvas.aPixelY(0.0, rect[1], rect[3])
            g2.color = Color(51, 65, 85)
            g2.stroke = BasicStroke(1.6f)
            g2.draw(Line2D.Double(rect[0].toDouble(), pyCero, (rect[0] + rect[2]).toDouble(), pyCero))
        }
        g2.color = Color(203, 213, 225)
        g2.stroke = BasicStroke(1.2f)
        g2.draw(Rectangle2D.Double(rect[0].toDouble(), rect[1].toDouble(), rect[2].toDouble(), rect[3].toDouble()))
    }

    private fun dibujarGrillaVertical(g2: Graphics2D, rect: IntArray) {
        val rangoX = (canvas.xMax - canvas.xMin).coerceAtLeast(1e-5)
        g2.font = Font(Font.SANS_SERIF, Font.PLAIN, 10)
        for (i in 0..5) {
            val valorX = canvas.xMin + (rangoX * i / 5)
            val px = canvas.aPixelX(valorX, rect[0], rect[2])
            g2.color = Color(241, 245, 249)
            g2.stroke = trazoGrilla
            g2.draw(Line2D.Double(px, rect[1].toDouble(), px, (rect[1] + rect[3]).toDouble()))
            g2.color = Color(100, 116, 139)
            val texto = formatear(valorX)
            g2.drawString(texto, (px - g2.fontMetrics.stringWidth(texto) / 2).toFloat(), (rect[1] + rect[3] + 16).toFloat())
        }
        g2.font = Font(Font.SANS_SERIF, Font.BOLD, 10)
        g2.color = Color(71, 85, 105)
        g2.drawString(canvas.etiquetaEjeX, (rect[0] + rect[2] - g2.fontMetrics.stringWidth(canvas.etiquetaEjeX)).toFloat(), (rect[1] + rect[3] + 30).toFloat())
    }

    private fun dibujarGrillaHorizontal(g2: Graphics2D, rect: IntArray) {
        val rangoY = (canvas.yMax - canvas.yMin).coerceAtLeast(1e-5)
        g2.font = Font(Font.SANS_SERIF, Font.PLAIN, 10)
        for (i in 0..5) {
            val valorY = canvas.yMin + (rangoY * i / 5)
            val py = canvas.aPixelY(valorY, rect[1], rect[3])
            g2.color = Color(241, 245, 249)
            g2.stroke = trazoGrilla
            g2.draw(Line2D.Double(rect[0].toDouble(), py, (rect[0] + rect[2]).toDouble(), py))
            g2.color = Color(100, 116, 139)
            val texto = formatear(valorY)
            g2.drawString(texto, (rect[0] - g2.fontMetrics.stringWidth(texto) - 8).toFloat(), (py + g2.fontMetrics.ascent / 2 - 1).toFloat())
        }
        g2.font = Font(Font.SANS_SERIF, Font.BOLD, 10)
        g2.color = Color(71, 85, 105)
        g2.drawString(canvas.etiquetaEjeY, rect[0].toFloat(), (rect[1] - 8).toFloat())
    }

    fun dibujarSeries(g2: Graphics2D, rect: IntArray) {
        val clipOriginal = g2.clip
        g2.setClip(rect[0], rect[1], rect[2], rect[3])
        for (serie in canvas.obtenerSeries()) {
            trazarCurvaSerie(g2, serie, rect)
        }
        g2.clip = clipOriginal
    }

    private fun trazarCurvaSerie(g2: Graphics2D, serie: SerieCartesiana, rect: IntArray) {
        if (serie.puntos.isEmpty() || canvas.xMax < canvas.xMin) return
        g2.color = serie.color
        g2.stroke = serie.trazo
        val path = Path2D.Double()
        for ((idx, punto) in serie.puntos.withIndex()) {
            val px = canvas.aPixelX(punto.x, rect[0], rect[2])
            val py = canvas.aPixelY(punto.y, rect[1], rect[3])
            if (idx == 0) path.moveTo(px, py) else path.lineTo(px, py)
        }
        g2.draw(path)
        val ultimo = serie.puntos.last()
        val pxU = canvas.aPixelX(ultimo.x, rect[0], rect[2])
        val pyU = canvas.aPixelY(ultimo.y, rect[1], rect[3])
        g2.fill(Ellipse2D.Double(pxU - 3.5, pyU - 3.5, 7.0, 7.0))
    }

    fun dibujarCursorTiempo(g2: Graphics2D, rect: IntArray) {
        if (canvas.tiempoActual < canvas.xMin || canvas.tiempoActual > canvas.xMax) return
        val px = canvas.aPixelX(canvas.tiempoActual, rect[0], rect[2])
        val clipOriginal = g2.clip
        g2.setClip(rect[0] - 1, rect[1], rect[2] + 2, rect[3])
        g2.color = Color(239, 68, 68, 200); g2.stroke = trazoCursor
        g2.draw(Line2D.Double(px, rect[1].toDouble(), px, (rect[1] + rect[3]).toDouble()))
        for (serie in canvas.obtenerSeries()) {
            val valor = serie.valorActual ?: continue
            val py = canvas.aPixelY(valor, rect[1], rect[3])
            g2.color = Color(serie.color.red, serie.color.green, serie.color.blue, 70); g2.fill(Ellipse2D.Double(px - 5.5, py - 5.5, 11.0, 11.0))
            g2.color = serie.color; g2.fill(Ellipse2D.Double(px - 3.5, py - 3.5, 7.0, 7.0))
            g2.color = Color.WHITE; g2.stroke = BasicStroke(1.2f); g2.draw(Ellipse2D.Double(px - 3.5, py - 3.5, 7.0, 7.0))
        }
        g2.clip = clipOriginal
    }

    fun dibujarEncabezadoYLeyenda(g2: Graphics2D, anchoTotal: Int) {
        g2.font = Font(Font.SANS_SERIF, Font.BOLD, 12)
        g2.color = Color(15, 23, 42)
        g2.drawString(canvas.titulo, 62f, 20f)
        var posLegX = anchoTotal - 24
        g2.font = Font(Font.SANS_SERIF, Font.BOLD, 10)
        for (serie in canvas.obtenerSeries().reversed()) {
            val valorTexto = serie.valorActual?.let { "${formatear(it)} ${serie.unidad}".trim() } ?: "—"
            val texto = "${serie.nombre}: $valorTexto"
            posLegX -= (g2.fontMetrics.stringWidth(texto) + 16)
            if (posLegX >= 142) {
                g2.color = serie.color
                g2.fill(Ellipse2D.Double(posLegX.toDouble(), 14.0, 7.0, 7.0))
                g2.color = Color(30, 41, 59)
                g2.drawString(texto, (posLegX + 11).toFloat(), 20f)
                posLegX -= 10
            }
        }
    }

    private fun formatear(valor: Double): String {
        val v = if (canvas.xMax >= canvas.xMin) valor else valor
        return String.format(Locale.US, "%.1f", v)
    }
}
