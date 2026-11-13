package org.example.ui.simulacion

import java.awt.BasicStroke
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import javax.swing.JPanel

/**
 * Lienzo 2D tematizado para la animación en tiempo real del tiro oblicuo.
 */
class LienzoSimulacion2D(
    private val controlador: ControladorSimulacion,
    conversorInicial: ConversorCoordenadas = ConversorCoordenadas.nuevo,
    themeRenderer: ThemeRenderer = CanonThemeRenderer.instancia
) : JPanel() {

    var conversor: ConversorCoordenadas = conversorInicial
        private set

    var themeRenderer: ThemeRenderer = themeRenderer
        private set

    var panelControl: PanelControlSimulacion = PanelControlSimulacion(controlador = controlador)
        private set

    private val capas = RenderizadorCapasLienzo2D(conversor)

    init {
        preferredSize = Dimension(740, 420)
        minimumSize = Dimension(400, 280)
        isDoubleBuffered = true
        controlador.agregarListener { _, _, _ -> repaint() }
    }

    fun actualizarThemeRenderer(nuevoRenderer: ThemeRenderer) {
        this.themeRenderer = nuevoRenderer
        repaint()
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val g2d = g.create() as Graphics2D
        try {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
            val maximos = doubleArrayOf(controlador.resultadoTeorico.distanciaHorizontal.coerceAtLeast(10.0), controlador.resultadoTeorico.alturaMaxima.coerceAtLeast(5.0))
            dibujarEscenario(g2d, width, height, maximos)
            dibujarTrayectoriasYElementos(g2d, width, height, maximos)
            capas.dibujarHUD(g2d, width, controlador, themeRenderer.tematica)
        } finally {
            g2d.dispose()
        }
    }

    private fun dibujarEscenario(g2d: Graphics2D, w: Int, h: Int, maximos: DoubleArray) {
        themeRenderer.dibujarFondo(g2d, w, h)
        capas.dibujarCuadricula(g2d, w, h, maximos)
        val sueloY = conversor.sueloPixelY(h)
        themeRenderer.dibujarSuelo(g2d, w, h, sueloY)
        val datos = controlador.datos
        if (datos.alturaInicial > 0.0) {
            val baseAltoY = conversor.metroAPixelY(datos.alturaInicial, w, h, maximos)
            themeRenderer.dibujarPlataformaInicial(g2d, conversor.origenPixelX(), baseAltoY, sueloY)
        }
        val alcance = controlador.resultadoTeorico.distanciaHorizontal
        if (alcance > 0.0) {
            val targetX = conversor.metroAPixelX(alcance, w, h, maximos)
            themeRenderer.dibujarObjetivoLlegada(g2d, targetX, sueloY, alcance)
        }
    }

    private fun dibujarTrayectoriasYElementos(g2d: Graphics2D, w: Int, h: Int, maximos: DoubleArray) {
        val gCurvas = g2d.create() as Graphics2D
        try {
            val dims = intArrayOf(w, h)
            gCurvas.color = Color(147, 197, 253, 150)
            gCurvas.stroke = BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10.0f, floatArrayOf(5f, 4f), 0f)
            capas.dibujarCurvaPuntos(gCurvas, controlador.calculadorFisico.calcularTrayectoria(controlador.datos, 120), dims, maximos)
            gCurvas.color = Color(37, 99, 235)
            gCurvas.stroke = BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
            capas.dibujarCurvaPuntos(gCurvas, controlador.puntosEstela, dims, maximos)
        } finally {
            gCurvas.dispose()
        }
        val datos = controlador.datos
        themeRenderer.dibujarLanzador(g2d, conversor.origenPixelX(), conversor.metroAPixelY(datos.alturaInicial, w, h, maximos), datos.angulo)
        val pos = controlador.posicionActual
        val finalizado = controlador.estado == EstadoSimulacion.FINALIZADO
        themeRenderer.dibujarProyectil(g2d, conversor.metroAPixelX(pos.x, w, h, maximos), conversor.metroAPixelY(pos.y, w, h, maximos), finalizado)
    }
}
