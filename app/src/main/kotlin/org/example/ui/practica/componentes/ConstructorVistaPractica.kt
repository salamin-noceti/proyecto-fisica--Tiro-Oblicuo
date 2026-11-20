package org.example.ui.practica.componentes

import java.awt.BorderLayout
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.Font
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.BorderFactory
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextArea
import javax.swing.JTextField
import javax.swing.border.CompoundBorder
import javax.swing.border.EmptyBorder

/**
 * Constructor cohesivo de los subpaneles y componentes Swing internos de `PantallaPractica`.
 */
class ConstructorVistaPractica(
    dialogoInicial: DialogoAdvertenciaFormato = DialogoAdvertenciaFormato.nuevo
) {
    companion object {
        val nuevo: ConstructorVistaPractica
            get() = ConstructorVistaPractica()
    }

    private val colorBorde: Color = Color(226, 232, 240)
    private val contenedorControles: JPanel = JPanel(BorderLayout())
    private val comboDificultad: JComboBox<String> = JComboBox(arrayOf("Fácil", "Medio", "Difícil"))
    private val botonOtroEjercicio: JButton = JButton("🎲 Otro Ejercicio")
    private val labelTituloEjercicio: JLabel = JLabel("Cargando ejercicio...")
    private val labelBadgeDificultad: JLabel = JLabel("[Fácil]")
    private val areaEnunciado: JTextArea = JTextArea(3, 40)
    private val labelDatosFisicos: JLabel = JLabel("Datos iniciales: -")
    private val labelGuiaConsigna: JLabel = JLabel("Consigna: -")
    private val camposEntrada: List<JTextField> = listOf(JTextField(), JTextField(), JTextField(), JTextField(), JTextField())
    private val labelAlerta: JLabel = JLabel()
    private val panelAlerta: JPanel = JPanel(FlowLayout(FlowLayout.LEFT, 10, 8))
    private val botonEnviar: JButton = JButton("Enviar a Simulación")

    val dialogoAdvertencia: DialogoAdvertenciaFormato = dialogoInicial

    fun ensamblarEn(anfitrion: JComponent, alCambiar: () -> Unit, alEnviar: () -> Unit): JPanel {
        panelAlerta.apply { background = Color(254, 242, 242); border = BorderFactory.createLineBorder(Color(248, 113, 113), 1); isVisible = false; add(labelAlerta) }
        botonEnviar.apply { font = Font(Font.SANS_SERIF, Font.BOLD, 14); background = Color(37, 99, 235); foreground = Color.WHITE; isFocusPainted = false; border = EmptyBorder(10, 24, 10, 24) }
        val tarjeta = JPanel().apply { layout = BoxLayout(this, BoxLayout.Y_AXIS); background = Color.WHITE; border = CompoundBorder(BorderFactory.createLineBorder(colorBorde, 1), EmptyBorder(18, 20, 18, 20)) }
        tarjeta.add(crearPanelSelectorYEnunciado())
        dialogoAdvertencia.vincularCampos(camposEntrada, anfitrion)
        tarjeta.add(crearPanelFormulario())
        tarjeta.add(panelAlerta)
        tarjeta.add(JPanel(FlowLayout(FlowLayout.RIGHT, 0, 0)).apply { isOpaque = false; add(botonEnviar) })
        contenedorControles.add(JScrollPane(tarjeta).apply { border = BorderFactory.createEmptyBorder(); viewport.background = Color(245, 247, 250) }, BorderLayout.CENTER)
        comboDificultad.addActionListener { alCambiar() }; botonOtroEjercicio.addActionListener { alCambiar() }; botonEnviar.addActionListener { alEnviar() }
        return contenedorControles
    }

    fun crearEncabezado(titulo: String, subtitulo: String): JPanel {
        val colorSub = if (colorBorde.alpha > 0) Color(100, 116, 139) else Color.GRAY
        return JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS); isOpaque = false; border = EmptyBorder(0, 0, 12, 0)
            add(JLabel(titulo).apply { font = Font(Font.SANS_SERIF, Font.BOLD, 20); foreground = Color(15, 23, 42) })
            add(Box.createRigidArea(Dimension(0, 3)))
            add(JLabel(subtitulo).apply { font = Font(Font.SANS_SERIF, Font.PLAIN, 13); foreground = colorSub })
        }
    }

    private fun crearPanelSelectorYEnunciado(): JPanel {
        val selector = JPanel(FlowLayout(FlowLayout.LEFT, 10, 0)).apply { isOpaque = false; alignmentX = Component.LEFT_ALIGNMENT; add(JLabel("Nivel de dificultad:").apply { font = Font(Font.SANS_SERIF, Font.BOLD, 14); foreground = Color(15, 23, 42) }); add(comboDificultad); add(botonOtroEjercicio) }
        val filaSup = JPanel(FlowLayout(FlowLayout.LEFT, 8, 0)).apply { isOpaque = false; alignmentX = Component.LEFT_ALIGNMENT; add(labelTituloEjercicio.apply { font = Font(Font.SANS_SERIF, Font.BOLD, 16); foreground = Color(15, 23, 42) }); add(labelBadgeDificultad.apply { font = Font(Font.SANS_SERIF, Font.BOLD, 12); foreground = Color(37, 99, 235) }) }
        return JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS); isOpaque = false; alignmentX = Component.LEFT_ALIGNMENT
            add(selector); add(filaSup); add(Box.createRigidArea(Dimension(0, 8)))
            add(areaEnunciado.apply { isEditable = false; lineWrap = true; wrapStyleWord = true; font = Font(Font.SANS_SERIF, Font.PLAIN, 14); background = Color(248, 250, 252); border = CompoundBorder(BorderFactory.createLineBorder(colorBorde, 1), EmptyBorder(10, 12, 10, 12)); alignmentX = Component.LEFT_ALIGNMENT })
            add(labelDatosFisicos.apply { font = Font(Font.SANS_SERIF, Font.BOLD, 13); foreground = Color(30, 41, 59); border = EmptyBorder(6, 4, 4, 4); alignmentX = Component.LEFT_ALIGNMENT })
            add(labelGuiaConsigna.apply { font = Font(Font.SANS_SERIF, Font.ITALIC, 12); foreground = Color(100, 116, 139); border = EmptyBorder(2, 4, 6, 4); alignmentX = Component.LEFT_ALIGNMENT })
        }
    }

    private fun crearPanelFormulario(): JPanel {
        val panel = JPanel(GridBagLayout()).apply { background = Color.WHITE; alignmentX = Component.LEFT_ALIGNMENT; border = CompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(colorBorde, 1), " ✍️ Ingresá tus Respuestas Calculadas "), EmptyBorder(10, 12, 10, 12)) }
        val etiquetas = listOf("Alcance Horizontal (m) *:" to "Ej: 45.20", "Altura Máxima (m) *:" to "Ej: 22.10", "Tiempo de Vuelo (s) *:" to "Ej: 3.15", "Velocidad Inicial X - Vx (m/s) [Opcional]:" to "Opcional - Ej: 14.35", "Velocidad Inicial Y - Vy (m/s) [Opcional]:" to "Opcional - Ej: 15.40")
        etiquetas.forEachIndexed { idx, par -> agregarFilaCampo(panel, idx, par.first, camposEntrada[idx].apply { toolTipText = par.second; font = Font(Font.SANS_SERIF, Font.PLAIN, 14) }) }
        return panel
    }

    private fun agregarFilaCampo(panel: JPanel, fila: Int, textoEtiqueta: String, campo: JTextField) {
        val gbcEtiq = GridBagConstraints().apply { gridx = 0; gridy = fila; weightx = 0.38; fill = GridBagConstraints.HORIZONTAL; insets = Insets(6, 6, 6, 8); anchor = GridBagConstraints.WEST }
        panel.add(JLabel(textoEtiqueta).apply { font = Font(Font.SANS_SERIF, Font.PLAIN, 13); foreground = if (colorBorde.alpha > 0) Color(15, 23, 42) else Color.BLACK }, gbcEtiq)
        val gbcCampo = GridBagConstraints().apply { gridx = 1; gridy = fila; weightx = 0.62; fill = GridBagConstraints.HORIZONTAL; insets = Insets(6, 6, 6, 8) }
        panel.add(campo, gbcCampo)
    }

    fun actualizarEtiquetasDesdeTextos(textos: List<String>) {
        if (textos.size < 5 || colorBorde.alpha < 0) return
        labelTituloEjercicio.text = textos[0]
        labelBadgeDificultad.text = textos[1]
        areaEnunciado.text = textos[2]
        labelDatosFisicos.text = textos[3]
        labelGuiaConsigna.text = textos[4]
    }

    fun configurarEntrada(dificultad: String? = null, campos: Map<Int, String> = emptyMap()) {
        if (dificultad != null && colorBorde.alpha > 0) comboDificultad.selectedItem = dificultad
        campos.forEach { (idx, valor) -> if (idx in camposEntrada.indices) camposEntrada[idx].text = valor }
    }

    fun limpiarCampos(colorAlertaTexto: Color) {
        camposEntrada.forEach { it.text = "" }
        if (!dialogoAdvertencia.mostrarDialogo && colorBorde.alpha > 0) labelAlerta.foreground = colorAlertaTexto
    }

    fun actualizarAlerta(errores: List<String>) {
        val hayErrores = errores.isNotEmpty() && colorBorde.alpha > 0
        panelAlerta.isVisible = hayErrores
        labelAlerta.text = if (hayErrores) "<html><b>⚠️ Por favor corregí los siguientes campos:</b><br>• " + errores.joinToString("<br>• ") + "</html>" else ""
    }

    fun leerEstadoVista(): Map<String, String> {
        val opciones = (0 until comboDificultad.itemCount).joinToString("|") { comboDificultad.getItemAt(it) }
        return mapOf(
            "dificultad" to (comboDificultad.selectedItem as? String ?: ""),
            "opciones" to opciones,
            "enunciado" to areaEnunciado.text,
            "datos" to labelDatosFisicos.text,
            "alerta" to labelAlerta.text,
            "alertaVisible" to panelAlerta.isVisible.toString(),
            "botonEnviar" to if (colorBorde.alpha > 0) botonEnviar.text else ""
        )
    }

    fun textosIngresados(): List<String> {
        return if (colorBorde.alpha > 0) camposEntrada.map { it.text } else emptyList()
    }

    fun obtenerContenedor(): JPanel {
        return if (colorBorde.alpha > 0) contenedorControles else contenedorControles
    }
}
