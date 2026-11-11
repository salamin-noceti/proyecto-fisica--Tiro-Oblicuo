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
 * Dibuja una cancha de fútbol profesional con césped cortado en franjas,
 * la silueta estilizada de un futbolista en posición de remate y una pelota de fútbol con gajos.
 */
class FutbolThemeRenderer : ThemeRenderer {

    override val tematica: TematicaSimulacion = TematicaSimulacion.FUTBOL

    override fun dibujarFondo(g2d: Graphics2D, ancho: Int, alto: Int) {
        // Cielo diurno con atmósfera de estadio
        val cielo = GradientPaint(
            0f, 0f, Color(186, 230, 253),
            0f, alto.toFloat(), Color(241, 245, 249)
        )
        g2d.paint = cielo
        g2d.fillRect(0, 0, ancho, alto)

        // Torres de iluminación lejanas del estadio (siluetas tenues)
        g2d.color = Color(203, 213, 225, 120)
        g2d.stroke = BasicStroke(1.5f)
        // Torre izquierda
        g2d.drawLine(40, (alto * 0.45).toInt(), 40, (alto * 0.15).toInt())
        g2d.drawRect(30, (alto * 0.12).toInt(), 20, 10)
        // Torre derecha
        g2d.drawLine(ancho - 50, (alto * 0.45).toInt(), ancho - 50, (alto * 0.15).toInt())
        g2d.drawRect(ancho - 60, (alto * 0.12).toInt(), 20, 10)
    }

    override fun dibujarSuelo(g2d: Graphics2D, ancho: Int, alto: Int, sueloY: Double) {
        val sueloInt = sueloY.toInt()
        val altoCancha = alto - sueloInt

        // Cancha de fútbol con césped profesional cortado en franjas alternadas
        val anchoFranja = 44
        var x = 0
        var franjaClara = true

        while (x < ancho) {
            val colorVerde = if (franjaClara) Color(34, 197, 94) else Color(22, 163, 74)
            g2d.color = colorVerde
            g2d.fillRect(x, sueloInt, anchoFranja.coerceAtMost(ancho - x), altoCancha)
            x += anchoFranja
            franjaClara = !franjaClara
        }

        // Línea de banda / cal blanca
        g2d.color = Color.WHITE
        g2d.stroke = BasicStroke(2.5f)
        g2d.drawLine(0, sueloInt, ancho, sueloInt)

        // Punto de penal / área en el suelo
        g2d.fill(Ellipse2D.Double(20.0, sueloInt - 2.0, 5.0, 4.0))
    }

