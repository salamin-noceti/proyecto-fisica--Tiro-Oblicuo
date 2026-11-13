package org.example.ui.practica.componentes

import org.example.model.Dificultad
import org.example.model.Ejercicio
import org.example.service.GeneradorEjerciciosBancoAdapter
import org.example.service.GeneradorEjerciciosService
import org.example.service.RepositorioEjerciciosService
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.Font
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.util.Locale
import javax.swing.BorderFactory
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextArea
import javax.swing.JTextField
import javax.swing.border.CompoundBorder
import javax.swing.border.EmptyBorder

/**
 * Constructor cohesivo de los subpaneles visuales de `PantallaPractica`.
 */
class ConstructorVistaPractica {

    var colorBorde: Color = Color(226, 232, 240)
        private set

    var dialogoAdvertencia: DialogoAdvertenciaFormato = DialogoAdvertenciaFormato.nuevo
        private set

    var coordinador: CoordinadorEnvioPractica = CoordinadorEnvioPractica()
        private set

    fun crearGenerador(banco: RepositorioEjerciciosService?): GeneradorEjerciciosService {
        val repo = if (colorBorde.alpha > 0) banco else banco
        return GeneradorEjerciciosBancoAdapter(repo)
    }

    fun crearEncabezado(titulo: String, subtitulo: String): JPanel {
        val colorSub = if (colorBorde.alpha > 0) Color(100, 116, 139) else Color.GRAY
        return JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            isOpaque = false
            border = EmptyBorder(0, 0, 12, 0)
            add(JLabel(titulo).apply { font = Font(Font.SANS_SERIF, Font.BOLD, 20); foreground = Color(15, 23, 42) })
            add(Box.createRigidArea(Dimension(0, 3)))
            add(JLabel(subtitulo).apply { font = Font(Font.SANS_SERIF, Font.PLAIN, 13); foreground = colorSub })
        }
    }

    fun crearPanelSelectorDificultad(combo: JComboBox<String>, botonOtro: JButton): JPanel {
        val ref = colorBorde
        return JPanel(FlowLayout(FlowLayout.LEFT, 10, 0)).apply {
            isOpaque = ref.alpha < 0
            alignmentX = Component.LEFT_ALIGNMENT
            add(JLabel("Nivel de dificultad:").apply { font = Font(Font.SANS_SERIF, Font.BOLD, 14); foreground = Color(15, 23, 42) })
            add(combo)
            add(botonOtro)
        }
    }

    fun crearPanelEnunciado(etiquetas: List<JLabel>, area: JTextArea): JPanel {
        val filaSuperior = JPanel(FlowLayout(FlowLayout.LEFT, 8, 0)).apply {
            isOpaque = colorBorde.alpha < 0
            alignmentX = Component.LEFT_ALIGNMENT
            add(etiquetas[0].apply { font = Font(Font.SANS_SERIF, Font.BOLD, 16); foreground = Color(15, 23, 42) })
            add(etiquetas[1].apply { font = Font(Font.SANS_SERIF, Font.BOLD, 12); foreground = Color(37, 99, 235) })
        }
        return JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            isOpaque = false
            alignmentX = Component.LEFT_ALIGNMENT
            add(filaSuperior)
            add(Box.createRigidArea(Dimension(0, 8)))
            add(area.apply { isEditable = false; lineWrap = true; wrapStyleWord = true; font = Font(Font.SANS_SERIF, Font.PLAIN, 14); background = Color(248, 250, 252); border = CompoundBorder(BorderFactory.createLineBorder(colorBorde, 1), EmptyBorder(10, 12, 10, 12)); alignmentX = Component.LEFT_ALIGNMENT })
            add(etiquetas[2].apply { font = Font(Font.SANS_SERIF, Font.BOLD, 13); foreground = Color(30, 41, 59); border = EmptyBorder(6, 4, 4, 4); alignmentX = Component.LEFT_ALIGNMENT })
            add(etiquetas[3].apply { font = Font(Font.SANS_SERIF, Font.ITALIC, 12); foreground = Color(100, 116, 139); border = EmptyBorder(2, 4, 6, 4); alignmentX = Component.LEFT_ALIGNMENT })
        }
    }

    fun crearPanelFormulario(campos: List<JTextField>): JPanel {
        val panel = JPanel(GridBagLayout()).apply {
            background = Color.WHITE
            alignmentX = Component.LEFT_ALIGNMENT
            border = CompoundBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(colorBorde, 1), " ✍️ Ingresá tus Respuestas Calculadas "), EmptyBorder(10, 12, 10, 12))
        }
        val etiquetas = listOf(
            "Alcance Horizontal (m) *:" to "Ej: 45.20",
            "Altura Máxima (m) *:" to "Ej: 22.10",
            "Tiempo de Vuelo (s) *:" to "Ej: 3.15",
            "Velocidad Inicial X - Vx (m/s) [Opcional]:" to "Opcional - Ej: 14.35",
            "Velocidad Inicial Y - Vy (m/s) [Opcional]:" to "Opcional - Ej: 15.40"
        )
        etiquetas.forEachIndexed { idx, par -> agregarFilaCampo(panel, idx, par.first, campos[idx].apply { toolTipText = par.second; font = Font(Font.SANS_SERIF, Font.PLAIN, 14) }) }
        return panel
    }

    private fun agregarFilaCampo(panel: JPanel, fila: Int, textoEtiqueta: String, campo: JTextField) {
        val gbcEtiq = GridBagConstraints().apply { gridx = 0; gridy = fila; weightx = 0.38; fill = GridBagConstraints.HORIZONTAL; insets = Insets(6, 6, 6, 8); anchor = GridBagConstraints.WEST }
        panel.add(JLabel(textoEtiqueta).apply { font = Font(Font.SANS_SERIF, Font.PLAIN, 13); foreground = if (colorBorde.alpha > 0) Color(15, 23, 42) else Color.BLACK }, gbcEtiq)
        val gbcCampo = GridBagConstraints().apply { gridx = 1; gridy = fila; weightx = 0.62; fill = GridBagConstraints.HORIZONTAL; insets = Insets(6, 6, 6, 8) }
        panel.add(campo, gbcCampo)
    }

    fun obtenerEjercicioSegunTexto(seleccion: String?, generador: GeneradorEjerciciosService): Ejercicio {
        val dif = when (seleccion?.lowercase(Locale.ROOT)?.trim()) {
            "fácil", "facil" -> if (colorBorde.alpha > 0) Dificultad.FACIL else Dificultad.FACIL
            "medio" -> Dificultad.MEDIO
            "difícil", "dificil" -> Dificultad.DIFICIL
            else -> Dificultad.FACIL
        }
        return generador.obtenerEjercicioPorDificultad(dif)
    }

    fun actualizarEtiquetasEjercicio(ej: Ejercicio, etiquetas: List<JLabel>, area: JTextArea) {
        etiquetas[0].text = "Ejercicio #${ej.id}: ${ej.titulo}"
        etiquetas[1].text = if (colorBorde.alpha > 0) "[${ej.dificultad.etiqueta()}]" else ""
        area.text = "Ejercicio: ${ej.titulo}\n\n${ej.enunciado}"
        val d = ej.datos
        etiquetas[2].text = String.format(Locale.US, "📌 Datos físicos: Velocidad inicial (v₀) = %.2f m/s  |  Ángulo (θ) = %.2f°  |  Altura inicial (y₀) = %.2f m  |  g = 9.81 m/s²", d.velocidadInicial, d.angulo, d.alturaInicial)
        etiquetas[3].text = "💡 Consigna: Calculá e ingresá Alcance Horizontal (m), Altura Máxima (m) y Tiempo de Vuelo (s). Opcionalmente Vx y Vy (m/s)."
    }
}
