package org.example.ui.autocorrecion

import org.example.model.EstadoEvaluacion
import org.example.model.EvaluacionParametro
import org.example.model.ReporteAutocorreccion
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.GridLayout
import java.util.Locale
import javax.swing.BorderFactory
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.border.CompoundBorder
import javax.swing.border.EmptyBorder

/**
 * Componente visual para la **Sección A: Resumen de Calificación y Comparativa de Respuestas**.
 *
 * Renderiza:
 * 1. **Encabezado de Estado:** Resumen global del intento (ej. "4 de 5 respuestas correctas"),
 *    mensaje de diagnóstico general y datos del ejercicio evaluado.
 * 2. **Tarjetas Comparativas por Parámetro ($V_x, V_y, t_{vuelo}, y_{max}, x_{alcance}$):**
 *    Con fondo/borde verde suave si es correcto, rojo suave si es incorrecto, mostrando el
 *    valor del alumno, el valor real esperado con unidades y el badge de estado.
 */
class PanelResumenCalificacion : JPanel(BorderLayout(0, 12)) {

    companion object {
        val COLOR_VERDE_FONDO = Color(240, 253, 244)
        val COLOR_VERDE_BORDE = Color(34, 197, 94)
        val COLOR_VERDE_TEXTO = Color(21, 128, 61)

        val COLOR_ROJO_FONDO = Color(254, 242, 242)
        val COLOR_ROJO_BORDE = Color(239, 68, 68)
        val COLOR_ROJO_TEXTO = Color(185, 28, 28)

        val COLOR_NEUTRO_FONDO = Color(248, 250, 252)
        val COLOR_NEUTRO_BORDE = Color(203, 213, 225)
        val COLOR_NEUTRO_TEXTO = Color(71, 85, 105)
    }

    /** Etiqueta con el conteo global de aciertos (ej. "4 de 5 respuestas correctas"). */
    val labelResumenConteo: JLabel = JLabel()

    /** Etiqueta con el estado general (ej. "¡Excelente trabajo!" o "Requiere revisión"). */
    val labelEstadoGeneral: JLabel = JLabel()

    /** Etiqueta informativa con el título y parámetros iniciales del ejercicio. */
    val labelDatosEjercicio: JLabel = JLabel()

    /** Grilla contenedora de las 5 tarjetas comparativas de parámetros físicos. */
    val contenedorTarjetas: JPanel = JPanel(GridLayout(1, 5, 10, 0)).apply {
        isOpaque = false
    }

    private val panelBannerEstado: JPanel = JPanel(BorderLayout(12, 4))

    init {
        isOpaque = false
        configurarBannerInicial()
        add(panelBannerEstado, BorderLayout.NORTH)
        add(contenedorTarjetas, BorderLayout.CENTER)
    }

    /**
     * Actualiza el encabezado de calificación y reconstruye las tarjetas comparativas según el [reporte].
     */
    fun actualizarConReporte(reporte: ReporteAutocorreccion) {
        val d = reporte.ejercicio.datos
        labelDatosEjercicio.text = String.format(
            Locale.US,
            "📌 Ejercicio #%d: %s  |  Condiciones: v₀ = %.2f m/s, θ = %.2f°, y₀ = %.2f m",
            reporte.ejercicio.id,
            reporte.ejercicio.titulo,
            d.velocidadInicial,
            d.angulo,
            d.alturaInicial
        )

        labelResumenConteo.text = "📊 Calificación: ${reporte.textoResumenAciertos()} (${String.format(Locale.US, "%.0f%%", reporte.porcentajeAcierto)})"
        labelEstadoGeneral.text = reporte.mensajeGeneral

        val (fondoBanner, bordeBanner, colorTextoEstado) = when {
            reporte.respuestaEstudiante == null -> Triple(COLOR_NEUTRO_FONDO, COLOR_NEUTRO_BORDE, COLOR_NEUTRO_TEXTO)
            reporte.esExcelente() -> Triple(COLOR_VERDE_FONDO, COLOR_VERDE_BORDE, COLOR_VERDE_TEXTO)
            else -> Triple(COLOR_ROJO_FONDO, COLOR_ROJO_BORDE, COLOR_ROJO_TEXTO)
        }

        panelBannerEstado.background = fondoBanner
        panelBannerEstado.border = CompoundBorder(
            BorderFactory.createMatteBorder(0, 5, 0, 0, bordeBanner),
            CompoundBorder(
                BorderFactory.createLineBorder(bordeBanner, 1),
                EmptyBorder(12, 16, 12, 16)
            )
        )
        labelEstadoGeneral.foreground = colorTextoEstado

        contenedorTarjetas.removeAll()
        for (evaluacion in reporte.evaluaciones) {
            contenedorTarjetas.add(crearTarjetaParametro(evaluacion))
        }

        revalidate()
        repaint()
    }

