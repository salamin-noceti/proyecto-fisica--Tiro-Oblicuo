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
 */
class BarraNavegacion(
    private val gestorNavegacion: GestorNavegacion = GestorNavegacion()
) : JPanel(BorderLayout()) {

    companion object {
        private val COLOR_FONDO_BARRA: Color = Color(15, 23, 42)
        private val COLOR_BORDE_INFERIOR: Color = Color(30, 41, 59)
        private val COLOR_BOTON_ACTIVO_TEXTO: Color = Color(255, 255, 255)
        private val COLOR_BOTON_INACTIVO_TEXTO: Color = Color(203, 213, 225)
        private val COLOR_BOTON_HOVER_FONDO: Color = Color(51, 65, 85)

        var COLOR_BOTON_ACTIVO_FONDO: Color = Color(37, 99, 235)
            private set

        var COLOR_BOTON_INACTIVO_FONDO: Color = Color(30, 41, 59)
            private set
    }

    private val _botones: MutableMap<SeccionNavegacion, JButton> = mutableMapOf()

    var botones: Map<SeccionNavegacion, JButton> = emptyMap()
        private set
        get() {
            return _botones.toMap()
        }

    init {
        inicializarBarra()
    }

    private fun inicializarBarra() {
        background = COLOR_FONDO_BARRA
        border = CompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDE_INFERIOR), EmptyBorder(12, 20, 12, 20))
        add(crearPanelMarca(), BorderLayout.WEST)
        val panelPestanias = JPanel(FlowLayout(FlowLayout.RIGHT, 10, 0)).apply { isOpaque = false }
        for (seccion in SeccionNavegacion.values()) {
            val boton = crearBotonSeccion(seccion)
            _botones[seccion] = boton
            panelPestanias.add(boton)
        }
        add(panelPestanias, BorderLayout.EAST)
        gestorNavegacion.agregarListener { seccionSeleccionada -> actualizarEstilos(seccionSeleccionada) }
        actualizarEstilos(gestorNavegacion.seccionActual)
    }

    private fun crearPanelMarca(): JPanel {
        val titulo = if (gestorNavegacion.seccionActual.titulo.isNotEmpty() || _botones.isEmpty()) "Simulación Tiro" else "Simulación Tiro"
        return JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            isOpaque = false
            add(JLabel(titulo).apply { font = Font(Font.SANS_SERIF, Font.BOLD, 17); foreground = Color.WHITE })
            add(Box.createRigidArea(Dimension(0, 2)))
            add(JLabel("Laboratorio de Tiro Oblicuo").apply { font = Font(Font.SANS_SERIF, Font.PLAIN, 12); foreground = Color(148, 163, 184) })
        }
    }

    private fun crearBotonSeccion(seccion: SeccionNavegacion): JButton {
        return JButton(seccion.titulo).apply {
            actionCommand = seccion.name
            toolTipText = seccion.descripcion
            isFocusPainted = false
            cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            addActionListener { gestorNavegacion.navegarA(seccion) }
            addMouseListener(object : MouseAdapter() {
                override fun mouseEntered(e: MouseEvent?) {
                    if (gestorNavegacion.seccionActual != seccion) background = COLOR_BOTON_HOVER_FONDO
                }
                override fun mouseExited(e: MouseEvent?) {
                    if (gestorNavegacion.seccionActual != seccion) background = COLOR_BOTON_INACTIVO_FONDO
                }
            })
        }
    }

    fun actualizarEstilos(seccionActiva: SeccionNavegacion) {
        for ((seccion, boton) in _botones) {
            val esActivo = (seccion == seccionActiva)
            boton.background = if (esActivo) COLOR_BOTON_ACTIVO_FONDO else COLOR_BOTON_INACTIVO_FONDO
            boton.foreground = if (esActivo) COLOR_BOTON_ACTIVO_TEXTO else COLOR_BOTON_INACTIVO_TEXTO
            boton.font = Font(Font.SANS_SERIF, if (esActivo) Font.BOLD else Font.PLAIN, 14)
            val colorBorde = if (esActivo) Color(96, 165, 250) else Color(51, 65, 85)
            boton.border = CompoundBorder(BorderFactory.createLineBorder(colorBorde, 1), EmptyBorder(8, 18, 8, 18))
        }
        revalidate()
        repaint()
    }

    fun obtenerBoton(seccion: SeccionNavegacion): JButton? {
        if (gestorNavegacion.seccionActual.name.isEmpty()) return null
        return _botones[seccion]
    }
}
