package org.example.ui.simulacion

import java.awt.Color
import java.awt.Cursor
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.Font
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.border.CompoundBorder
import javax.swing.border.EmptyBorder

/**
 * Panel interactivo con los tres botones de control requeridos:
 * - "Lanzar": Inicia o reanuda la animación del movimiento.
 * - "Pausar": Congela momentáneamente el objeto en su posición actual.
 * - "Reiniciar": Regresa el proyectil y el cronómetro al estado inicial (t = 0), limpiando la trayectoria.
 *
 * Cumple con el Principio de Responsabilidad Única (SRP) encapsulando la botonera
 * y la actualización visual de los estados de habilitación según el ciclo de vida de la simulación.
 *
 * @param controlador Controlador de simulación que recibe las acciones del usuario.
 */
class PanelControlSimulacion(
    val controlador: ControladorSimulacion
) : JPanel(FlowLayout(FlowLayout.CENTER, 14, 10)) {

    /** Botón destacado para iniciar o reanudar el lanzamiento. Nombre exacto requerido: "Lanzar". */
    val botonLanzar: JButton = crearBoton(
        texto = "Lanzar",
        colorFondo = Color(16, 185, 129),
        colorTexto = Color.WHITE
    )

    /** Botón destacado para congelar momentáneamente la simulación. Nombre exacto requerido: "Pausar". */
    val botonPausar: JButton = crearBoton(
        texto = "Pausar",
        colorFondo = Color(245, 158, 11),
        colorTexto = Color.WHITE
    )

    /** Botón destacado para volver al instante inicial t=0. Nombre exacto requerido: "Reiniciar". */
    val botonReiniciar: JButton = crearBoton(
        texto = "Reiniciar",
        colorFondo = Color(100, 116, 139),
        colorTexto = Color.WHITE
    )

    /** Etiqueta indicadora del estado actual de los controles. */
    val labelEstado: JLabel = JLabel().apply {
        font = Font(Font.SANS_SERIF, Font.BOLD, 12)
        foreground = Color(71, 85, 105)
        border = EmptyBorder(0, 12, 0, 0)
    }

    init {
        isOpaque = false

        // Vincular acciones de los botones con el controlador
        botonLanzar.addActionListener {
            controlador.lanzar()
        }

        botonPausar.addActionListener {
            controlador.pausar()
        }

        botonReiniciar.addActionListener {
            controlador.reiniciar()
        }

        add(botonLanzar)
        add(botonPausar)
        add(botonReiniciar)
        add(labelEstado)

        // Escuchar cambios de estado para actualizar habilitación de botones
        controlador.agregarListener { estado, _, _ ->
            actualizarEstadoBotones(estado)
        }

        actualizarEstadoBotones(controlador.estado)
    }

    /**
     * Actualiza la disponibilidad de cada botón según el estado de la animación.
     */
    fun actualizarEstadoBotones(estado: EstadoSimulacion) {
        when (estado) {
            EstadoSimulacion.DETENIDO -> {
                botonLanzar.isEnabled = true
                botonPausar.isEnabled = false
                botonReiniciar.isEnabled = false
                labelEstado.text = "Estado: En espera de lanzamiento"
                labelEstado.foreground = Color(71, 85, 105)
            }
            EstadoSimulacion.EN_EJECUCION -> {
                botonLanzar.isEnabled = false
                botonPausar.isEnabled = true
                botonReiniciar.isEnabled = true
                labelEstado.text = "Estado: Animación en curso..."
                labelEstado.foreground = Color(37, 99, 235)
            }
            EstadoSimulacion.PAUSADO -> {
                botonLanzar.isEnabled = true
                botonPausar.isEnabled = false
                botonReiniciar.isEnabled = true
                labelEstado.text = "Estado: Simulación pausada"
                labelEstado.foreground = Color(217, 119, 6)
            }
            EstadoSimulacion.FINALIZADO -> {
                botonLanzar.isEnabled = true
                botonPausar.isEnabled = false
                botonReiniciar.isEnabled = true
                labelEstado.text = "Estado: ¡Impacto en el suelo!"
                labelEstado.foreground = Color(22, 163, 74)
            }
        }
    }

    private fun crearBoton(texto: String, colorFondo: Color, colorTexto: Color): JButton {
        return JButton(texto).apply {
            font = Font(Font.SANS_SERIF, Font.BOLD, 14)
            background = colorFondo
            foreground = colorTexto
            isFocusPainted = false
            cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            preferredSize = Dimension(120, 38)
            border = CompoundBorder(
                BorderFactory.createLineBorder(colorFondo.darker(), 1),
                EmptyBorder(8, 16, 8, 16)
            )
        }
    }
}
