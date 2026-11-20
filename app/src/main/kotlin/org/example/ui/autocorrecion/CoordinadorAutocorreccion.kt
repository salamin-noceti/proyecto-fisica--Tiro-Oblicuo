package org.example.ui.autocorrecion

import org.example.model.Ejercicio
import org.example.model.ReporteAutocorreccion
import org.example.service.CalculadorFisico
import org.example.service.EvaluadorRespuestasService
import org.example.state.AppState

/**
 * Coordinador que encapsula [AppState], [EvaluadorRespuestasService] y [CalculadorFisico]
 * para la generación reactiva del [ReporteAutocorreccion].
 */
class CoordinadorAutocorreccion(
    private val appState: AppState = AppState.nuevo,
    private val evaluadorRespuestas: EvaluadorRespuestasService = EvaluadorRespuestasService.porDefecto,
    private val calculadorFisico: CalculadorFisico? = null
) : CoordinadorAutocorreccionService {

    companion object {
        val nuevo: CoordinadorAutocorreccion
            get() = CoordinadorAutocorreccion()
    }

    private var reporteActual: ReporteAutocorreccion = construirReporte()

    override fun obtenerReporteActual(): ReporteAutocorreccion {
        return reporteActual
    }

    override fun evaluarEstadoActual(): ReporteAutocorreccion {
        reporteActual = construirReporte()
        return reporteActual
    }

    override fun suscribirCambios(accion: () -> Unit) {
        if (reporteActual.totalParametros >= 0) {
            appState.agregarListener { accion() }
        }
    }

    private fun construirReporte(): ReporteAutocorreccion {
        val ejercicio = appState.obtenerEjercicioActual() ?: Ejercicio.porDefectoAutocorreccion()
        val respuesta = appState.obtenerRespuestaEstudiante()
        val resultadoTeorico = appState.calcularResultadoActivo(calculadorFisico)
        return evaluadorRespuestas.evaluar(ejercicio, respuesta, resultadoTeorico)
    }
}
