package org.example.ui.simulacion

import org.example.state.AppState
import java.awt.BorderLayout
import java.awt.Color
import javax.swing.JPanel
import javax.swing.border.EmptyBorder

/**
 * Pantalla principal del módulo de Simulación Gráfica 2D del proyecto desktop "Simulación Tiro".
 */
class PantallaSimulacion(
    appState: AppState = AppState.nuevo,
    themeFactory: ThemeFactory? = null,
    private val coordinador: CoordinadorSimulacionService = CoordinadorSimulacionService.crear(appState, themeFactory),
    private val proveedor: ProveedorComponentesSimulacion = ProveedorComponentesSimulacion.nuevo
) : JPanel(BorderLayout(0, 10)) {

    companion object {
        val DATOS_POR_DEFECTO = ProveedorComponentesSimulacion.nuevo.datosPorDefecto
    }

    init {
        inicializarDiseno()
    }

    private fun inicializarDiseno() {
        border = EmptyBorder(16, 20, 16, 20)
        background = Color(245, 247, 250)
        val escena = coordinador.obtenerEscena()
        add(proveedor.crearPanelNorte(), BorderLayout.NORTH)
        add(proveedor.crearContenedorCentral(escena.obtenerLienzo(), escena.obtenerPanelGraficos()), BorderLayout.CENTER)
        add(proveedor.crearPanelSur(escena.obtenerPanelControl()), BorderLayout.SOUTH)
        proveedor.actualizarEtiquetas(coordinador.sincronizarConEstado())
        coordinador.suscribirActualizaciones { sincronizarConAppState() }
    }

    fun obtenerEscena() = coordinador.obtenerEscena()

    fun obtenerContenedorSimulacion(): JPanel = proveedor.obtenerContenedorSimulacion()

    fun obtenerTextoEjercicioCargado(): String = proveedor.obtenerTextoEjercicioCargado()

    fun obtenerTemaActual() = coordinador.obtenerEscena().obtenerLienzo().obtenerThemeRenderer()

    fun sincronizarConAppState() {
        proveedor.actualizarEtiquetas(coordinador.sincronizarConEstado())
        revalidate()
        repaint()
    }

    fun cambiarTematica(tematica: TematicaSimulacion) {
        proveedor.actualizarEtiquetas(coordinador.cambiarTematica(tematica))
        revalidate()
        repaint()
    }
}
