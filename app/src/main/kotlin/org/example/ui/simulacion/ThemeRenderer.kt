package org.example.ui.simulacion

import java.awt.BasicStroke
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.geom.Path2D
import java.awt.geom.RoundRectangle2D
import java.util.Locale

/**
 * Estrategia de renderizado temático (Patrón Strategy) para desacoplar el dibujo gráfico 2D
 * del escenario, lanzador y proyectil en el lienzo de simulación.
 */
interface ThemeRenderer {

    val tematica: TematicaSimulacion

    fun dibujarPlataformaInicial(g2d: Graphics2D, pivoteX: Double, baseY: Double, sueloY: Double) {
        if (sueloY <= baseY) return
        val gPlat = g2d.create() as Graphics2D
        try {
            val platRect = RoundRectangle2D.Double(pivoteX - 20.0, baseY, 34.0, sueloY - baseY, 6.0, 6.0)
            gPlat.color = Color(148, 163, 184)
            gPlat.fill(platRect)
            gPlat.color = Color(71, 85, 105)
            gPlat.stroke = BasicStroke(1.5f)
            gPlat.draw(platRect)
        } finally {
            gPlat.dispose()
        }
    }

    fun dibujarObjetivoLlegada(g2d: Graphics2D, targetX: Double, targetY: Double, distanciaMetros: Double) {
        val gTarget = g2d.create() as Graphics2D
        try {
            val tx = targetX.toInt()
            val ty = targetY.toInt()
            gTarget.stroke = BasicStroke(2.0f)
            gTarget.color = Color(51, 65, 85)
            gTarget.drawLine(tx, ty, tx, ty - 26)
            val flagPath = Path2D.Double().apply { moveTo(targetX, (ty - 26).toDouble()); lineTo(targetX + 14, (ty - 20).toDouble()); lineTo(targetX, (ty - 14).toDouble()); closePath() }
            gTarget.color = Color(239, 68, 68)
            gTarget.fill(flagPath)
            gTarget.color = Color(30, 41, 59)
            gTarget.font = Font(Font.SANS_SERIF, Font.BOLD, 10)
            gTarget.drawString(String.format(Locale.US, "d = %.1fm", distanciaMetros), tx - 14, ty + 24)
        } finally {
            gTarget.dispose()
        }
    }

    fun dibujarFondo(g2d: Graphics2D, ancho: Int, alto: Int)

    fun dibujarSuelo(g2d: Graphics2D, ancho: Int, alto: Int, sueloY: Double)

    fun dibujarLanzador(g2d: Graphics2D, pivoteX: Double, pivoteY: Double, anguloGrados: Double)

    fun dibujarProyectil(g2d: Graphics2D, px: Double, py: Double, impactoFinalizado: Boolean)
}
