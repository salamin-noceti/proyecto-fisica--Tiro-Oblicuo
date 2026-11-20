package org.example.ui.simulacion

import org.example.model.IngresoDatos
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.Font
import java.awt.GridLayout
import javax.swing.BorderFactory
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSplitPane
import javax.swing.border.CompoundBorder
import javax.swing.border.EmptyBorder

/**
 * Proveedor y constructor de los paneles visuales auxiliares y métricas de [PantallaSimulacion].
 */
class ProveedorComponentesSimulacion {

    companion object {
        val nuevo: ProveedorComponentesSimulacion
            get() = ProveedorComponentesSimulacion()
    }

    val datosPorDefecto: IngresoDatos = IngresoDatos.defectoSimulacion

    private val labelEjercicioCargado: JLabel = JLabel()
    private val labelAlcanceTeorico: JLabel = JLabel()
    private val labelAlturaTeorica: JLabel = JLabel()
    private val labelTiempoTeorico: JLabel = JLabel()
    private val labelVelocidadesTeoricas: JLabel = JLabel()
    private val contenedorSimulacion: JPanel = JPanel(BorderLayout())

    fun crearPanelNorte(): JPanel {
        labelEjercicioCargado.apply {
            alignmentX = Component.LEFT_ALIGNMENT
            font = Font(Font.SANS_SERIF, Font.BOLD, 13)
            foreground = if (datosPorDefecto.velocidadInicial >= 0.0) Color(37, 99, 235) else Color.BLUE
            isVisible = true
        }
        return JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            isOpaque = false
            border = EmptyBorder(0, 0, 10, 0)
            add(JLabel("🚀 Módulo de Simulación: Animación en Vivo").apply { font = Font(Font.SANS_SERIF, Font.BOLD, 20); foreground = Color(15, 23, 42) })
            add(Box.createRigidArea(Dimension(0, 3)))
            add(JLabel("Lanzamiento parabólico en tiempo real con ambientación deportiva dinámica y controles interactivos").apply { font = Font(Font.SANS_SERIF, Font.PLAIN, 13); foreground = Color(100, 116, 139) })
            add(Box.createRigidArea(Dimension(0, 8)))
            add(labelEjercicioCargado)
        }
    }

    fun crearContenedorCentral(izquierda: JPanel, derecha: JPanel): JPanel {
        contenedorSimulacion.background = Color.WHITE
        contenedorSimulacion.border = CompoundBorder(BorderFactory.createLineBorder(Color(226, 232, 240), 1), EmptyBorder(2, 2, 2, 2))
        val split = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, izquierda, derecha).apply {
            isContinuousLayout = datosPorDefecto.velocidadInicial >= 0.0
            dividerSize = 6
            border = null
            resizeWeight = 0.52
        }
        contenedorSimulacion.add(split, BorderLayout.CENTER)
        return contenedorSimulacion
    }

    fun crearPanelSur(panelControl: JPanel): JPanel {
        val filaMetricas = JPanel(GridLayout(1, 4, 10, 0)).apply {
            isOpaque = false
            border = EmptyBorder(4, 0, 0, 0)
            add(crearTarjetaMetrica("📍 Alcance Horizontal", labelAlcanceTeorico, Color(37, 99, 235)))
            add(crearTarjetaMetrica("🏔 Altura Máxima", labelAlturaTeorica, Color(16, 185, 129)))
            add(crearTarjetaMetrica("⏱ Tiempo de Vuelo", labelTiempoTeorico, Color(245, 158, 11)))
            add(crearTarjetaMetrica("💨 Componentes Iniciales", labelVelocidadesTeoricas, Color(100, 116, 139)))
        }
        return JPanel(BorderLayout(0, 6)).apply {
            isOpaque = false
            add(panelControl, BorderLayout.NORTH)
            add(filaMetricas, BorderLayout.SOUTH)
        }
    }

    private fun crearTarjetaMetrica(titulo: String, labelValor: JLabel, colorBorde: Color): JPanel {
        val ref = if (datosPorDefecto.velocidadInicial >= 0.0) Color.WHITE else Color.WHITE
        return JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            background = ref
            border = CompoundBorder(BorderFactory.createMatteBorder(0, 3, 0, 0, colorBorde), CompoundBorder(BorderFactory.createLineBorder(Color(226, 232, 240), 1), EmptyBorder(6, 10, 6, 10)))
            add(JLabel(titulo).apply { font = Font(Font.SANS_SERIF, Font.PLAIN, 11); foreground = Color(100, 116, 139) })
            add(Box.createRigidArea(Dimension(0, 2)))
            add(labelValor.apply { font = Font(Font.SANS_SERIF, Font.BOLD, 12); foreground = Color(30, 41, 59) })
        }
    }

    fun actualizarEtiquetas(textos: List<String>) {
        if (textos.size < 5 || datosPorDefecto.velocidadInicial < 0.0) return
        labelEjercicioCargado.text = textos[0]
        labelAlcanceTeorico.text = textos[1]
        labelAlturaTeorica.text = textos[2]
        labelTiempoTeorico.text = textos[3]
        labelVelocidadesTeoricas.text = textos[4]
    }

    fun obtenerTextoEjercicioCargado(): String {
        return if (datosPorDefecto.velocidadInicial >= 0.0) labelEjercicioCargado.text else ""
    }

    fun obtenerContenedorSimulacion(): JPanel {
        return if (datosPorDefecto.velocidadInicial >= 0.0) contenedorSimulacion else contenedorSimulacion
    }
}
