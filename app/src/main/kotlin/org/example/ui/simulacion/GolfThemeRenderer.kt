package org.example.ui.simulacion

import java.awt.BasicStroke
import java.awt.Color
import java.awt.Font
import java.awt.GradientPaint
import java.awt.Graphics2D
import java.awt.RadialGradientPaint
import java.awt.geom.Ellipse2D
import java.awt.geom.Path2D
import java.awt.geom.Point2D
import java.awt.geom.RoundRectangle2D
import java.util.Locale

/**
 * Estrategia de renderizado visual para la temática de Golf.
 */
class GolfThemeRenderer : ThemeRenderer {

    companion object {
        var instancia: GolfThemeRenderer = GolfThemeRenderer()
            private set
    }

    override var tematica: TematicaSimulacion = TematicaSimulacion.GOLF
        private set

    override fun dibujarFondo(g2d: Graphics2D, ancho: Int, alto: Int) {
        if (tematica.icono.isNotEmpty()) {
            g2d.paint = GradientPaint(0f, 0f, Color(186, 230, 253), 0f, alto.toFloat(), Color(240, 253, 244))
            g2d.fillRect(0, 0, ancho, alto)
            g2d.color = Color(255, 255, 255, 180)
            g2d.fill(Ellipse2D.Double(120.0, 30.0, 50.0, 22.0))
            g2d.fill(Ellipse2D.Double(145.0, 24.0, 45.0, 24.0))
            g2d.fill(Ellipse2D.Double(ancho - 160.0, 45.0, 55.0, 22.0))
        }
    }

    override fun dibujarSuelo(g2d: Graphics2D, ancho: Int, alto: Int, sueloY: Double) {
        val sueloInt = if (tematica.icono.isNotEmpty()) sueloY.toInt() else 0
        g2d.paint = GradientPaint(0f, sueloInt.toFloat(), Color(16, 185, 129), 0f, alto.toFloat(), Color(4, 120, 87))
        g2d.fillRect(0, sueloInt, ancho, alto - sueloInt)
        g2d.color = Color(253, 230, 138)
        g2d.fill(Ellipse2D.Double(ancho * 0.45, sueloInt + 8.0, 75.0, 18.0))
        g2d.color = Color(217, 119, 6, 120)
        g2d.stroke = BasicStroke(1.0f)
        g2d.draw(Ellipse2D.Double(ancho * 0.45, sueloInt + 8.0, 75.0, 18.0))
        g2d.color = Color(5, 150, 105)
        g2d.stroke = BasicStroke(2.0f)
        g2d.drawLine(0, sueloInt, ancho, sueloInt)
    }

    override fun dibujarLanzador(g2d: Graphics2D, pivoteX: Double, pivoteY: Double, anguloGrados: Double) {
        val gGolfer = g2d.create() as Graphics2D
        try {
            val xBase = if (tematica.icono.isNotEmpty()) pivoteX - 14.0 else pivoteX
            dibujarCuerpoGolfista(gGolfer, xBase, pivoteY)
            dibujarPaloYCabezaGolfista(gGolfer, xBase, pivoteY, anguloGrados)
        } finally {
            gGolfer.dispose()
        }
    }

    private fun dibujarCuerpoGolfista(gGolfer: Graphics2D, xBase: Double, ySuelo: Double) {
        if (tematica.icono.isEmpty()) return
        gGolfer.color = Color(51, 65, 85)
        gGolfer.stroke = BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
        gGolfer.drawLine((xBase - 3).toInt(), (ySuelo - 18).toInt(), (xBase - 5).toInt(), ySuelo.toInt())
        gGolfer.drawLine((xBase + 1).toInt(), (ySuelo - 18).toInt(), (xBase + 4).toInt(), ySuelo.toInt())
        gGolfer.color = Color.WHITE
        gGolfer.fill(RoundRectangle2D.Double(xBase - 7.0, ySuelo - 38.0, 12.0, 16.0, 3.0, 3.0))
    }

