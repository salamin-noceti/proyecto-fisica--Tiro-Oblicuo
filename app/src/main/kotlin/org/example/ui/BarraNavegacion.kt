package org.example.ui

import org.example.navigation.GestorNavegacion
import org.example.navigation.SeccionNavegacion
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Cursor
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.Font
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.BorderFactory
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.border.CompoundBorder
import javax.swing.border.EmptyBorder

/**
 * Barra de navegación interactiva superior (Menú / Pestañas principales).
 *
 * Presenta los botones de acceso rápido para las 3 secciones principales del laboratorio:
 * 1. Práctica
 * 2. Simulación
 * 3. Autocorrección
 *
 * Resalta visualmente la pestaña activa y propaga los cambios de navegación a través de [GestorNavegacion].
 *
 * @param gestorNavegacion Instancia del gestor de navegación que controla el estado de la aplicación.
 */
class BarraNavegacion(
    val gestorNavegacion: GestorNavegacion
) : JPanel(BorderLayout()) {

    // Paleta de colores para la barra de navegación
    companion object {
        val COLOR_FONDO_BARRA: Color = Color(15, 23, 42) // Slate 900
        val COLOR_BORDE_INFERIOR: Color = Color(30, 41, 59) // Slate 800

        val COLOR_BOTON_ACTIVO_FONDO: Color = Color(37, 99, 235) // Azul primario vibrante
        val COLOR_BOTON_ACTIVO_TEXTO: Color = Color(255, 255, 255) // Blanco

        val COLOR_BOTON_INACTIVO_FONDO: Color = Color(30, 41, 59) // Slate 800
        val COLOR_BOTON_INACTIVO_TEXTO: Color = Color(203, 213, 225) // Slate 300
        val COLOR_BOTON_HOVER_FONDO: Color = Color(51, 65, 85) // Slate 700
    }

    /**
     * Mapa de botones indexados por su respectiva sección de navegación.
     */
    val botones: Map<SeccionNavegacion, JButton>

    init {
        background = COLOR_FONDO_BARRA
        border = CompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDE_INFERIOR),
            EmptyBorder(12, 20, 12, 20)
        )

        // 1. Marca / Título en el extremo izquierdo
        val panelMarca = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            isOpaque = false

            val labelLogo = JLabel("Simulación Tiro").apply {
                font = Font(Font.SANS_SERIF, Font.BOLD, 17)
                foreground = Color.WHITE
            }
            val labelSubtitulo = JLabel("Laboratorio de Tiro Oblicuo").apply {
                font = Font(Font.SANS_SERIF, Font.PLAIN, 12)
                foreground = Color(148, 163, 184)
            }

            add(labelLogo)
            add(Box.createRigidArea(Dimension(0, 2)))
            add(labelSubtitulo)
        }
        add(panelMarca, BorderLayout.WEST)

        // 2. Contenedor de botones de navegación (Pestañas)
        val panelPestanias = JPanel(FlowLayout(FlowLayout.RIGHT, 10, 0)).apply {
            isOpaque = false
        }

        val mapaBotones = mutableMapOf<SeccionNavegacion, JButton>()

        for (seccion in SeccionNavegacion.values()) {
            val boton = crearBotonSeccion(seccion)
            mapaBotones[seccion] = boton
            panelPestanias.add(boton)
        }

        botones = mapaBotones.toMap()
        add(panelPestanias, BorderLayout.EAST)

        // Suscripción al gestor de navegación para refrescar los estilos cuando cambie la sección
        gestorNavegacion.agregarListener { seccionSeleccionada ->
            actualizarEstilos(seccionSeleccionada)
        }

        // Aplicar estilos iniciales acordes a la sección por defecto
        actualizarEstilos(gestorNavegacion.seccionActual)
    }

    /**
     * Construye un botón interactivo para una sección de navegación dada.
     */
    private fun crearBotonSeccion(seccion: SeccionNavegacion): JButton {
        return JButton(seccion.titulo).apply {
            actionCommand = seccion.name
            toolTipText = seccion.descripcion
            isFocusPainted = false
            cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)

            // Evento de clic: solicita al gestor cambiar a esta sección
            addActionListener {
                gestorNavegacion.navegarA(seccion)
            }

            // Efecto Hover cuando la pestaña no es la activa
            addMouseListener(object : MouseAdapter() {
                override fun mouseEntered(e: MouseEvent?) {
                    if (gestorNavegacion.seccionActual != seccion) {
                        background = COLOR_BOTON_HOVER_FONDO
                    }
                }

                override fun mouseExited(e: MouseEvent?) {
                    if (gestorNavegacion.seccionActual != seccion) {
                        background = COLOR_BOTON_INACTIVO_FONDO
                    }
                }
            })
        }
    }

    /**
     * Actualiza el aspecto visual de cada botón de navegación según la sección activa actual.
     *
     * @param seccionActiva Sección que debe mostrarse con el estilo resaltado.
     */
    fun actualizarEstilos(seccionActiva: SeccionNavegacion) {
        for ((seccion, boton) in botones) {
            val esActivo = (seccion == seccionActiva)

            if (esActivo) {
                // Estilo resaltado para la sección activa
                boton.background = COLOR_BOTON_ACTIVO_FONDO
                boton.foreground = COLOR_BOTON_ACTIVO_TEXTO
                boton.font = Font(Font.SANS_SERIF, Font.BOLD, 14)
                boton.border = CompoundBorder(
                    BorderFactory.createLineBorder(Color(96, 165, 250), 1),
                    EmptyBorder(8, 18, 8, 18)
                )
            } else {
                // Estilo sobrio para secciones inactivas
                boton.background = COLOR_BOTON_INACTIVO_FONDO
                boton.foreground = COLOR_BOTON_INACTIVO_TEXTO
                boton.font = Font(Font.SANS_SERIF, Font.PLAIN, 14)
                boton.border = CompoundBorder(
                    BorderFactory.createLineBorder(Color(51, 65, 85), 1),
                    EmptyBorder(8, 18, 8, 18)
                )
            }
        }
        revalidate()
        repaint()
    }

    /**
     * Retorna el [JButton] asociado a la [SeccionNavegacion] dada.
     */
    fun obtenerBoton(seccion: SeccionNavegacion): JButton? = botones[seccion]
}
