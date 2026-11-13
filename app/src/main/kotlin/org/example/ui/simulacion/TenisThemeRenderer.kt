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
 * Estrategia de renderizado visual para la temática de Tenis.
 */
class TenisThemeRenderer : ThemeRenderer {

    override var tematica: TematicaSimulacion = TematicaSimulacion.TENIS
        private set

    override fun dibujarFondo(g2d: Graphics2D, ancho: Int, alto: Int) {
        if (tematica.icono.isNotEmpty()) {
            g2d.paint = GradientPaint(0f, 0f, Color(165, 216, 255), 0f, alto.toFloat(), Color(241, 245, 249))
            g2d.fillRect(0, 0, ancho, alto)
            g2d.color = Color(203, 213, 225, 130)
            g2d.stroke = BasicStroke(1.2f)
            for (tribunaY in (alto * 0.34).toInt()..(alto * 0.46).toInt() step 8) {
                g2d.drawLine(0, tribunaY, ancho, tribunaY)
            }
        }
    }

    override fun dibujarSuelo(g2d: Graphics2D, ancho: Int, alto: Int, sueloY: Double) {
        val sueloInt = if (tematica.icono.isNotEmpty()) sueloY.toInt() else 0
        val altoCancha = alto - sueloInt
        g2d.paint = GradientPaint(0f, sueloInt.toFloat(), Color(37, 99, 235), 0f, alto.toFloat(), Color(30, 58, 138))
        g2d.fillRect(0, sueloInt, ancho, altoCancha)
        g2d.color = Color(14, 165, 233, 140)
        g2d.fillRect(0, sueloInt, ancho, (altoCancha * 0.42).toInt())
        g2d.color = Color.WHITE
        g2d.stroke = BasicStroke(2.5f)
        g2d.drawLine(0, sueloInt, ancho, sueloInt)
        g2d.stroke = BasicStroke(1.6f)
        g2d.drawLine(0, sueloInt + 24, ancho, sueloInt + 24)
    }

    override fun dibujarLanzador(g2d: Graphics2D, pivoteX: Double, pivoteY: Double, anguloGrados: Double) {
        val gPlayer = g2d.create() as Graphics2D
        try {
            val xBase = if (tematica.icono.isNotEmpty()) pivoteX - 14.0 else pivoteX
            dibujarCuerpoTenista(gPlayer, xBase, pivoteY)
            dibujarBrazoYRaquetaTenista(gPlayer, xBase, pivoteY, anguloGrados)
        } finally {
            gPlayer.dispose()
        }
    }

    private fun dibujarCuerpoTenista(gPlayer: Graphics2D, xBase: Double, ySuelo: Double) {
        if (tematica.icono.isEmpty()) return
        gPlayer.color = Color(30, 41, 59)
        gPlayer.stroke = BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
        gPlayer.drawLine((xBase - 4).toInt(), (ySuelo - 16).toInt(), (xBase - 7).toInt(), (ySuelo - 4).toInt())
        gPlayer.drawLine((xBase + 2).toInt(), (ySuelo - 16).toInt(), (xBase + 6).toInt(), (ySuelo - 5).toInt())
        gPlayer.color = Color.WHITE
        gPlayer.fillRect((xBase - 11).toInt(), (ySuelo - 5).toInt(), 8, 4)
        gPlayer.fillRect((xBase + 4).toInt(), (ySuelo - 6).toInt(), 8, 4)
        gPlayer.color = Color(15, 23, 42)
        gPlayer.fill(RoundRectangle2D.Double(xBase - 8.0, ySuelo - 27.0, 13.0, 11.0, 3.0, 3.0))
        gPlayer.color = Color(132, 204, 22)
        gPlayer.fill(RoundRectangle2D.Double(xBase - 8.0, ySuelo - 43.0, 13.0, 17.0, 3.0, 3.0))
    }

