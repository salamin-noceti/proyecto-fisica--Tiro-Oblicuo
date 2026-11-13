package org.example

import org.example.model.RespuestaEstudiante
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RespuestaEstudianteTest {

    private fun crearRespuesta(alcance: Double, altura: Double, tiempo: Double): RespuestaEstudiante {
        return RespuestaEstudiante(alcanceHorizontal = alcance, alturaMaxima = altura, tiempoVuelo = tiempo)
    }

    @Test
    fun testCrearRespuestaValidaYConvertirAResultadoTiroOblicuo() {
        val respuesta = crearRespuesta(100.0, 25.0, 4.5)
        val resultado = respuesta.aResultadoTiroOblicuo()
        assertEquals(100.0, resultado.distanciaHorizontal)
        assertEquals(25.0, resultado.alturaMaxima)
        assertEquals(4.5, resultado.tiempoVuelo)
    }

    @Test
    fun testImpedirConstruccionConValoresNegativos() {
        assertFailsWith<IllegalArgumentException> { crearRespuesta(-1.0, 10.0, 5.0) }
        assertFailsWith<IllegalArgumentException> { crearRespuesta(10.0, -1.0, 5.0) }
        assertFailsWith<IllegalArgumentException> { crearRespuesta(10.0, 10.0, -1.0) }
    }
}
