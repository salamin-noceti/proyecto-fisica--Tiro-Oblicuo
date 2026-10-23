package org.example.ui.simulacion.graficos

import org.example.model.IngresoDatos
import org.example.model.PosicionTrayectoria
import org.example.model.ResultadoTiroOblicuo
import java.awt.BorderLayout
import java.awt.CardLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.Font
import java.awt.GridLayout
import javax.swing.BorderFactory
import javax.swing.ButtonGroup
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JRadioButton
import javax.swing.JTabbedPane
import javax.swing.border.CompoundBorder
import javax.swing.border.EmptyBorder

/**
 * Panel contenedor responsivo para la organización de los tres Gráficos Cinemáticos Cartesianos:
 * 1. Posición vs Tiempo: $x(t)$ e $y(t)$
 * 2. Velocidad vs Tiempo: $v_x(t)$ y $v_y(t)$
 * 3. Aceleración vs Tiempo: $a_x(t)$ y $a_y(t)$
 *
 * Ofrece dos modalidades de visualización:
 * - **Por Pestañas (JTabbedPane):** Para ver cada gráfico en alta resolución y detalle individual.
 * - **En Grilla Unificada (GridLayout):** Para observar en vivo las tres curvas cinemáticas simultáneamente.
 *
 * Cumple con los Principios SOLID:
 * - **SRP:** Orquesta el contenedor visual y la delegación hacia los renderizadores sin recalcular física.
 * - **DIP:** Recibe los renderizadores [RenderizadorGraficoCinematico] inyectados por constructor.
 * - **POO:** Encapsula la alternancia dinámica de vistas en Swing evitando duplicación de componentes.
 */
