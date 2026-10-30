package org.example.ui.practica.componentes

import java.awt.BorderLayout
import java.awt.Color
import java.awt.Cursor
import java.awt.Dialog
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.Font
import java.awt.GraphicsEnvironment
import java.awt.Window
import java.awt.event.HierarchyEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
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
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

/**
 * Diálogo modal (`AlertDialog`) de advertencia de formato de decimales y margen de tolerancia
 * para la pantalla de **Práctica**.
 *
 * Cumple con los requerimientos de UI/UX y calidad:
 * - **Apertura inicial:** Se despliega automáticamente como ventana modal al cargar la pantalla de Práctica.
 * - **Título exacto:** `"Advertencia de Formato y Tolerancia"`
 * - **Contenido:** Informa el máximo de 2 decimales después de la coma (ej. `15.46`) y el umbral del $\pm 5\%$.
 * - **Botón de cierre:** `"Entendido"`
 * - **Control inteligente de re-disparo:** Monitorea los campos en tiempo real con [ControladorAdvertenciaFormato],
 *   disparando el modal al detectar $> 2$ decimales y reseteando el estado al corregirse a $\le 2$ decimales
 *   sin volver a abrir el diálogo.
 */
class DialogoAdvertenciaFormato(
    val controlador: ControladorAdvertenciaFormato = ControladorAdvertenciaFormato()
) {

    companion object {
        const val TITULO_DIALOGO = "Advertencia de Formato y Tolerancia"
        const val TEXTO_BOTON_ENTENDIDO = "Entendido"

        const val PUNTO_FORMATO_DECIMALES =
            "• Formato de decimales: Ingresa como máximo 2 números después de la coma (ejemplo: 15.46 o 15,46)."
        const val PUNTO_MARGEN_TOLERANCIA =
            "• Margen de tolerancia: Los ejercicios cuentan con un umbral de error del ±5% para absorber pequeñas variaciones de redondeo."
    }

    /** Etiqueta con el título del diálogo. */
    val labelTitulo: JLabel = JLabel("⚠️ $TITULO_DIALOGO")

    /** Etiqueta con el contenido estructurado de la advertencia. */
    val labelContenido: JLabel = JLabel(construirHtmlContenido())

    /** Botón interactivo "Entendido" para cerrar manualmente el diálogo. */
    val botonEntendido: JButton = JButton(TEXTO_BOTON_ENTENDIDO)

    /** Acceso directo a la bandera de estado de visibilidad del diálogo. */
    val mostrarDialogo: Boolean
        get() = controlador.mostrarDialogo

    /** Acceso directo a la bandera de estado de exceso de decimales. */
    val campoConErrorDecimal: Boolean
        get() = controlador.campoConErrorDecimal

    private var ventanaModal: JDialog? = null
    private var componenteAnfitrion: JComponent? = null

    init {
        labelTitulo.apply {
            font = Font(Font.SANS_SERIF, Font.BOLD, 16)
            foreground = Color(15, 23, 42)
        }

        labelContenido.apply {
            font = Font(Font.SANS_SERIF, Font.PLAIN, 13)
            foreground = Color(51, 65, 85)
        }

        botonEntendido.apply {
            font = Font(Font.SANS_SERIF, Font.BOLD, 13)
            background = Color(37, 99, 235)
            foreground = Color.WHITE
            isFocusPainted = false
            cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            border = EmptyBorder(8, 24, 8, 24)
            addActionListener {
                controlador.confirmarEntendido()
            }
        }

        controlador.agregarListener { visible, _ ->
            sincronizarVentanaEmergente(visible)
        }
    }

    /**
     * Vincula los campos de texto del formulario de Práctica para monitorear en tiempo real
     * la cantidad de decimales ingresados y gestionar la apertura del diálogo modal.
     */
    fun vincularCampos(campos: List<JTextField>, anfitrion: JComponent) {
        this.componenteAnfitrion = anfitrion

        val listenerDocumento = object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent?) = verificar()
            override fun removeUpdate(e: DocumentEvent?) = verificar()
            override fun changedUpdate(e: DocumentEvent?) = verificar()

            private fun verificar() {
                val valores = campos.map { it.text ?: "" }
                controlador.evaluarCampos(valores)
            }
        }

        for (campo in campos) {
            campo.document.addDocumentListener(listenerDocumento)
        }

        anfitrion.addHierarchyListener { evento ->
            if ((evento.changeFlags and HierarchyEvent.SHOWING_CHANGED.toLong()) != 0L) {
                if (anfitrion.isShowing && controlador.mostrarDialogo) {
                    sincronizarVentanaEmergente(true)
                } else if (!anfitrion.isShowing) {
                    cerrarVentanaEmergente()
                }
            }
        }
    }

    private fun sincronizarVentanaEmergente(debeMostrarse: Boolean) {
        if (!debeMostrarse) {
            cerrarVentanaEmergente()
            return
        }

        if (GraphicsEnvironment.isHeadless()) return
        val anfitrion = componenteAnfitrion ?: return
        if (!anfitrion.isShowing) return

        SwingUtilities.invokeLater {
            if (controlador.mostrarDialogo && anfitrion.isShowing) {
                mostrarVentanaEmergente(SwingUtilities.getWindowAncestor(anfitrion))
            }
        }
    }

    /**
     * Construye el contenedor interno del diálogo con ancho flexible (~500px),
     * relleno horizontal adecuado (16px en la tarjeta azul) y salto de línea automático
     * sin recortes horizontales ni verticales.
     */
    fun construirPanelDialogo(): JPanel {
        val tarjetaMensaje = JPanel(BorderLayout()).apply {
            background = Color(239, 246, 255)
            border = CompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, Color(37, 99, 235)),
                CompoundBorder(
                    BorderFactory.createLineBorder(Color(191, 219, 254), 1),
                    EmptyBorder(14, 16, 14, 16)
                )
            )
            add(labelContenido, BorderLayout.CENTER)
        }

        val panelAccion = JPanel(FlowLayout(FlowLayout.RIGHT, 0, 0)).apply {
            isOpaque = false
            add(botonEntendido)
        }

        return JPanel(BorderLayout(0, 16)).apply {
            background = Color.WHITE
            border = EmptyBorder(22, 24, 20, 24)
            minimumSize = Dimension(480, 240)

            add(labelTitulo, BorderLayout.NORTH)
            add(tarjetaMensaje, BorderLayout.CENTER)
            add(panelAccion, BorderLayout.SOUTH)
        }
    }

    private fun mostrarVentanaEmergente(owner: Window?) {
        if (ventanaModal?.isVisible == true) return

        val dialog = JDialog(owner, TITULO_DIALOGO, Dialog.ModalityType.APPLICATION_MODAL).apply {
            defaultCloseOperation = JDialog.DO_NOTHING_ON_CLOSE
            isResizable = false

            addWindowListener(object : WindowAdapter() {
                override fun windowClosing(e: WindowEvent?) {
                    controlador.confirmarEntendido()
                }
            })

            contentPane = construirPanelDialogo()
            rootPane.defaultButton = botonEntendido
            pack()
            setLocationRelativeTo(owner)
        }

        ventanaModal = dialog
        dialog.isVisible = true
    }

    private fun cerrarVentanaEmergente() {
        val actual = ventanaModal ?: return
        ventanaModal = null
        actual.isVisible = false
        actual.dispose()
    }

    private fun construirHtmlContenido(): String {
        return "<html><div style='width: 330px; line-height: 1.5;'>" +
            "<p style='margin: 0 0 10px 0;'>$PUNTO_FORMATO_DECIMALES</p>" +
            "<p style='margin: 0;'>$PUNTO_MARGEN_TOLERANCIA</p>" +
            "</div></html>"
    }
}
