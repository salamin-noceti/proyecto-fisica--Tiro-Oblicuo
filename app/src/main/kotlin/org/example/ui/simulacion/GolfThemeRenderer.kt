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
 * Dibuja un campo de golf (fairway) con búnker de arena,
 * la silueta de un golfista en el remate de su swing y una pelota de golf con hoyuelos.
 */
class GolfThemeRenderer : ThemeRenderer {

    override val tematica: TematicaSimulacion = TematicaSimulacion.GOLF

    override fun dibujarFondo(g2d: Graphics2D, ancho: Int, alto: Int) {
        val cielo = GradientPaint(
            0f, 0f, Color(186, 230, 253),
            0f, alto.toFloat(), Color(240, 253, 244)
        )
        g2d.paint = cielo
        g2d.fillRect(0, 0, ancho, alto)

        // Nubes suaves lejanas
        g2d.color = Color(255, 255, 255, 180)
        g2d.fill(Ellipse2D.Double(120.0, 30.0, 50.0, 22.0))
        g2d.fill(Ellipse2D.Double(145.0, 24.0, 45.0, 24.0))
        g2d.fill(Ellipse2D.Double(ancho - 160.0, 45.0, 55.0, 22.0))
    }

    override fun dibujarSuelo(g2d: Graphics2D, ancho: Int, alto: Int, sueloY: Double) {
        val sueloInt = sueloY.toInt()
        val altoGreen = alto - sueloInt

        // Fairway de césped esmeralda
        val greenGrad = GradientPaint(
            0f, sueloInt.toFloat(), Color(16, 185, 129),
            0f, alto.toFloat(), Color(4, 120, 87)
        )
        g2d.paint = greenGrad
        g2d.fillRect(0, sueloInt, ancho, altoGreen)

        // Búnker / trampa de arena en el relieve
        g2d.color = Color(253, 230, 138) // Arena dorada
        g2d.fill(Ellipse2D.Double(ancho * 0.45, sueloInt + 8.0, 75.0, 18.0))
        g2d.color = Color(217, 119, 6, 120)
        g2d.stroke = BasicStroke(1.0f)
        g2d.draw(Ellipse2D.Double(ancho * 0.45, sueloInt + 8.0, 75.0, 18.0))

        // Corte suave de borde del green
        g2d.color = Color(5, 150, 105)
        g2d.stroke = BasicStroke(2.0f)
        g2d.drawLine(0, sueloInt, ancho, sueloInt)
    }

    override fun dibujarLanzador(
        g2d: Graphics2D,
        pivoteX: Double,
        pivoteY: Double,
        anguloGrados: Double
    ) {
        val gGolfer = g2d.create() as Graphics2D
        try {
            val xBase = pivoteX - 14.0
            val ySuelo = pivoteY

            // Piernas del golfista
            gGolfer.color = Color(51, 65, 85)
            gGolfer.stroke = BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
            gGolfer.drawLine((xBase - 3).toInt(), (ySuelo - 18).toInt(), (xBase - 5).toInt(), ySuelo.toInt())
            gGolfer.drawLine((xBase + 1).toInt(), (ySuelo - 18).toInt(), (xBase + 4).toInt(), ySuelo.toInt())

            // Torso con chomba de golf
            gGolfer.color = Color(255, 255, 255) // Chomba blanca
            gGolfer.fill(RoundRectangle2D.Double(xBase - 7.0, ySuelo - 38.0, 12.0, 16.0, 3.0, 3.0))

            // Palo de golf (driver) orientado en el ángulo del swing
            val radAngle = Math.toRadians(anguloGrados.coerceIn(15.0, 75.0))
            val manosX = xBase + 2.0
            val manosY = ySuelo - 32.0
            val puntaPaloX = manosX + 22.0 * Math.cos(radAngle)
            val puntaPaloY = manosY - 20.0 * Math.sin(radAngle)

            // Brazo
            gGolfer.color = Color(254, 215, 170)
            gGolfer.stroke = BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
            gGolfer.drawLine((xBase - 2).toInt(), (ySuelo - 34).toInt(), manosX.toInt(), manosY.toInt())

            // Varilla de grafito del palo de golf
            gGolfer.color = Color(71, 85, 105)
            gGolfer.stroke = BasicStroke(1.5f)
            gGolfer.drawLine(manosX.toInt(), manosY.toInt(), puntaPaloX.toInt(), puntaPaloY.toInt())

            // Cabeza del driver de golf
            gGolfer.color = Color(15, 23, 42)
            gGolfer.fill(RoundRectangle2D.Double(puntaPaloX - 2.0, puntaPaloY - 2.0, 6.0, 4.0, 1.0, 1.0))

            // Cabeza y gorra
            gGolfer.color = Color(254, 215, 170)
            gGolfer.fill(Ellipse2D.Double(xBase - 4.5, ySuelo - 47.0, 7.5, 8.5))
            // Visera / gorra
            gGolfer.color = Color(16, 185, 129)
            gGolfer.fill(RoundRectangle2D.Double(xBase - 6.0, ySuelo - 48.0, 10.0, 4.0, 2.0, 2.0))

        } finally {
            gGolfer.dispose()
        }
    }

