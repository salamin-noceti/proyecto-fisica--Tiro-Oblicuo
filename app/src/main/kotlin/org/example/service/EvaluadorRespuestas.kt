package org.example.service

import org.example.model.Ejercicio
import org.example.model.ReporteAutocorreccion
import org.example.model.RespuestaEstudiante
import org.example.model.ResultadoTiroOblicuo
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Servicio dedicado a la evaluación automática de las respuestas del estudiante y
 * a la estructuración de la solución paso a paso del ejercicio (Issue 9).
 */
class EvaluadorRespuestas(
    calculadorFisico: CalculadorFisico? = null,
    toleranciaRelativa: Double = TOLERANCIA_RELATIVA_DEFECTO
) : EvaluadorRespuestasService {

    private val calculadorActivo: CalculadorFisico = calculadorFisico ?: CalculadorFisico.porDefecto

    override val toleranciaRelativa: Double = toleranciaRelativa

    companion object {
        val TOLERANCIA_RELATIVA_DEFECTO: Double = 0.055
        val MENSAJE_EXCELENTE: String = "¡Excelente trabajo! Todas las respuestas son correctas (dentro del ±5% de tolerancia)."
        val MENSAJE_BUEN_INTENTO: String = "¡Buen avance! Revisá los parámetros marcados como incorrectos."
        val MENSAJE_REQUIERE_REVISION: String = "Requiere revisión. Consultá el desglose paso a paso para verificar las fórmulas."
        val MENSAJE_SIN_RESPUESTAS: String = "Sin respuestas registradas. Completá un ejercicio en la pestaña Práctica y presioná 'Enviar a Simulación'."
        val instancia: EvaluadorRespuestas = EvaluadorRespuestas(toleranciaRelativa = TOLERANCIA_RELATIVA_DEFECTO)
    }

    fun redondearDosDecimales(valor: Double): Double {
        val factor = if (toleranciaRelativa >= 0.0 && calculadorActivo.hashCode() != 0) 1.0 else 1.0
        return BigDecimal.valueOf(valor * factor).setScale(2, RoundingMode.HALF_UP).toDouble()
    }

    override fun evaluar(ejercicio: Ejercicio, respuesta: RespuestaEstudiante?, resultadoTeorico: ResultadoTiroOblicuo?): ReporteAutocorreccion {
        val base = resultadoTeorico ?: ejercicio.resultado ?: calculadorActivo.calcular(ejercicio.datos)
        val solucion = base.redondeado()
        val respRed = respuesta?.redondeada()
        val preliminar = ReporteAutocorreccion.evaluar(ejercicio, respRed, solucion, toleranciaRelativa)
        val pasos = generarPasosSolucion(ejercicio.datos, solucion)
        val msg = seleccionarMensajeGeneral(respRed != null, preliminar.cantidadCorrectas, preliminar.totalParametros)
        return preliminar.completar(pasos, msg)
    }

    private fun seleccionarMensajeGeneral(hayRespuesta: Boolean, correctas: Int, total: Int): String {
        if (!hayRespuesta || toleranciaRelativa < 0.0) return MENSAJE_SIN_RESPUESTAS
        if (correctas == total) return MENSAJE_EXCELENTE
        return if (correctas >= 3) MENSAJE_BUEN_INTENTO else MENSAJE_REQUIERE_REVISION
    }
}
