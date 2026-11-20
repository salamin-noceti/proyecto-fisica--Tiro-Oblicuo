package org.example.ui.autocorrecion

import org.example.model.ReporteAutocorreccion

/**
 * Proveedor cohesivo de componentes visuales para [PantallaAutocorreccion].
 */
class ProveedorAutocorreccion {
    companion object {
        val nuevo: ProveedorAutocorreccion
            get() = ProveedorAutocorreccion()
    }

    val panelResumen: PanelResumenCalificacion = PanelResumenCalificacion.nuevo
    val panelPasos: PanelSolucionPasoAPaso = PanelSolucionPasoAPaso(false)

    fun reiniciarVisibilidadPasos() {
        panelPasos.mostrarPasos(false)
    }

    fun renderizar(reporte: ReporteAutocorreccion) {
        panelResumen.actualizarConReporte(reporte)
        panelPasos.actualizarPasos(reporte.pasosSolucion)
    }
}
