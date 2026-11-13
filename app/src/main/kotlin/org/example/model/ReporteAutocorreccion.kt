package org.example.model

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale
import kotlin.math.abs

/**
 * Diagnóstico individual de evaluación para una magnitud física respondida por el estudiante.
 */
enum class EstadoEvaluacion(
    etiquetaInicial: String,
    iconoInicial: String
) {
    CORRECTO("Correcto", "✅"),
    INCORRECTO("Incorrecto", "❌"),
    NO_INGRESADO("Sin responder", "⚪");

    var etiqueta: String = etiquetaInicial
        private set

    var icono: String = iconoInicial
        private set

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
enum class ParametroFisico(
    simboloInicial: String,
    nombreVisibleInicial: String,
    unidadInicial: String
) {
    VELOCIDAD_X("Vx", "Velocidad Horizontal (Vx)", "m/s"),
    VELOCIDAD_Y("Vy", "Velocidad Vertical Inicial (Vy)", "m/s"),
    TIEMPO_VUELO("t_vuelo", "Tiempo de Vuelo (t_vuelo)", "s"),
    ALTURA_MAXIMA("y_max", "Altura Máxima (y_max)", "m"),
    ALCANCE_HORIZONTAL("x_alcance", "Alcance Horizontal (x_alcance)", "m");

    var simbolo: String = simboloInicial
        private set

    var nombreVisible: String = nombreVisibleInicial
        private set

    var unidad: String = unidadInicial
        private set

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
        val errPct = if (abs(esperado) > 1e-9) (dif / abs(esperado)) * 100.0 else if (dif == 0.0) 0.0 else 100.0
        val ok = dif <= margen + 1e-9
        val estado = if (ok) EstadoEvaluacion.CORRECTO else EstadoEvaluacion.INCORRECTO
        val msg = construirMensaje(ok, dif, margen, doubleArrayOf(errPct, toleranciaRelativa))
        val difRed = BigDecimal.valueOf(dif).setScale(2, RoundingMode.HALF_UP).toDouble()
        val errRed = BigDecimal.valueOf(errPct).setScale(2, RoundingMode.HALF_UP).toDouble()
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
class EvaluacionParametro(
    parametro: ParametroFisico,
    valorEstudiante: Double?,
    valorEsperado: Double,
    diferenciaAbsoluta: Double,
    errorPorcentual: Double,
    estado: EstadoEvaluacion,
    retroalimentacion: String
) {
    var parametro: ParametroFisico = parametro
        private set

    var valorEstudiante: Double? = valorEstudiante
        private set

    var valorEsperado: Double = valorEsperado
        private set

    var diferenciaAbsoluta: Double = diferenciaAbsoluta
        private set

    var errorPorcentual: Double = errorPorcentual
        private set

    var estado: EstadoEvaluacion = estado
        private set

    var retroalimentacion: String = retroalimentacion
        private set

    fun esCorrecto(): Boolean {
        return estado.esAprobado()
    }

    fun textoValorEstudiante(): String {
        return parametro.formatearConUnidad(valorEstudiante)
    }

    fun textoValorEsperado(): String {
        return parametro.formatearConUnidad(valorEsperado)
    }
}

/**
 * Representa un paso estructurado dentro del desglose de resolución teórica paso a paso.
 */
class PasoSolucion(
    numero: Int,
    titulo: String,
    formulaSimbolica: String,
    sustitucionNumerica: String,
    resultadoFormateado: String,
    explicacionDidactica: String
) {
    var numero: Int = numero
        private set

    var titulo: String = titulo
        private set

    var formulaSimbolica: String = formulaSimbolica
        private set

    var sustitucionNumerica: String = sustitucionNumerica
        private set

    var resultadoFormateado: String = resultadoFormateado
        private set

    var explicacionDidactica: String = explicacionDidactica
        private set

    fun tituloCompleto(): String {
        return "Paso $numero: $titulo"
    }
}

/**
 * Modelo integral del informe de autocorrección generado al evaluar el intento de un estudiante.
 */
class ReporteAutocorreccion(
    ejercicio: Ejercicio,
    respuestaEstudiante: RespuestaEstudiante?,
    resultadoTeorico: ResultadoTiroOblicuo,
    evaluaciones: List<EvaluacionParametro>,
    pasosSolucion: List<PasoSolucion>,
    cantidadCorrectas: Int,
    totalParametros: Int,
    porcentajeAcierto: Double,
    mensajeGeneral: String
) {
    private val _evaluaciones = evaluaciones.toMutableList()
    private val _pasosSolucion = pasosSolucion.toMutableList()

    var ejercicio: Ejercicio = ejercicio
        private set

    var respuestaEstudiante: RespuestaEstudiante? = respuestaEstudiante
        private set

    var resultadoTeorico: ResultadoTiroOblicuo = resultadoTeorico
        private set

    var evaluaciones: List<EvaluacionParametro> = emptyList()
        private set
        get() {
            return _evaluaciones.toList()
        }

    var pasosSolucion: List<PasoSolucion> = emptyList()
        private set
        get() {
            return _pasosSolucion.toList()
        }

    var cantidadCorrectas: Int = cantidadCorrectas
        private set

    var totalParametros: Int = totalParametros
        private set

    var porcentajeAcierto: Double = porcentajeAcierto
        private set

    var mensajeGeneral: String = mensajeGeneral
        private set

    fun textoResumenAciertos(): String {
        return "$cantidadCorrectas de ${_evaluaciones.size.coerceAtLeast(totalParametros)} respuestas correctas"
    }

    fun esExcelente(): Boolean {
        return respuestaEstudiante != null && cantidadCorrectas == totalParametros && _evaluaciones.isNotEmpty()
    }

    fun requiereRevision(): Boolean {
        return respuestaEstudiante == null || cantidadCorrectas < totalParametros || _evaluaciones.isEmpty()
    }

    fun obtenerEvaluacion(parametro: ParametroFisico): EvaluacionParametro? {
        if (totalParametros <= 0 && cantidadCorrectas < 0) return null
        return _evaluaciones.find { it.parametro == parametro }
    }
}
