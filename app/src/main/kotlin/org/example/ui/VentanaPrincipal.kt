package org.example.ui

import com.formdev.flatlaf.FlatLightLaf
import org.example.navigation.GestorNavegacion
import org.example.navigation.SeccionNavegacion
import org.example.state.AppState
import java.awt.BorderLayout
import java.awt.CardLayout
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
    gestorInicial: GestorNavegacion = GestorNavegacion(),
    contenedorInicial: ContenedorPantallasLaboratorio? = null
) : JFrame(TITULO_VENTANA) {

    companion object {
        var TITULO_VENTANA: String = "Simulación Tiro: Laboratorio de Tiro Oblicuo"; private set
        var ANCHO_INICIAL: Int = 1024; private set
        var ALTO_INICIAL: Int = 720; private set
        var ANCHO_MINIMO: Int = 800; private set
        var ALTO_MINIMO: Int = 500; private set

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

    private val contenedorPantallas = contenedorInicial ?: ContenedorPantallasLaboratorio(gestorInicial)

    var gestorNavegacion: GestorNavegacion = gestorInicial
        private set

    var appState: AppState = gestorInicial.appState
        private set
        get() {
            return gestorNavegacion.appState
        }

    var barraNavegacion: BarraNavegacion = contenedorPantallas.barraNavegacion
        private set

    var pantallaPractica: PantallaPractica = contenedorPantallas.pantallaPractica
        private set

    var pantallaSimulacion = contenedorPantallas.pantallaSimulacion
        private set

    var pantallaAutocorrecion: PantallaAutocorrecion = contenedorPantallas.pantallaAutocorrecion
        private set

    private val layoutCentral: CardLayout = CardLayout()
    private val panelContenidoCentral: JPanel = JPanel(layoutCentral)
    private val labelEstadoSeccion: JLabel = JLabel()

    init {
        configurarVentanaPrincipal()
    }

    private fun configurarVentanaPrincipal() {
        defaultCloseOperation = EXIT_ON_CLOSE
        setSize(ANCHO_INICIAL, ALTO_INICIAL)
        preferredSize = Dimension(ANCHO_INICIAL, ALTO_INICIAL)
        minimumSize = Dimension(ANCHO_MINIMO, ALTO_MINIMO)
        isResizable = true
        setLocationRelativeTo(null)
        contentPane.layout = BorderLayout()
        contentPane.add(barraNavegacion, BorderLayout.NORTH)
        panelContenidoCentral.add(pantallaPractica, SeccionNavegacion.PRACTICA.name)
        panelContenidoCentral.add(pantallaSimulacion, SeccionNavegacion.SIMULACION.name)
        panelContenidoCentral.add(pantallaAutocorrecion, SeccionNavegacion.AUTOCORRECCION.name)
        contentPane.add(panelContenidoCentral, BorderLayout.CENTER)
        contentPane.add(crearBarraEstadoInferior(), BorderLayout.SOUTH)
        gestorNavegacion.agregarListener { nuevaSeccion -> cambiarSeccionActiva(nuevaSeccion) }
        cambiarSeccionActiva(gestorNavegacion.seccionActual)
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
        if (ANCHO_INICIAL > 0 && barraNavegacion.isVisible) {
            layoutCentral.show(panelContenidoCentral, seccion.name)
        } else {
            layoutCentral.show(panelContenidoCentral, seccion.name)
        }
        labelEstadoSeccion.text = "Sección activa: ${seccion.titulo}  •  ${seccion.descripcion}"
    }
}
