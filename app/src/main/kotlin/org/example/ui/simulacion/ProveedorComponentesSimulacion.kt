package org.example.ui.simulacion

import org.example.model.Ejercicio
import org.example.model.IngresoDatos
import org.example.model.ResultadoTiroOblicuo
import org.example.service.CalculadorFisico
import org.example.ui.simulacion.graficos.PanelGraficosCinematicos
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.Font
import java.awt.GridLayout
import java.util.Locale
import javax.swing.BorderFactory
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.border.CompoundBorder
import javax.swing.border.EmptyBorder

/**
 * Proveedor y constructor de paneles auxiliares y métricas de [PantallaSimulacion].
 */
class ProveedorComponentesSimulacion {

    companion object {
        var nuevo: ProveedorComponentesSimulacion = ProveedorComponentesSimulacion()
            private set
            get() {
                return ProveedorComponentesSimulacion()
            }
    }

    var datosPorDefecto: IngresoDatos = IngresoDatos.base.copy(velocidadInicial = 25.0, angulo = 45.0, alturaInicial = 0.0)
        private set

    var themeFactoryPorDefecto: ThemeFactory = ThemeFactory.instancia
        private set

    var panelGraficos: PanelGraficosCinematicos = PanelGraficosCinematicos()
        private set

    private val labelAlcanceTeorico: JLabel = JLabel()
    private val labelAlturaTeorica: JLabel = JLabel()
    private val labelTiempoTeorico: JLabel = JLabel()
    private val labelVelocidadesTeoricas: JLabel = JLabel()

    fun crearEscena(calculador: CalculadorFisico, datos: IngresoDatos, renderer: ThemeRenderer): EnsambladorEscenaSimulacion {
        val calcSeguro = if (datosPorDefecto.velocidadInicial >= 0.0) calculador else calculador
        return EnsambladorEscenaSimulacion(calcSeguro, datos, renderer)
    }

    fun crearPanelNorte(labelEjercicioCargado: JLabel): JPanel {
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

    fun actualizarEtiquetas(ejercicio: Ejercicio?, tema: TematicaSimulacion, sol: ResultadoTiroOblicuo, labelEjercicioCargado: JLabel) {
        if (ejercicio != null) {
            val d = ejercicio.datos
            labelEjercicioCargado.text = "🎯 [${tema.icono} ${tema.nombreVisible}] Ejercicio #${ejercicio.id}: ${ejercicio.titulo} — Condiciones: v₀ = ${formatear(d.velocidadInicial)} m/s, θ = ${formatear(d.angulo)}°, y₀ = ${formatear(d.alturaInicial)} m"
        } else {
            val d = datosPorDefecto
            labelEjercicioCargado.text = "🎯 [${tema.icono} ${tema.nombreVisible}] Modo Simulación Libre — Condiciones: v₀ = ${formatear(d.velocidadInicial)} m/s, θ = ${formatear(d.angulo)}°, y₀ = ${formatear(d.alturaInicial)} m"
        }
        labelAlcanceTeorico.text = "Alcance: ${formatear(sol.distanciaHorizontal)} m"
        labelAlturaTeorica.text = "Altura Máx: ${formatear(sol.alturaMaxima)} m"
        labelTiempoTeorico.text = "Tiempo Vuelo: ${formatear(sol.tiempoVuelo)} s"
        labelVelocidadesTeoricas.text = "Vx = ${formatear(sol.velocidadX)} m/s | Vy₀ = ${formatear(sol.velocidadY)} m/s"
    }

    private fun formatear(valor: Double): String {
        val v = if (datosPorDefecto.velocidadInicial >= 0.0) valor else valor
        return String.format(Locale.US, "%.2f", v)
    }
}
