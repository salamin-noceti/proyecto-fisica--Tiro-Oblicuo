package org.example.ui.simulacion

import java.awt.BasicStroke
import java.awt.Color
import java.awt.Font
import java.awt.GradientPaint
import java.awt.Graphics2D
import java.awt.geom.Ellipse2D
import java.awt.geom.Path2D
import java.awt.geom.RoundRectangle2D
import java.util.Locale

/**
 * Estrategia de renderizado visual para la temática de Atletismo (Jabalina / Lanzamiento).
 * Dibuja una pista sintética de atletismo (color terracota con andariveles blancos),
 * un atleta en postura atlética de lanzamiento y una jabalina aerodinámica orientada al tiro.
 */
class AtletismoThemeRenderer : ThemeRenderer {

    override val tematica: TematicaSimulacion = TematicaSimulacion.ATLETISMO

    override fun dibujarFondo(g2d: Graphics2D, ancho: Int, alto: Int) {
        // Cielo diurno despejado de estadio olímpico
        val cielo = GradientPaint(
            0f, 0f, Color(191, 219, 254),
            0f, alto.toFloat(), Color(241, 245, 249)
        )
        g2d.paint = cielo
        g2d.fillRect(0, 0, ancho, alto)

        // Gradas o tribunas lejanas del estadio
        g2d.color = Color(203, 213, 225, 140)
        g2d.stroke = BasicStroke(1.2f)
        for (ty in (alto * 0.40).toInt()..(alto * 0.48).toInt() step 6) {
            g2d.drawLine(0, ty, ancho, ty)
        }
    }

    override fun dibujarSuelo(g2d: Graphics2D, ancho: Int, alto: Int, sueloY: Double) {
        val sueloInt = sueloY.toInt()
        val altoPista = alto - sueloInt

        // Pista sintética de tartán rojo ladrillo / terracota
        val pistaGrad = GradientPaint(
            0f, sueloInt.toFloat(), Color(194, 65, 12),
            0f, alto.toFloat(), Color(153, 27, 27)
        )
        g2d.paint = pistaGrad
        g2d.fillRect(0, sueloInt, ancho, altoPista)

        // Líneas blancas continuas de los andariveles (carriles de atletismo)
        g2d.color = Color(255, 255, 255, 220)
        g2d.stroke = BasicStroke(2.0f)
        g2d.drawLine(0, sueloInt, ancho, sueloInt)

        // Andariveles interiores
        g2d.color = Color(255, 255, 255, 180)
        g2d.stroke = BasicStroke(1.5f)
        for (carrilY in sueloInt + 12 until alto step 14) {
            g2d.drawLine(0, carrilY, ancho, carrilY)
        }

        // Línea de salida / límite de tiro
        g2d.color = Color.WHITE
        g2d.stroke = BasicStroke(3.0f)
        g2d.drawLine(20, sueloInt, 20, alto)
    }

