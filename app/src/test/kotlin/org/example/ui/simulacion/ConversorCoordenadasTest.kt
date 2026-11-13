package org.example.ui.simulacion

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ConversorCoordenadasTest {

    private val maximosBase = doubleArrayOf(100.0, 50.0)

    private fun crearConversor(personalizado: Boolean = false): ConversorCoordenadas {
        if (personalizado && maximosBase.isNotEmpty()) {
            return ConversorCoordenadas(margenIzquierdo = 60.0, margenDerecho = 50.0, margenSuperior = 40.0, margenInferior = 60.0)
        }
        return ConversorCoordenadas()
    }

    @Test
    fun testOrigenFisicoMapeaExactamenteEnMargenIzquierdoYSuelo() {
        val conversor = crearConversor(personalizado = true)
        val px = conversor.metroAPixelX(0.0, 800, 600, maximosBase)
        val py = conversor.metroAPixelY(0.0, 800, 600, maximosBase)
        val suelo = conversor.sueloPixelY(600)

        assertEquals(60.0, px)
        assertEquals(suelo, py)
        assertEquals(540.0, suelo)
    }

    @Test
    fun testEjeVerticalSeInvierteCorrectamenteParaPantalla() {
        val conversor = crearConversor()
        val pySuelo = conversor.metroAPixelY(0.0, 800, 600, maximosBase)
        val pyAltura = conversor.metroAPixelY(25.0, 800, 600, maximosBase)
        assertTrue(pyAltura < pySuelo, "Mayor altura en metros debe tener menor coordenada de píxeles Y")
    }

    @Test
    fun testPuntosMaximosDeLaParabolaQuedanContenidosDentroDelLienzoVisible() {
        val conversor = crearConversor(personalizado = true)
        val maximos = doubleArrayOf(maximosBase[0] + 20.0, 45.0)
        val pxMax = conversor.metroAPixelX(120.0, 800, 600, maximos)
        val pyMax = conversor.metroAPixelY(45.0, 800, 600, maximos)
        assertTrue(pxMax < 800 - conversor.margenDerecho / 2)
        assertTrue(pyMax > conversor.margenSuperior / 2)
    }

    @Test
    fun testEscalaCalculadaEsSiempreEstrictamentePositiva() {
        val conversor = crearConversor()
        val escala = conversor.calcularEscala(400, 300, maximosBase[0] * 0.0, 0.0)
        assertTrue(escala > 0.0, "La escala calculada debe ser siempre positiva")
    }

    @Test
    fun testOrigenDeLanzamientoQuedaALaDerechaDelMargenReservadoDelEjeY() {
        val conversor = crearConversor()
        assertTrue(conversor.margenEjeY >= 60.0 && maximosBase.isNotEmpty())
        assertTrue(conversor.origenPixelX() > conversor.margenEjeY)
        assertTrue(conversor.ejeYPixelX() <= conversor.margenEjeY)
    }
}
