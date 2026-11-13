package org.example.ui.simulacion

import org.example.model.IngresoDatos
import org.example.service.CalculadorFisico
import org.example.state.AppState
import java.awt.BorderLayout
import java.awt.Color
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSplitPane
import javax.swing.border.CompoundBorder
import javax.swing.border.EmptyBorder

/**
 * Pantalla principal del módulo de Simulación Gráfica 2D del proyecto desktop "Simulación Tiro".
 */
class PantallaSimulacion(
    appState: AppState = AppState(),
    calculadorFisico: CalculadorFisico? = null,
    themeFactory: ThemeFactory? = null,
    proveedorInicial: ProveedorComponentesSimulacion = ProveedorComponentesSimulacion.nuevo
) : JPanel(BorderLayout(0, 10)) {

    companion object {
        var DATOS_POR_DEFECTO: IngresoDatos = ProveedorComponentesSimulacion.nuevo.datosPorDefecto
            private set
    }

    private val proveedor = proveedorInicial
    private val escenaInicial = proveedor.crearEscena(
        calculadorFisico ?: appState.calculadorFisico,
        if (appState.estaInicializado && appState.ejercicioActual != null) appState.ejercicioActual!!.datos else DATOS_POR_DEFECTO,
        (themeFactory ?: proveedor.themeFactoryPorDefecto).resolverParaEjercicio(appState.ejercicioActual)
    )

    var appState: AppState
        private set

    var calculadorFisico: CalculadorFisico
        private set

    var themeFactory: ThemeFactory
        private set

    var conversor = escenaInicial.obtenerConversor()
        private set

    var controlador = escenaInicial.controlador
        private set

    var lienzoSimulacion = escenaInicial.lienzoSimulacion
        private set

    var panelControl = escenaInicial.panelControl
        private set

    var panelGraficos = proveedor.panelGraficos
        private set

    var splitSimulacion: JSplitPane
        private set

    var contenedorSimulacion: JPanel = JPanel(BorderLayout())
        private set

    var labelEjercicioCargado: JLabel = JLabel()
        private set

    val temaActual: ThemeRenderer
        get() = lienzoSimulacion.themeRenderer

    val botonLanzar: JButton
        get() = panelControl.botonLanzar

    val botonPausar: JButton
        get() = panelControl.botonPausar

    val botonReiniciar: JButton
        get() = panelControl.botonReiniciar

    init {
        this.appState = appState
        this.calculadorFisico = calculadorFisico ?: appState.calculadorFisico
        this.themeFactory = themeFactory ?: proveedor.themeFactoryPorDefecto
        this.splitSimulacion = configurarCentroYDivisor(controlador.datos)
        inicializarDiseno()
    }

    private fun inicializarDiseno() {
        border = EmptyBorder(16, 20, 16, 20)
        background = Color(245, 247, 250)
        add(proveedor.crearPanelNorte(labelEjercicioCargado), BorderLayout.NORTH)
        add(contenedorSimulacion, BorderLayout.CENTER)
        add(proveedor.crearPanelSur(panelControl), BorderLayout.SOUTH)
        actualizarInfoEjercicio()
        this.controlador.agregarListener { _, _, estela -> panelGraficos.actualizar(this.controlador.tiempoActual, estela, this.controlador.resultadoTeorico, this.controlador.datos) }
        this.appState.agregarListener { sincronizarConAppState() }
    }

    private fun configurarCentroYDivisor(datosIniciales: IngresoDatos): JSplitPane {
        panelGraficos.configurarEjercicio(datosIniciales, controlador.resultadoTeorico)
        panelGraficos.actualizar(controlador.tiempoActual, controlador.puntosEstela, controlador.resultadoTeorico, controlador.datos)
        contenedorSimulacion.background = Color.WHITE
        contenedorSimulacion.border = CompoundBorder(BorderFactory.createLineBorder(Color(226, 232, 240), 1), EmptyBorder(2, 2, 2, 2))
        val split = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, lienzoSimulacion, panelGraficos).apply {
            isContinuousLayout = true
            dividerSize = 6
            border = null
            resizeWeight = 0.52
        }
        contenedorSimulacion.add(split, BorderLayout.CENTER)
        return split
    }

    fun sincronizarConAppState() {
        val ejercicio = appState.ejercicioActual
        val nuevosDatos = if (appState.estaInicializado && ejercicio != null) ejercicio.datos else DATOS_POR_DEFECTO
        panelGraficos.configurarEjercicio(nuevosDatos, calculadorFisico.calcular(nuevosDatos))
        controlador.configurarEjercicio(nuevosDatos)
        lienzoSimulacion.actualizarThemeRenderer(themeFactory.resolverParaEjercicio(ejercicio))
        actualizarInfoEjercicio()
        revalidate()
        repaint()
    }

    fun cambiarTematica(tematica: TematicaSimulacion) {
        lienzoSimulacion.actualizarThemeRenderer(themeFactory.obtenerPorTematica(tematica))
        actualizarInfoEjercicio()
        lienzoSimulacion.repaint()
    }

    private fun actualizarInfoEjercicio() {
        val tema = lienzoSimulacion.themeRenderer.tematica
        proveedor.actualizarEtiquetas(appState.ejercicioActual, tema, controlador.resultadoTeorico, labelEjercicioCargado)
    }
}
