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
 * Panel contenedor exclusivo por pestañas para los tres Gráficos Cinemáticos Cartesianos:
 * 1. **Posición vs Tiempo ($y(t), x(t)$)**
 * 2. **Velocidad vs Tiempo ($v_y(t), v_x(t)$)**
 * 3. **Aceleración vs Tiempo ($a_y(t), a_x(t)$)**
 *
 * Renderiza únicamente el gráfico de la pestaña activa ocupando todo el alto disponible
 * del contenedor, evitando fuentes apretadas o ejes superpuestos.
 *
 * Cumple con los Principios SOLID y métricas de calidad del repositorio:
 * - **DUP -> 0%:** Reutiliza [CartesianChartCanvas] en los tres renderizadores.
 * - **SRP:** Orquesta el contenedor de pestañas y delega los datos hacia los renderizadores sin recalcular física.
 * - **DIP:** Recibe los renderizadores [RenderizadorGraficoCinematico] inyectados por constructor.
 * - **POO:** Encapsula toda la gestión visual de pestañas en una clase cohesiva sin funciones top-level.
 */
class PanelGraficosCinematicos(
    val renderizadorPosicion: RenderizadorPosicion = RenderizadorPosicion(),
    val renderizadorVelocidad: RenderizadorVelocidad = RenderizadorVelocidad(),
    val renderizadorAceleracion: RenderizadorAceleracion = RenderizadorAceleracion()
) : JPanel(BorderLayout()) {

    companion object {
        const val TITULO_PESTANA_POSICION = "Posición vs Tiempo (y(t), x(t))"
        const val TITULO_PESTANA_VELOCIDAD = "Velocidad vs Tiempo (vy(t), vx(t))"
        const val TITULO_PESTANA_ACELERACION = "Aceleración vs Tiempo (ay(t), ax(t))"
    }

    /** Contenedor exclusivo de navegación por pestañas individuales. */
    val tabbedPane: JTabbedPane = JTabbedPane(JTabbedPane.TOP)

    /** Índice de la pestaña actualmente activa. */
    val pestanaActiva: Int
        get() = tabbedPane.selectedIndex

    init {
        background = Color.WHITE
        border = CompoundBorder(
            BorderFactory.createLineBorder(Color(226, 232, 240), 1),
            EmptyBorder(8, 8, 8, 8)
        )

        // 1. Armar el encabezado limpio sin selector "Ver los 3"
        val panelCabecera = crearPanelCabecera()
        add(panelCabecera, BorderLayout.NORTH)

        // 2. Configurar navegación exclusiva por pestañas ocupando todo el alto disponible
        tabbedPane.apply {
            font = Font(Font.SANS_SERIF, Font.BOLD, 12)
            background = Color.WHITE
            addTab(TITULO_PESTANA_POSICION, renderizadorPosicion.canvas)
            addTab(TITULO_PESTANA_VELOCIDAD, renderizadorVelocidad.canvas)
            addTab(TITULO_PESTANA_ACELERACION, renderizadorAceleracion.canvas)
        }

        add(tabbedPane, BorderLayout.CENTER)
    }

    /**
     * Selecciona la pestaña indicada por su índice (0: Posición, 1: Velocidad, 2: Aceleración).
     */
    fun seleccionarPestana(indice: Int) {
        if (indice in 0 until tabbedPane.tabCount) {
            tabbedPane.selectedIndex = indice
            revalidate()
            repaint()
        }
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

    private fun crearPanelCabecera(): JPanel {
        return JPanel(BorderLayout()).apply {
            isOpaque = false
            border = EmptyBorder(0, 4, 6, 4)

            val labelTitulo = JLabel("📊 Gráficos Cinemáticos en Tiempo Real").apply {
                font = Font(Font.SANS_SERIF, Font.BOLD, 13)
                foreground = Color(15, 23, 42)
            }

            add(labelTitulo, BorderLayout.WEST)
        }
    }
}
