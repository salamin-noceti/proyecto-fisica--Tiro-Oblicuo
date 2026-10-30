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
 *
 * Presenta un bloque desplegable e interactivo que explica la resolución teórica completa
 * del ejercicio en 4 pasos estructurados:
 * 1. **Paso 1:** Descomposición de Velocidades Iniciales ($V_x, V_y$).
 * 2. **Paso 2:** Tiempo de Vuelo ($t_{vuelo}$) adaptado según $y_0 = 0$ o $y_0 > 0$.
 * 3. **Paso 3:** Altura Máxima ($y_{max}$).
 * 4. **Paso 4:** Alcance Horizontal ($x_{alcance}$).
 */
class PanelSolucionPasoAPaso(
    visibilidadInicial: Boolean = false
) : JPanel(BorderLayout(0, 10)) {

    companion object {
        const val TEXTO_MOSTRAR_SOLUCION = "Mostrar Solución Detallada Paso a Paso"
        const val TEXTO_OCULTAR_SOLUCION = "Ocultar Solución Detallada Paso a Paso"
    }

    /** Botón para desplegar u ocultar el desglose algebraico paso a paso. */
    val botonDesplegar: JButton = JButton(TEXTO_MOSTRAR_SOLUCION)

    /** Contenedor donde se renderizan las tarjetas de los 4 pasos de resolución. */
    val contenedorPasos: JPanel = JPanel(GridLayout(2, 2, 12, 12)).apply {
        isOpaque = false
        isVisible = visibilidadInicial
    }

    /** Indica si el bloque de solución paso a paso se encuentra actualmente visible (inicia en false). */
    var desplegado: Boolean = visibilidadInicial
        private set

    /** Propiedad de estado equivalente a [desplegado] para consulta directa de visibilidad. */
    val mostrarSolucion: Boolean
        get() = desplegado

    init {
        isOpaque = false

        val barraSuperior = crearBarraEncabezado()
        add(barraSuperior, BorderLayout.NORTH)
        add(contenedorPasos, BorderLayout.CENTER)

        mostrarPasos(visibilidadInicial)
    }

    /**
     * Alterna la visibilidad del panel de pasos algebraicos.
     */
    fun alternarDespliegue() {
        mostrarPasos(!desplegado)
    }

    /**
     * Define explícitamente si los pasos de solución deben mostrarse u ocultarse.
     */
    fun mostrarPasos(visible: Boolean) {
        desplegado = visible
        contenedorPasos.isVisible = visible
        botonDesplegar.text = if (visible) {
            TEXTO_OCULTAR_SOLUCION
        } else {
            TEXTO_MOSTRAR_SOLUCION
        }
        revalidate()
        repaint()
    }

    /**
     * Renderiza las tarjetas explicativas correspondientes a la lista de [pasos].
     */
    fun actualizarPasos(pasos: List<PasoSolucion>) {
        contenedorPasos.removeAll()
        for (paso in pasos) {
            contenedorPasos.add(crearTarjetaPaso(paso))
        }
        revalidate()
        repaint()
    }

    private fun crearBarraEncabezado(): JPanel {
        return JPanel(BorderLayout()).apply {
            background = Color.WHITE
            border = CompoundBorder(
                BorderFactory.createLineBorder(Color(226, 232, 240), 1),
                EmptyBorder(8, 14, 8, 14)
            )

            val labelTitulo = JLabel("📘 Resolución Teórica Paso a Paso (Sustitución de Fórmulas)").apply {
                font = Font(Font.SANS_SERIF, Font.BOLD, 14)
                foreground = Color(15, 23, 42)
            }

            botonDesplegar.apply {
                font = Font(Font.SANS_SERIF, Font.BOLD, 12)
                foreground = Color(37, 99, 235)
                background = Color(239, 246, 255)
                isFocusPainted = false
                cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                border = EmptyBorder(6, 12, 6, 12)
                addActionListener {
                    alternarDespliegue()
                }
            }

            add(labelTitulo, BorderLayout.WEST)
            add(botonDesplegar, BorderLayout.EAST)
        }
    }

    private fun crearTarjetaPaso(paso: PasoSolucion): JPanel {
        return JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            background = Color.WHITE
            border = CompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, Color(37, 99, 235)),
                CompoundBorder(
                    BorderFactory.createLineBorder(Color(226, 232, 240), 1),
                    EmptyBorder(12, 14, 12, 14)
                )
            )

            val labelEncabezado = JLabel(paso.tituloCompleto()).apply {
                font = Font(Font.SANS_SERIF, Font.BOLD, 13)
                foreground = Color(30, 41, 59)
            }

            val labelExplicacion = JLabel("<html><div style='color:#64748b;'>${paso.explicacionDidactica}</div></html>").apply {
                font = Font(Font.SANS_SERIF, Font.PLAIN, 11)
            }

            val labelFormula = JLabel("📐 Fórmula: ${paso.formulaSimbolica}").apply {
                font = Font(Font.MONOSPACED, Font.BOLD, 12)
                foreground = Color(37, 99, 235)
            }

            val labelSustitucion = JLabel("🔢 Reemplazo: ${paso.sustitucionNumerica}").apply {
                font = Font(Font.MONOSPACED, Font.PLAIN, 12)
                foreground = Color(51, 65, 85)
            }

            val labelResultado = JLabel("🎯 Resultado exacto: ${paso.resultadoFormateado}").apply {
                font = Font(Font.SANS_SERIF, Font.BOLD, 12)
                foreground = Color(16, 185, 129)
            }

            add(labelEncabezado)
            add(Box.createRigidArea(Dimension(0, 4)))
            add(labelExplicacion)
            add(Box.createRigidArea(Dimension(0, 8)))
            add(labelFormula)
            add(Box.createRigidArea(Dimension(0, 4)))
            add(labelSustitucion)
            add(Box.createRigidArea(Dimension(0, 6)))
            add(labelResultado)
        }
    }
}
