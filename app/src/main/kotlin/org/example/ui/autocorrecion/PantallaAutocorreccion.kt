package org.example.ui.autocorrecion

import org.example.model.Dificultad
import org.example.model.Ejercicio
import org.example.model.IngresoDatos
import org.example.model.ReporteAutocorreccion
import org.example.service.CalculadorFisico
import org.example.service.CalculadorTiroOblicuo
import org.example.service.EvaluadorRespuestas
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
 *
 * Lee reactivamente desde [AppState] el ejercicio activo, los resultados teóricos
 * de [CalculadorTiroOblicuo] y las respuestas ingresadas por el estudiante, delegando
 * la evaluación con tolerancia en [EvaluadorRespuestasService].
 *
 * Organiza la vista en dos secciones estructuradas:
 * - **Sección A ([PanelResumenCalificacion]):** Encabezado de estado y tarjetas comparativas
 *   para cada parámetro ($V_x, V_y, t_{vuelo}, y_{max}, x_{alcance}$) con indicadores verdes/rojos.
 * - **Sección B ([PanelSolucionPasoAPaso]):** Bloque desplegable con los 4 pasos de resolución
 *   teórica y sustitución numérica de las fórmulas.
 *
 * @param appState Estado compartido inyectado (DIP).
 * @param evaluadorRespuestas Servicio de evaluación con tolerancia inyectado (DIP).
 * @param calculadorFisico Motor físico analítico inyectado (DIP).
 */
open class PantallaAutocorreccion(
    val appState: AppState = AppState(),
    val evaluadorRespuestas: EvaluadorRespuestasService = EvaluadorRespuestas(),
    val calculadorFisico: CalculadorFisico = CalculadorTiroOblicuo()
) : JPanel(BorderLayout(0, 14)) {

    companion object {
        /** Ejercicio de referencia mostrado cuando aún no se ha enviado un intento desde Práctica. */
        val EJERCICIO_POR_DEFECTO = Ejercicio(
            id = 1,
            titulo = "Futbolista — Tiro libre inicial",
            enunciado = "Un futbolista patea una pelota desde el suelo con una velocidad inicial de 20 m/s y un ángulo de 30°.",
            dificultad = Dificultad.FACIL,
            datos = IngresoDatos(velocidadInicial = 20.0, angulo = 30.0, alturaInicial = 0.0)
        )
    }

    /** Contenedor central de corrección (mantiene compatibilidad con la infraestructura de navegación). */
    val contenedorCorreccion: JPanel = JPanel(BorderLayout(0, 14)).apply {
        isOpaque = false
    }

    /** Sección A: Resumen de calificación y tarjetas comparativas por parámetro. */
    val panelResumen: PanelResumenCalificacion = PanelResumenCalificacion()

    /** Sección B: Desglose de la solución detallada paso a paso. */
    val panelPasos: PanelSolucionPasoAPaso = PanelSolucionPasoAPaso()

    /** Último reporte de autocorrección generado y renderizado en pantalla. */
    var reporteActual: ReporteAutocorreccion
        private set

    init {
        border = EmptyBorder(16, 20, 16, 20)
        background = Color(245, 247, 250)

        // 1. Encabezado principal de la pantalla
        add(crearEncabezado(), BorderLayout.NORTH)

        // 2. Estructurar Sección A (arriba) y Sección B (abajo) dentro de un contenedor desplazable
        contenedorCorreccion.add(panelResumen, BorderLayout.NORTH)
        contenedorCorreccion.add(panelPasos, BorderLayout.CENTER)

        val scrollPane = JScrollPane(contenedorCorreccion).apply {
            border = BorderFactory.createEmptyBorder()
            viewport.background = Color(245, 247, 250)
            verticalScrollBar.unitIncrement = 16
        }
        add(scrollPane, BorderLayout.CENTER)

        // 3. Evaluar estado inicial y suscribir a actualizaciones de AppState
        reporteActual = generarReporteDesdeEstado()
        renderizarReporte(reporteActual)

        appState.agregarListener {
            sincronizarConAppState()
        }
    }

    /**
     * Re-evalúa los datos almacenados en [AppState] y actualiza ambas secciones de la pantalla,
     * iniciando con la resolución paso a paso oculta por defecto.
     */
    fun sincronizarConAppState() {
        panelPasos.mostrarPasos(false)
        reporteActual = generarReporteDesdeEstado()
        renderizarReporte(reporteActual)
    }

    private fun generarReporteDesdeEstado(): ReporteAutocorreccion {
        val ejercicio = appState.ejercicioActual ?: EJERCICIO_POR_DEFECTO
        val respuesta = appState.respuestaEstudiante
        val resultadoTeorico = appState.resultadoCorrecto ?: calculadorFisico.calcular(ejercicio.datos)

        return evaluadorRespuestas.evaluar(
            ejercicio = ejercicio,
            respuesta = respuesta,
            resultadoTeorico = resultadoTeorico
        )
    }

    private fun renderizarReporte(reporte: ReporteAutocorreccion) {
        panelResumen.actualizarConReporte(reporte)
        panelPasos.actualizarPasos(reporte.pasosSolucion)
        revalidate()
        repaint()
    }

    private fun crearEncabezado(): JPanel {
        return JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            isOpaque = false
            border = EmptyBorder(0, 0, 4, 0)

            val labelTitulo = JLabel("✅ Módulo de Autocorrección: Revisión Paso a Paso").apply {
                font = Font(Font.SANS_SERIF, Font.BOLD, 20)
                foreground = Color(15, 23, 42)
            }
            val labelSubtitulo = JLabel(
                "Comparativa automática con tolerancia de redondeo y desglose algebraico de las ecuaciones físicas"
            ).apply {
                font = Font(Font.SANS_SERIF, Font.PLAIN, 13)
                foreground = Color(100, 116, 139)
            }

            add(labelTitulo)
            add(Box.createRigidArea(Dimension(0, 3)))
            add(labelSubtitulo)
        }
    }
}

/**
 * Alias de compatibilidad en el paquete `org.example.ui.autocorrecion`.
 */
typealias PantallaAutocorrecion = PantallaAutocorreccion