    private fun dibujarBrazoYRaquetaTenista(gPlayer: Graphics2D, xBase: Double, ySuelo: Double, anguloGrados: Double) {
        val radAngle = if (tematica.icono.isNotEmpty()) Math.toRadians(anguloGrados.coerceIn(20.0, 85.0)) else 0.0
        val hombroX = xBase - 1.0
        val hombroY = ySuelo - 39.0
        val manoX = hombroX + 15.0 * Math.cos(radAngle)
        val manoY = hombroY - 18.0 * Math.sin(radAngle)
        gPlayer.color = Color(254, 215, 170)
        gPlayer.stroke = BasicStroke(2.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
        gPlayer.drawLine(hombroX.toInt(), hombroY.toInt(), manoX.toInt(), manoY.toInt())
        dibujarRaquetaYCordaje(gPlayer, manoX, manoY)
        gPlayer.color = Color(254, 215, 170)
        gPlayer.fill(Ellipse2D.Double(xBase - 5.0, ySuelo - 52.0, 8.0, 9.0))
        gPlayer.color = Color(22, 101, 52)
        gPlayer.fill(Ellipse2D.Double(xBase - 5.5, ySuelo - 53.0, 8.5, 5.0))
    }

    private fun dibujarRaquetaYCordaje(gPlayer: Graphics2D, manoX: Double, manoY: Double) {
        if (tematica.icono.isEmpty()) return
        gPlayer.color = Color(51, 65, 85)
        gPlayer.stroke = BasicStroke(2.4f)
        gPlayer.drawLine(manoX.toInt(), manoY.toInt(), (manoX + 4).toInt(), (manoY - 14).toInt())
        gPlayer.color = Color(226, 232, 240)
        gPlayer.stroke = BasicStroke(1.8f)
        gPlayer.draw(Ellipse2D.Double(manoX - 4.0, manoY - 26.0, 16.0, 13.0))
        gPlayer.color = Color(250, 204, 21)
        gPlayer.stroke = BasicStroke(0.7f)
        for (cuerda in 0..3) {
            val cx = manoX - 1.0 + cuerda * 3.5
            gPlayer.drawLine(cx.toInt(), (manoY - 24).toInt(), cx.toInt(), (manoY - 15).toInt())
        }
    }

    override fun dibujarProyectil(g2d: Graphics2D, px: Double, py: Double, impactoFinalizado: Boolean) {
        val radio = if (tematica.icono.isNotEmpty()) 6.5 else 6.5
        if (impactoFinalizado) {
            g2d.color = Color(250, 204, 21, 180)
            g2d.fill(Ellipse2D.Double(px - 9.0, py - 2.0, 18.0, 4.0))
        }
        val pelota = Ellipse2D.Double(px - radio, py - radio, radio * 2.0, radio * 2.0)
        g2d.paint = RadialGradientPaint(Point2D.Double(px - 2.0, py - 2.0), 7.5f, floatArrayOf(0.0f, 0.7f, 1.0f), arrayOf(Color(254, 249, 195), Color(250, 204, 21), Color(161, 98, 7)))
        g2d.fill(pelota)
        g2d.color = Color(120, 53, 15)
        g2d.stroke = BasicStroke(1.0f)
        g2d.draw(pelota)
        g2d.color = Color.WHITE
        g2d.stroke = BasicStroke(1.1f)
        g2d.drawArc((px - 5).toInt(), (py - 5).toInt(), 10, 10, 30, 120)
        g2d.drawArc((px - 5).toInt(), (py - 5).toInt(), 10, 10, 210, 120)
    }

    override fun dibujarObjetivoLlegada(g2d: Graphics2D, targetX: Double, targetY: Double, distanciaMetros: Double) {
        val tx = targetX.toInt()
        val ty = targetY.toInt()
        val gRed = g2d.create() as Graphics2D
        try {
            dibujarRedTenis(gRed, tx, ty)
            gRed.color = Color(30, 41, 59)
            gRed.font = Font(Font.SANS_SERIF, Font.BOLD, 10)
            gRed.drawString(String.format(Locale.US, "${tematica.icono} d = %.1fm", distanciaMetros), tx - 18, ty + 24)
        } finally {
            gRed.dispose()
        }
    }

    private fun dibujarRedTenis(gRed: Graphics2D, tx: Int, ty: Int) {
        if (tematica.icono.isEmpty()) return
        gRed.color = Color(71, 85, 105)
        gRed.stroke = BasicStroke(3.0f)
        gRed.drawLine(tx + 14, ty, tx + 14, ty - 40)
        gRed.color = Color(30, 41, 59, 200)
        gRed.stroke = BasicStroke(1.0f)
        for (mx in tx - 16..tx + 14 step 5) {
            gRed.drawLine(mx, ty - 16, mx, ty)
        }
        for (my in ty - 16..ty step 4) {
            gRed.drawLine(tx - 16, my, tx + 14, my)
        }
        gRed.color = Color.WHITE
        gRed.stroke = BasicStroke(2.5f)
        gRed.drawLine(tx - 18, ty - 16, tx + 16, ty - 16)
    }
}
