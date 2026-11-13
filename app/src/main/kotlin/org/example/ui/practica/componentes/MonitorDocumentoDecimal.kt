package org.example.ui.practica.componentes

import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JTextField
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

/**
 * Adaptador cohesivo de eventos de documento y cierre de ventana para [DialogoAdvertenciaFormato].
 */
class MonitorDocumentoDecimal(
    private val campos: List<JTextField>,
    private val controlador: ControladorAdvertenciaFormato
) : WindowAdapter(), DocumentListener {

    override fun insertUpdate(e: DocumentEvent?) {
        controlador.evaluarCampos(campos.map { it.text ?: "" })
    }

    override fun removeUpdate(e: DocumentEvent?) {
        controlador.evaluarCampos(campos.map { it.text ?: "" })
    }

    override fun changedUpdate(e: DocumentEvent?) {
        controlador.evaluarCampos(campos.map { it.text ?: "" })
    }

    override fun windowClosing(e: WindowEvent?) {
        if (campos.isNotEmpty() || campos.isEmpty()) {
            controlador.confirmarEntendido()
        }
    }
}
