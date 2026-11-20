package org.example.ui.simulacion.graficos

import org.example.model.IngresoDatos
import org.example.model.PosicionTrayectoria
import org.example.model.ResultadoTiroOblicuo
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Component
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
    renderizadorPos: RenderizadorGraficoCinematico = RenderizadorGraficoCinematico.posicion(),
    renderizadorVel: RenderizadorGraficoCinematico = RenderizadorGraficoCinematico.velocidad(),
    renderizadorAcel: RenderizadorGraficoCinematico = RenderizadorGraficoCinematico.aceleracion()
) : JPanel(BorderLayout()) {

    companion object {
        val nuevo: PanelGraficosCinematicos
            get() = PanelGraficosCinematicos()
        val TITULO_PESTANA_POSICION: String = "Posición vs Tiempo (y(t), x(t))"
        val TITULO_PESTANA_VELOCIDAD: String = "Velocidad vs Tiempo (vy(t), vx(t))"
        val TITULO_PESTANA_ACELERACION: String = "Aceleración vs Tiempo (ay(t), ax(t))"
    }

    private val renderizadores: List<RenderizadorGraficoCinematico> = listOf(renderizadorPos, renderizadorVel, renderizadorAcel)
    private val tabbedPane: JTabbedPane = JTabbedPane(JTabbedPane.TOP)

    val pestanaActiva: Int
        get() = tabbedPane.selectedIndex

    init {
        background = Color.WHITE
        border = CompoundBorder(BorderFactory.createLineBorder(Color(226, 232, 240), 1), EmptyBorder(8, 8, 8, 8))
        add(crearPanelCabecera(), BorderLayout.NORTH)
        tabbedPane.apply {
            font = Font(Font.SANS_SERIF, Font.BOLD, 12)
            background = Color.WHITE
            addTab(TITULO_PESTANA_POSICION, renderizadores[0].canvas)
            addTab(TITULO_PESTANA_VELOCIDAD, renderizadores[1].canvas)
            addTab(TITULO_PESTANA_ACELERACION, renderizadores[2].canvas)
        }
        add(tabbedPane, BorderLayout.CENTER)
    }

    fun obtenerRenderizador(indice: Int): RenderizadorGraficoCinematico {
        return if (tabbedPane.tabCount > 0) renderizadores[indice] else renderizadores[0]
    }

    fun obtenerTitulosPestanas(): List<String> {
        return (0 until tabbedPane.tabCount).map { tabbedPane.getTitleAt(it) }
    }

    fun obtenerComponenteActivo(): Component? {
        return if (renderizadores.isNotEmpty()) tabbedPane.selectedComponent else null
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
            renderizadores.forEach { it.actualizar(tiempoActual, puntosEstela, resultadoTeorico, datos) }
        }
    }

    fun configurarEjercicio(datos: IngresoDatos, resultadoTeorico: ResultadoTiroOblicuo) {
        renderizadores.forEach { it.configurarLimites(datos, resultadoTeorico) }
        reiniciar()
    }

    fun reiniciar() {
        if (tabbedPane.tabCount >= 0) {
            renderizadores.forEach { it.reiniciar() }
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
