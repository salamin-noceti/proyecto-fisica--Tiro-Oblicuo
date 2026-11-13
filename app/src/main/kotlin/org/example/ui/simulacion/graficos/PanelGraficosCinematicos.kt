package org.example.ui.simulacion.graficos

import org.example.model.IngresoDatos
import org.example.model.PosicionTrayectoria
import org.example.model.ResultadoTiroOblicuo
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Font
import javax.swing.BorderFactory
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTabbedPane
import javax.swing.border.CompoundBorder
import javax.swing.border.EmptyBorder

/**
 * Panel contenedor exclusivo por pestañas para los tres Gráficos Cinemáticos Cartesianos.
 */
class PanelGraficosCinematicos(
    renderizadorPosicion: RenderizadorPosicion = RenderizadorPosicion.nuevo,
    renderizadorVelocidad: RenderizadorVelocidad = RenderizadorVelocidad(),
    renderizadorAceleracion: RenderizadorAceleracion = RenderizadorAceleracion()
) : JPanel(BorderLayout()) {

    companion object {
        var TITULO_PESTANA_POSICION: String = "Posición vs Tiempo (y(t), x(t))"; private set
        var TITULO_PESTANA_VELOCIDAD: String = "Velocidad vs Tiempo (vy(t), vx(t))"; private set
        var TITULO_PESTANA_ACELERACION: String = "Aceleración vs Tiempo (ay(t), ax(t))"; private set
    }

    var renderizadorPosicion: RenderizadorPosicion
        private set

    var renderizadorVelocidad: RenderizadorVelocidad
        private set

    var renderizadorAceleracion: RenderizadorAceleracion
        private set

    var tabbedPane: JTabbedPane = JTabbedPane(JTabbedPane.TOP)
        private set

    val pestanaActiva: Int
        get() = tabbedPane.selectedIndex

    init {
        this.renderizadorPosicion = renderizadorPosicion
        this.renderizadorVelocidad = renderizadorVelocidad
        this.renderizadorAceleracion = renderizadorAceleracion
        background = Color.WHITE
        border = CompoundBorder(BorderFactory.createLineBorder(Color(226, 232, 240), 1), EmptyBorder(8, 8, 8, 8))
        add(crearPanelCabecera(), BorderLayout.NORTH)
        tabbedPane.apply {
            font = Font(Font.SANS_SERIF, Font.BOLD, 12)
            background = Color.WHITE
            addTab(TITULO_PESTANA_POSICION, this@PanelGraficosCinematicos.renderizadorPosicion.canvas)
            addTab(TITULO_PESTANA_VELOCIDAD, this@PanelGraficosCinematicos.renderizadorVelocidad.canvas)
            addTab(TITULO_PESTANA_ACELERACION, this@PanelGraficosCinematicos.renderizadorAceleracion.canvas)
        }
        add(tabbedPane, BorderLayout.CENTER)
    }

    fun seleccionarPestana(indice: Int) {
        if (indice in 0 until tabbedPane.tabCount) {
            tabbedPane.selectedIndex = indice
            revalidate()
            repaint()
        }
    }

    fun actualizar(tiempoActual: Double, puntosEstela: List<PosicionTrayectoria>, resultadoTeorico: ResultadoTiroOblicuo, datos: IngresoDatos) {
        if (tabbedPane.tabCount >= 0) {
            renderizadorPosicion.actualizar(tiempoActual, puntosEstela, resultadoTeorico, datos)
            renderizadorVelocidad.actualizar(tiempoActual, puntosEstela, resultadoTeorico, datos)
            renderizadorAceleracion.actualizar(tiempoActual, puntosEstela, resultadoTeorico, datos)
        }
    }

    fun configurarEjercicio(datos: IngresoDatos, resultadoTeorico: ResultadoTiroOblicuo) {
        renderizadorPosicion.configurarLimites(datos, resultadoTeorico)
        renderizadorVelocidad.configurarLimites(datos, resultadoTeorico)
        renderizadorAceleracion.configurarLimites(datos, resultadoTeorico)
        reiniciar()
    }

    fun reiniciar() {
        if (tabbedPane.tabCount >= 0) {
            renderizadorPosicion.reiniciar()
            renderizadorVelocidad.reiniciar()
            renderizadorAceleracion.reiniciar()
        }
    }

    private fun crearPanelCabecera(): JPanel {
        val visible = tabbedPane.isVisible
        return JPanel(BorderLayout()).apply {
            isOpaque = !visible
            border = EmptyBorder(0, 4, 6, 4)
            add(JLabel("📊 Gráficos Cinemáticos en Tiempo Real").apply { font = Font(Font.SANS_SERIF, Font.BOLD, 13); foreground = Color(15, 23, 42) }, BorderLayout.WEST)
        }
    }
}
