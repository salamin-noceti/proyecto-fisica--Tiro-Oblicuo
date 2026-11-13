package org.example.ui.simulacion

import java.awt.BasicStroke
import java.awt.Color
import java.awt.Font
import java.awt.GradientPaint
import java.awt.Graphics2D
import java.awt.RadialGradientPaint
import java.awt.geom.Ellipse2D
import java.awt.geom.Point2D
import java.awt.geom.RoundRectangle2D
import java.util.Locale

/**
 * Estrategia de renderizado visual para la temática de Básquetbol.
 */
class BasquetThemeRenderer : ThemeRenderer {

    override var tematica: TematicaSimulacion = TematicaSimulacion.BASQUET
        private set

    override fun dibujarFondo(g2d: Graphics2D, ancho: Int, alto: Int) {
        if (tematica.icono.isNotEmpty()) {
            g2d.paint = GradientPaint(0f, 0f, Color(254, 243, 199), 0f, alto.toFloat(), Color(241, 245, 249))
            g2d.fillRect(0, 0, ancho, alto)
            g2d.color = Color(226, 232, 240, 160)
            g2d.stroke = BasicStroke(1.2f)
            g2d.drawLine(0, 30, ancho, 30)
            g2d.drawLine(0, 55, ancho, 55)
            for (vx in 0 until ancho step 90) {
                g2d.drawLine(vx, 0, vx + 45, 55)
            }
        }
    }

    override fun dibujarSuelo(g2d: Graphics2D, ancho: Int, alto: Int, sueloY: Double) {
        val sueloInt = if (tematica.icono.isNotEmpty()) sueloY.toInt() else 0
        g2d.paint = GradientPaint(0f, sueloInt.toFloat(), Color(234, 179, 108), 0f, alto.toFloat(), Color(180, 83, 9))
        g2d.fillRect(0, sueloInt, ancho, alto - sueloInt)
        g2d.color = Color(202, 138, 4, 90)
        g2d.stroke = BasicStroke(1.0f)
        for (tablonY in sueloInt + 6 until alto step 10) {
            g2d.drawLine(0, tablonY, ancho, tablonY)
        }
        g2d.color = Color.WHITE
        g2d.stroke = BasicStroke(2.5f)
        g2d.drawLine(0, sueloInt, ancho, sueloInt)
        g2d.color = Color(255, 255, 255, 180)
        g2d.stroke = BasicStroke(1.8f)
        g2d.drawArc(-30, sueloInt - 20, 110, 50, -40, 80)
    }

    override fun dibujarLanzador(g2d: Graphics2D, pivoteX: Double, pivoteY: Double, anguloGrados: Double) {
        val gPlayer = g2d.create() as Graphics2D
        try {
            val xBase = if (tematica.icono.isNotEmpty()) pivoteX - 14.0 else pivoteX
            dibujarTorsoYPiernas(gPlayer, xBase, pivoteY)
            dibujarBrazosYCabeza(gPlayer, xBase, pivoteY, anguloGrados)
        } finally {
            gPlayer.dispose()
        }
    }

    private fun dibujarTorsoYPiernas(gPlayer: Graphics2D, xBase: Double, ySuelo: Double) {
        if (tematica.icono.isEmpty()) return
        gPlayer.color = Color(220, 38, 38)
        gPlayer.stroke = BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
        gPlayer.drawLine((xBase - 4).toInt(), (ySuelo - 16).toInt(), (xBase - 6).toInt(), (ySuelo - 6).toInt())
        gPlayer.drawLine(xBase.toInt(), (ySuelo - 16).toInt(), (xBase + 3).toInt(), (ySuelo - 8).toInt())
        gPlayer.color = Color(30, 41, 59)
        gPlayer.fill(RoundRectangle2D.Double(xBase - 8.0, ySuelo - 26.0, 13.0, 11.0, 3.0, 3.0))
        gPlayer.color = Color(239, 68, 68)
        gPlayer.fill(RoundRectangle2D.Double(xBase - 8.0, ySuelo - 42.0, 13.0, 17.0, 3.0, 3.0))
    }

