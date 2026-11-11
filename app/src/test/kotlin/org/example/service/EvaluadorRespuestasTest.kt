package org.example.service

import org.example.model.Dificultad
import org.example.model.Ejercicio
import org.example.model.EstadoEvaluacion
import org.example.model.IngresoDatos
import org.example.model.ParametroFisico
import org.example.model.RespuestaEstudiante
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EvaluadorRespuestasTest {

    private val calculador = CalculadorTiroOblicuo()
    private val evaluador = EvaluadorRespuestas(calculadorFisico = calculador)

    private val ejercicioSuelo = Ejercicio(
        id = 1,
        titulo = "Tiro libre de fútbol",
        enunciado = "Un futbolista patea una pelota a 20 m/s con un ángulo de 30°.",
        dificultad = Dificultad.FACIL,
        datos = IngresoDatos(velocidadInicial = 20.0, angulo = 30.0, alturaInicial = 0.0)
    )

    private val ejercicioAltura = Ejercicio(
        id = 5,
        titulo = "Lanzamiento desde edificio",
        enunciado = "Se lanza un proyectil a 20 m/s y 30° desde 15 m de altura.",
        dificultad = Dificultad.DIFICIL,
        datos = IngresoDatos(velocidadInicial = 20.0, angulo = 30.0, alturaInicial = 15.0)
    )

    @Test
    fun `tolerancia por redondeo o uso de g=9_8 considera correctas las respuestas cercanas`() {
        // Para v0 = 20, θ = 30°, y0 = 0:
        // Con g = 9.81: Vx = 17.32, Vy = 10.00, t = 2.0387, y_max = 5.0968, x = 35.31
        // Con g = 9.80: Vx = 17.32, Vy = 10.00, t = 2.04,   y_max = 5.10,   x = 35.35
        val respuestaConG98 = RespuestaEstudiante(
            alcanceHorizontal = 35.35,
            alturaMaxima = 5.10,
            tiempoVuelo = 2.04,
            velocidadX = 17.3,
            velocidadY = 10.0
        )

        val reporte = evaluador.evaluar(ejercicioSuelo, respuestaConG98)

        assertTrue(reporte.esExcelente(), "Las respuestas redondeadas o con g=9.8 deben aprobarse")
        assertEquals(5, reporte.cantidadCorrectas)
        assertEquals("5 de 5 respuestas correctas", reporte.textoResumenAciertos())
        assertEquals(EvaluadorRespuestas.MENSAJE_EXCELENTE, reporte.mensajeGeneral)
        assertTrue(reporte.evaluaciones.all { it.estado == EstadoEvaluacion.CORRECTO })
    }

    @Test
    fun `identifica respuestas incorrectas y calcula resumen parcial correctamente`() {
        val respuestaParcial = RespuestaEstudiante(
            alcanceHorizontal = 35.31,
            alturaMaxima = 5.10,
            tiempoVuelo = 2.04,
            velocidadX = 17.32,
            velocidadY = 25.0 // Incorrecto (esperado 10.0)
        )

        val reporte = evaluador.evaluar(ejercicioSuelo, respuestaParcial)

        assertFalse(reporte.esExcelente())
        assertTrue(reporte.requiereRevision())
        assertEquals(4, reporte.cantidadCorrectas)
        assertEquals("4 de 5 respuestas correctas", reporte.textoResumenAciertos())

        val evalVy = reporte.obtenerEvaluacion(ParametroFisico.VELOCIDAD_Y)
        assertEquals(EstadoEvaluacion.INCORRECTO, evalVy?.estado)
        assertEquals(EstadoEvaluacion.CORRECTO, reporte.obtenerEvaluacion(ParametroFisico.VELOCIDAD_X)?.estado)
    }

    @Test
    fun `redondeo estandar HALF_UP a 2 decimales y formato con unidad se aplican antes de evaluar`() {
        assertEquals(15.46, evaluador.redondearDosDecimales(15.456))
        assertEquals(15.46, evaluador.redondearDosDecimales(15.455))
        assertEquals(10.0, evaluador.redondearDosDecimales(10.004))

        val eval = evaluador.evaluarParametro(
            parametro = ParametroFisico.ALCANCE_HORIZONTAL,
            valorEstudiante = 15.456,
            valorEsperado = 15.462
        )

        assertEquals(15.46, eval.valorEstudiante)
        assertEquals(15.46, eval.valorEsperado)
        assertEquals("15.46 m", eval.textoValorEstudiante())
        assertEquals("15.46 m", eval.textoValorEsperado())
        assertEquals(EstadoEvaluacion.CORRECTO, eval.estado)
    }

    @Test
    fun `tolerancia del 5_5 por ciento aprueba variaciones dentro del margen y rechaza mayores al 5_5 por ciento`() {
        // Valor teórico esperado = 100.0 -> margen ±5.5% = [94.50, 105.50]
        val variacion502PorCiento = evaluador.evaluarParametro(ParametroFisico.ALCANCE_HORIZONTAL, 94.98, 100.0)
        val enElLimite = evaluador.evaluarParametro(ParametroFisico.ALCANCE_HORIZONTAL, 105.50, 100.0)
        val fueraDeTolerancia = evaluador.evaluarParametro(ParametroFisico.ALCANCE_HORIZONTAL, 94.40, 100.0)

        assertEquals(EstadoEvaluacion.CORRECTO, variacion502PorCiento.estado)
        assertEquals(EstadoEvaluacion.CORRECTO, enElLimite.estado)
        assertEquals(EstadoEvaluacion.INCORRECTO, fueraDeTolerancia.estado)
    }

    @Test
    fun `cuando no hay respuesta del estudiante o el campo es nulo marca como INCORRECTO`() {
        val reporte = evaluador.evaluar(ejercicioSuelo, respuesta = null)

        assertEquals(0, reporte.cantidadCorrectas)
        assertTrue(reporte.evaluaciones.all { it.estado == EstadoEvaluacion.INCORRECTO })
        assertEquals(EvaluadorRespuestas.MENSAJE_SIN_RESPUESTAS, reporte.mensajeGeneral)
    }

    @Test
    fun `generarPasosSolucion construye los 4 pasos y adapta formula de tiempo segun y0`() {
        val solSuelo = calculador.calcular(ejercicioSuelo.datos)
        val pasosSuelo = evaluador.generarPasosSolucion(ejercicioSuelo.datos, solSuelo)

        assertEquals(4, pasosSuelo.size)
        assertTrue(pasosSuelo[0].formulaSimbolica.contains("cos"))
        assertTrue(pasosSuelo[1].formulaSimbolica.contains("(2 · Vy) / g"))
        assertTrue(pasosSuelo[2].formulaSimbolica.contains("y_max"))
        assertTrue(pasosSuelo[3].formulaSimbolica.contains("Vx · t_vuelo"))

        val solAltura = calculador.calcular(ejercicioAltura.datos)
        val pasosAltura = evaluador.generarPasosSolucion(ejercicioAltura.datos, solAltura)

        assertEquals(4, pasosAltura.size)
        assertTrue(pasosAltura[1].formulaSimbolica.contains("2 · g · y₀"))
    }
}