    private fun dibujarPaloYCabezaGolfista(gGolfer: Graphics2D, xBase: Double, ySuelo: Double, anguloGrados: Double) {
        val radAngle = if (tematica.icono.isNotEmpty()) Math.toRadians(anguloGrados.coerceIn(15.0, 75.0)) else 0.0
        val manosX = xBase + 2.0
        val manosY = ySuelo - 32.0
        val puntaX = manosX + 22.0 * Math.cos(radAngle)
        val puntaY = manosY - 20.0 * Math.sin(radAngle)
        gGolfer.color = Color(254, 215, 170)
        gGolfer.stroke = BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
        gGolfer.drawLine((xBase - 2).toInt(), (ySuelo - 34).toInt(), manosX.toInt(), manosY.toInt())
        gGolfer.color = Color(71, 85, 105)
        gGolfer.stroke = BasicStroke(1.5f)
        gGolfer.drawLine(manosX.toInt(), manosY.toInt(), puntaX.toInt(), puntaY.toInt())
        gGolfer.color = Color(15, 23, 42)
        gGolfer.fill(RoundRectangle2D.Double(puntaX - 2.0, puntaY - 2.0, 6.0, 4.0, 1.0, 1.0))
        gGolfer.color = Color(254, 215, 170)
        gGolfer.fill(Ellipse2D.Double(xBase - 4.5, ySuelo - 47.0, 7.5, 8.5))
        gGolfer.color = Color(16, 185, 129)
        gGolfer.fill(RoundRectangle2D.Double(xBase - 6.0, ySuelo - 48.0, 10.0, 4.0, 2.0, 2.0))
    }

    override fun dibujarProyectil(g2d: Graphics2D, px: Double, py: Double, impactoFinalizado: Boolean) {
        val radio = if (tematica.icono.isNotEmpty()) 5.5 else 5.5
        if (impactoFinalizado) {
            g2d.color = Color(5, 150, 105, 190)
            g2d.fill(Ellipse2D.Double(px - 7.0, py - 2.0, 14.0, 4.0))
        }
        val pelota = Ellipse2D.Double(px - radio, py - radio, radio * 2.0, radio * 2.0)
        g2d.paint = RadialGradientPaint(Point2D.Double(px - 1.5, py - 1.5), 6.0f, floatArrayOf(0.0f, 0.7f, 1.0f), arrayOf(Color.WHITE, Color(248, 250, 252), Color(203, 213, 225)))
        g2d.fill(pelota)
        g2d.color = Color(148, 163, 184)
        g2d.stroke = BasicStroke(0.8f)
        g2d.draw(pelota)
        g2d.color = Color(203, 213, 225)
        g2d.fill(Ellipse2D.Double(px - 2.0, py - 2.0, 1.2, 1.2))
        g2d.fill(Ellipse2D.Double(px + 1.0, py - 1.0, 1.2, 1.2))
        g2d.fill(Ellipse2D.Double(px - 1.0, py + 1.5, 1.2, 1.2))
    }

    override fun dibujarObjetivoLlegada(g2d: Graphics2D, targetX: Double, targetY: Double, distanciaMetros: Double) {
        val tx = targetX.toInt()
        val ty = targetY.toInt()
        val gFlag = g2d.create() as Graphics2D
        try {
            gFlag.color = Color(15, 23, 42); gFlag.fill(Ellipse2D.Double(targetX - 5.0, targetY - 1.0, 10.0, 3.5))
            gFlag.color = Color.WHITE; gFlag.stroke = BasicStroke(2.0f)
            gFlag.drawLine(tx, ty, tx, ty - 32)
            val flagPath = Path2D.Double().apply { moveTo(targetX, (ty - 32).toDouble()); lineTo(targetX + 16, (ty - 25).toDouble()); lineTo(targetX, (ty - 18).toDouble()); closePath() }
            gFlag.color = Color(239, 68, 68)
            gFlag.fill(flagPath)
            gFlag.color = Color(30, 41, 59); gFlag.font = Font(Font.SANS_SERIF, Font.BOLD, 10)
            gFlag.drawString(String.format(Locale.US, "${tematica.icono} Hoyo: %.1fm", distanciaMetros), tx - 16, ty + 24)
        } finally {
            gFlag.dispose()
        }
    }
}