    override fun dibujarProyectil(
        g2d: Graphics2D,
        px: Double,
        py: Double,
        impactoFinalizado: Boolean
    ) {
        val radio = 5.5
        val diametro = radio * 2.0

        if (impactoFinalizado) {
            // Pique en el césped del green
            g2d.color = Color(5, 150, 105, 190)
            g2d.fill(Ellipse2D.Double(px - 7.0, py - 2.0, 14.0, 4.0))
        }

        // Pelota de golf blanca esférica
        val centro = Point2D.Double(px - 1.5, py - 1.5)
        val gradienteGolf = RadialGradientPaint(
            centro,
            6.0f,
            floatArrayOf(0.0f, 0.7f, 1.0f),
            arrayOf(Color.WHITE, Color(248, 250, 252), Color(203, 213, 225))
        )

        val pelota = Ellipse2D.Double(px - radio, py - radio, diametro, diametro)
        g2d.paint = gradienteGolf
        g2d.fill(pelota)

        g2d.color = Color(148, 163, 184)
        g2d.stroke = BasicStroke(0.8f)
        g2d.draw(pelota)

        // Hoyuelos sutiles de la pelota de golf
        g2d.color = Color(203, 213, 225)
        g2d.fill(Ellipse2D.Double(px - 2.0, py - 2.0, 1.2, 1.2))
        g2d.fill(Ellipse2D.Double(px + 1.0, py - 1.0, 1.2, 1.2))
        g2d.fill(Ellipse2D.Double(px - 1.0, py + 1.5, 1.2, 1.2))
    }

    override fun dibujarObjetivoLlegada(
        g2d: Graphics2D,
        targetX: Double,
        targetY: Double,
        distanciaMetros: Double
    ) {
        val tx = targetX.toInt()
        val ty = targetY.toInt()

        val gFlag = g2d.create() as Graphics2D
        try {
            // Hoyo en el suelo
            gFlag.color = Color(15, 23, 42)
            gFlag.fill(Ellipse2D.Double(targetX - 5.0, targetY - 1.0, 10.0, 3.5))

            // Asta del banderín de golf
            gFlag.color = Color.WHITE
            gFlag.stroke = BasicStroke(2.0f)
            gFlag.drawLine(tx, ty, tx, ty - 32)

            // Banderín rojo/amarillo del hoyo de golf
            val flagPath = Path2D.Double().apply {
                moveTo(targetX, (ty - 32).toDouble())
                lineTo(targetX + 16, (ty - 25).toDouble())
                lineTo(targetX, (ty - 18).toDouble())
                closePath()
            }
            gFlag.color = Color(239, 68, 68)
            gFlag.fill(flagPath)

            // Texto indicador del hoyo y distancia
            gFlag.color = Color(30, 41, 59)
            gFlag.font = Font(Font.SANS_SERIF, Font.BOLD, 10)
            val texto = String.format(Locale.US, "⛳ Hoyo: %.1fm", distanciaMetros)
            gFlag.drawString(texto, tx - 16, ty + 24)
        } finally {
            gFlag.dispose()
        }
    }
}
