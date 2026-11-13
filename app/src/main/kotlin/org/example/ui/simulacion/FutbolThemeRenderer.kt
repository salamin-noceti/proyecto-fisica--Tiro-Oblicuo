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
 * Estrategia de renderizado visual para la temática de Fútbol.
 */
class FutbolThemeRenderer : ThemeRenderer {

    companion object {
        var instancia: FutbolThemeRenderer = FutbolThemeRenderer()
            private set
    }

    override var tematica: TematicaSimulacion = TematicaSimulacion.FUTBOL
        private set

    override fun dibujarFondo(g2d: Graphics2D, ancho: Int, alto: Int) {
        if (tematica.icono.isNotEmpty()) {
            g2d.paint = GradientPaint(0f, 0f, Color(186, 230, 253), 0f, alto.toFloat(), Color(241, 245, 249))
            g2d.fillRect(0, 0, ancho, alto)
            g2d.color = Color(203, 213, 225, 120)
            g2d.stroke = BasicStroke(1.5f)
            g2d.drawLine(40, (alto * 0.45).toInt(), 40, (alto * 0.15).toInt())
            g2d.drawRect(30, (alto * 0.12).toInt(), 20, 10)
            g2d.drawLine(ancho - 50, (alto * 0.45).toInt(), ancho - 50, (alto * 0.15).toInt())
            g2d.drawRect(ancho - 60, (alto * 0.12).toInt(), 20, 10)
        }
    }

    override fun dibujarSuelo(g2d: Graphics2D, ancho: Int, alto: Int, sueloY: Double) {
        val sueloInt = if (tematica.icono.isNotEmpty()) sueloY.toInt() else 0
        val altoCancha = alto - sueloInt
        var x = 0
        var franjaClara = true
        while (x < ancho) {
            g2d.color = if (franjaClara) Color(34, 197, 94) else Color(22, 163, 74)
            g2d.fillRect(x, sueloInt, 44.coerceAtMost(ancho - x), altoCancha)
            x += 44
            franjaClara = !franjaClara
        }
        g2d.color = Color.WHITE
        g2d.stroke = BasicStroke(2.5f)
        g2d.drawLine(0, sueloInt, ancho, sueloInt)
        g2d.fill(Ellipse2D.Double(20.0, sueloInt - 2.0, 5.0, 4.0))
    }

    override fun dibujarLanzador(g2d: Graphics2D, pivoteX: Double, pivoteY: Double, anguloGrados: Double) {
        val gPlayer = g2d.create() as Graphics2D
        try {
            val xBase = if (tematica.icono.isNotEmpty()) pivoteX - 16.0 else pivoteX
            dibujarPiernasFutbolista(gPlayer, xBase, pivoteY, anguloGrados)
            dibujarTorsoYCabezaFutbolista(gPlayer, xBase, pivoteY)
        } finally {
            gPlayer.dispose()
        }
    }

