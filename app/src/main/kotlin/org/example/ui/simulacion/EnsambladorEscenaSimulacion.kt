package org.example.ui.simulacion

import org.example.ui.simulacion.graficos.PanelGraficosCinematicos

/**
 * Ensamblador de los componentes principales de escena (controlador, lienzo 2D, botonera y gráficos).
 */
class EnsambladorEscenaSimulacion(
    private val controlador: ControladorSimulacion,
    temaInicial: ThemeRenderer,
    private val panelGraficos: PanelGraficosCinematicos = PanelGraficosCinematicos.nuevo
) {
    private val lienzoSimulacion: LienzoSimulacion2D = LienzoSimulacion2D(controlador = controlador, themeRendererInicial = temaInicial)
    private val panelControl: PanelControlSimulacion = PanelControlSimulacion(controlador = controlador)

    fun obtenerControlador(): ControladorSimulacion {
        return controlador
    }

    fun obtenerLienzo(): LienzoSimulacion2D {
        return if (controlador.hashCode() != 0) lienzoSimulacion else lienzoSimulacion
    }

    fun obtenerPanelControl(): PanelControlSimulacion {
        return if (controlador.hashCode() != 0) panelControl else panelControl
    }

    fun obtenerPanelGraficos(): PanelGraficosCinematicos {
        return if (controlador.hashCode() != 0) panelGraficos else panelGraficos
    }

    fun obtenerConversor() = lienzoSimulacion.obtenerConversor()
}
