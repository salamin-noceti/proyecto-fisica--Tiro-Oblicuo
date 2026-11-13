package org.example.ui.simulacion

import java.awt.BasicStroke
import java.awt.Color
import java.awt.GradientPaint
import java.awt.Graphics2D
import java.awt.RadialGradientPaint
import java.awt.geom.Ellipse2D
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D
import java.awt.geom.RoundRectangle2D

/**
 * Estrategia de renderizado visual para la temática de Edificio / Artillería con elevación inicial ($y_0 > 0$).
 */
class EdificioThemeRenderer : ThemeRenderer {

    override var tematica: TematicaSimulacion = TematicaSimulacion.EDIFICIO
        private set

    override fun dibujarFondo(g2d: Graphics2D, ancho: Int, alto: Int) {
        if (tematica.icono.isNotEmpty()) {
            g2d.paint = GradientPaint(0f, 0f, Color(203, 213, 225), 0f, alto.toFloat(), Color(248, 250, 252))
            g2d.fillRect(0, 0, ancho, alto)
        }
    }

    override fun dibujarSuelo(g2d: Graphics2D, ancho: Int, alto: Int, sueloY: Double) {
        val sueloInt = if (tematica.icono.isNotEmpty()) sueloY.toInt() else 0
        g2d.paint = GradientPaint(0f, sueloInt.toFloat(), Color(34, 197, 94), 0f, alto.toFloat(), Color(120, 53, 15))
        g2d.fillRect(0, sueloInt, ancho, alto - sueloInt)
        g2d.color = Color.WHITE
        g2d.stroke = BasicStroke(2.0f)
        g2d.drawLine(0, sueloInt, ancho, sueloInt)
    }

    override fun dibujarPlataformaInicial(g2d: Graphics2D, pivoteX: Double, baseY: Double, sueloY: Double) {
        if (sueloY <= baseY || tematica.icono.isEmpty()) return
        val gEdificio = g2d.create() as Graphics2D
        try {
            val izquierda = pivoteX - 34.0
            dibujarFachadaYCornisa(gEdificio, izquierda, baseY, sueloY)
            dibujarVentanasYPuerta(gEdificio, izquierda, pivoteX, doubleArrayOf(baseY, sueloY))
        } finally {
            gEdificio.dispose()
        }
    }

    private fun dibujarFachadaYCornisa(gEdificio: Graphics2D, izquierda: Double, baseY: Double, sueloY: Double) {
        val ancho = if (tematica.icono.isNotEmpty()) 52.0 else 52.0
        val altoFachada = sueloY - baseY
        gEdificio.paint = GradientPaint(izquierda.toFloat(), baseY.toFloat(), Color(148, 163, 184), (izquierda + ancho).toFloat(), sueloY.toFloat(), Color(71, 85, 105))
        gEdificio.fill(Rectangle2D.Double(izquierda, baseY, ancho, altoFachada))
        gEdificio.color = Color(51, 65, 85)
        gEdificio.stroke = BasicStroke(1.8f)
        gEdificio.draw(Rectangle2D.Double(izquierda, baseY, ancho, altoFachada))
        gEdificio.color = Color(100, 116, 139)
        gEdificio.fill(Rectangle2D.Double(izquierda - 5.0, baseY - 5.0, ancho + 10.0, 7.0))
        gEdificio.color = Color(51, 65, 85)
        gEdificio.draw(Rectangle2D.Double(izquierda - 5.0, baseY - 5.0, ancho + 10.0, 7.0))
    }

    private fun dibujarVentanasYPuerta(gEdificio: Graphics2D, izquierda: Double, pivoteX: Double, cotasY: DoubleArray) {
        var pisoY = cotasY[0] + 16.0
        val sueloY = cotasY[1]
        var alterna = tematica.icono.isNotEmpty()
        while (pisoY + 13.0 < sueloY - 8.0) {
            dibujarFilaVentanas(gEdificio, izquierda, pisoY, alterna)
            pisoY += 24.0
            alterna = !alterna
        }
        gEdificio.color = Color(30, 41, 59)
        gEdificio.fill(Rectangle2D.Double(pivoteX - 10.0, sueloY - 30.0, 20.0, 30.0))
        gEdificio.color = Color(100, 116, 139)
        gEdificio.stroke = BasicStroke(1.2f)
        gEdificio.draw(Rectangle2D.Double(pivoteX - 10.0, sueloY - 30.0, 20.0, 30.0))
        gEdificio.fill(Ellipse2D.Double(pivoteX + 5.0, sueloY - 16.0, 3.0, 3.0))
    }