    override fun dibujarLanzador(
        g2d: Graphics2D,
        pivoteX: Double,
        pivoteY: Double,
        anguloGrados: Double
    ) {
        val gPlayer = g2d.create() as Graphics2D
        try {
            // Posicionar al futbolista justo a la izquierda del punto de impacto de la pelota
            val xBase = pivoteX - 16.0
            val ySuelo = pivoteY

            // 1. Pierna de apoyo (firme sobre el suelo)
            gPlayer.color = Color(30, 41, 59) // Medias oscuras
            gPlayer.stroke = BasicStroke(3.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
            gPlayer.drawLine((xBase - 4).toInt(), (ySuelo - 18).toInt(), (xBase - 6).toInt(), ySuelo.toInt())
            // Botín de apoyo
            gPlayer.color = Color(15, 23, 42)
            gPlayer.fillRect((xBase - 10).toInt(), (ySuelo - 3).toInt(), 8, 3)

            // 2. Pierna de pateo orientada dinámicamente en el ángulo del remate
            val radKicking = Math.toRadians(anguloGrados.coerceIn(15.0, 75.0))
            val caderaX = xBase - 2.0
            val caderaY = ySuelo - 20.0
            val pieKickingX = caderaX + 16.0 * Math.cos(radKicking)
            val pieKickingY = caderaY - 14.0 * Math.sin(radKicking)

            gPlayer.color = Color(37, 99, 235) // Medias / pierna
            gPlayer.drawLine(caderaX.toInt(), caderaY.toInt(), pieKickingX.toInt(), pieKickingY.toInt())

            // Botín de pateo
            gPlayer.color = Color(245, 158, 11) // Botín flúo naranja/amarillo
            gPlayer.fill(Ellipse2D.Double(pieKickingX - 2.0, pieKickingY - 3.0, 6.0, 4.0))

            // 3. Pantalón corto
            gPlayer.color = Color(30, 41, 59)
            gPlayer.fill(RoundRectangle2D.Double(xBase - 8.0, ySuelo - 26.0, 12.0, 9.0, 2.0, 2.0))

            // 4. Torso con camiseta de fútbol deportiva
            gPlayer.color = Color(14, 165, 233) // Celeste deportivo
            gPlayer.fill(RoundRectangle2D.Double(xBase - 9.0, ySuelo - 40.0, 13.0, 15.0, 4.0, 4.0))

            // Brazos en posición atlética de balance
            gPlayer.color = Color(248, 113, 113) // Brazos
            gPlayer.stroke = BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
            gPlayer.drawLine((xBase - 8).toInt(), (ySuelo - 36).toInt(), (xBase - 15).toInt(), (ySuelo - 30).toInt())
            gPlayer.drawLine((xBase + 2).toInt(), (ySuelo - 36).toInt(), (xBase + 8).toInt(), (ySuelo - 32).toInt())

            // 5. Cabeza del futbolista
            gPlayer.color = Color(254, 215, 170) // Tono de piel
            gPlayer.fill(Ellipse2D.Double(xBase - 6.0, ySuelo - 48.0, 8.0, 9.0))
            // Cabello
            gPlayer.color = Color(69, 26, 3)
            gPlayer.fill(Ellipse2D.Double(xBase - 6.5, ySuelo - 49.0, 8.5, 5.0))

        } finally {
            gPlayer.dispose()
        }
    }

    override fun dibujarProyectil(
        g2d: Graphics2D,
        px: Double,
        py: Double,
        impactoFinalizado: Boolean
    ) {
        val radio = 7.5
        val diametro = radio * 2.0

        if (impactoFinalizado) {
            // Salpicadura de césped por el impacto del balón
            g2d.color = Color(34, 197, 94, 210)
            g2d.fill(Ellipse2D.Double(px - 10.0, py - 2.0, 20.0, 5.0))
        }

        // Balón de fútbol blanco esférico con sombreado 3D
        val centro = Point2D.Double(px - 2.0, py - 2.0)
        val gradientePelota = RadialGradientPaint(
            centro,
            8.0f,
            floatArrayOf(0.0f, 0.7f, 1.0f),
            arrayOf(Color.WHITE, Color(241, 245, 249), Color(148, 163, 184))
        )

        val pelota = Ellipse2D.Double(px - radio, py - radio, diametro, diametro)
        g2d.paint = gradientePelota
        g2d.fill(pelota)

        // Contorno exterior
        g2d.color = Color(51, 65, 85)
        g2d.stroke = BasicStroke(1.2f)
        g2d.draw(pelota)

        // Gajos negros tradicionales del balón de fútbol
        g2d.color = Color(30, 41, 59)
        // Gajo pentagonal central
        val gajoCentro = Ellipse2D.Double(px - 2.0, py - 2.0, 4.0, 4.0)
        g2d.fill(gajoCentro)

        // Costuras / líneas que unen los gajos
        g2d.stroke = BasicStroke(0.9f)
        g2d.drawLine(px.toInt(), py.toInt(), (px - 5).toInt(), (py - 4).toInt())
        g2d.drawLine(px.toInt(), py.toInt(), (px + 5).toInt(), (py - 4).toInt())
        g2d.drawLine(px.toInt(), py.toInt(), px.toInt(), (py + 6).toInt())
        g2d.drawLine(px.toInt(), py.toInt(), (px - 6).toInt(), (py + 2).toInt())
        g2d.drawLine(px.toInt(), py.toInt(), (px + 6).toInt(), (py + 2).toInt())
    }

    override fun dibujarObjetivoLlegada(
        g2d: Graphics2D,
        targetX: Double,
        targetY: Double,
        distanciaMetros: Double
    ) {
        val tx = targetX.toInt()
        val ty = targetY.toInt()

        // Banderín de córner / arco de fútbol en la línea de llegada
        val gPost = g2d.create() as Graphics2D
        try {
            // Mástil blanco
            gPost.color = Color.WHITE
            gPost.stroke = BasicStroke(2.0f)
            gPost.drawLine(tx, ty, tx, ty - 28)

            // Banderín triangular amarillo brillante de córner
            val flagPath = Path2D.Double().apply {
                moveTo(targetX, (ty - 28).toDouble())
                lineTo(targetX + 16, (ty - 21).toDouble())
                lineTo(targetX, (ty - 14).toDouble())
                closePath()
            }
            gPost.color = Color(245, 158, 11)
            gPost.fill(flagPath)

            // Texto indicador de distancia de llegada
            gPost.color = Color(30, 41, 59)
            gPost.font = Font(Font.SANS_SERIF, Font.BOLD, 10)
            val texto = String.format(Locale.US, "⚽ d = %.1fm", distanciaMetros)
            gPost.drawString(texto, tx - 16, ty + 24)
        } finally {
            gPost.dispose()
        }
    }
}
