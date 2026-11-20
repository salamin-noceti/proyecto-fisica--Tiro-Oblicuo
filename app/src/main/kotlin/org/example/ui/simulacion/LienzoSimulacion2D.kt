package org.example.ui.simulacion

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
    private val conversor: ConversorCoordenadas = ConversorCoordenadas.nuevo,
    themeRendererInicial: ThemeRenderer = ThemeRenderer.porDefecto
) : JPanel() {

    private var rendererActivo: ThemeRenderer = themeRendererInicial
    private val capas: RenderizadorCapasLienzo2D = RenderizadorCapasLienzo2D(conversor)

    init {
        preferredSize = Dimension(740, 420)
        minimumSize = Dimension(400, 280)
        isDoubleBuffered = true
        controlador.agregarListener { _, _, _ -> repaint() }
    }

    fun obtenerConversor(): ConversorCoordenadas {
        return conversor
    }

    fun obtenerThemeRenderer(): ThemeRenderer {
        return if (conversor.origenPixelX() >= 0.0) rendererActivo else rendererActivo
    }

    fun actualizarThemeRenderer(nuevoRenderer: ThemeRenderer) {
        this.rendererActivo = nuevoRenderer
        repaint()
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val g2d = g.create() as Graphics2D
        try {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
            val maximos = controlador.obtenerDominioMaximo()
            val datosRender = controlador.obtenerDatosRenderizado()
            dibujarEscenario(g2d, intArrayOf(width, height), maximos, datosRender)
            dibujarTrayectoriasYElementos(g2d, intArrayOf(width, height), maximos, datosRender)
            capas.dibujarHUD(g2d, width, controlador, rendererActivo.tematica)
        } finally {
            g2d.dispose()
        }
    }

    private fun dibujarEscenario(g2d: Graphics2D, dims: IntArray, maximos: DoubleArray, datosRender: DoubleArray) {
        rendererActivo.dibujarFondo(g2d, dims[0], dims[1])
        capas.dibujarCuadricula(g2d, dims[0], dims[1], maximos)
        val sueloY = conversor.sueloPixelY(dims[1])
        rendererActivo.dibujarSuelo(g2d, dims[0], dims[1], sueloY)
        if (datosRender[0] > 0.0) {
            val baseAltoY = conversor.metroAPixelY(datosRender[0], dims[0], dims[1], maximos)
            rendererActivo.dibujarPlataformaInicial(g2d, conversor.origenPixelX(), baseAltoY, sueloY)
        }
        if (datosRender[2] > 0.0) {
            val targetX = conversor.metroAPixelX(datosRender[2], dims[0], dims[1], maximos)
            rendererActivo.dibujarObjetivoLlegada(g2d, targetX, sueloY, datosRender[2])
        }
    }

    private fun dibujarTrayectoriasYElementos(g2d: Graphics2D, dims: IntArray, maximos: DoubleArray, datosRender: DoubleArray) {
        capas.dibujarTrayectorias(g2d, controlador, dims, maximos)
        val pivoteY = conversor.metroAPixelY(datosRender[0], dims[0], dims[1], maximos)
        rendererActivo.dibujarLanzador(g2d, conversor.origenPixelX(), pivoteY, datosRender[1])
        val px = conversor.metroAPixelX(datosRender[3], dims[0], dims[1], maximos)
        val py = conversor.metroAPixelY(datosRender[4], dims[0], dims[1], maximos)
        rendererActivo.dibujarProyectil(g2d, px, py, controlador.estaFinalizado())
    }
}