    override fun dibujarLanzador(
        g2d: Graphics2D,
        pivoteX: Double,
        pivoteY: Double,
        anguloGrados: Double
    ) {
        val gAtleta = g2d.create() as Graphics2D
        try {
            val xBase = pivoteX - 16.0
            val ySuelo = pivoteY

            // Piernas en postura atlética de despegue y lanzamiento
            gAtleta.color = Color(30, 41, 59)
            gAtleta.stroke = BasicStroke(3.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
            // Pierna delantera firme
            gAtleta.drawLine((xBase + 2).toInt(), (ySuelo - 18).toInt(), (xBase + 6).toInt(), ySuelo.toInt())
            // Pierna trasera flexionada
            gAtleta.drawLine((xBase - 6).toInt(), (ySuelo - 18).toInt(), (xBase - 12).toInt(), ySuelo.toInt())

            // Zapatillas de clavos
            gAtleta.color = Color(239, 68, 68)
            gAtleta.fillRect((xBase + 4).toInt(), (ySuelo - 3).toInt(), 8, 3)
            gAtleta.fillRect((xBase - 14).toInt(), (ySuelo - 3).toInt(), 8, 3)

            // Pantalón corto de atletismo
            gAtleta.color = Color(30, 41, 59)
            gAtleta.fill(RoundRectangle2D.Double(xBase - 8.0, ySuelo - 26.0, 14.0, 9.0, 2.0, 2.0))

            // Musculosa / Singlet de atletismo
            gAtleta.color = Color(245, 158, 11) // Dorado / amarillo
            gAtleta.fill(RoundRectangle2D.Double(xBase - 8.0, ySuelo - 40.0, 13.0, 15.0, 3.0, 3.0))

            // Brazo de lanzamiento extendido en el ángulo del tiro
            val radAngle = Math.toRadians(anguloGrados.coerceIn(15.0, 75.0))
            val hombroX = xBase + 2.0
            val hombroY = ySuelo - 36.0
            val manoX = hombroX + 16.0 * Math.cos(radAngle)
            val manoY = hombroY - 16.0 * Math.sin(radAngle)

            // Brazo libre de equilibrio hacia adelante
            gAtleta.color = Color(254, 215, 170)
            gAtleta.stroke = BasicStroke(2.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
            gAtleta.drawLine((xBase - 6).toInt(), (ySuelo - 34).toInt(), (xBase - 14).toInt(), (ySuelo - 28).toInt())

            // Brazo de tiro
            gAtleta.drawLine(hombroX.toInt(), hombroY.toInt(), manoX.toInt(), manoY.toInt())

            // Cabeza
            gAtleta.fill(Ellipse2D.Double(xBase - 4.5, ySuelo - 50.0, 8.0, 9.0))
            gAtleta.color = Color(30, 41, 59)
            gAtleta.fill(Ellipse2D.Double(xBase - 5.0, ySuelo - 51.0, 8.5, 4.5))

        } finally {
            gAtleta.dispose()
        }
    }

    override fun dibujarProyectil(
        g2d: Graphics2D,
        px: Double,
        py: Double,
        impactoFinalizado: Boolean
    ) {
        val gProj = g2d.create() as Graphics2D
        try {
            if (impactoFinalizado) {
                // Jabalina clavada en el tartán / césped a 45 grados
                gProj.color = Color(153, 27, 27)
                gProj.fill(Ellipse2D.Double(px - 6.0, py - 2.0, 12.0, 4.0))

                // Astil inclinado clavado
                gProj.color = Color(203, 213, 225)
                gProj.stroke = BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
                gProj.drawLine(px.toInt(), py.toInt(), (px - 14).toInt(), (py - 12).toInt())
                return
            }

            // Proyectil en vuelo: Jabalina aerodinámica esbelta
            gProj.stroke = BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
            // Cuerpo metálico plateado
            gProj.color = Color(226, 232, 240)
            gProj.drawLine((px - 10).toInt(), (py + 4).toInt(), (px + 10).toInt(), (py - 4).toInt())

            // Empuñadura central roja
            gProj.color = Color(239, 68, 68)
            gProj.stroke = BasicStroke(2.6f)
            gProj.drawLine((px - 2).toInt(), (py + 1).toInt(), (px + 2).toInt(), (py - 1).toInt())

            // Punta afilada
            gProj.color = Color(30, 41, 59)
            gProj.fill(Ellipse2D.Double(px + 8.0, py - 6.0, 3.5, 3.5))

        } finally {
            gProj.dispose()
        }
    }

    override fun dibujarObjetivoLlegada(
        g2d: Graphics2D,
        targetX: Double,
        targetY: Double,
        distanciaMetros: Double
    ) {
        val tx = targetX.toInt()
        val ty = targetY.toInt()

        val gPost = g2d.create() as Graphics2D
        try {
            // Asta de récord olímpico
            gPost.color = Color.WHITE
            gPost.stroke = BasicStroke(2.2f)
            gPost.drawLine(tx, ty, tx, ty - 30)

            // Banderín olímpico dorado
            val flagPath = Path2D.Double().apply {
                moveTo(targetX, (ty - 30).toDouble())
                lineTo(targetX + 18, (ty - 22).toDouble())
                lineTo(targetX, (ty - 14).toDouble())
                closePath()
            }
            gPost.color = Color(245, 158, 11)
            gPost.fill(flagPath)

            // Texto indicador de marca
            gPost.color = Color(30, 41, 59)
            gPost.font = Font(Font.SANS_SERIF, Font.BOLD, 10)
            val texto = String.format(Locale.US, "🏃 d = %.1fm", distanciaMetros)
            gPost.drawString(texto, tx - 16, ty + 24)
        } finally {
            gPost.dispose()
        }
    }
}
