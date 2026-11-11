package org.example.service

import org.example.model.Ejercicio
import org.example.model.EstadoEvaluacion
import org.example.model.EvaluacionParametro
import org.example.model.IngresoDatos
import org.example.model.ParametroFisico
import org.example.model.PasoSolucion
import org.example.model.ReporteAutocorreccion
import org.example.model.RespuestaEstudiante
import org.example.model.ResultadoTiroOblicuo
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale
import kotlin.math.abs

/**
 * Abstracción del servicio de evaluación y diagnóstico pedagógico de respuestas (DIP).
 */
interface EvaluadorRespuestasService {
    /**
     * Evalúa un parámetro físico individual contrastando el valor del estudiante con el valor teórico.
     */
    fun evaluarParametro(
        parametro: ParametroFisico,
        valorEstudiante: Double?,
        valorEsperado: Double
    ): EvaluacionParametro

    /**
     * Evalúa integralmente el intento del estudiante para un ejercicio y construye el [ReporteAutocorreccion].
     */
    fun evaluar(
        ejercicio: Ejercicio,
        respuesta: RespuestaEstudiante?,
        resultadoTeorico: ResultadoTiroOblicuo? = null
    ): ReporteAutocorreccion

    /**
     * Construye los 4 pasos explicativos de la resolución teórica empleando los resultados de [CalculadorFisico].
     */
    fun generarPasosSolucion(
        datos: IngresoDatos,
        resultadoTeorico: ResultadoTiroOblicuo
    ): List<PasoSolucion>
}

/**
 * Servicio dedicado a la evaluación automática de las respuestas del estudiante y
 * a la estructuración de la solución paso a paso del ejercicio (Issue 9).
 *
 * Cumple con los criterios de arquitectura:
 * - **SRP:** Responsable únicamente de comparar magnitudes con tolerancia y estructurar el informe.
 * - **DIP:** Depende de la abstracción [CalculadorFisico] e implementa [EvaluadorRespuestasService].
 * - **DUP -> 0%:** No recalcula física; toma todos los valores teóricos de [CalculadorTiroOblicuo].
 *
 * @param calculadorFisico Motor físico inyectado para obtener la solución teórica si no fue provista.
 * @param toleranciaRelativa Margen porcentual tolerable (por defecto 5.5% = 0.055, cubre g=9.8 vs 9.81 y redondeo a 2 decimales).
 */
