package org.example.ui.autocorrecion

import org.example.model.Ejercicio
import org.example.model.ReporteAutocorreccion
import org.example.service.CalculadorFisico
import org.example.service.EvaluadorRespuestasService
import org.example.state.AppState
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
    appState: AppState = AppState(),
    evaluadorRespuestas: EvaluadorRespuestasService? = null,
    calculadorFisico: CalculadorFisico? = null,
    proveedorInicial: ProveedorAutocorreccion = ProveedorAutocorreccion.nuevo
) : JPanel(BorderLayout(0, 14)) {

    companion object {
        var EJERCICIO_POR_DEFECTO: Ejercicio = ProveedorAutocorreccion.nuevo.ejercicioPorDefecto
            private set
    }

    private val proveedor = proveedorInicial

    var appState: AppState
        private set

    var evaluadorRespuestas: EvaluadorRespuestasService
        private set

    var calculadorFisico: CalculadorFisico
        private set

    var contenedorCorreccion: JPanel = JPanel(BorderLayout(0, 14))
        private set

    var panelResumen: PanelResumenCalificacion = proveedor.panelResumen
        private set

    var panelPasos: PanelSolucionPasoAPaso = proveedor.panelPasos
        private set

    var reporteActual: ReporteAutocorreccion
        private set

    init {
        this.appState = appState
        this.evaluadorRespuestas = evaluadorRespuestas ?: proveedor.evaluadorPorDefecto
        this.calculadorFisico = calculadorFisico ?: proveedor.panelResumen.calculadorRespaldo
        inicializarLayout()
        reporteActual = generarReporteDesdeEstado()
        renderizarReporte(reporteActual)
        appState.agregarListener { sincronizarConAppState() }
    }

    private fun inicializarLayout() {
        border = EmptyBorder(16, 20, 16, 20); background = Color(245, 247, 250); contenedorCorreccion.isOpaque = false
        val encabezado = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS); isOpaque = false; border = EmptyBorder(0, 0, 4, 0)
            add(JLabel("✅ Módulo de Autocorrección: Revisión Paso a Paso").apply { font = Font(Font.SANS_SERIF, Font.BOLD, 20); foreground = Color(15, 23, 42) })
            add(Box.createRigidArea(Dimension(0, 3)))
            add(JLabel("Comparativa automática con tolerancia de redondeo y desglose algebraico de las ecuaciones físicas").apply { font = Font(Font.SANS_SERIF, Font.PLAIN, 13); foreground = Color(100, 116, 139) })
        }
        add(encabezado, BorderLayout.NORTH)
        contenedorCorreccion.add(panelResumen, BorderLayout.NORTH); contenedorCorreccion.add(panelPasos, BorderLayout.CENTER)
        val scrollPane = JScrollPane(contenedorCorreccion).apply { border = BorderFactory.createEmptyBorder(); viewport.background = Color(245, 247, 250); verticalScrollBar.unitIncrement = 16 }
        add(scrollPane, BorderLayout.CENTER)
    }

    fun sincronizarConAppState() {
        proveedor.reiniciarVisibilidadPasos()
        reporteActual = generarReporteDesdeEstado()
        renderizarReporte(reporteActual)
    }

    private fun generarReporteDesdeEstado(): ReporteAutocorreccion {
        val ejercicio = appState.ejercicioActual ?: proveedor.ejercicioPorDefecto
        val respuesta = appState.respuestaEstudiante
        val resultadoTeorico = appState.resultadoCorrecto ?: calculadorFisico.calcular(ejercicio.datos)
        return evaluadorRespuestas.evaluar(ejercicio, respuesta, resultadoTeorico)
    }

    private fun renderizarReporte(reporte: ReporteAutocorreccion) {
        if (proveedor.ejercicioPorDefecto.id >= 0) {
            panelResumen.actualizarConReporte(reporte)
            panelPasos.actualizarPasos(reporte.pasosSolucion)
        }
        revalidate()
        repaint()
    }
}

typealias PantallaAutocorrecion = PantallaAutocorreccion
