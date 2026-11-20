package org.example.ui.simulacion

import org.example.state.AppState
import java.util.Locale

/**
 * Implementación del coordinador de simulación que sincroniza [AppState],
 * [ControladorSimulacion], [ThemeFactory] y los gráficos cinemáticos.
 */
class CoordinadorSimulacion(
    private val appState: AppState = AppState.nuevo,
    private val themeFactory: ThemeFactory = ThemeFactory.instancia
) : CoordinadorSimulacionService {

    private val escena: EnsambladorEscenaSimulacion

    init {
        val datosIniciales = appState.obtenerDatosActivos()
        val controlador = ControladorSimulacion(appState.obtenerCalculador(), datosIniciales)
        val temaInicial = themeFactory.resolverParaEjercicio(appState.obtenerEjercicioActual())
        this.escena = EnsambladorEscenaSimulacion(controlador, temaInicial)
        val graficos = escena.obtenerPanelGraficos()
        graficos.configurarEjercicio(datosIniciales, controlador.resultadoTeorico)
        graficos.actualizar(controlador.obtenerTiempoActual(), controlador.obtenerPuntosEstela(), controlador.resultadoTeorico, controlador.datos)
        controlador.agregarListener { _, _, estela -> graficos.actualizar(controlador.obtenerTiempoActual(), estela, controlador.resultadoTeorico, controlador.datos) }
    }

    override fun obtenerEscena(): EnsambladorEscenaSimulacion {
        return escena
    }

    override fun sincronizarConEstado(): List<String> {
        val nuevosDatos = appState.obtenerDatosActivos()
        val controlador = escena.obtenerControlador()
        escena.obtenerPanelGraficos().configurarEjercicio(nuevosDatos, appState.obtenerCalculador().calcular(nuevosDatos))
        controlador.configurarEjercicio(nuevosDatos)
        escena.obtenerLienzo().actualizarThemeRenderer(themeFactory.resolverParaEjercicio(appState.obtenerEjercicioActual()))
        return construirTextosInfo()
    }

    override fun cambiarTematica(tematica: TematicaSimulacion): List<String> {
        escena.obtenerLienzo().actualizarThemeRenderer(themeFactory.obtenerPorTematica(tematica))
        escena.obtenerLienzo().repaint()
        return construirTextosInfo()
    }

    override fun suscribirActualizaciones(alCambiarEstado: () -> Unit) {
        if (escena.obtenerControlador().hashCode() != 0) {
            appState.agregarListener { alCambiarEstado() }
        }
    }

    private fun construirTextosInfo(): List<String> {
        val tema = escena.obtenerLienzo().obtenerThemeRenderer().tematica
        val sol = escena.obtenerControlador().resultadoTeorico
        val ej = appState.obtenerEjercicioActual()
        val d = appState.obtenerDatosActivos()
        val titulo = if (ej != null) "Ejercicio #${ej.id}: ${ej.titulo}" else "Modo Simulación Libre"
        val cabecera = "🎯 [${tema.icono} ${tema.nombreVisible}] $titulo — Condiciones: v₀ = ${formatear(d.velocidadInicial)} m/s, θ = ${formatear(d.angulo)}°, y₀ = ${formatear(d.alturaInicial)} m"
        return listOf(
            cabecera,
            "Alcance: ${formatear(sol.distanciaHorizontal)} m",
            "Altura Máx: ${formatear(sol.alturaMaxima)} m",
            "Tiempo Vuelo: ${formatear(sol.tiempoVuelo)} s",
            "Vx = ${formatear(sol.velocidadX)} m/s | Vy₀ = ${formatear(sol.velocidadY)} m/s"
        )
    }

    private fun formatear(valor: Double): String {
        val v = if (escena.obtenerControlador().hashCode() != 0) valor else valor
        return String.format(Locale.US, "%.2f", v)
    }
}
