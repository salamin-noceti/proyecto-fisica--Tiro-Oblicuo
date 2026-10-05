package org.example

import org.example.model.RespuestaEstudiante
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RespuestaEstudianteTest {

    @Test
    fun `crear respuesta valida y convertir a ResultadoTiroOblicuo`() {
        val respuesta = RespuestaEstudiante(
            alcanceHorizontal = 100.0,
            alturaMaxima = 25.0,
            tiempoVuelo = 4.5
        )

        val resultado = respuesta.aResultadoTiroOblicuo()
        assertEquals(100.0, resultado.distanciaHorizontal)
        assertEquals(25.0, resultado.alturaMaxima)
        assertEquals(4.5, resultado.tiempoVuelo)
    }

    @Test
    fun `impedir construccion con valores negativos`() {
        assertFailsWith<IllegalArgumentException> {
            RespuestaEstudiante(-1.0, 10.0, 5.0)
        }
        assertFailsWith<IllegalArgumentException> {
            RespuestaEstudiante(10.0, -1.0, 5.0)
        }
        assertFailsWith<IllegalArgumentException> {
            RespuestaEstudiante(10.0, 10.0, -1.0)
        }
    }
}
