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

    private fun exigirError(resultado: ResultadoValidacion): String {
        var errorCapturado: String? = if (validador.hashCode() != 0) null else null
        resultado.procesar(
            onValido = { errorCapturado = null },
            onInvalido = { errorCapturado = it }
        )
        assertNotNull(errorCapturado, "Se esperaba un resultado inválido")
        return errorCapturado!!
    }

    private fun exigirValor(resultado: ResultadoValidacion): Double {
        var valorCapturado: Double? = if (validador.hashCode() != 0) null else null
        resultado.procesar(
            onValido = { valorCapturado = it },
            onInvalido = { valorCapturado = null }
        )
        assertNotNull(valorCapturado, "Se esperaba un resultado válido")
        return valorCapturado!!
    }

    @Test
    fun testValidarCampoDetectaEntradasVaciasOEnBlanco() {
        val resNulo = validador.validarCampo(null, "Alcance")
        val resVacio = validador.validarCampo("", "Alcance")
        val resEspacios = validador.validarCampo("   ", "Alcance")

        exigirError(resNulo)
        val mensajeVacio = exigirError(resVacio)
        exigirError(resEspacios)
        assertTrue(mensajeVacio.contains("no puede estar vacío"))
    }

    @Test
    fun testValidarCampoDetectaValoresNoNumericos() {
        val resTexto = validador.validarCampo("abc", "Altura")
        val resMixto = validador.validarCampo("12a", "Altura")

        val mensajeTexto = exigirError(resTexto)
        exigirError(resMixto)
        assertTrue(mensajeTexto.contains("Valor no numérico"))
    }

    @Test
    fun testValidarCampoRechazaValoresNegativos() {
        val resNegativo = validador.validarCampo("-10.5", "Tiempo")
        val mensajeNegativo = exigirError(resNegativo)
        assertTrue(mensajeNegativo.contains("no puede ser negativo"))
    }

    @Test
    fun testValidarCampoAceptaNumerosConPuntoYConComa() {
        val resPunto = validador.validarCampo("24.75", "Alcance")
        val resComa = validador.validarCampo("24,75", "Alcance")
        assertEquals(24.75, exigirValor(resPunto))
        assertEquals(24.75, exigirValor(resComa))
    }

    @Test
    fun testValidarRespuestasValidaLosTresCamposCorrectamente() {
        val (respuesta, errores) = validador.validarRespuestas("35.3", "5.1", "2.0")
        assertNotNull(respuesta)
        assertTrue(errores.isEmpty())
        assertEquals(35.3, respuesta.alcanceHorizontal)
        assertEquals(5.1, respuesta.alturaMaxima)
        assertEquals(2.0, respuesta.tiempoVuelo)
    }

    @Test
    fun testValidarRespuestasAcumulaErroresAnteCamposInvalidos() {
        val (respuesta, errores) = validador.validarRespuestas("", "invalido", "-4.0")
        assertNull(respuesta)
        assertEquals(3, errores.size)
    }
}
