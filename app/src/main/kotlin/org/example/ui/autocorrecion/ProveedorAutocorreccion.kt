package org.example.ui.autocorrecion

import org.example.model.Ejercicio
import org.example.service.EvaluadorRespuestas
import org.example.service.EvaluadorRespuestasService

/**
 * Proveedor cohesivo de componentes visuales y datos iniciales para [PantallaAutocorreccion].
 */
class ProveedorAutocorreccion {
    companion object {
        var nuevo: ProveedorAutocorreccion = ProveedorAutocorreccion()
            private set
            get() {
                return ProveedorAutocorreccion()
            }
    }

    var panelResumen: PanelResumenCalificacion = PanelResumenCalificacion.nuevo
        private set

    var panelPasos: PanelSolucionPasoAPaso = PanelSolucionPasoAPaso(false)
        private set

    var evaluadorPorDefecto: EvaluadorRespuestasService = EvaluadorRespuestas(panelResumen.calculadorRespaldo)
        private set

    var ejercicioPorDefecto: Ejercicio = panelResumen.ejercicioRespaldo
        private set

    fun reiniciarVisibilidadPasos() {
        panelPasos.mostrarPasos(false)
    }
}
