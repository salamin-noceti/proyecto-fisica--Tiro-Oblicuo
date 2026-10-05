package org.example

import org.example.service.ResultadoValidacion
import org.example.service.ValidadorEntradaRespuesta
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ValidadorEntradaRespuestaTest {

    private val validador = ValidadorEntradaRespuesta()

    @Test
    fun `validarCampo detecta entradas vacias o en blanco`() {
        val resNulo = validador.validarCampo(null, "Alcance")
        val resVacio = validador.validarCampo("", "Alcance")
        val resEspacios = validador.validarCampo("   ", "Alcance")

        assertTrue(resNulo is ResultadoValidacion.Invalido)
        assertTrue(resVacio is ResultadoValidacion.Invalido)
        assertTrue(resEspacios is ResultadoValidacion.Invalido)

        assertTrue(resVacio.mensajeError.contains("no puede estar vacío"))
    }

    @Test
    fun `validarCampo detecta valores no numericos`() {
        val resTexto = validador.validarCampo("abc", "Altura")
        val resMixto = validador.validarCampo("12a", "Altura")

        assertTrue(resTexto is ResultadoValidacion.Invalido)
        assertTrue(resMixto is ResultadoValidacion.Invalido)
        assertTrue(resTexto.mensajeError.contains("Valor no numérico"))
    }

    @Test
    fun `validarCampo rechaza valores negativos`() {
        val resNegativo = validador.validarCampo("-10.5", "Tiempo")

        assertTrue(resNegativo is ResultadoValidacion.Invalido)
        assertTrue(resNegativo.mensajeError.contains("no puede ser negativo"))
    }

    @Test
    fun `validarCampo acepta numeros con punto y con coma`() {
        val resPunto = validador.validarCampo("24.75", "Alcance")
        val resComa = validador.validarCampo("24,75", "Alcance")

        assertTrue(resPunto is ResultadoValidacion.Valido)
        assertTrue(resComa is ResultadoValidacion.Valido)
        assertEquals(24.75, resPunto.valor)
        assertEquals(24.75, resComa.valor)
    }

    @Test
    fun `validarRespuestas valida los tres campos correctamente`() {
        val (respuesta, errores) = validador.validarRespuestas("35.3", "5.1", "2.0")

        assertNotNull(respuesta)
        assertTrue(errores.isEmpty())
        assertEquals(35.3, respuesta.alcanceHorizontal)
        assertEquals(5.1, respuesta.alturaMaxima)
        assertEquals(2.0, respuesta.tiempoVuelo)
    }

    @Test
    fun `validarRespuestas acumula errores ante campos invalidos`() {
        val (respuesta, errores) = validador.validarRespuestas("", "invalido", "-4.0")

        assertNull(respuesta)
        assertEquals(3, errores.size)
    }
}
