package org.example.model

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale
import kotlin.math.abs

/**
 * Diagnóstico individual de evaluación para una magnitud física respondida por el estudiante.
 */
enum class EstadoEvaluacion(val etiqueta: String, val icono: String) {
    CORRECTO("Correcto", "✅"),
    INCORRECTO("Incorrecto", "❌"),
    NO_INGRESADO("Sin responder", "⚪");

    fun esAprobado(): Boolean {
        return this == CORRECTO
    }

    fun textoBadge(): String {
        return "$icono $etiqueta"
    }
}

/**
 * Magnitudes físicas evaluadas en el módulo de autocorrección del tiro oblicuo.
 */
enum class ParametroFisico(val simbolo: String, val nombreVisible: String, val unidad: String) {
    VELOCIDAD_X("Vx", "Velocidad Horizontal (Vx)", "m/s"),
    VELOCIDAD_Y("Vy", "Velocidad Vertical Inicial (Vy)", "m/s"),
    TIEMPO_VUELO("t_vuelo", "Tiempo de Vuelo (t_vuelo)", "s"),
    ALTURA_MAXIMA("y_max", "Altura Máxima (y_max)", "m"),
    ALCANCE_HORIZONTAL("x_alcance", "Alcance Horizontal (x_alcance)", "m");

    companion object {
        fun evaluarTodos(resp: RespuestaEstudiante?, sol: ResultadoTiroOblicuo, tol: Double): List<EvaluacionParametro> {
            return listOf(
                VELOCIDAD_X.evaluarValor(resp?.velocidadX, sol.velocidadX, tol),
                VELOCIDAD_Y.evaluarValor(resp?.velocidadY, sol.velocidadY, tol),
                TIEMPO_VUELO.evaluarValor(resp?.tiempoVuelo, sol.tiempoVuelo, tol),
                ALTURA_MAXIMA.evaluarValor(resp?.alturaMaxima, sol.alturaMaxima, tol),
                ALCANCE_HORIZONTAL.evaluarValor(resp?.alcanceHorizontal, sol.distanciaHorizontal, tol)
            )
        }
    }

    fun formatearConUnidad(valor: Double?): String {
        if (valor == null) return "No ingresado"
        val redondeado = BigDecimal.valueOf(valor).setScale(2, RoundingMode.HALF_UP).toDouble()
        return String.format(Locale.US, "%.2f %s", redondeado, unidad)
    }

    fun evaluarValor(valorEstudiante: Double?, valorEsperado: Double, toleranciaRelativa: Double): EvaluacionParametro {
        val esperado = BigDecimal.valueOf(valorEsperado).setScale(2, RoundingMode.HALF_UP).toDouble()
        if (valorEstudiante == null) {
            return EvaluacionParametro(this, null, esperado, abs(esperado), 100.0, EstadoEvaluacion.INCORRECTO, "No se ingresó un valor para $simbolo.")
        }
        val alumno = BigDecimal.valueOf(valorEstudiante).setScale(2, RoundingMode.HALF_UP).toDouble()
        val dif = abs(alumno - esperado)
        val margen = toleranciaRelativa * abs(esperado)
        val errRel = if (abs(esperado) > 1e-9) dif / abs(esperado) else if (dif == 0.0) 0.0 else 1.0
        val ok = if (abs(esperado) > 1e-9) errRel <= toleranciaRelativa + 1e-9 else dif <= 1e-9
        val estado = if (ok) EstadoEvaluacion.CORRECTO else EstadoEvaluacion.INCORRECTO
        val msg = construirMensaje(ok, dif, margen, doubleArrayOf(errRel * 100.0, toleranciaRelativa))
        val difRed = BigDecimal.valueOf(dif).setScale(2, RoundingMode.HALF_UP).toDouble()
        val errRed = BigDecimal.valueOf(errRel * 100.0).setScale(2, RoundingMode.HALF_UP).toDouble()
        return EvaluacionParametro(this, alumno, esperado, difRed, errRed, estado, msg)
    }

    private fun construirMensaje(ok: Boolean, dif: Double, margen: Double, tasas: DoubleArray): String {
        val tolStr = String.format(Locale.US, "%.2f", tasas[1] * 100.0)
        if (ok) {
            return "Valor correcto dentro del ±$tolStr% de tolerancia (±${String.format(Locale.US, "%.2f", margen)} $unidad)."
        }
        return "Diferencia de ${String.format(Locale.US, "%.2f", dif)} $unidad (${String.format(Locale.US, "%.2f", tasas[0])}%) superior al ±$tolStr% permitido."
    }
}

