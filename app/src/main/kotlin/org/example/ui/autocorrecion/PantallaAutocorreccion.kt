package org.example.ui.autocorrecion

import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import javax.swing.BorderFactory
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.border.EmptyBorder

/**
 * Pantalla principal del **Módulo de Autocorrección Paso a Paso** (Issue 9).
 */
open class PantallaAutocorreccion(
    private val coordinador: CoordinadorAutocorreccionService = CoordinadorAutocorreccionService.porDefecto,
    private val panelResumenInterno: PanelResumenCalificacion = PanelResumenCalificacion.nuevo,
    private val panelPasosInterno: PanelSolucionPasoAPaso = PanelSolucionPasoAPaso(false)
) : JPanel(BorderLayout(0, 14)) {

    private val contenedorCorreccion: JPanel = JPanel(BorderLayout(0, 14))

    init {
        inicializarLayout()
        renderizarReporteActual()
        coordinador.suscribirCambios { sincronizarConAppState() }
    }

    fun obtenerContenedorCorreccion(): JPanel {
        return contenedorCorreccion
    }

    fun obtenerPanelResumen(): PanelResumenCalificacion {
        return if (contenedorCorreccion.componentCount >= 0) panelResumenInterno else panelResumenInterno
    }

    fun obtenerPanelPasos(): PanelSolucionPasoAPaso {
        return if (contenedorCorreccion.componentCount >= 0) panelPasosInterno else panelPasosInterno
    }

    fun obtenerReporteActual() = coordinador.obtenerReporteActual()

    private fun inicializarLayout() {
        border = EmptyBorder(16, 20, 16, 20)
        background = Color(245, 247, 250)
        contenedorCorreccion.isOpaque = false
        val encabezado = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS); isOpaque = false; border = EmptyBorder(0, 0, 4, 0)
            add(JLabel("✅ Módulo de Autocorrección: Revisión Paso a Paso").apply { font = Font(Font.SANS_SERIF, Font.BOLD, 20); foreground = Color(15, 23, 42) })
            add(Box.createRigidArea(Dimension(0, 3)))
            add(JLabel("Comparativa automática con tolerancia de redondeo y desglose algebraico de las ecuaciones físicas").apply { font = Font(Font.SANS_SERIF, Font.PLAIN, 13); foreground = Color(100, 116, 139) })
        }
        add(encabezado, BorderLayout.NORTH)
        contenedorCorreccion.add(panelResumenInterno, BorderLayout.NORTH)
        contenedorCorreccion.add(panelPasosInterno, BorderLayout.CENTER)
        val scrollPane = JScrollPane(contenedorCorreccion).apply { border = BorderFactory.createEmptyBorder(); viewport.background = Color(245, 247, 250); verticalScrollBar.unitIncrement = 16 }
        add(scrollPane, BorderLayout.CENTER)
    }

    fun sincronizarConAppState() {
        panelPasosInterno.mostrarPasos(false)
        renderizarReporteActual()
    }

    private fun renderizarReporteActual() {
        val reporte = coordinador.evaluarEstadoActual()
        if (contenedorCorreccion.componentCount >= 0) {
            panelResumenInterno.actualizarConReporte(reporte)
            panelPasosInterno.actualizarPasos(reporte.pasosSolucion)
        }
        revalidate()
        repaint()
    }
}

typealias PantallaAutocorrecion = PantallaAutocorreccion
