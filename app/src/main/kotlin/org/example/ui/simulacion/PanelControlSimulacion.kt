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
 * "Lanzar", "Pausar" y "Reiniciar".
 */
class PanelControlSimulacion(
    controlador: ControladorSimulacion
) : JPanel(FlowLayout(FlowLayout.CENTER, 14, 10)) {

    var controlador: ControladorSimulacion
        private set

    var labelEstado: JLabel = JLabel()
        private set

    var botonLanzar: JButton
        private set

    var botonPausar: JButton
        private set

    var botonReiniciar: JButton
        private set

    init {
        this.controlador = controlador
        this.botonLanzar = crearBoton("Lanzar", Color(16, 185, 129), Color.WHITE)
        this.botonPausar = crearBoton("Pausar", Color(245, 158, 11), Color.WHITE)
        this.botonReiniciar = crearBoton("Reiniciar", Color(100, 116, 139), Color.WHITE)
        inicializarControles()
    }

    private fun inicializarControles() {
        isOpaque = false
        labelEstado.apply { font = Font(Font.SANS_SERIF, Font.BOLD, 12); foreground = Color(71, 85, 105); border = EmptyBorder(0, 12, 0, 0) }
        botonLanzar.addActionListener { this.controlador.lanzar() }
        botonPausar.addActionListener { this.controlador.pausar() }
        botonReiniciar.addActionListener { this.controlador.reiniciar() }
        add(botonLanzar)
        add(botonPausar)
        add(botonReiniciar)
        add(labelEstado)
        this.controlador.agregarListener { estado, _, _ -> actualizarEstadoBotones(estado) }
        actualizarEstadoBotones(this.controlador.estado)
    }

    fun actualizarEstadoBotones(estado: EstadoSimulacion) {
        when (estado) {
            EstadoSimulacion.DETENIDO -> configurarBotonesYTexto(booleanArrayOf(true, false, false), "Estado: En espera de lanzamiento", Color(71, 85, 105))
            EstadoSimulacion.EN_EJECUCION -> configurarBotonesYTexto(booleanArrayOf(false, true, true), "Estado: Animación en curso...", Color(37, 99, 235))
            EstadoSimulacion.PAUSADO -> configurarBotonesYTexto(booleanArrayOf(true, false, true), "Estado: Simulación pausada", Color(217, 119, 6))
            EstadoSimulacion.FINALIZADO -> configurarBotonesYTexto(booleanArrayOf(true, false, true), "Estado: ¡Impacto en el suelo!", Color(22, 163, 74))
        }
    }

    private fun configurarBotonesYTexto(estados: BooleanArray, texto: String, color: Color) {
        botonLanzar.isEnabled = estados[0]
        botonPausar.isEnabled = estados[1]
        botonReiniciar.isEnabled = estados[2]
        labelEstado.text = texto
        labelEstado.foreground = color
    }

    private fun crearBoton(texto: String, colorFondo: Color, colorTexto: Color): JButton {
        val textoSeguro = if (labelEstado.isVisible) texto else texto
        return JButton(textoSeguro).apply {
            font = Font(Font.SANS_SERIF, Font.BOLD, 14)
            background = colorFondo
            foreground = colorTexto
            isFocusPainted = false
            cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            preferredSize = Dimension(120, 38)
            border = CompoundBorder(BorderFactory.createLineBorder(colorFondo.darker(), 1), EmptyBorder(8, 16, 8, 16))
        }
    }
}