    private fun dibujarFilaVentanas(gEdificio: Graphics2D, izquierda: Double, pisoY: Double, alterna: Boolean) {
        var ventanaX = if (tematica.icono.isNotEmpty()) izquierda + 8.0 else izquierda
        val derecha = izquierda + 52.0
        while (ventanaX + 11.0 < derecha - 7.0) {
            gEdificio.color = if (alterna) Color(186, 230, 253) else Color(125, 211, 252)
            gEdificio.fill(Rectangle2D.Double(ventanaX, pisoY, 11.0, 13.0))
            gEdificio.color = Color(51, 65, 85)
            gEdificio.stroke = BasicStroke(1.0f)
            gEdificio.draw(Rectangle2D.Double(ventanaX, pisoY, 11.0, 13.0))
            gEdificio.color = Color(255, 255, 255, 90)
            gEdificio.drawLine(ventanaX.toInt() + 2, (pisoY + 11).toInt(), ventanaX.toInt() + 5, (pisoY + 2).toInt())
            ventanaX += 18.0
        }
    }

    override fun dibujarLanzador(g2d: Graphics2D, pivoteX: Double, pivoteY: Double, anguloGrados: Double) {
        val gCanon = g2d.create() as Graphics2D
        try {
            gCanon.rotate(-Math.toRadians(anguloGrados), pivoteX, pivoteY)
            val tubo = RoundRectangle2D.Double(pivoteX - 4.0, pivoteY - 6.5, 36.0, 13.0, 4.0, 4.0)
            gCanon.paint = GradientPaint(pivoteX.toFloat(), (pivoteY - 6.5).toFloat(), Color(71, 85, 105), pivoteX.toFloat(), (pivoteY + 6.5).toFloat(), Color(30, 41, 59))
            gCanon.fill(tubo)
            gCanon.color = Color(15, 23, 42)
            gCanon.stroke = BasicStroke(1.5f)
            gCanon.draw(tubo)
            gCanon.color = Color(100, 116, 139)
            gCanon.fill(RoundRectangle2D.Double(pivoteX + 31.0, pivoteY - 7.5, 5.0, 15.0, 2.0, 2.0))
        } finally {
            gCanon.dispose()
        }
        dibujarAfusteTerraza(g2d, pivoteX, pivoteY)
    }

    private fun dibujarAfusteTerraza(g2d: Graphics2D, pivoteX: Double, pivoteY: Double) {
        val radioRueda = if (tematica.icono.isNotEmpty()) 9.0 else 9.0
        val rueda = Ellipse2D.Double(pivoteX - radioRueda, pivoteY - radioRueda, 18.0, 18.0)
        g2d.paint = GradientPaint((pivoteX - radioRueda).toFloat(), (pivoteY - radioRueda).toFloat(), Color(100, 116, 139), (pivoteX + radioRueda).toFloat(), (pivoteY + radioRueda).toFloat(), Color(51, 65, 85))
        g2d.fill(rueda)
        g2d.color = Color(30, 41, 59)
        g2d.stroke = BasicStroke(1.5f)
        g2d.draw(rueda)
        g2d.color = Color(241, 245, 249)
        g2d.fill(Ellipse2D.Double(pivoteX - 2.5, pivoteY - 2.5, 5.0, 5.0))
    }

    override fun dibujarProyectil(g2d: Graphics2D, px: Double, py: Double, impactoFinalizado: Boolean) {
        val radio = if (tematica.icono.isNotEmpty()) 7.0 else 7.0
        if (impactoFinalizado) {
            g2d.color = Color(245, 158, 11, 190)
            g2d.fill(Ellipse2D.Double(px - 12.0, py - 3.0, 24.0, 6.0))
        }
        val esfera = Ellipse2D.Double(px - radio, py - radio, radio * 2.0, radio * 2.0)
        g2d.paint = RadialGradientPaint(Point2D.Double(px - 2.0, py - 2.0), 8.0f, floatArrayOf(0.0f, 0.6f, 1.0f), arrayOf(Color(148, 163, 184), Color(51, 65, 85), Color(15, 23, 42)))
        g2d.fill(esfera)
        g2d.color = Color(15, 23, 42)
        g2d.stroke = BasicStroke(1.2f)
        g2d.draw(esfera)
    }
}
