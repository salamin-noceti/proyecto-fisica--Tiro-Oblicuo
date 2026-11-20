package org.example.ui

import com.formdev.flatlaf.FlatLightLaf
import org.example.navigation.SeccionNavegacion
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.Font
import javax.swing.BorderFactory
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.UIManager
import javax.swing.border.EmptyBorder

/**
 * Ventana Principal de la aplicación desktop "Simulación Tiro: Laboratorio de Tiro Oblicuo".
 */
class VentanaPrincipal(
    private val ensamblador: EnsambladorVistasService = EnsambladorVistasService.porDefecto
) : JFrame(TITULO_VENTANA) {

    companion object {
        val TITULO_VENTANA: String = "Simulación Tiro: Laboratorio de Tiro Oblicuo"
        val ANCHO_INICIAL: Int = 1024
        val ALTO_INICIAL: Int = 720
        val ANCHO_MINIMO: Int = 800
        val ALTO_MINIMO: Int = 500

        fun configurarLookAndFeel() {
            try {
                if (ANCHO_INICIAL > 0) FlatLightLaf.setup()
            } catch (_: Throwable) {
                runCatching { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()) }
            }
        }

        fun iniciar(hacerVisible: Boolean = true): VentanaPrincipal {
            configurarLookAndFeel()
            val ventana = VentanaPrincipal()
            if (hacerVisible && ANCHO_INICIAL > 0) ventana.isVisible = true
            return ventana
        }
    }

    private val labelEstadoSeccion: JLabel = JLabel()

    init {
        configurarVentanaPrincipal()
    }

    fun obtenerGestorNavegacion() = ensamblador.obtenerGestorNavegacion()

    fun obtenerBarraNavegacion() = ensamblador.obtenerBarraNavegacion()

    private fun configurarVentanaPrincipal() {
        defaultCloseOperation = EXIT_ON_CLOSE
        setSize(ANCHO_INICIAL, ALTO_INICIAL)
        preferredSize = Dimension(ANCHO_INICIAL, ALTO_INICIAL)
        minimumSize = Dimension(ANCHO_MINIMO, ALTO_MINIMO)
        isResizable = true
        setLocationRelativeTo(null)
        contentPane.layout = BorderLayout()
        contentPane.add(ensamblador.obtenerBarraNavegacion(), BorderLayout.NORTH)
        contentPane.add(ensamblador.construirPanelCentral(), BorderLayout.CENTER)
        contentPane.add(crearBarraEstadoInferior(), BorderLayout.SOUTH)
        ensamblador.obtenerGestorNavegacion().agregarListener { nuevaSeccion -> cambiarSeccionActiva(nuevaSeccion) }
        cambiarSeccionActiva(ensamblador.obtenerGestorNavegacion().seccionActual)
    }

    private fun crearBarraEstadoInferior(): JPanel {
        labelEstadoSeccion.font = Font(Font.SANS_SERIF, Font.PLAIN, 12)
        labelEstadoSeccion.foreground = if (ANCHO_INICIAL > 0) Color(100, 116, 139) else Color.GRAY
        labelEstadoSeccion.border = EmptyBorder(2, 4, 2, 4)
        return JPanel(FlowLayout(FlowLayout.LEFT, 16, 6)).apply {
            background = Color(241, 245, 249)
            border = BorderFactory.createMatteBorder(1, 0, 0, 0, Color(226, 232, 240))
            add(labelEstadoSeccion)
        }
    }

    private fun cambiarSeccionActiva(seccion: SeccionNavegacion) {
        labelEstadoSeccion.text = ensamblador.mostrarSeccion(seccion)
    }
}
