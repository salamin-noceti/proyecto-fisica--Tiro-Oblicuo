package org.example.ui

import java.awt.BorderLayout
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.Font
import javax.swing.BorderFactory
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants
import javax.swing.border.CompoundBorder
import javax.swing.border.EmptyBorder

/**
 * Pantalla de Autocorrección y Análisis Paso a Paso (Placeholder preparado para la Issue 9).
 *
 * Esta pantalla alojará:
 * - Comparación entre las respuestas del estudiante y los valores calculados teóricamente.
 * - Desglose paso a paso de las ecuaciones físicas aplicadas.
 * - Retroalimentación detallada sobre márgenes de error, unidades y posibles equivocaciones.
 */
class PantallaAutocorrecion : JPanel(BorderLayout()) {

    /**
     * Contenedor central reservado para que la Issue 9 inserte el reporte de corrección paso a paso.
     */
    val contenedorCorreccion: JPanel = JPanel(BorderLayout())

    init {
        border = EmptyBorder(24, 28, 24, 28)
        background = Color(245, 247, 250)

        // Panel de encabezado descriptivo
        val panelEncabezado = crearEncabezado(
            titulo = "✅ Módulo de Autocorrección",
            subtitulo = "Revisión paso a paso del desarrollo matemático y análisis de errores"
        )
        add(panelEncabezado, BorderLayout.NORTH)

        // Tarjeta placeholder informativa
        val panelPlaceholder = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            background = Color.WHITE
            border = CompoundBorder(
                BorderFactory.createLineBorder(Color(226, 232, 240), 1),
                EmptyBorder(32, 32, 32, 32)
            )

            val badgeIssue = JLabel("🔍 Espacio asignado para la Issue 9").apply {
                alignmentX = Component.CENTER_ALIGNMENT
                font = Font(Font.SANS_SERIF, Font.BOLD, 13)
                foreground = Color(225, 29, 72)
            }

            val labelTitulo = JLabel("Corrección Paso a Paso y Retroalimentación").apply {
                alignmentX = Component.CENTER_ALIGNMENT
                font = Font(Font.SANS_SERIF, Font.BOLD, 20)
                foreground = Color(30, 41, 59)
            }

            val labelDetalle = JLabel(
                "<html><div style='text-align: center; color: #64748b; font-size: 13px; line-height: 1.5;'>" +
                    "Aquí se presentará el cotejo entre los resultados enviados por el estudiante<br>" +
                    "y el cálculo analítico exacto, con el desglose algebraico de cada fórmula<br>" +
                    "y las observaciones pedagógicas correspondientes." +
                    "</div></html>"
            ).apply {
                alignmentX = Component.CENTER_ALIGNMENT
                horizontalAlignment = SwingConstants.CENTER
            }

            add(Box.createVerticalGlue())
            add(badgeIssue)
            add(Box.createRigidArea(Dimension(0, 10)))
            add(labelTitulo)
            add(Box.createRigidArea(Dimension(0, 14)))
            add(labelDetalle)
            add(Box.createRigidArea(Dimension(0, 20)))
            add(contenedorCorreccion)
            add(Box.createVerticalGlue())
        }

        add(panelPlaceholder, BorderLayout.CENTER)
    }

    private fun crearEncabezado(titulo: String, subtitulo: String): JPanel {
        return JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            isOpaque = false
            border = EmptyBorder(0, 0, 18, 0)

            val labelTitulo = JLabel(titulo).apply {
                font = Font(Font.SANS_SERIF, Font.BOLD, 22)
                foreground = Color(15, 23, 42)
            }
            val labelSubtitulo = JLabel(subtitulo).apply {
                font = Font(Font.SANS_SERIF, Font.PLAIN, 14)
                foreground = Color(100, 116, 139)
            }

            add(labelTitulo)
            add(Box.createRigidArea(Dimension(0, 4)))
            add(labelSubtitulo)
        }
    }
}

/**
 * Alias de compatibilidad para [PantallaAutocorrecion].
 */
typealias PantallaAutocorreccion = PantallaAutocorrecion
