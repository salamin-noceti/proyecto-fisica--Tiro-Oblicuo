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
 * Dibuja un estadio cerrado con piso de parqué de madera barnizado,
 * la silueta de un jugador en suspensión para el lanzamiento y una pelota de básquet reglamentaria.
 */
class BasquetThemeRenderer : ThemeRenderer {

    override val tematica: TematicaSimulacion = TematicaSimulacion.BASQUET

    override fun dibujarFondo(g2d: Graphics2D, ancho: Int, alto: Int) {
        // Ambiente de estadio techado con luces cálidas
        val fondoArena = GradientPaint(
            0f, 0f, Color(254, 243, 199),
            0f, alto.toFloat(), Color(241, 245, 249)
        )
        g2d.paint = fondoArena
        g2d.fillRect(0, 0, ancho, alto)

        // Estructura de vigas del techo del estadio
        g2d.color = Color(226, 232, 240, 160)
        g2d.stroke = BasicStroke(1.2f)
        g2d.drawLine(0, 30, ancho, 30)
        g2d.drawLine(0, 55, ancho, 55)
        for (vx in 0 until ancho step 90) {
            g2d.drawLine(vx, 0, vx + 45, 55)
        }
    }

    override fun dibujarSuelo(g2d: Graphics2D, ancho: Int, alto: Int, sueloY: Double) {
        val sueloInt = sueloY.toInt()
        val altoPiso = alto - sueloInt

        // Piso de parqué de madera lustrado (tablones dorados)
        val degradadoMadera = GradientPaint(
            0f, sueloInt.toFloat(), Color(234, 179, 108),
            0f, alto.toFloat(), Color(180, 83, 9)
        )
        g2d.paint = degradadoMadera
        g2d.fillRect(0, sueloInt, ancho, altoPiso)

        // Líneas de tablones de madera
        g2d.color = Color(202, 138, 4, 90)
        g2d.stroke = BasicStroke(1.0f)
        for (tablonY in sueloInt + 6 until alto step 10) {
            g2d.drawLine(0, tablonY, ancho, tablonY)
        }

        // Línea lateral blanca de la cancha
        g2d.color = Color.WHITE
        g2d.stroke = BasicStroke(2.5f)
        g2d.drawLine(0, sueloInt, ancho, sueloInt)

        // Línea de triples / área de la llave
        g2d.color = Color(255, 255, 255, 180)
        g2d.stroke = BasicStroke(1.8f)
        g2d.drawArc(-30, sueloInt - 20, 110, 50, -40, 80)
    }

