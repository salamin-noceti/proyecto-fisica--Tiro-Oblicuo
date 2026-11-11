package org.example.ui.simulacion

import java.awt.BasicStroke
import java.awt.Color
import java.awt.GradientPaint
import java.awt.Graphics2D
import java.awt.RadialGradientPaint
import java.awt.geom.Ellipse2D
import java.awt.geom.Point2D
import java.awt.geom.RoundRectangle2D

/**
 * Estrategia de renderizado visual para la temática clásica de Cañón de artillería.
 * Dibuja un campo abierto, cañón de acero orientable en ángulo $\theta$ y bala de cañón esférica.
 */
class CanonThemeRenderer : ThemeRenderer {

    override val tematica: TematicaSimulacion = TematicaSimulacion.CANON

    override fun dibujarFondo(g2d: Graphics2D, ancho: Int, alto: Int) {
        val degradado = GradientPaint(
            0f, 0f, Color(219, 234, 254),
            0f, alto.toFloat(), Color(248, 250, 252)
        )
        g2d.paint = degradado
        g2d.fillRect(0, 0, ancho, alto)
    }

    override fun dibujarSuelo(g2d: Graphics2D, ancho: Int, alto: Int, sueloY: Double) {
        val sueloInt = sueloY.toInt()

        val degradadoPasto = GradientPaint(
            0f, sueloInt.toFloat(), Color(34, 197, 94),
            0f, alto.toFloat(), Color(21, 128, 61)
        )
        g2d.paint = degradadoPasto
        g2d.fillRect(0, sueloInt, ancho, alto - sueloInt)

        g2d.color = Color.WHITE
        g2d.stroke = BasicStroke(2.0f)
        g2d.drawLine(0, sueloInt, ancho, sueloInt)

        g2d.color = Color(22, 101, 52, 120)
        g2d.drawLine(0, sueloInt + 2, ancho, sueloInt + 2)
    }

    override fun dibujarLanzador(
        g2d: Graphics2D,
        pivoteX: Double,
        pivoteY: Double,
        anguloGrados: Double
    ) {
        val gCanon = g2d.create() as Graphics2D
        try {
            // Rotación del cañón orientada según el ángulo del tiro
            val radianes = -Math.toRadians(anguloGrados)
            gCanon.rotate(radianes, pivoteX, pivoteY)

            // Tubo del cañón de artillería
            val largoTubo = 36.0
            val anchoTubo = 13.0
            val tubo = RoundRectangle2D.Double(
                pivoteX - 4.0,
                pivoteY - (anchoTubo / 2.0),
                largoTubo,
                anchoTubo,
                4.0,
                4.0
            )

            gCanon.paint = GradientPaint(
                pivoteX.toFloat(), (pivoteY - anchoTubo / 2).toFloat(), Color(71, 85, 105),
                pivoteX.toFloat(), (pivoteY + anchoTubo / 2).toFloat(), Color(30, 41, 59)
            )
            gCanon.fill(tubo)

            gCanon.color = Color(15, 23, 42)
            gCanon.stroke = BasicStroke(1.5f)
            gCanon.draw(tubo)

            // Anillo reforzado de la boca del cañón
            val anillo = RoundRectangle2D.Double(
                pivoteX + largoTubo - 5.0,
                pivoteY - (anchoTubo / 2.0) - 1.0,
                5.0,
                anchoTubo + 2.0,
                2.0,
                2.0
            )
            gCanon.color = Color(100, 116, 139)
            gCanon.fill(anillo)
        } finally {
            gCanon.dispose()
        }

        // Rueda del cañón en el eje
        val radioRueda = 9.0
        val diametroRueda = radioRueda * 2.0
        val rueda = Ellipse2D.Double(
            pivoteX - radioRueda,
            pivoteY - radioRueda,
            diametroRueda,
            diametroRueda
        )

        g2d.paint = GradientPaint(
            (pivoteX - radioRueda).toFloat(), (pivoteY - radioRueda).toFloat(), Color(100, 116, 139),
            (pivoteX + radioRueda).toFloat(), (pivoteY + radioRueda).toFloat(), Color(51, 65, 85)
        )
        g2d.fill(rueda)

        g2d.color = Color(30, 41, 59)
        g2d.stroke = BasicStroke(1.5f)
        g2d.draw(rueda)

        // Eje central
        g2d.color = Color(241, 245, 249)
        g2d.fill(Ellipse2D.Double(pivoteX - 2.5, pivoteY - 2.5, 5.0, 5.0))
    }

    override fun dibujarProyectil(
        g2d: Graphics2D,
        px: Double,
        py: Double,
        impactoFinalizado: Boolean
    ) {
        val radio = 7.0
        val diametro = radio * 2.0

        if (impactoFinalizado) {
            // Efecto gráfico de impacto en tierra
            g2d.color = Color(245, 158, 11, 190)
            g2d.fill(Ellipse2D.Double(px - 12.0, py - 3.0, 24.0, 6.0))
        }

        // Bala de cañón esférica oscura con brillo metálico
        val centro = Point2D.Double(px - 2.0, py - 2.0)
        val gradiente = RadialGradientPaint(
            centro,
            8.0f,
            floatArrayOf(0.0f, 0.6f, 1.0f),
            arrayOf(Color(148, 163, 184), Color(51, 65, 85), Color(15, 23, 42))
        )

        val esfera = Ellipse2D.Double(px - radio, py - radio, diametro, diametro)
        g2d.paint = gradiente
        g2d.fill(esfera)

        g2d.color = Color(15, 23, 42)
        g2d.stroke = BasicStroke(1.2f)
        g2d.draw(esfera)
    }
}
