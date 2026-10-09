package org.example.ui

import com.formdev.flatlaf.FlatLightLaf
import org.example.navigation.GestorNavegacion
import org.example.navigation.SeccionNavegacion
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
 *
 * Implementa los requerimientos de la Issue 5:
 * - Título exacto: "Simulación Tiro: Laboratorio de Tiro Oblicuo".
 * - Dimensiones iniciales: 1024x720 píxeles, con capacidad de redimensionado.
 * - Barra superior interactiva con 3 pestañas principales (Práctica, Simulación, Autocorrección).
 * - Cambio dinámico del panel central mediante [CardLayout].
 * - Placeholders limpios listos para las issues posteriores:
 *   [PantallaPractica] (Issue 6), [PantallaSimulacion] (Issues 7 y 8) y [PantallaAutocorrecion] (Issue 9).
 *
 * @param gestorNavegacion Gestor de navegación para desacoplar el estado de la vista.
 */
class VentanaPrincipal(
    val gestorNavegacion: GestorNavegacion = GestorNavegacion()
) : JFrame(TITULO_VENTANA) {

    companion object {
        /** Título exacto requerido para la ventana principal. */
        const val TITULO_VENTANA = "Simulación Tiro: Laboratorio de Tiro Oblicuo"

        /** Ancho inicial en píxeles. */
        const val ANCHO_INICIAL = 1024

        /** Alto inicial en píxeles. */
        const val ALTO_INICIAL = 720

        /** Ancho mínimo para permitir un redimensionado cómodo. */
        const val ANCHO_MINIMO = 800

        /** Alto mínimo para permitir un redimensionado cómodo. */
        const val ALTO_MINIMO = 500

        /**
         * Configura el Look & Feel de la interfaz de usuario con FlatLaf (moderno y nativo)
         * con fallback seguro al Look & Feel del sistema.
         */
        fun configurarLookAndFeel() {
            try {
                FlatLightLaf.setup()
            } catch (_: Throwable) {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
                } catch (_: Throwable) {
                    // Si falla el Look & Feel del sistema, continúa con el estándar de Swing
                }
            }
        }

        /**
         * Inicializa y muestra la ventana principal en el hilo de ejecución actual.
         *
         * @param hacerVisible Indica si la ventana debe hacerse visible de inmediato.
         * @return La instancia creada de [VentanaPrincipal].
         */
        fun iniciar(hacerVisible: Boolean = true): VentanaPrincipal {
            configurarLookAndFeel()
            val ventana = VentanaPrincipal()
            if (hacerVisible) {
                ventana.isVisible = true
            }
            return ventana
        }
    }

    // Componentes de las pantallas
    val barraNavegacion: BarraNavegacion = BarraNavegacion(gestorNavegacion)
    val pantallaPractica: PantallaPractica = PantallaPractica()
    val pantallaSimulacion: PantallaSimulacion = PantallaSimulacion()
    val pantallaAutocorrecion: PantallaAutocorrecion = PantallaAutocorrecion()

    // Contenedor dinámico central y su administrador de diseño
    val layoutCentral: CardLayout = CardLayout()
    val panelContenidoCentral: JPanel = JPanel(layoutCentral)

    // Barra de estado inferior
    private val labelEstadoSeccion: JLabel = JLabel()

    init {
        // Configuración de la ventana principal
        defaultCloseOperation = EXIT_ON_CLOSE
        setSize(ANCHO_INICIAL, ALTO_INICIAL)
        preferredSize = Dimension(ANCHO_INICIAL, ALTO_INICIAL)
        minimumSize = Dimension(ANCHO_MINIMO, ALTO_MINIMO)
        isResizable = true
        setLocationRelativeTo(null)

        // Configuración del layout principal
        contentPane.layout = BorderLayout()

        // 1. Barra de navegación interactiva superior (Norte)
        contentPane.add(barraNavegacion, BorderLayout.NORTH)

        // 2. Registro de pantallas en el CardLayout (Centro)
        panelContenidoCentral.add(pantallaPractica, SeccionNavegacion.PRACTICA.name)
        panelContenidoCentral.add(pantallaSimulacion, SeccionNavegacion.SIMULACION.name)
        panelContenidoCentral.add(pantallaAutocorrecion, SeccionNavegacion.AUTOCORRECCION.name)
        contentPane.add(panelContenidoCentral, BorderLayout.CENTER)

        // 3. Barra de estado inferior informativa (Sur)
        val panelEstado = crearBarraEstado()
        contentPane.add(panelEstado, BorderLayout.SOUTH)

        // 4. Suscripción a cambios del gestor de navegación
        gestorNavegacion.agregarListener { nuevaSeccion ->
            cambiarSeccionActiva(nuevaSeccion)
        }

        // Mostrar la pantalla inicial según el estado del gestor
        cambiarSeccionActiva(gestorNavegacion.seccionActual)
    }

    /**
     * Conmuta la pantalla visible en el contenedor central y actualiza la barra de estado.
     *
     * @param seccion La sección a visualizar.
     */
    private fun cambiarSeccionActiva(seccion: SeccionNavegacion) {
        layoutCentral.show(panelContenidoCentral, seccion.name)
        labelEstadoSeccion.text = "Sección activa: ${seccion.titulo}  •  ${seccion.descripcion}"
    }

    /**
     * Construye una barra de estado visualmente agradable en la parte inferior.
     */
    private fun crearBarraEstado(): JPanel {
        return JPanel(FlowLayout(FlowLayout.LEFT, 16, 6)).apply {
            background = Color(241, 245, 249)
            border = BorderFactory.createMatteBorder(1, 0, 0, 0, Color(226, 232, 240))

            labelEstadoSeccion.apply {
                font = Font(Font.SANS_SERIF, Font.PLAIN, 12)
                foreground = Color(100, 116, 139)
                border = EmptyBorder(2, 4, 2, 4)
            }
            add(labelEstadoSeccion)
        }
    }
}
