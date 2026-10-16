package org.example.ui.simulacion

import java.awt.BasicStroke
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.geom.Path2D
import java.util.Locale

/**
 * Estrategia de renderizado temático (Patrón Strategy) para desacoplar el dibujo gráfico 2D
 * del escenario, lanzador y proyectil en el lienzo de simulación.
 *
 * Cada temática implementa su propia ambientación visual (fondo, suelo, personaje/dispositivo y proyectil)
 * preservando la exactitud de las coordenadas físicas y la escala métrica calculada.
 */
interface ThemeRenderer {

    /** Identificador de la temática asociada. */
    val tematica: TematicaSimulacion

    /**
     * Dibuja el fondo y la atmósfera ambiental del escenario (cielo, estadio, arena, etc.).
     *
     * @param g2d Contexto gráfico 2D con antialiasing activo.
     * @param ancho Ancho actual del componente en píxeles.
     * @param alto Alto actual del componente en píxeles.
     */
    fun dibujarFondo(g2d: Graphics2D, ancho: Int, alto: Int)

    /**
     * Dibuja la superficie del suelo o cancha correspondiente al deporte/escenario.
     *
     * @param g2d Contexto gráfico 2D.
     * @param ancho Ancho actual del componente en píxeles.
     * @param alto Alto actual del componente en píxeles.
     * @param sueloY Posición vertical en píxeles de la línea del suelo (y = 0 m).
     */
    fun dibujarSuelo(g2d: Graphics2D, ancho: Int, alto: Int, sueloY: Double)

    /**
     * Dibuja al personaje o lanzador posicionado en el origen físico (x = 0, y = y0),
     * orientado en el ángulo de disparo especificado.
     *
     * @param g2d Contexto gráfico 2D.
     * @param pivoteX Posición horizontal en píxeles del punto de disparo.
     * @param pivoteY Posición vertical en píxeles del punto de disparo.
     * @param anguloGrados Ángulo de elevación del tiro en grados sexagesimales.
     */
    fun dibujarLanzador(g2d: Graphics2D, pivoteX: Double, pivoteY: Double, anguloGrados: Double)

    /**
     * Dibuja el objeto proyectil en su posición actual durante el vuelo o impacto.
     *
     * @param g2d Contexto gráfico 2D.
     * @param px Posición horizontal instantánea en píxeles.
     * @param py Posición vertical instantánea en píxeles.
     * @param impactoFinalizado `true` si el proyectil ya impactó contra el suelo.
     */
    fun dibujarProyectil(g2d: Graphics2D, px: Double, py: Double, impactoFinalizado: Boolean)

    /**
     * Dibuja el banderín o asta de objetivo en la posición del alcance horizontal teórico (x = d).
     *
     * @param g2d Contexto gráfico 2D.
     * @param targetX Posición horizontal en píxeles del alcance total.
     * @param targetY Posición vertical en píxeles del suelo.
     * @param distanciaMetros Valor numérico en metros del alcance horizontal.
     */
    fun dibujarObjetivoLlegada(g2d: Graphics2D, targetX: Double, targetY: Double, distanciaMetros: Double) {
        val gTarget = g2d.create() as Graphics2D
        try {
            val tx = targetX.toInt()
            val ty = targetY.toInt()

            // Asta de bandera vertical
            gTarget.stroke = BasicStroke(2.0f)
            gTarget.color = Color(51, 65, 85)
            gTarget.drawLine(tx, ty, tx, ty - 26)

            // Banderín triangular
            val flagPath = Path2D.Double().apply {
                moveTo(targetX, (ty - 26).toDouble())
                lineTo(targetX + 14, (ty - 20).toDouble())
                lineTo(targetX, (ty - 14).toDouble())
                closePath()
            }
            gTarget.color = Color(239, 68, 68)
            gTarget.fill(flagPath)

            // Texto indicador del alcance total d
            gTarget.color = Color(30, 41, 59)
            gTarget.font = Font(Font.SANS_SERIF, Font.BOLD, 10)
            val texto = String.format(Locale.US, "d = %.1fm", distanciaMetros)
            gTarget.drawString(texto, tx - 14, ty + 24)
        } finally {
            gTarget.dispose()
        }
    }
}