    private fun configurarBannerInicial() {
        labelResumenConteo.font = Font(Font.SANS_SERIF, Font.BOLD, 16)
        labelResumenConteo.foreground = Color(15, 23, 42)

        labelEstadoGeneral.font = Font(Font.SANS_SERIF, Font.BOLD, 13)

        labelDatosEjercicio.font = Font(Font.SANS_SERIF, Font.PLAIN, 12)
        labelDatosEjercicio.foreground = Color(71, 85, 105)

        val panelTextos = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            isOpaque = false
            add(labelResumenConteo)
            add(Box.createRigidArea(Dimension(0, 4)))
            add(labelEstadoGeneral)
            add(Box.createRigidArea(Dimension(0, 4)))
            add(labelDatosEjercicio)
        }

        panelBannerEstado.add(panelTextos, BorderLayout.CENTER)
    }

    private fun crearTarjetaParametro(evaluacion: EvaluacionParametro): JPanel {
        val (colorFondo, colorBorde, colorBadge) = when (evaluacion.estado) {
            EstadoEvaluacion.CORRECTO -> Triple(COLOR_VERDE_FONDO, COLOR_VERDE_BORDE, COLOR_VERDE_TEXTO)
            EstadoEvaluacion.INCORRECTO -> Triple(COLOR_ROJO_FONDO, COLOR_ROJO_BORDE, COLOR_ROJO_TEXTO)
            EstadoEvaluacion.NO_INGRESADO -> Triple(COLOR_NEUTRO_FONDO, COLOR_NEUTRO_BORDE, COLOR_NEUTRO_TEXTO)
        }

        return JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            background = colorFondo
            border = CompoundBorder(
                BorderFactory.createMatteBorder(3, 0, 0, 0, colorBorde),
                CompoundBorder(
                    BorderFactory.createLineBorder(colorBorde, 1),
                    EmptyBorder(10, 12, 10, 12)
                )
            )

            val labelParametro = JLabel(evaluacion.parametro.nombreVisible).apply {
                font = Font(Font.SANS_SERIF, Font.BOLD, 12)
                foreground = Color(15, 23, 42)
            }

            val labelBadge = JLabel(evaluacion.estado.textoBadge()).apply {
                font = Font(Font.SANS_SERIF, Font.BOLD, 12)
                foreground = colorBadge
            }

            val labelValorAlumno = JLabel("Tu respuesta: ${evaluacion.textoValorEstudiante()}").apply {
                font = Font(Font.SANS_SERIF, Font.PLAIN, 12)
                foreground = Color(30, 41, 59)
            }

            val labelValorEsperado = JLabel("Valor esperado: ${evaluacion.textoValorEsperado()}").apply {
                font = Font(Font.SANS_SERIF, Font.BOLD, 12)
                foreground = Color(15, 23, 42)
            }

            add(labelParametro)
            add(Box.createRigidArea(Dimension(0, 6)))
            add(labelBadge)
            add(Box.createRigidArea(Dimension(0, 8)))
            add(labelValorAlumno)
            add(Box.createRigidArea(Dimension(0, 4)))
            add(labelValorEsperado)
        }
    }
}