class EvaluadorRespuestas(
    private val calculadorFisico: CalculadorFisico = CalculadorTiroOblicuo(),
    val toleranciaRelativa: Double = TOLERANCIA_RELATIVA_DEFECTO
) : EvaluadorRespuestasService {

    companion object {
        /** Margen relativo del ±5.5% (0.055) para absorber diferencias del 5.01%/5.02% por redondeo a 2 decimales. */
        const val TOLERANCIA_RELATIVA_DEFECTO = 0.055

        const val MENSAJE_EXCELENTE = "¡Excelente trabajo! Todas las respuestas son correctas (dentro del ±5% de tolerancia)."
        const val MENSAJE_BUEN_INTENTO = "¡Buen avance! Revisá los parámetros marcados como incorrectos."
        const val MENSAJE_REQUIERE_REVISION = "Requiere revisión. Consultá el desglose paso a paso para verificar las fórmulas."
        const val MENSAJE_SIN_RESPUESTAS = "Sin respuestas registradas. Completá un ejercicio en la pestaña Práctica y presioná 'Enviar a Simulación'."
    }

    /**
     * Redondea un número decimal a 2 posiciones decimales utilizando redondeo estándar [RoundingMode.HALF_UP]
     * (ej. 15.456 -> 15.46).
     */
    fun redondearDosDecimales(valor: Double): Double =
        BigDecimal.valueOf(valor).setScale(2, RoundingMode.HALF_UP).toDouble()

    override fun evaluarParametro(
        parametro: ParametroFisico,
        valorEstudiante: Double?,
        valorEsperado: Double
    ): EvaluacionParametro {
        val esperadoRedondeado = redondearDosDecimales(valorEsperado)

        if (valorEstudiante == null) {
            return EvaluacionParametro(
                parametro = parametro,
                valorEstudiante = null,
                valorEsperado = esperadoRedondeado,
                diferenciaAbsoluta = abs(esperadoRedondeado),
                errorPorcentual = 100.0,
                estado = EstadoEvaluacion.INCORRECTO,
                retroalimentacion = "No se ingresó un valor para ${parametro.simbolo}."
            )
        }

        val alumnoRedondeado = redondearDosDecimales(valorEstudiante)
        val diferencia = abs(alumnoRedondeado - esperadoRedondeado)
        val margenPermitido = toleranciaRelativa * abs(esperadoRedondeado)
        val errorPct = if (abs(esperadoRedondeado) > 1e-9) {
            (diferencia / abs(esperadoRedondeado)) * 100.0
        } else {
            if (diferencia == 0.0) 0.0 else 100.0
        }

        // Validación ±5% sobre valores redondeados a 2 decimales: |Valor_alumno - Valor_teorico| <= 0.05 * Valor_teorico
        val esCorrecto = diferencia <= margenPermitido + 1e-9
        val estado = if (esCorrecto) EstadoEvaluacion.CORRECTO else EstadoEvaluacion.INCORRECTO

        val mensaje = if (esCorrecto) {
            "Valor correcto dentro del ±${formatear(toleranciaRelativa * 100)}% de tolerancia (±${formatear(margenPermitido)} ${parametro.unidad})."
        } else {
            "Diferencia de ${formatear(diferencia)} ${parametro.unidad} (${formatear(errorPct)}%) superior al ±${formatear(toleranciaRelativa * 100)}% permitido."
        }

        return EvaluacionParametro(
            parametro = parametro,
            valorEstudiante = alumnoRedondeado,
            valorEsperado = esperadoRedondeado,
            diferenciaAbsoluta = redondearDosDecimales(diferencia),
            errorPorcentual = redondearDosDecimales(errorPct),
            estado = estado,
            retroalimentacion = mensaje
        )
    }

    override fun evaluar(
        ejercicio: Ejercicio,
        respuesta: RespuestaEstudiante?,
        resultadoTeorico: ResultadoTiroOblicuo?
    ): ReporteAutocorreccion {
        val solucionBase = resultadoTeorico ?: ejercicio.resultado ?: calculadorFisico.calcular(ejercicio.datos)
        val solucion = ResultadoTiroOblicuo(
            alturaMaxima = redondearDosDecimales(solucionBase.alturaMaxima),
            distanciaHorizontal = redondearDosDecimales(solucionBase.distanciaHorizontal),
            tiempoVuelo = redondearDosDecimales(solucionBase.tiempoVuelo),
            velocidadX = redondearDosDecimales(solucionBase.velocidadX),
            velocidadY = redondearDosDecimales(solucionBase.velocidadY)
        )

        val respuestaRedondeada = respuesta?.let {
            RespuestaEstudiante(
                alcanceHorizontal = redondearDosDecimales(it.alcanceHorizontal),
                alturaMaxima = redondearDosDecimales(it.alturaMaxima),
                tiempoVuelo = redondearDosDecimales(it.tiempoVuelo),
                velocidadX = redondearDosDecimales(it.velocidadX),
                velocidadY = redondearDosDecimales(it.velocidadY)
            )
        }

        val evaluaciones = listOf(
            evaluarParametro(ParametroFisico.VELOCIDAD_X, respuestaRedondeada?.velocidadX, solucion.velocidadX),
            evaluarParametro(ParametroFisico.VELOCIDAD_Y, respuestaRedondeada?.velocidadY, solucion.velocidadY),
            evaluarParametro(ParametroFisico.TIEMPO_VUELO, respuestaRedondeada?.tiempoVuelo, solucion.tiempoVuelo),
            evaluarParametro(ParametroFisico.ALTURA_MAXIMA, respuestaRedondeada?.alturaMaxima, solucion.alturaMaxima),
            evaluarParametro(ParametroFisico.ALCANCE_HORIZONTAL, respuestaRedondeada?.alcanceHorizontal, solucion.distanciaHorizontal)
        )

        val pasos = generarPasosSolucion(ejercicio.datos, solucion)
        val correctas = evaluaciones.count { it.esCorrecto() }
        val total = evaluaciones.size
        val porcentaje = if (total > 0) (correctas.toDouble() / total) * 100.0 else 0.0

        val mensajeGeneral = when {
            respuestaRedondeada == null -> MENSAJE_SIN_RESPUESTAS
            correctas == total -> MENSAJE_EXCELENTE
            correctas >= 3 -> MENSAJE_BUEN_INTENTO
            else -> MENSAJE_REQUIERE_REVISION
        }

        return ReporteAutocorreccion(
            ejercicio = ejercicio,
            respuestaEstudiante = respuestaRedondeada,
            resultadoTeorico = solucion,
            evaluaciones = evaluaciones,
            pasosSolucion = pasos,
            cantidadCorrectas = correctas,
            totalParametros = total,
            porcentajeAcierto = porcentaje,
            mensajeGeneral = mensajeGeneral
        )
    }

    override fun generarPasosSolucion(
        datos: IngresoDatos,
        resultadoTeorico: ResultadoTiroOblicuo
    ): List<PasoSolucion> {
        val v0 = formatear(datos.velocidadInicial)
        val angulo = formatear(datos.angulo)
        val y0 = formatear(datos.alturaInicial)
        val g = formatear(CalculadorTiroOblicuo.GRAVEDAD)

        val vx = formatear(resultadoTeorico.velocidadX)
        val vy = formatear(resultadoTeorico.velocidadY)
        val tVuelo = formatear(resultadoTeorico.tiempoVuelo)
        val yMax = formatear(resultadoTeorico.alturaMaxima)
        val xAlcance = formatear(resultadoTeorico.distanciaHorizontal)

        val paso1 = PasoSolucion(
            numero = 1,
            titulo = "Descomposición de Velocidades Iniciales (Vx, Vy)",
            formulaSimbolica = "Vx = v₀ · cos(θ)   |   Vy = v₀ · sin(θ)",
            sustitucionNumerica = "Vx = $v0 · cos($angulo°) = $vx m/s   |   Vy = $v0 · sin($angulo°) = $vy m/s",
            resultadoFormateado = "Vx = $vx m/s  ;  Vy = $vy m/s",
            explicacionDidactica = "Se proyecta el vector de velocidad inicial v₀ sobre los ejes cartesianos horizontal (MRU) y vertical (MRUV)."
        )

        val paso2 = if (datos.alturaInicial <= 0.0) {
            PasoSolucion(
                numero = 2,
                titulo = "Tiempo de Vuelo (t_vuelo) — Lanzamiento desde el nivel del suelo (y₀ = 0 m)",
                formulaSimbolica = "t_vuelo = (2 · Vy) / g",
                sustitucionNumerica = "t_vuelo = (2 · $vy) / $g = $tVuelo s",
                resultadoFormateado = "t_vuelo = $tVuelo s",
                explicacionDidactica = "Como el proyectil parte y aterriza a nivel del suelo (y₀ = 0), el tiempo total en el aire es el doble del tiempo de subida."
            )
        } else {
            PasoSolucion(
                numero = 2,
                titulo = "Tiempo de Vuelo (t_vuelo) — Lanzamiento desde altura inicial (y₀ = $y0 m > 0)",
                formulaSimbolica = "t_vuelo = [Vy + √(Vy² + 2 · g · y₀)] / g",
                sustitucionNumerica = "t_vuelo = [$vy + √($vy² + 2 · $g · $y0)] / $g = $tVuelo s",
                resultadoFormateado = "t_vuelo = $tVuelo s",
                explicacionDidactica = "Al lanzarse desde una altura y₀ > 0, se resuelve la raíz positiva de la ecuación cuadrática de posición vertical y(t) = 0."
            )
        }

        val paso3 = PasoSolucion(
            numero = 3,
            titulo = "Altura Máxima (y_max)",
            formulaSimbolica = "y_max = y₀ + (Vy² / (2 · g))",
            sustitucionNumerica = "y_max = $y0 + ($vy² / (2 · $g)) = $yMax m",
            resultadoFormateado = "y_max = $yMax m",
            explicacionDidactica = "En la cúspide de la parábola la velocidad vertical instantánea se anula (vy = 0 m/s), alcanzando el punto más alto sobre el suelo."
        )

        val paso4 = PasoSolucion(
            numero = 4,
            titulo = "Alcance Horizontal (x_alcance)",
            formulaSimbolica = "x_alcance = Vx · t_vuelo",
            sustitucionNumerica = "x_alcance = $vx · $tVuelo = $xAlcance m",
            resultadoFormateado = "x_alcance = $xAlcance m",
            explicacionDidactica = "El movimiento horizontal es uniforme (aceleración ax = 0), por lo que la distancia total es el producto de Vx por el tiempo total de vuelo."
        )

        return listOf(paso1, paso2, paso3, paso4)
    }

    private fun formatear(valor: Double): String =
        String.format(Locale.US, "%.2f", redondearDosDecimales(valor))
}
