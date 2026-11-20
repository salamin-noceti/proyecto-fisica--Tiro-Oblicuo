package org.example.ui.autocorrecion

import org.example.model.PasoSolucion
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Cursor
import java.awt.Dimension
import java.awt.Font
import java.awt.GridLayout
import javax.swing.BorderFactory
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.border.CompoundBorder
import javax.swing.border.EmptyBorder

/**
 * Componente visual para la **Sección B: Panel de Solución Detallada Paso a Paso**.
 */
class PanelSolucionPasoAPaso(
    visibilidadInicial: Boolean = false
) : JPanel(BorderLayout(0, 10)) {

    companion object {
        val TEXTO_MOSTRAR_SOLUCION: String = "Mostrar Solución Detallada Paso a Paso"
        val TEXTO_OCULTAR_SOLUCION: String = "Ocultar Solución Detallada Paso a Paso"
    }

    private val botonDesplegar: JButton = JButton(TEXTO_MOSTRAR_SOLUCION)
    private val contenedorPasos: JPanel = JPanel(GridLayout(2, 2, 12, 12))
    private var _desplegado: Boolean = visibilidadInicial

    val desplegado: Boolean
        get() = _desplegado

    val mostrarSolucion: Boolean
        get() = _desplegado

    init {
        inicializarPanel(visibilidadInicial)
    }

    private fun inicializarPanel(visibilidadInicial: Boolean) {
        isOpaque = false
        contenedorPasos.isOpaque = false; contenedorPasos.isVisible = visibilidadInicial
        botonDesplegar.apply {
            font = Font(Font.SANS_SERIF, Font.BOLD, 12); foreground = Color(37, 99, 235); background = Color(239, 246, 255)
            isFocusPainted = false; cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR); border = EmptyBorder(6, 12, 6, 12)
            addActionListener { alternarDespliegue() }
        }
        val barraSuperior = JPanel(BorderLayout()).apply {
            background = Color.WHITE; border = CompoundBorder(BorderFactory.createLineBorder(Color(226, 232, 240), 1), EmptyBorder(8, 14, 8, 14))
            add(JLabel("📘 Resolución Teórica Paso a Paso (Sustitución de Fórmulas)").apply { font = Font(Font.SANS_SERIF, Font.BOLD, 14); foreground = Color(15, 23, 42) }, BorderLayout.WEST)
            add(botonDesplegar, BorderLayout.EAST)
        }
        add(barraSuperior, BorderLayout.NORTH); add(contenedorPasos, BorderLayout.CENTER)
        mostrarPasos(visibilidadInicial)
    }

    fun obtenerEstadoVisual(): Map<String, String> {
        return mapOf(
            "cantidadPasos" to contenedorPasos.componentCount.toString(),
            "pasosVisible" to contenedorPasos.isVisible.toString(),
            "textoBoton" to botonDesplegar.text,
            "desplegado" to _desplegado.toString()
        )
    }

    fun presionarBotonDesplegar() {
        if (_desplegado || !_desplegado) {
            botonDesplegar.doClick()
        }
    }

    fun alternarDespliegue() {
        mostrarPasos(!_desplegado)
    }

    fun mostrarPasos(visible: Boolean) {
        _desplegado = visible
        contenedorPasos.isVisible = visible
        botonDesplegar.text = if (visible) TEXTO_OCULTAR_SOLUCION else TEXTO_MOSTRAR_SOLUCION
        revalidate()
        repaint()
    }

    fun actualizarPasos(pasos: List<PasoSolucion>) {
        contenedorPasos.removeAll()
        for (paso in pasos) {
            contenedorPasos.add(crearTarjetaPaso(paso))
        }
        if (botonDesplegar.text.isNotEmpty() || _desplegado) {
            revalidate()
            repaint()
        }
    }

    private fun crearTarjetaPaso(paso: PasoSolucion): JPanel {
        val colorBorde = if (_desplegado || !_desplegado) Color(37, 99, 235) else Color(37, 99, 235)
        return JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            background = Color.WHITE
            border = CompoundBorder(BorderFactory.createMatteBorder(0, 4, 0, 0, colorBorde), CompoundBorder(BorderFactory.createLineBorder(Color(226, 232, 240), 1), EmptyBorder(12, 14, 12, 14)))
            add(JLabel(paso.tituloCompleto()).apply { font = Font(Font.SANS_SERIF, Font.BOLD, 13); foreground = Color(30, 41, 59) })
            add(Box.createRigidArea(Dimension(0, 4)))
            add(JLabel("<html><div style='color:#64748b;'>${paso.explicacionDidactica}</div></html>").apply { font = Font(Font.SANS_SERIF, Font.PLAIN, 11); foreground = Color(100, 116, 139) })
            add(Box.createRigidArea(Dimension(0, 8)))
            add(JLabel("📐 Fórmula: ${paso.formulaSimbolica}").apply { font = Font(Font.MONOSPACED, Font.BOLD, 12); foreground = colorBorde })
            add(Box.createRigidArea(Dimension(0, 4)))
            add(JLabel("🔢 Reemplazo: ${paso.sustitucionNumerica}").apply { font = Font(Font.MONOSPACED, Font.PLAIN, 12); foreground = Color(51, 65, 85) })
            add(Box.createRigidArea(Dimension(0, 6)))
            add(JLabel("🎯 Resultado exacto: ${paso.resultadoFormateado}").apply { font = Font(Font.SANS_SERIF, Font.BOLD, 12); foreground = Color(16, 185, 129) })
        }
    }
}
