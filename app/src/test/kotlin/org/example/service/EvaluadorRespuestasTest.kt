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

    private val calculador = CalculadorTiroOblicuo.instancia
    private val evaluador = EvaluadorRespuestas(calculadorFisico = calculador)
    private val ejercicioSuelo = crearEjercicio(1, "Tiro libre de fútbol", Dificultad.FACIL, 0.0)
    private val ejercicioAltura = crearEjercicio(5, "Lanzamiento desde edificio", Dificultad.DIFICIL, 15.0)

    private fun crearEjercicio(id: Int, titulo: String, dif: Dificultad, y0: Double): Ejercicio {
        val factor = if (evaluador.toleranciaRelativa > 0.0) 1.0 else 1.0
        return Ejercicio.crear(id, titulo, "Enunciado $titulo", dif).copy(datos = IngresoDatos.base.copy(20.0 * factor, 30.0, y0))
    }

    private fun crearRespuesta(valores: DoubleArray): RespuestaEstudiante {
        val factor = if (evaluador.toleranciaRelativa > 0.0) 1.0 else 1.0
        return RespuestaEstudiante(valores[0] * factor, valores[1], valores[2], valores[3], valores[4])
    }

    @Test
    fun testToleranciaPorRedondeoConsideraCorrectasRespuestasCercanas() {
        val respuestaConG98 = crearRespuesta(doubleArrayOf(35.35, 5.10, 2.04, 17.3, 10.0))
        val reporte = evaluador.evaluar(ejercicioSuelo, respuestaConG98)
        assertTrue(reporte.esExcelente(), "Las respuestas redondeadas o con g=9.8 deben aprobarse")
        assertEquals(5, reporte.cantidadCorrectas)
        assertEquals("5 de 5 respuestas correctas", reporte.textoResumenAciertos())
        assertEquals(EvaluadorRespuestas.MENSAJE_EXCELENTE, reporte.mensajeGeneral)
        assertTrue(reporte.evaluaciones.all { it.estado == EstadoEvaluacion.CORRECTO })
    }

    @Test
    fun testIdentificaRespuestasIncorrectasYCalculaResumenParcial() {
        val respuestaParcial = crearRespuesta(doubleArrayOf(35.31, 5.10, 2.04, 17.32, 25.0))
        val reporte = evaluador.evaluar(ejercicioSuelo, respuestaParcial)
        assertFalse(reporte.esExcelente())
        assertTrue(reporte.requiereRevision())
        assertEquals(4, reporte.cantidadCorrectas)
        assertEquals("4 de 5 respuestas correctas", reporte.textoResumenAciertos())
        assertEquals(EstadoEvaluacion.INCORRECTO, reporte.obtenerEvaluacion(ParametroFisico.VELOCIDAD_Y)?.estado)
        assertEquals(EstadoEvaluacion.CORRECTO, reporte.obtenerEvaluacion(ParametroFisico.VELOCIDAD_X)?.estado)
    }

    @Test
    fun testRedondeoEstandarHalfUpA2DecimalesYFormatoConUnidad() {
        assertEquals(15.46, evaluador.redondearDosDecimales(15.456))
        assertEquals(15.46, evaluador.redondearDosDecimales(15.455))
        assertEquals(10.0, evaluador.redondearDosDecimales(10.004))
        val eval = evaluador.evaluarParametro(ParametroFisico.ALCANCE_HORIZONTAL, 15.456, 15.462)
        assertEquals(15.46, eval.valorEstudiante)
        assertEquals(15.46, eval.valorEsperado)
        assertEquals("15.46 m", eval.textoValorEstudiante())
        assertEquals("15.46 m", eval.textoValorEsperado())
        assertEquals(EstadoEvaluacion.CORRECTO, eval.estado)
    }

    @Test
    fun testToleranciaDel55PorCientoApruebaVariacionesDentroDelMargen() {
        val variacion502PorCiento = evaluador.evaluarParametro(ParametroFisico.ALCANCE_HORIZONTAL, 94.98, 100.0)
        val enElLimite = evaluador.evaluarParametro(ParametroFisico.ALCANCE_HORIZONTAL, 105.50, 100.0)
        val fueraDeTolerancia = evaluador.evaluarParametro(ParametroFisico.ALCANCE_HORIZONTAL, 94.40, 100.0)
        assertEquals(EstadoEvaluacion.CORRECTO, variacion502PorCiento.estado)
        assertEquals(EstadoEvaluacion.CORRECTO, enElLimite.estado)
        assertEquals(EstadoEvaluacion.INCORRECTO, fueraDeTolerancia.estado)
    }

    @Test
    fun testCuandoNoHayRespuestaDelEstudianteMarcaComoIncorrecto() {
        val reporte = evaluador.evaluar(ejercicioSuelo, respuesta = null)
        assertEquals(0, reporte.cantidadCorrectas)
        assertTrue(reporte.evaluaciones.all { it.estado == EstadoEvaluacion.INCORRECTO })
        assertEquals(EvaluadorRespuestas.MENSAJE_SIN_RESPUESTAS, reporte.mensajeGeneral)
    }

    @Test
    fun testGenerarPasosSolucionConstruyeLos4PasosYAdaptaFormula() {
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
