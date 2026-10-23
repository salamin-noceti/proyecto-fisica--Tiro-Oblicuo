package org.example.ui.simulacion

import java.awt.BasicStroke
import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.geom.Path2D
import java.awt.geom.RoundRectangle2D
import java.util.Locale
import javax.swing.JPanel

/**
 * Lienzo 2D tematizado para la animación en tiempo real del tiro oblicuo.
 *
 * Cumple con el Principio de Responsabilidad Única (SRP) y el patrón **Strategy**:
 * - Delega la ambientación gráfica (fondo, suelo, personaje/lanzador y proyectil)
 *   en la estrategia activa [ThemeRenderer].
 * - Mantiene la escala métrica automática, las marcas del eje Y (2m, 4m), la cuadrícula,
 *   el asta de bandera en el alcance total ($d$) y la telemetría flotante.
 *
 * @param controlador Controlador de la simulación del que observa el estado y coordenadas.
 * @param conversor Conversor métrico a coordenadas de pantalla en píxeles.
 * @param themeRenderer Estrategia de dibujo temático inicial.
 */
class LienzoSimulacion2D(
    val controlador: ControladorSimulacion,
    val conversor: ConversorCoordenadas = ConversorCoordenadas(),
    var themeRenderer: ThemeRenderer = CanonThemeRenderer()
) : JPanel() {

    init {
        preferredSize = Dimension(740, 420)
        minimumSize = Dimension(400, 280)
        isDoubleBuffered = true

        // Redibujar automáticamente el lienzo en cada actualización del bucle de simulación
        controlador.agregarListener { _, _, _ ->
            repaint()
        }
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val g2d = g.create() as Graphics2D

        try {
            // Activar renderizado de alta calidad y antialiasing para trazos fluidos
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)

            val w = width
            val h = height

            val datos = controlador.datos
            val resultado = controlador.resultadoTeorico
            val xMax = resultado.distanciaHorizontal.coerceAtLeast(10.0)
            val yMax = resultado.alturaMaxima.coerceAtLeast(5.0)

            // 1. Dibujar fondo tematizado mediante Strategy
            themeRenderer.dibujarFondo(g2d, w, h)

            // 2. Dibujar cuadrícula y cotas métricas (con marcas 2m, 4m en eje Y)
            dibujarCuadricula(g2d, w, h, xMax, yMax)

            // 3. Dibujar suelo o cancha deportiva mediante Strategy
            val sueloY = conversor.sueloPixelY(h)
            themeRenderer.dibujarSuelo(g2d, w, h, sueloY)

            // 4. Dibujar plataforma/soporte de elevación si y0 > 0 (delegado al Strategy temático)
            if (datos.alturaInicial > 0.0) {
                val baseAltoY = conversor.metroAPixelY(datos.alturaInicial, w, h, xMax, yMax)
                themeRenderer.dibujarPlataformaInicial(
                    g2d,
                    conversor.origenPixelX(),
                    baseAltoY,
                    sueloY,
                    datos.alturaInicial
                )
            }

            // 5. Dibujar banderín / objetivo en el alcance teórico total (d)
            val targetX = conversor.metroAPixelX(resultado.distanciaHorizontal, w, h, xMax, yMax)
            themeRenderer.dibujarObjetivoLlegada(g2d, targetX, sueloY, resultado.distanciaHorizontal)

            // 6. Dibujar guía teórica punteada de la parábola completa
            dibujarGuiaTeorica(g2d, w, h, xMax, yMax)

            // 7. Dibujar la estela de la trayectoria recorrida hasta el momento
            dibujarEstela(g2d, w, h, xMax, yMax)

            // 8. Dibujar el personaje o lanzador tematizado en el origen físico (x=0, y=y0)
            val lanzadorX = conversor.origenPixelX()
            val lanzadorY = conversor.metroAPixelY(datos.alturaInicial, w, h, xMax, yMax)
            themeRenderer.dibujarLanzador(g2d, lanzadorX, lanzadorY, datos.angulo)

            // 9. Dibujar el proyectil tematizado en su posición actual
            val posActual = controlador.posicionActual
            val proyectilX = conversor.metroAPixelX(posActual.x, w, h, xMax, yMax)
            val proyectilY = conversor.metroAPixelY(posActual.y, w, h, xMax, yMax)
            val finalizado = controlador.estado == EstadoSimulacion.FINALIZADO
            themeRenderer.dibujarProyectil(g2d, proyectilX, proyectilY, finalizado)

            // 10. Dibujar tarjeta HUD con telemetría en tiempo real
            dibujarHUD(g2d, w)

        } finally {
            g2d.dispose()
        }
    }

    private fun dibujarCuadricula(
        g2d: Graphics2D,
        w: Int,
        h: Int,
        xMax: Double,
        yMax: Double
    ) {
        val colorLineas = Color(226, 232, 240, 180)
        val colorTexto = Color(148, 163, 184)
        val colorEjeY = Color(100, 116, 139)
        val strokeDashed = BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, floatArrayOf(4f, 4f), 0f)

        g2d.color = colorLineas
        g2d.stroke = strokeDashed
        g2d.font = Font(Font.SANS_SERIF, Font.PLAIN, 10)

        val sueloY = conversor.sueloPixelY(h)
        val ejeX = conversor.ejeYPixelX()

        // Marcas horizontales (distancia en metros)
        val pasoX = calcularPasoMetricoX(xMax)
        var mX = pasoX
        while (mX <= xMax * 1.05) {
            val px = conversor.metroAPixelX(mX, w, h, xMax, yMax)
            if (px < w - 20) {
                g2d.color = colorLineas
                g2d.drawLine(px.toInt(), conversor.margenSuperior.toInt(), px.toInt(), sueloY.toInt())
                g2d.color = colorTexto
                val etiqueta = "${mX.toInt()}m"
                g2d.drawString(etiqueta, px.toInt() - 10, (sueloY + 16).toInt())
            }
            mX += pasoX
        }

        // Eje Y vertical: línea sólida en el límite del margen reservado
        g2d.color = colorEjeY
        g2d.stroke = BasicStroke(1.4f)
        g2d.drawLine(ejeX.toInt(), conversor.margenSuperior.toInt(), ejeX.toInt(), sueloY.toInt())

        // Marcas verticales del eje Y (con cotas precisas de 2m, 4m para tiros bajos/medianos).
        // Las etiquetas se alinean a la derecha dentro de [0, margenEjeY] para que ningún sprite las tape.
        g2d.stroke = strokeDashed
        val pasoY = calcularPasoMetricoY(yMax)
        var mY = pasoY
        while (mY <= yMax * 1.1) {
            val py = conversor.metroAPixelY(mY, w, h, xMax, yMax)
            if (py > conversor.margenSuperior) {
                g2d.color = colorLineas
                g2d.drawLine(ejeX.toInt(), py.toInt(), (w - conversor.margenDerecho).toInt(), py.toInt())
                g2d.color = colorTexto
                val etiqueta = "${mY.toInt()}m"
                val anchoTexto = g2d.fontMetrics.stringWidth(etiqueta)
                val textoX = (ejeX - anchoTexto - 5).toInt().coerceAtLeast(2)
                g2d.drawString(etiqueta, textoX, (py + 4).toInt())
            }
            mY += pasoY
        }
    }

    private fun calcularPasoMetricoX(maxValor: Double): Double {
        return when {
            maxValor <= 15.0 -> 2.0
            maxValor <= 30.0 -> 5.0
            maxValor <= 75.0 -> 10.0
            maxValor <= 150.0 -> 25.0
            maxValor <= 300.0 -> 50.0
            else -> 100.0
        }
    }

    private fun calcularPasoMetricoY(maxValor: Double): Double {
        return when {
            maxValor <= 20.0 -> 2.0 // Asegura cotas de 2m, 4m, 6m...
            maxValor <= 40.0 -> 5.0
            maxValor <= 100.0 -> 10.0
            maxValor <= 200.0 -> 20.0
            else -> 50.0
        }
    }

    private fun dibujarGuiaTeorica(
        g2d: Graphics2D,
        w: Int,
        h: Int,
        xMax: Double,
        yMax: Double
    ) {
        val puntosGuia = controlador.calculadorFisico.calcularTrayectoria(controlador.datos, 120)
        if (puntosGuia.size < 2) return

        val gGuia = g2d.create() as Graphics2D
        try {
            gGuia.color = Color(147, 197, 253, 150)
            gGuia.stroke = BasicStroke(
                1.5f,
                BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_ROUND,
                10.0f,
                floatArrayOf(5f, 4f),
                0f
            )

            val path = Path2D.Double()
            val primero = puntosGuia.first()
            path.moveTo(
                conversor.metroAPixelX(primero.x, w, h, xMax, yMax),
                conversor.metroAPixelY(primero.y, w, h, xMax, yMax)
            )

            for (i in 1 until puntosGuia.size) {
                val pt = puntosGuia[i]
                path.lineTo(
                    conversor.metroAPixelX(pt.x, w, h, xMax, yMax),
                    conversor.metroAPixelY(pt.y, w, h, xMax, yMax)
                )
            }
            gGuia.draw(path)
        } finally {
            gGuia.dispose()
        }
    }

    private fun dibujarEstela(
        g2d: Graphics2D,
        w: Int,
        h: Int,
        xMax: Double,
        yMax: Double
    ) {
        val puntos = controlador.puntosEstela
        if (puntos.size < 2) return

        val gEstela = g2d.create() as Graphics2D
        try {
            // Trazo continuo azul brillante
            gEstela.color = Color(37, 99, 235)
            gEstela.stroke = BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)

            val path = Path2D.Double()
            val primero = puntos.first()
            path.moveTo(
                conversor.metroAPixelX(primero.x, w, h, xMax, yMax),
                conversor.metroAPixelY(primero.y, w, h, xMax, yMax)
            )

            for (i in 1 until puntos.size) {
                val pt = puntos[i]
                path.lineTo(
                    conversor.metroAPixelX(pt.x, w, h, xMax, yMax),
                    conversor.metroAPixelY(pt.y, w, h, xMax, yMax)
                )
            }
            gEstela.draw(path)
        } finally {
            gEstela.dispose()
        }
    }

    private fun dibujarHUD(g2d: Graphics2D, anchoCanvas: Int) {
        val anchoCard = 246
        val altoCard = 104
        val xCard = anchoCanvas - anchoCard - 18
        val yCard = 14

        val cardRect = RoundRectangle2D.Double(
            xCard.toDouble(),
            yCard.toDouble(),
            anchoCard.toDouble(),
            altoCard.toDouble(),
            12.0,
            12.0
        )

        // Fondo oscuro semi-transparente
        g2d.color = Color(15, 23, 42, 215)
        g2d.fill(cardRect)

        g2d.color = Color(51, 65, 85)
        g2d.stroke = BasicStroke(1.2f)
        g2d.draw(cardRect)

        // Título telemetría con insignia de temática
        val pos = controlador.posicionActual
        val tActual = controlador.tiempoActual
        val tVuelo = controlador.resultadoTeorico.tiempoVuelo

        g2d.color = Color(248, 250, 252)
        g2d.font = Font(Font.SANS_SERIF, Font.BOLD, 12)
        val tituloHud = "⏱ ${themeRenderer.tematica.icono} ${themeRenderer.tematica.nombreVisible}"
        g2d.drawString(tituloHud, xCard + 12, yCard + 20)

        // Estado visual
        val estadoBadge = when (controlador.estado) {
            EstadoSimulacion.DETENIDO -> "Listo 🎯"
            EstadoSimulacion.EN_EJECUCION -> "En vuelo 🚀"
            EstadoSimulacion.PAUSADO -> "Pausado ⏸"
            EstadoSimulacion.FINALIZADO -> "Aterrizado 🏁"
        }
        val colorBadge = when (controlador.estado) {
            EstadoSimulacion.DETENIDO -> Color(148, 163, 184)
            EstadoSimulacion.EN_EJECUCION -> Color(56, 189, 248)
            EstadoSimulacion.PAUSADO -> Color(251, 191, 36)
            EstadoSimulacion.FINALIZADO -> Color(74, 222, 128)
        }
        g2d.color = colorBadge
        g2d.font = Font(Font.SANS_SERIF, Font.BOLD, 11)
        g2d.drawString(estadoBadge, xCard + anchoCard - 12 - g2d.fontMetrics.stringWidth(estadoBadge), yCard + 20)

        // Valores numéricos
        g2d.font = Font(Font.MONOSPACED, Font.PLAIN, 11)
        g2d.color = Color(203, 213, 225)

        val strTiempo = String.format(Locale.US, "Tiempo : %5.2f s / %5.2f s", tActual, tVuelo)
        val strPosicion = String.format(Locale.US, "Posición: X=%5.1fm | Y=%5.1fm", pos.x, pos.y)
        val strVelocidad = String.format(Locale.US, "Veloc.  : Vx=%4.1f | Vy=%4.1f m/s", pos.velocidadX, pos.velocidadY)

        g2d.drawString(strTiempo, xCard + 12, yCard + 44)
        g2d.drawString(strPosicion, xCard + 12, yCard + 64)
        g2d.drawString(strVelocidad, xCard + 12, yCard + 84)
    }
}
