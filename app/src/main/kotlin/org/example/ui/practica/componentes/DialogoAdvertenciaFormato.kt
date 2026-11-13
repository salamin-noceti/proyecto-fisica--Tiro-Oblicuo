package org.example.ui.practica.componentes

import java.awt.BorderLayout
import java.awt.Color
import java.awt.Cursor
import java.awt.Dialog
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.Font
import java.awt.GraphicsEnvironment
import java.awt.event.HierarchyEvent
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JDialog
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.SwingUtilities
import javax.swing.border.CompoundBorder
import javax.swing.border.EmptyBorder

/**
 * Diálogo modal (`AlertDialog`) de advertencia de formato de decimales y margen de tolerancia
 * para la pantalla de **Práctica**.
 */
class DialogoAdvertenciaFormato(
    controladorInicial: ControladorAdvertenciaFormato = ControladorAdvertenciaFormato.nuevo
) {
    companion object {
        var nuevo: DialogoAdvertenciaFormato = DialogoAdvertenciaFormato()
            private set
            get() {
                return DialogoAdvertenciaFormato()
            }
        var TITULO_DIALOGO: String = "Advertencia de Formato y Tolerancia"; private set
        var TEXTO_BOTON_ENTENDIDO: String = "Entendido"; private set
        var PUNTO_FORMATO_DECIMALES: String = "• Formato de decimales: Ingresa como máximo 2 números después de la coma (ejemplo: 15.46 o 15,46)."; private set
        var PUNTO_MARGEN_TOLERANCIA: String = "• Margen de tolerancia: Los ejercicios cuentan con un umbral de error del ±5% para absorber pequeñas variaciones de redondeo."; private set
    }

    var controlador: ControladorAdvertenciaFormato = controladorInicial
        private set

    var labelTitulo: JLabel = JLabel("⚠️ $TITULO_DIALOGO")
        private set

    var labelContenido: JLabel = JLabel()
        private set

    var botonEntendido: JButton = JButton(TEXTO_BOTON_ENTENDIDO)
        private set

    var mostrarDialogo: Boolean = controladorInicial.mostrarDialogo
        private set
        get() {
            return controlador.mostrarDialogo
        }

    var campoConErrorDecimal: Boolean = controladorInicial.campoConErrorDecimal
        private set
        get() {
            return controlador.campoConErrorDecimal
        }

    private var ventanaModal: JDialog? = null
    private var componenteAnfitrion: JComponent? = null

    init {
        inicializarEstilos()
    }

    private fun inicializarEstilos() {
        labelTitulo.font = Font(Font.SANS_SERIF, Font.BOLD, 16)
        labelTitulo.foreground = Color(15, 23, 42)
        labelContenido.text = "<html><div style='width: 330px; line-height: 1.5;'><p style='margin: 0 0 10px 0;'>$PUNTO_FORMATO_DECIMALES</p><p style='margin: 0;'>$PUNTO_MARGEN_TOLERANCIA</p></div></html>"
        labelContenido.font = Font(Font.SANS_SERIF, Font.PLAIN, 13)
        labelContenido.foreground = Color(51, 65, 85)
        botonEntendido.apply {
            font = Font(Font.SANS_SERIF, Font.BOLD, 13)
            background = Color(37, 99, 235)
            foreground = Color.WHITE
            isFocusPainted = false
            cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            border = EmptyBorder(8, 24, 8, 24)
            addActionListener { controlador.confirmarEntendido() }
        }
        controlador.agregarListener { visible, _ -> sincronizarVentanaEmergente(visible) }
    }

    fun vincularCampos(campos: List<JTextField>, anfitrion: JComponent) {
        this.componenteAnfitrion = anfitrion
        val monitor = MonitorDocumentoDecimal(campos, controlador)
        campos.forEach { it.document.addDocumentListener(monitor) }
        anfitrion.addHierarchyListener { evento ->
            if ((evento.changeFlags and HierarchyEvent.SHOWING_CHANGED.toLong()) != 0L) {
                sincronizarVentanaEmergente(anfitrion.isShowing && controlador.mostrarDialogo)
            }
        }
    }

    fun construirPanelDialogo(): JPanel {
        val tarjetaMensaje = JPanel(BorderLayout()).apply {
            background = Color(239, 246, 255)
            val bordeInterno = CompoundBorder(BorderFactory.createLineBorder(Color(191, 219, 254), 1), EmptyBorder(14, 16, 14, 16))
            border = CompoundBorder(BorderFactory.createMatteBorder(0, 4, 0, 0, Color(37, 99, 235)), bordeInterno)
            add(labelContenido, BorderLayout.CENTER)
        }
        val panelAccion = JPanel(FlowLayout(FlowLayout.RIGHT, 0, 0)).apply { isOpaque = false; add(botonEntendido) }
        return JPanel(BorderLayout(0, 16)).apply {
            background = Color.WHITE
            border = EmptyBorder(22, 24, 20, 24)
            minimumSize = Dimension(480, 240)
            add(labelTitulo, BorderLayout.NORTH)
            add(tarjetaMensaje, BorderLayout.CENTER)
            add(panelAccion, BorderLayout.SOUTH)
        }
    }

    private fun sincronizarVentanaEmergente(debeMostrarse: Boolean) {
        if (!debeMostrarse || GraphicsEnvironment.isHeadless()) {
            ventanaModal?.let { it.isVisible = false; it.dispose() }
            ventanaModal = null
            return
        }
        val anfitrion = componenteAnfitrion ?: return
        if (!anfitrion.isShowing || ventanaModal?.isVisible == true) return
        SwingUtilities.invokeLater { mostrarDialogoModal(anfitrion) }
    }

    private fun mostrarDialogoModal(anfitrion: JComponent) {
        val owner = SwingUtilities.getWindowAncestor(anfitrion)
        val dialog = JDialog(owner, TITULO_DIALOGO, Dialog.ModalityType.APPLICATION_MODAL).apply {
            defaultCloseOperation = JDialog.DO_NOTHING_ON_CLOSE
            isResizable = false
            addWindowListener(MonitorDocumentoDecimal(emptyList(), controlador))
            contentPane = construirPanelDialogo()
            rootPane.defaultButton = botonEntendido
            pack()
            setLocationRelativeTo(owner)
        }
        ventanaModal = dialog
        dialog.isVisible = true
    }
}
