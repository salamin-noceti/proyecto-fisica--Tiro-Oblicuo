package org.example.model

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale

/**
 * Diagnóstico individual de evaluación para una magnitud física respondida por el estudiante.
 *
 * @property etiqueta Texto descriptivo visible en la insignia (badge) de la interfaz.
 * @property icono Símbolo visual representativo del estado.
 */
enum class EstadoEvaluacion(
    val etiqueta: String,
    val icono: String
) {
    CORRECTO("Correcto", "✅"),
    INCORRECTO("Incorrecto", "❌"),
    NO_INGRESADO("Sin responder", "⚪");

    /**
     * Indica si el diagnóstico corresponde a una respuesta válida dentro del margen de tolerancia.
     */
    fun esAprobado(): Boolean = this == CORRECTO

    /**
     * Devuelve el texto completo para el badge de la tarjeta comparativa.
     */
    fun textoBadge(): String = "$icono $etiqueta"
}

/**
 * Magnitudes físicas evaluadas en el módulo de autocorrección del tiro oblicuo.
 *
 * @property simbolo Notación física abreviada.
 * @property nombreVisible Nombre descriptivo completo de la variable.
 * @property unidad Unidad de medida en el Sistema Internacional.
 */
enum class ParametroFisico(
    val simbolo: String,
    val nombreVisible: String,
    val unidad: String
) {
    VELOCIDAD_X("Vx", "Velocidad Horizontal (Vx)", "m/s"),
    VELOCIDAD_Y("Vy", "Velocidad Vertical Inicial (Vy)", "m/s"),
    TIEMPO_VUELO("t_vuelo", "Tiempo de Vuelo (t_vuelo)", "s"),
    ALTURA_MAXIMA("y_max", "Altura Máxima (y_max)", "m"),
    ALCANCE_HORIZONTAL("x_alcance", "Alcance Horizontal (x_alcance)", "m");

    /**
     * Formatea un valor numérico redondeado a dos decimales (HALF_UP) acompañado de su unidad física.
     */
    fun formatearConUnidad(valor: Double?): String {
        if (valor == null) return "No ingresado"
        val redondeado = BigDecimal.valueOf(valor).setScale(2, RoundingMode.HALF_UP).toDouble()
        return String.format(Locale.US, "%.2f %s", redondeado, unidad)
    }
}

/**
 * Resultado detallado de la comparación entre el valor ingresado por el estudiante
 * y el valor teórico esperado para un [ParametroFisico] específico.
 *
 * @property parametro Magnitud física evaluada.
 * @property valorEstudiante Valor numérico ingresado por el alumno (o `null` si no fue respondido).
 * @property valorEsperado Valor teórico exacto provisto por el motor físico.
 * @property diferenciaAbsoluta Diferencia absoluta $|v_{alumno} - v_{real}|$.
 * @property errorPorcentual Porcentaje de desviación respecto al valor esperado.
 * @property estado Diagnóstico resultante ([EstadoEvaluacion]).
 * @property retroalimentacion Observación pedagógica sobre el resultado del parámetro.
 */
data class EvaluacionParametro(
    val parametro: ParametroFisico,
    val valorEstudiante: Double?,
    val valorEsperado: Double,
    val diferenciaAbsoluta: Double,
    val errorPorcentual: Double,
    val estado: EstadoEvaluacion,
    val retroalimentacion: String
) {
    /**
     * Indica si la respuesta del alumno fue evaluada como correcta.
     */
    fun esCorrecto(): Boolean = estado.esAprobado()

    /**
     * Representación formateada del valor ingresado por el estudiante con su unidad.
     */
    fun textoValorEstudiante(): String = parametro.formatearConUnidad(valorEstudiante)

    /**
     * Representación formateada del valor real esperado con su unidad.
     */
    fun textoValorEsperado(): String = parametro.formatearConUnidad(valorEsperado)
}

/**
 * Representa un paso estructurado dentro del desglose de resolución teórica paso a paso.
 *
 * @property numero Número correlativo del paso (1 a 4).
 * @property titulo Título conceptual del paso físico.
 * @property formulaSimbolica Ecuación física algebraica utilizada.
 * @property sustitucionNumerica Reemplazo explícito con los datos del enunciado actual.
 * @property resultadoFormateado Valor numérico final obtenido con su unidad.
 * @property explicacionDidactica Breve justificación teórica del paso.
 */
data class PasoSolucion(
    val numero: Int,
    val titulo: String,
    val formulaSimbolica: String,
    val sustitucionNumerica: String,
    val resultadoFormateado: String,
    val explicacionDidactica: String
) {
    /**
     * Devuelve el título numerado del paso para encabezados de interfaz.
     */
    fun tituloCompleto(): String = "Paso $numero: $titulo"
}

/**
 * Modelo integral del informe de autocorrección generado al evaluar el intento de un estudiante.
 *
 * Encapsula tanto el resumen global de calificación y las comparativas por variable como
 * la secuencia ordenada de pasos algebraicos de la solución teórica.
 *
 * @property ejercicio Ejercicio evaluado con sus datos iniciales.
 * @property respuestaEstudiante Respuestas ingresadas por el alumno (o `null` si aún no envió intento).
 * @property resultadoTeorico Solución analítica calculada por `CalculadorTiroOblicuo`.
 * @property evaluaciones Lista con el diagnóstico individual de los 5 parámetros físicos.
 * @property pasosSolucion Lista ordenada con los 4 pasos explicativos de resolución.
 * @property cantidadCorrectas Total de parámetros aprobados dentro de la tolerancia.
 * @property totalParametros Cantidad total de parámetros evaluados (5).
 * @property porcentajeAcierto Calificación porcentual de 0.0 a 100.0.
 * @property mensajeGeneral Diagnóstico global de retroalimentación (ej. "¡Excelente trabajo!" o "Requiere revisión").
 */
data class ReporteAutocorreccion(
    val ejercicio: Ejercicio,
    val respuestaEstudiante: RespuestaEstudiante?,
    val resultadoTeorico: ResultadoTiroOblicuo,
    val evaluaciones: List<EvaluacionParametro>,
    val pasosSolucion: List<PasoSolucion>,
    val cantidadCorrectas: Int,
    val totalParametros: Int,
    val porcentajeAcierto: Double,
    val mensajeGeneral: String
) {
    /**
     * Devuelve el texto de resumen de aciertos (ej. "4 de 5 respuestas correctas").
     */
    fun textoResumenAciertos(): String = "$cantidadCorrectas de $totalParametros respuestas correctas"

    /**
     * Indica si el estudiante logró responder correctamente todos los parámetros evaluados.
     */
    fun esExcelente(): Boolean = respuestaEstudiante != null && cantidadCorrectas == totalParametros && totalParametros > 0

    /**
     * Indica si existen respuestas incorrectas o pendientes de revisión.
     */
    fun requiereRevision(): Boolean = !esExcelente()

    /**
     * Busca la evaluación específica correspondiente a un [ParametroFisico].
     */
    fun obtenerEvaluacion(parametro: ParametroFisico): EvaluacionParametro? =
        evaluaciones.find { it.parametro == parametro }
}