class PanelGraficosCinematicos(
    val renderizadorPosicion: RenderizadorPosicion = RenderizadorPosicion(),
    val renderizadorVelocidad: RenderizadorVelocidad = RenderizadorVelocidad(),
    val renderizadorAceleracion: RenderizadorAceleracion = RenderizadorAceleracion()
) : JPanel(BorderLayout()) {

    /** Contenedor de pestañas para la visualización individual. */
    val tabbedPane: JTabbedPane = JTabbedPane(JTabbedPane.TOP)

    /** Contenedor de grilla para la visualización simultánea de los tres gráficos. */
    val panelGrilla: JPanel = JPanel(GridLayout(3, 1, 0, 8))

    /** Contenedor de conmutación de vistas mediante CardLayout. */
    private val layoutVistas = CardLayout()
    private val contenedorVistas = JPanel(layoutVistas)

    private val radioPestanas: JRadioButton = JRadioButton("Pestañas", true)
    private val radioGrilla: JRadioButton = JRadioButton("Ver los 3", false)

    companion object {
        private const val VISTA_PESTANAS = "PESTANAS"
        private const val VISTA_GRILLA = "GRILLA"
    }

    init {
        background = Color.WHITE
        border = CompoundBorder(
            BorderFactory.createLineBorder(Color(226, 232, 240), 1),
            EmptyBorder(8, 8, 8, 8)
        )

        // 1. Armar la barra superior con el selector de visualización
        val panelCabecera = crearPanelCabecera()
        add(panelCabecera, BorderLayout.NORTH)

        // 2. Configurar pestañas iniciales
        tabbedPane.apply {
            font = Font(Font.SANS_SERIF, Font.BOLD, 11)
            background = Color.WHITE
            addTab("📈 Posición [x, y]", renderizadorPosicion.canvas)
            addTab("⚡ Velocidad [vx, vy]", renderizadorVelocidad.canvas)
            addTab("⏱ Aceleración [ax, ay]", renderizadorAceleracion.canvas)
        }

        // 3. Registrar vistas en el CardLayout
        contenedorVistas.add(tabbedPane, VISTA_PESTANAS)
        contenedorVistas.add(panelGrilla, VISTA_GRILLA)
        add(contenedorVistas, BorderLayout.CENTER)

        // 4. Configurar escuchadores de cambio de modo de visualización
        configurarSelectorModo()
    }

    /**
     * Sincroniza en tiempo real los tres gráficos con las coordenadas instantáneas y el historial acumulado.
     */
    fun actualizar(
        tiempoActual: Double,
        puntosEstela: List<PosicionTrayectoria>,
        resultadoTeorico: ResultadoTiroOblicuo,
        datos: IngresoDatos
    ) {
        renderizadorPosicion.actualizar(tiempoActual, puntosEstela, resultadoTeorico, datos)
        renderizadorVelocidad.actualizar(tiempoActual, puntosEstela, resultadoTeorico, datos)
        renderizadorAceleracion.actualizar(tiempoActual, puntosEstela, resultadoTeorico, datos)
    }

    /**
     * Ajusta los límites de los tres gráficos cuando se carga un nuevo ejercicio o cambian las condiciones iniciales.
     */
    fun configurarEjercicio(datos: IngresoDatos, resultadoTeorico: ResultadoTiroOblicuo) {
        renderizadorPosicion.configurarLimites(datos, resultadoTeorico)
        renderizadorVelocidad.configurarLimites(datos, resultadoTeorico)
        renderizadorAceleracion.configurarLimites(datos, resultadoTeorico)
        reiniciar()
    }

    /**
     * Limpia las curvas dibujadas en los tres gráficos y regresa el marcador temporal a t = 0.
     */
    fun reiniciar() {
        renderizadorPosicion.reiniciar()
        renderizadorVelocidad.reiniciar()
        renderizadorAceleracion.reiniciar()
    }

    /**
     * Alterna la visualización hacia el modo de pestañas individuales.
     */
    fun mostrarModoPestanas() {
        radioPestanas.isSelected = true
        moverCanvasesAPestanas()
        layoutVistas.show(contenedorVistas, VISTA_PESTANAS)
        revalidate()
        repaint()
    }

    /**
     * Alterna la visualización hacia el modo de grilla simultánea con los tres gráficos.
     */
    fun mostrarModoGrilla() {
        radioGrilla.isSelected = true
        moverCanvasesAGrilla()
        layoutVistas.show(contenedorVistas, VISTA_GRILLA)
        revalidate()
        repaint()
    }

    private fun moverCanvasesAPestanas() {
        panelGrilla.removeAll()
        tabbedPane.removeAll()
        tabbedPane.addTab("📈 Posición [x, y]", renderizadorPosicion.canvas)
        tabbedPane.addTab("⚡ Velocidad [vx, vy]", renderizadorVelocidad.canvas)
        tabbedPane.addTab("⏱ Aceleración [ax, ay]", renderizadorAceleracion.canvas)
    }

    private fun moverCanvasesAGrilla() {
        tabbedPane.removeAll()
        panelGrilla.removeAll()
        panelGrilla.add(renderizadorPosicion.canvas)
        panelGrilla.add(renderizadorVelocidad.canvas)
        panelGrilla.add(renderizadorAceleracion.canvas)
    }

    private fun configurarSelectorModo() {
        val grupo = ButtonGroup()
        grupo.add(radioPestanas)
        grupo.add(radioGrilla)

        radioPestanas.addActionListener {
            mostrarModoPestanas()
        }

        radioGrilla.addActionListener {
            mostrarModoGrilla()
        }
    }

    private fun crearPanelCabecera(): JPanel {
        return JPanel(BorderLayout()).apply {
            isOpaque = false
            border = EmptyBorder(0, 4, 6, 4)

            val labelTitulo = JLabel("📊 Gráficos Cinemáticos").apply {
                font = Font(Font.SANS_SERIF, Font.BOLD, 13)
                foreground = Color(15, 23, 42)
            }

            val panelRadios = JPanel(FlowLayout(FlowLayout.RIGHT, 6, 0)).apply {
                isOpaque = false

                radioPestanas.font = Font(Font.SANS_SERIF, Font.PLAIN, 11)
                radioPestanas.isOpaque = false
                radioGrilla.font = Font(Font.SANS_SERIF, Font.PLAIN, 11)
                radioGrilla.isOpaque = false

                add(JLabel("Vista: ").apply {
                    font = Font(Font.SANS_SERIF, Font.BOLD, 11)
                    foreground = Color(100, 116, 139)
                })
                add(radioPestanas)
                add(radioGrilla)
            }

            add(labelTitulo, BorderLayout.WEST)
            add(panelRadios, BorderLayout.EAST)
        }
    }
}
