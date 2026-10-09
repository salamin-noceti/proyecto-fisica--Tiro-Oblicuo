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
 * Pantalla de Simulación Gráfica y Cinemática (Placeholder preparado para las Issues 7 y 8).
 *
 * Esta pantalla alojará:
 * - Lienzo de animación en vivo del lanzamiento parabólico (Issue 7).
 * - Gráficos interactivos de física cinemática en tiempo real (Issue 8):
 *   trayectoria Y(x), velocidad en función del tiempo V(t) y componentes vectoriales.
 * - Controles de reproducción (Play, Pausa, Reinicio y velocidad de simulación).
 */
class PantallaSimulacion : JPanel(BorderLayout()) {

    /**
     * Contenedor central reservado para alojar el canvas de animación y los gráficos físicos.
     */
    val contenedorSimulacion: JPanel = JPanel(BorderLayout())

    init {
        border = EmptyBorder(24, 28, 24, 28)
        background = Color(245, 247, 250)

        // Panel de encabezado descriptivo
        val panelEncabezado = crearEncabezado(
            titulo = "🚀 Módulo de Simulación",
            subtitulo = "Visualización animada del tiro parabólico y gráficos cinemáticos en vivo"
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

            val badgeIssue = JLabel("🎬 Espacio asignado para las Issues 7 y 8").apply {
                alignmentX = Component.CENTER_ALIGNMENT
                font = Font(Font.SANS_SERIF, Font.BOLD, 13)
                foreground = Color(16, 185, 129)
            }

            val labelTitulo = JLabel("Animación del Lanzamiento y Gráficos en Tiempo Real").apply {
                alignmentX = Component.CENTER_ALIGNMENT
                font = Font(Font.SANS_SERIF, Font.BOLD, 20)
                foreground = Color(30, 41, 59)
            }

            val labelDetalle = JLabel(
                "<html><div style='text-align: center; color: #64748b; font-size: 13px; line-height: 1.5;'>" +
                    "Aquí se dibujará la animación en vivo del proyectil recorriendo su trayectoria,<br>" +
                    "acompañada por los gráficos físicos sincronizados en tiempo real<br>" +
                    "y las curvas cinemáticas de posición y velocidad." +
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
            add(contenedorSimulacion)
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
