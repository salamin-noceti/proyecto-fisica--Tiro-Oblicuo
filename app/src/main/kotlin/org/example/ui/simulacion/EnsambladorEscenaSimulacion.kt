package org.example.ui.simulacion

import org.example.model.IngresoDatos
import org.example.service.CalculadorFisico

/**
 * Ensamblador de los componentes principales de escena (controlador, lienzo 2D y botonera).
 */
class EnsambladorEscenaSimulacion(
    calculadorFisico: CalculadorFisico,
    datosIniciales: IngresoDatos,
    temaInicial: ThemeRenderer
) {
    var controlador: ControladorSimulacion = ControladorSimulacion(calculadorFisico = calculadorFisico, datosIniciales = datosIniciales)
        private set

    var lienzoSimulacion: LienzoSimulacion2D = LienzoSimulacion2D(controlador = controlador, themeRenderer = temaInicial)
        private set

    var panelControl: PanelControlSimulacion = lienzoSimulacion.panelControl
        private set

    fun obtenerConversor(): ConversorCoordenadas {
        return lienzoSimulacion.conversor
    }
}