    override fun dibujarLanzador(
        g2d: Graphics2D,
        pivoteX: Double,
        pivoteY: Double,
        anguloGrados: Double
    ) {
        val gPlayer = g2d.create() as Graphics2D
        try {
            val xBase = pivoteX - 14.0
            val ySuelo = pivoteY

            // Jugador de básquet en suspensión ejecutando el tiro en suspensión
            // Piernas en elevación
            gPlayer.color = Color(220, 38, 38) // Medias / zapatillas rojas
            gPlayer.stroke = BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
            gPlayer.drawLine((xBase - 4).toInt(), (ySuelo - 16).toInt(), (xBase - 6).toInt(), (ySuelo - 6).toInt())
            gPlayer.drawLine((xBase).toInt(), (ySuelo - 16).toInt(), (xBase + 3).toInt(), (ySuelo - 8).toInt())

            // Pantalones cortos holgados de básquet
            gPlayer.color = Color(30, 41, 59)
            gPlayer.fill(RoundRectangle2D.Double(xBase - 8.0, ySuelo - 26.0, 13.0, 11.0, 3.0, 3.0))

            // Musculosa deportiva
            gPlayer.color = Color(239, 68, 68) // Camiseta roja
            gPlayer.fill(RoundRectangle2D.Double(xBase - 8.0, ySuelo - 42.0, 13.0, 17.0, 3.0, 3.0))

            // Brazos extendidos hacia arriba en el ángulo de lanzamiento
            val radAngle = Math.toRadians(anguloGrados.coerceIn(20.0, 80.0))
            val hombroX = xBase - 1.0
            val hombroY = ySuelo - 38.0
            val manoX = hombroX + 16.0 * Math.cos(radAngle)
            val manoY = hombroY - 18.0 * Math.sin(radAngle)

            gPlayer.color = Color(254, 215, 170)
            gPlayer.stroke = BasicStroke(2.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
            gPlayer.drawLine(hombroX.toInt(), hombroY.toInt(), manoX.toInt(), manoY.toInt())

            // Cabeza
            gPlayer.fill(Ellipse2D.Double(xBase - 5.0, ySuelo - 51.0, 8.0, 9.0))
            gPlayer.color = Color(30, 41, 59)
            gPlayer.fill(Ellipse2D.Double(xBase - 5.5, ySuelo - 52.0, 8.5, 5.0))

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
        val radio = 8.0
        val diametro = radio * 2.0

        if (impactoFinalizado) {
            // Marca de pique en la madera
            g2d.color = Color(180, 83, 9, 180)
            g2d.fill(Ellipse2D.Double(px - 10.0, py - 2.0, 20.0, 4.0))
        }

        // Pelota de básquetbol naranja con sombreado esférico
        val centro = Point2D.Double(px - 2.0, py - 2.0)
        val gradienteBasquet = RadialGradientPaint(
            centro,
            9.0f,
            floatArrayOf(0.0f, 0.7f, 1.0f),
            arrayOf(Color(251, 146, 60), Color(234, 88, 12), Color(154, 52, 18))
        )

        val pelota = Ellipse2D.Double(px - radio, py - radio, diametro, diametro)
        g2d.paint = gradienteBasquet
        g2d.fill(pelota)

        // Líneas negras características de la pelota de básquet
        g2d.color = Color(30, 41, 59)
        g2d.stroke = BasicStroke(1.0f)
        g2d.draw(pelota)

        // Costilla horizontal y vertical
        g2d.drawLine((px - radio).toInt(), py.toInt(), (px + radio).toInt(), py.toInt())
        g2d.drawLine(px.toInt(), (py - radio).toInt(), px.toInt(), (py + radio).toInt())
        // Costillas curvadas laterales
        g2d.drawArc((px - 5).toInt(), (py - radio + 1).toInt(), 10, (diametro - 2).toInt(), 50, 80)
        g2d.drawArc((px - 5).toInt(), (py - radio + 1).toInt(), 10, (diametro - 2).toInt(), -130, 80)
    }

    override fun dibujarObjetivoLlegada(
        g2d: Graphics2D,
        targetX: Double,
        targetY: Double,
        distanciaMetros: Double
    ) {
        val tx = targetX.toInt()
        val ty = targetY.toInt()

        val gAro = g2d.create() as Graphics2D
        try {
            // Poste del tablero de básquet
            gAro.color = Color(71, 85, 105)
            gAro.stroke = BasicStroke(3.0f)
            gAro.drawLine(tx + 12, ty, tx + 12, ty - 38)

            // Tablero transparente / blanco con borde rojo
            gAro.color = Color.WHITE
            gAro.fillRect(tx + 8, ty - 42, 6, 26)
            gAro.color = Color(239, 68, 68)
            gAro.stroke = BasicStroke(1.2f)
            gAro.drawRect(tx + 8, ty - 42, 6, 26)

            // Aro de básquetbol naranja y red blanca
            gAro.color = Color(249, 115, 22)
            gAro.stroke = BasicStroke(2.0f)
            gAro.drawLine(tx - 4, ty - 24, tx + 8, ty - 24)

            // Red blanca
            gAro.color = Color(255, 255, 255, 200)
            gAro.stroke = BasicStroke(1.0f)
            gAro.drawLine(tx - 3, ty - 24, tx, ty - 16)
            gAro.drawLine(tx + 7, ty - 24, tx + 4, ty - 16)
            gAro.drawLine(tx, ty - 16, tx + 4, ty - 16)

            // Etiqueta de distancia del aro
            gAro.color = Color(30, 41, 59)
            gAro.font = Font(Font.SANS_SERIF, Font.BOLD, 10)
            val texto = String.format(Locale.US, "🏀 d = %.1fm", distanciaMetros)
            gAro.drawString(texto, tx - 18, ty + 24)
        } finally {
            gAro.dispose()
        }
    }
}
