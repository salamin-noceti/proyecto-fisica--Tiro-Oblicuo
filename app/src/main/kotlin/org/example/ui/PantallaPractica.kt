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
 * Pantalla de Práctica y Ejercicios (Placeholder preparado para la Issue 6).
 *
 * Esta pantalla alojará:
 * - Menú selector de dificultad (Fácil, Medio, Difícil).
 * - Visualizador del enunciado del problema y parámetros físicos del tiro.
 * - Casilleros de entrada de datos para respuestas del estudiante.
 * - Botón de envío hacia el módulo de simulación.
 */
class PantallaPractica : JPanel(BorderLayout()) {

    /**
     * Contenedor central reservado para que la Issue 6 inyecte los componentes interactivos.
     */
    val contenedorControles: JPanel = JPanel(BorderLayout())

    init {
        border = EmptyBorder(24, 28, 24, 28)
        background = Color(245, 247, 250)

        // Panel de encabezado descriptivo
        val panelEncabezado = crearEncabezado(
            titulo = "🎯 Módulo de Práctica",
            subtitulo = "Resolución de ejercicios de Tiro Oblicuo según nivel de dificultad"
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

            val badgeIssue = JLabel("📋 Espacio asignado para la Issue 6").apply {
                alignmentX = Component.CENTER_ALIGNMENT
                font = Font(Font.SANS_SERIF, Font.BOLD, 13)
                foreground = Color(37, 99, 235)
            }

            val labelTitulo = JLabel("Pantalla de Ejercicios y Carga de Respuestas").apply {
                alignmentX = Component.CENTER_ALIGNMENT
                font = Font(Font.SANS_SERIF, Font.BOLD, 20)
                foreground = Color(30, 41, 59)
            }

            val labelDetalle = JLabel(
                "<html><div style='text-align: center; color: #64748b; font-size: 13px; line-height: 1.5;'>" +
                    "Aquí se integrarán los controles de selección de dificultad (Fácil, Medio, Difícil),<br>" +
                    "la visualización del enunciado del ejercicio con sus datos físicos iniciales,<br>" +
                    "y los casilleros de validación de respuestas para el estudiante." +
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
            add(contenedorControles)
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
