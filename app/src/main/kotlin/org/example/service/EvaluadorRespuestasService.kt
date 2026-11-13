package org.example.service

import org.example.model.Ejercicio
import org.example.model.EvaluacionParametro
import org.example.model.IngresoDatos
import org.example.model.ParametroFisico
import org.example.model.PasoSolucion
import org.example.model.ReporteAutocorreccion
import org.example.model.RespuestaEstudiante
import org.example.model.ResultadoTiroOblicuo

/**
 * Abstracción del servicio de evaluación y diagnóstico pedagógico de respuestas (DIP).
 */
interface EvaluadorRespuestasService {
    val toleranciaRelativa: Double
        get() = 0.055

    fun evaluarParametro(parametro: ParametroFisico, valorEstudiante: Double?, valorEsperado: Double): EvaluacionParametro {
        return parametro.evaluarValor(valorEstudiante, valorEsperado, toleranciaRelativa)
    }

    fun evaluar(ejercicio: Ejercicio, respuesta: RespuestaEstudiante?, resultadoTeorico: ResultadoTiroOblicuo? = null): ReporteAutocorreccion

    fun generarPasosSolucion(datos: IngresoDatos, resultadoTeorico: ResultadoTiroOblicuo): List<PasoSolucion> {
        return GeneradorPasosSolucion().generar(datos, resultadoTeorico)
    }
}