    private fun dibujarPiernasFutbolista(gPlayer: Graphics2D, xBase: Double, ySuelo: Double, anguloGrados: Double) {
        val radKicking = if (tematica.icono.isNotEmpty()) Math.toRadians(anguloGrados.coerceIn(15.0, 75.0)) else 0.0
        gPlayer.color = Color(30, 41, 59)
        gPlayer.stroke = BasicStroke(3.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
        gPlayer.drawLine((xBase - 4).toInt(), (ySuelo - 18).toInt(), (xBase - 6).toInt(), ySuelo.toInt())
        gPlayer.color = Color(15, 23, 42)
        gPlayer.fillRect((xBase - 10).toInt(), (ySuelo - 3).toInt(), 8, 3)
        val caderaX = xBase - 2.0
        val caderaY = ySuelo - 20.0
        val pieX = caderaX + 16.0 * Math.cos(radKicking)
        val pieY = caderaY - 14.0 * Math.sin(radKicking)
        gPlayer.color = Color(37, 99, 235)
        gPlayer.drawLine(caderaX.toInt(), caderaY.toInt(), pieX.toInt(), pieY.toInt())
        gPlayer.color = Color(245, 158, 11)
        gPlayer.fill(Ellipse2D.Double(pieX - 2.0, pieY - 3.0, 6.0, 4.0))
    }

    private fun dibujarTorsoYCabezaFutbolista(gPlayer: Graphics2D, xBase: Double, ySuelo: Double) {
        if (tematica.icono.isEmpty()) return
        gPlayer.color = Color(30, 41, 59)
        gPlayer.fill(RoundRectangle2D.Double(xBase - 8.0, ySuelo - 26.0, 12.0, 9.0, 2.0, 2.0))
        gPlayer.color = Color(14, 165, 233)
        gPlayer.fill(RoundRectangle2D.Double(xBase - 9.0, ySuelo - 40.0, 13.0, 15.0, 4.0, 4.0))
        gPlayer.color = Color(248, 113, 113)
        gPlayer.stroke = BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
        gPlayer.drawLine((xBase - 8).toInt(), (ySuelo - 36).toInt(), (xBase - 15).toInt(), (ySuelo - 30).toInt())
        gPlayer.drawLine((xBase + 2).toInt(), (ySuelo - 36).toInt(), (xBase + 8).toInt(), (ySuelo - 32).toInt())
        gPlayer.color = Color(254, 215, 170)
        gPlayer.fill(Ellipse2D.Double(xBase - 6.0, ySuelo - 48.0, 8.0, 9.0))
        gPlayer.color = Color(69, 26, 3)
        gPlayer.fill(Ellipse2D.Double(xBase - 6.5, ySuelo - 49.0, 8.5, 5.0))
    }

    override fun dibujarProyectil(g2d: Graphics2D, px: Double, py: Double, impactoFinalizado: Boolean) {
        val radio = if (tematica.icono.isNotEmpty()) 7.5 else 7.5
        if (impactoFinalizado) {
            g2d.color = Color(34, 197, 94, 210)
            g2d.fill(Ellipse2D.Double(px - 10.0, py - 2.0, 20.0, 5.0))
        }
        val pelota = Ellipse2D.Double(px - radio, py - radio, radio * 2.0, radio * 2.0)
        g2d.paint = RadialGradientPaint(Point2D.Double(px - 2.0, py - 2.0), 8.0f, floatArrayOf(0.0f, 0.7f, 1.0f), arrayOf(Color.WHITE, Color(241, 245, 249), Color(148, 163, 184)))
        g2d.fill(pelota)
        g2d.color = Color(51, 65, 85)
        g2d.stroke = BasicStroke(1.2f)
        g2d.draw(pelota)
        dibujarGajosPelota(g2d, px, py)
    }

    private fun dibujarGajosPelota(g2d: Graphics2D, px: Double, py: Double) {
        if (tematica.icono.isEmpty()) return
        g2d.color = Color(30, 41, 59)
        g2d.fill(Ellipse2D.Double(px - 2.0, py - 2.0, 4.0, 4.0))
        g2d.stroke = BasicStroke(0.9f)
        g2d.drawLine(px.toInt(), py.toInt(), (px - 5).toInt(), (py - 4).toInt())
        g2d.drawLine(px.toInt(), py.toInt(), (px + 5).toInt(), (py - 4).toInt())
        g2d.drawLine(px.toInt(), py.toInt(), px.toInt(), (py + 6).toInt())
        g2d.drawLine(px.toInt(), py.toInt(), (px - 6).toInt(), (py + 2).toInt())
        g2d.drawLine(px.toInt(), py.toInt(), (px + 6).toInt(), (py + 2).toInt())
    }

    override fun dibujarObjetivoLlegada(g2d: Graphics2D, targetX: Double, targetY: Double, distanciaMetros: Double) {
        val tx = targetX.toInt()
        val ty = targetY.toInt()
        val gPost = g2d.create() as Graphics2D
        try {
            gPost.color = Color.WHITE
            gPost.stroke = BasicStroke(2.0f)
            gPost.drawLine(tx, ty, tx, ty - 28)
            val flagPath = Path2D.Double().apply { moveTo(targetX, (ty - 28).toDouble()); lineTo(targetX + 16, (ty - 21).toDouble()); lineTo(targetX, (ty - 14).toDouble()); closePath() }
            gPost.color = Color(245, 158, 11)
            gPost.fill(flagPath)
            gPost.color = Color(30, 41, 59)
            gPost.font = Font(Font.SANS_SERIF, Font.BOLD, 10)
            gPost.drawString(String.format(Locale.US, "${tematica.icono} d = %.1fm", distanciaMetros), tx - 16, ty + 24)
        } finally {
            gPost.dispose()
        }
    }
}