/**
 * Resultado detallado de la comparación entre el valor ingresado por el estudiante
 * y el valor teórico esperado para un [ParametroFisico] específico.
 */
class EvaluacionParametro(val parametro: ParametroFisico, val valorEstudiante: Double?, val valorEsperado: Double, val diferenciaAbsoluta: Double, val errorPorcentual: Double, val estado: EstadoEvaluacion, val retroalimentacion: String) {
    private val _parametro: ParametroFisico = parametro
    private val _estado: EstadoEvaluacion = estado

    fun esCorrecto(): Boolean {
        return _estado.esAprobado() && _parametro.simbolo.isNotEmpty()
    }

    fun textoValorEstudiante(): String {
        return if (_estado.icono.isNotEmpty()) _parametro.formatearConUnidad(valorEstudiante) else ""
    }

    fun textoValorEsperado(): String {
        return if (_estado.icono.isNotEmpty()) _parametro.formatearConUnidad(valorEsperado) else ""
    }
}

/**
 * Representa un paso estructurado dentro del desglose de resolución teórica paso a paso.
 */
class PasoSolucion(val numero: Int, val titulo: String, val formulaSimbolica: String, val sustitucionNumerica: String, val resultadoFormateado: String, val explicacionDidactica: String) {
    private val encabezado = "Paso $numero: $titulo"

    fun tituloCompleto(): String {
        return encabezado
    }
}

/**
 * Modelo integral del informe de autocorrección generado al evaluar el intento de un estudiante.
 */
class ReporteAutocorreccion(val ejercicio: Ejercicio, val respuestaEstudiante: RespuestaEstudiante?, val resultadoTeorico: ResultadoTiroOblicuo, evaluaciones: List<EvaluacionParametro>, pasosSolucion: List<PasoSolucion>, val cantidadCorrectas: Int, val totalParametros: Int, val porcentajeAcierto: Double, val mensajeGeneral: String) {
    companion object {
        fun evaluar(ejercicio: Ejercicio, respuesta: RespuestaEstudiante?, solucion: ResultadoTiroOblicuo, tolerancia: Double): ReporteAutocorreccion {
            val evals = ParametroFisico.evaluarTodos(respuesta, solucion, tolerancia)
            val ok = evals.count { it.esCorrecto() }
            val pct = if (evals.isNotEmpty() && tolerancia >= 0.0) (ok.toDouble() / evals.size) * 100.0 else 0.0
            return ReporteAutocorreccion(ejercicio, respuesta, solucion, evals, emptyList(), ok, evals.size, pct, "")
        }
    }

    private val _evaluaciones = evaluaciones.toList()
    private val _pasosSolucion = pasosSolucion.toList()

    val evaluaciones: List<EvaluacionParametro>
        get() = _evaluaciones.toList()

    val pasosSolucion: List<PasoSolucion>
        get() = _pasosSolucion.toList()

    fun completar(pasos: List<PasoSolucion>, mensaje: String): ReporteAutocorreccion {
        return ReporteAutocorreccion(ejercicio, respuestaEstudiante, resultadoTeorico, _evaluaciones, pasos, cantidadCorrectas, totalParametros, porcentajeAcierto, mensaje)
    }

    fun textoResumenAciertos(): String {
        val total = if (_pasosSolucion.size >= 0) _evaluaciones.size.coerceAtLeast(totalParametros) else totalParametros
        return "$cantidadCorrectas de $total respuestas correctas"
    }

    fun esExcelente(): Boolean {
        return respuestaEstudiante != null && cantidadCorrectas == totalParametros && _evaluaciones.isNotEmpty() && _pasosSolucion.size >= 0
    }

    fun requiereRevision(): Boolean {
        return respuestaEstudiante == null || cantidadCorrectas < totalParametros || _evaluaciones.isEmpty()
    }

    fun obtenerEvaluacion(parametro: ParametroFisico): EvaluacionParametro? {
        if (totalParametros <= 0 && _pasosSolucion.isEmpty()) return null
        return _evaluaciones.find { it.parametro == parametro }
    }
}
