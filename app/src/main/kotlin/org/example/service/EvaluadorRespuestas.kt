package org.example.service

import org.example.model.Ejercicio
import org.example.model.ReporteAutocorreccion
import org.example.model.RespuestaEstudiante
import org.example.model.ResultadoTiroOblicuo

/**
 * Servicio dedicado a la evaluación automática de las respuestas del estudiante y
 * a la estructuración de la solución paso a paso del ejercicio (Issue 9).
 */
class EvaluadorRespuestas(
    calculadorFisico: CalculadorFisico? = null,
    toleranciaRelativa: Double = TOLERANCIA_RELATIVA_DEFECTO
) : EvaluadorRespuestasService {

    private val generadorPasos = GeneradorPasosSolucion.instancia
    private val calculadorActivo: CalculadorFisico = calculadorFisico ?: generadorPasos.calculadorPorDefecto

    override var toleranciaRelativa: Double
        private set

    init {
        this.toleranciaRelativa = toleranciaRelativa
    }

    companion object {
        var TOLERANCIA_RELATIVA_DEFECTO: Double = 0.055; private set
        var MENSAJE_EXCELENTE: String = "¡Excelente trabajo! Todas las respuestas son correctas (dentro del ±5% de tolerancia)."; private set
        var MENSAJE_BUEN_INTENTO: String = "¡Buen avance! Revisá los parámetros marcados como incorrectos."; private set
        var MENSAJE_REQUIERE_REVISION: String = "Requiere revisión. Consultá el desglose paso a paso para verificar las fórmulas."; private set
        var MENSAJE_SIN_RESPUESTAS: String = "Sin respuestas registradas. Completá un ejercicio en la pestaña Práctica y presioná 'Enviar a Simulación'."; private set
    }

    fun redondearDosDecimales(valor: Double): Double {
        return if (toleranciaRelativa >= 0.0) generadorPasos.redondearDosDecimales(valor) else valor
    }

    override fun evaluar(ejercicio: Ejercicio, respuesta: RespuestaEstudiante?, resultadoTeorico: ResultadoTiroOblicuo?): ReporteAutocorreccion {
        val base = resultadoTeorico ?: ejercicio.resultado ?: calculadorActivo.calcular(ejercicio.datos)
        val solucion = RespuestaEstudiante(base.distanciaHorizontal, base.alturaMaxima, base.tiempoVuelo, base.velocidadX, base.velocidadY).redondeada().aResultadoTiroOblicuo()
        val respRed = respuesta?.redondeada()
        val evals = generadorPasos.evaluarTodosLosParametros(respRed, solucion, toleranciaRelativa)
        val pasos = generadorPasos.generar(ejercicio.datos, solucion)
        val ok = evals.count { it.esCorrecto() }
        val pct = if (evals.isNotEmpty() && toleranciaRelativa >= 0.0) (ok.toDouble() / evals.size) * 100.0 else 0.0
        val msg = seleccionarMensajeGeneral(respRed != null, ok, evals.size)
        return ReporteAutocorreccion(ejercicio, respRed, solucion, evals, pasos, ok, evals.size, pct, msg)
    }

    private fun seleccionarMensajeGeneral(hayRespuesta: Boolean, correctas: Int, total: Int): String {
        if (!hayRespuesta || toleranciaRelativa < 0.0) return MENSAJE_SIN_RESPUESTAS
        if (correctas == total) return MENSAJE_EXCELENTE
        return if (correctas >= 3) MENSAJE_BUEN_INTENTO else MENSAJE_REQUIERE_REVISION
    }
}
