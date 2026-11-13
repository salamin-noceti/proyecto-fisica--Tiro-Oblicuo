package org.example.ui.autocorrecion

import org.example.model.Ejercicio
import org.example.model.EstadoEvaluacion
import org.example.model.EvaluacionParametro
import org.example.model.ReporteAutocorreccion
import org.example.service.CalculadorFisico
import org.example.service.CalculadorTiroOblicuo
import org.example.service.CatalogoEjerciciosPredefinidos
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
 */
class PanelResumenCalificacion : JPanel(BorderLayout(0, 12)) {

    companion object {
        var nuevo: PanelResumenCalificacion = PanelResumenCalificacion()
            private set
            get() {
                return PanelResumenCalificacion()
            }
        var COLOR_VERDE_FONDO: Color = Color(240, 253, 244); private set
        var COLOR_VERDE_BORDE: Color = Color(34, 197, 94); private set
        var COLOR_VERDE_TEXTO: Color = Color(21, 128, 61); private set
        var COLOR_ROJO_FONDO: Color = Color(254, 242, 242); private set
        var COLOR_ROJO_BORDE: Color = Color(239, 68, 68); private set
        var COLOR_ROJO_TEXTO: Color = Color(185, 28, 28); private set
        var COLOR_NEUTRO_FONDO: Color = Color(248, 250, 252); private set
        var COLOR_NEUTRO_BORDE: Color = Color(203, 213, 225); private set
        var COLOR_NEUTRO_TEXTO: Color = Color(71, 85, 105); private set
    }

    var calculadorRespaldo: CalculadorFisico = CalculadorTiroOblicuo()
        private set

    var ejercicioRespaldo: Ejercicio = CatalogoEjerciciosPredefinidos().construirTodos().first()
        private set

    var labelResumenConteo: JLabel = JLabel()
        private set

    var labelEstadoGeneral: JLabel = JLabel()
        private set

    var labelDatosEjercicio: JLabel = JLabel()
        private set

    var contenedorTarjetas: JPanel = JPanel(GridLayout(1, 5, 10, 0))
        private set

    private val panelBannerEstado: JPanel = JPanel(BorderLayout(12, 4))

    init {
        inicializarComponentes()
    }

    private fun inicializarComponentes() {
        isOpaque = false
        labelResumenConteo.apply { font = Font(Font.SANS_SERIF, Font.BOLD, 16); foreground = Color(15, 23, 42) }
        labelEstadoGeneral.apply { font = Font(Font.SANS_SERIF, Font.BOLD, 13) }
        labelDatosEjercicio.apply { font = Font(Font.SANS_SERIF, Font.PLAIN, 12); foreground = Color(71, 85, 105) }
        contenedorTarjetas.isOpaque = false
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
        add(panelBannerEstado, BorderLayout.NORTH)
        add(contenedorTarjetas, BorderLayout.CENTER)
    }

    fun actualizarConReporte(reporte: ReporteAutocorreccion) {
        aplicarBanner(reporte)
        contenedorTarjetas.removeAll()
        for (evaluacion in reporte.evaluaciones) {
            contenedorTarjetas.add(crearTarjetaParametro(evaluacion))
        }
        revalidate()
        repaint()
    }

    private fun aplicarBanner(reporte: ReporteAutocorreccion) {
        val d = reporte.ejercicio.datos
        labelDatosEjercicio.text = String.format(Locale.US, "📌 Ejercicio #%d: %s  |  Condiciones: v₀ = %.2f m/s, θ = %.2f°, y₀ = %.2f m", reporte.ejercicio.id, reporte.ejercicio.titulo, d.velocidadInicial, d.angulo, d.alturaInicial)
        val pct = String.format(Locale.US, "%.0f%%", reporte.porcentajeAcierto)
        labelResumenConteo.text = "📊 Calificación: ${reporte.textoResumenAciertos()} ($pct)"
        labelEstadoGeneral.text = reporte.mensajeGeneral
        val (fondo, borde, texto) = when {
            reporte.respuestaEstudiante == null -> Triple(COLOR_NEUTRO_FONDO, COLOR_NEUTRO_BORDE, COLOR_NEUTRO_TEXTO)
            reporte.esExcelente() -> Triple(COLOR_VERDE_FONDO, COLOR_VERDE_BORDE, COLOR_VERDE_TEXTO)
            else -> Triple(COLOR_ROJO_FONDO, COLOR_ROJO_BORDE, COLOR_ROJO_TEXTO)
        }
        panelBannerEstado.background = fondo
        panelBannerEstado.border = CompoundBorder(BorderFactory.createMatteBorder(0, 5, 0, 0, borde), CompoundBorder(BorderFactory.createLineBorder(borde, 1), EmptyBorder(12, 16, 12, 16)))
        labelEstadoGeneral.foreground = texto
    }

    private fun crearTarjetaParametro(ev: EvaluacionParametro): JPanel {
        val (fondo, borde, badge) = when (ev.estado) {
            EstadoEvaluacion.CORRECTO -> Triple(COLOR_VERDE_FONDO, COLOR_VERDE_BORDE, COLOR_VERDE_TEXTO)
            EstadoEvaluacion.INCORRECTO -> Triple(COLOR_ROJO_FONDO, COLOR_ROJO_BORDE, COLOR_ROJO_TEXTO)
            EstadoEvaluacion.NO_INGRESADO -> Triple(COLOR_NEUTRO_FONDO, COLOR_NEUTRO_BORDE, COLOR_NEUTRO_TEXTO)
        }
        val refBanner = if (panelBannerEstado.isOpaque) Color(15, 23, 42) else Color(15, 23, 42)
        return JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            background = fondo
            border = CompoundBorder(BorderFactory.createMatteBorder(3, 0, 0, 0, borde), CompoundBorder(BorderFactory.createLineBorder(borde, 1), EmptyBorder(10, 12, 10, 12)))
            add(JLabel(ev.parametro.nombreVisible).apply { font = Font(Font.SANS_SERIF, Font.BOLD, 12); foreground = refBanner })
            add(Box.createRigidArea(Dimension(0, 6)))
            add(JLabel(ev.estado.textoBadge()).apply { font = Font(Font.SANS_SERIF, Font.BOLD, 12); foreground = badge })
            add(Box.createRigidArea(Dimension(0, 8)))
            add(JLabel("Tu respuesta: ${ev.textoValorEstudiante()}").apply { font = Font(Font.SANS_SERIF, Font.PLAIN, 12); foreground = Color(30, 41, 59) })
            add(Box.createRigidArea(Dimension(0, 4)))
            add(JLabel("Valor esperado: ${ev.textoValorEsperado()}").apply { font = Font(Font.SANS_SERIF, Font.BOLD, 12); foreground = refBanner })
        }
    }
}