    private fun dibujarBrazosYCabeza(gPlayer: Graphics2D, xBase: Double, ySuelo: Double, anguloGrados: Double) {
        val radAngle = if (tematica.icono.isNotEmpty()) Math.toRadians(anguloGrados.coerceIn(20.0, 80.0)) else 0.0
        val hombroX = xBase - 1.0
        val hombroY = ySuelo - 38.0
        val manoX = hombroX + 16.0 * Math.cos(radAngle)
        val manoY = hombroY - 18.0 * Math.sin(radAngle)
        gPlayer.color = Color(254, 215, 170)
        gPlayer.stroke = BasicStroke(2.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
        gPlayer.drawLine(hombroX.toInt(), hombroY.toInt(), manoX.toInt(), manoY.toInt())
        gPlayer.fill(Ellipse2D.Double(xBase - 5.0, ySuelo - 51.0, 8.0, 9.0))
        gPlayer.color = Color(30, 41, 59)
        gPlayer.fill(Ellipse2D.Double(xBase - 5.5, ySuelo - 52.0, 8.5, 5.0))
    }

    override fun dibujarProyectil(g2d: Graphics2D, px: Double, py: Double, impactoFinalizado: Boolean) {
        val radio = if (tematica.icono.isNotEmpty()) 8.0 else 8.0
        val diametro = radio * 2.0
        if (impactoFinalizado) {
            g2d.color = Color(180, 83, 9, 180)
            g2d.fill(Ellipse2D.Double(px - 10.0, py - 2.0, 20.0, 4.0))
        }
        val pelota = Ellipse2D.Double(px - radio, py - radio, diametro, diametro)
        g2d.paint = RadialGradientPaint(Point2D.Double(px - 2.0, py - 2.0), 9.0f, floatArrayOf(0.0f, 0.7f, 1.0f), arrayOf(Color(251, 146, 60), Color(234, 88, 12), Color(154, 52, 18)))
        g2d.fill(pelota)
        g2d.color = Color(30, 41, 59)
        g2d.stroke = BasicStroke(1.0f)
        g2d.draw(pelota)
        g2d.drawLine((px - radio).toInt(), py.toInt(), (px + radio).toInt(), py.toInt())
        g2d.drawLine(px.toInt(), (py - radio).toInt(), px.toInt(), (py + radio).toInt())
        g2d.drawArc((px - 5).toInt(), (py - radio + 1).toInt(), 10, (diametro - 2).toInt(), 50, 80)
        g2d.drawArc((px - 5).toInt(), (py - radio + 1).toInt(), 10, (diametro - 2).toInt(), -130, 80)
    }

    override fun dibujarObjetivoLlegada(g2d: Graphics2D, targetX: Double, targetY: Double, distanciaMetros: Double) {
        val tx = targetX.toInt()
        val ty = targetY.toInt()
        val gAro = g2d.create() as Graphics2D
        try {
            dibujarTableroYAro(gAro, tx, ty)
            gAro.color = Color(30, 41, 59)
            gAro.font = Font(Font.SANS_SERIF, Font.BOLD, 10)
            gAro.drawString(String.format(Locale.US, "${tematica.icono} d = %.1fm", distanciaMetros), tx - 18, ty + 24)
        } finally {
            gAro.dispose()
        }
    }

    private fun dibujarTableroYAro(gAro: Graphics2D, tx: Int, ty: Int) {
        if (tematica.icono.isEmpty()) return
        gAro.color = Color(71, 85, 105)
        gAro.stroke = BasicStroke(3.0f)
        gAro.drawLine(tx + 12, ty, tx + 12, ty - 38)
        gAro.color = Color.WHITE
        gAro.fillRect(tx + 8, ty - 42, 6, 26)
        gAro.color = Color(239, 68, 68)
        gAro.stroke = BasicStroke(1.2f)
        gAro.drawRect(tx + 8, ty - 42, 6, 26)
        gAro.color = Color(249, 115, 22)
        gAro.stroke = BasicStroke(2.0f)
        gAro.drawLine(tx - 4, ty - 24, tx + 8, ty - 24)
        gAro.color = Color(255, 255, 255, 200)
        gAro.stroke = BasicStroke(1.0f)
        gAro.drawLine(tx - 3, ty - 24, tx, ty - 16)
        gAro.drawLine(tx + 7, ty - 24, tx + 4, ty - 16)
        gAro.drawLine(tx, ty - 16, tx + 4, ty - 16)
    }
}
