package org.example.ui.simulacion

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ConversorCoordenadasTest {

    @Test
    fun `origen fisico mapea exactamente en margen izquierdo y suelo`() {
        val conversor = ConversorCoordenadas(
            margenIzquierdo = 60.0,
            margenDerecho = 50.0,
            margenSuperior = 40.0,
            margenInferior = 60.0
        )
        val w = 800
        val h = 600
        val xMax = 100.0
        val yMax = 50.0

        val px = conversor.metroAPixelX(0.0, w, h, xMax, yMax)
        val py = conversor.metroAPixelY(0.0, w, h, xMax, yMax)
        val suelo = conversor.sueloPixelY(h)

        assertEquals(60.0, px)
        assertEquals(suelo, py)
        assertEquals(540.0, suelo)
    }

    @Test
    fun `eje vertical se invierte correctamente para pantalla`() {
        val conversor = ConversorCoordenadas()
        val w = 800
        val h = 600
        val xMax = 100.0
        val yMax = 50.0

        val pySuelo = conversor.metroAPixelY(0.0, w, h, xMax, yMax)
        val pyAltura = conversor.metroAPixelY(25.0, w, h, xMax, yMax)

        // En la pantalla, mayor altura física debe traducirse en un menor valor de píxel Y (más arriba)
        assertTrue(pyAltura < pySuelo, "Mayor altura en metros debe tener menor coordenada de píxeles Y")
    }

    @Test
    fun `puntos maximos de la parabola quedan contenidos dentro del lienzo visible`() {
        val conversor = ConversorCoordenadas(
            margenIzquierdo = 60.0,
            margenDerecho = 50.0,
            margenSuperior = 40.0,
            margenInferior = 60.0
        )
        val w = 800
        val h = 600
        val xMax = 120.0
        val yMax = 45.0

        val pxMax = conversor.metroAPixelX(xMax, w, h, xMax, yMax)
        val pyMax = conversor.metroAPixelY(yMax, w, h, xMax, yMax)

        // Comprobar que no se salga por la derecha
        assertTrue(pxMax < w - conversor.margenDerecho / 2, "El alcance máximo no debe desbordar el ancho del lienzo")

        // Comprobar que no se salga por arriba
        assertTrue(pyMax > conversor.margenSuperior / 2, "La altura máxima no debe desbordar el alto superior del lienzo")
    }

    @Test
    fun `escala calculada es siempre estrictamente positiva incluso con valores pequenos`() {
        val conversor = ConversorCoordenadas()
        val escala = conversor.calcularEscala(400, 300, 0.0, 0.0)
        assertTrue(escala > 0.0, "La escala calculada debe ser siempre positiva")
    }

    @Test
    fun `origen de lanzamiento queda a la derecha del margen reservado del eje Y`() {
        val conversor = ConversorCoordenadas()
        assertTrue(
            conversor.margenEjeY >= 60.0,
            "El margen del eje Y debe reservar al menos 60px para las etiquetas numéricas"
        )
        assertTrue(
            conversor.origenPixelX() > conversor.margenEjeY,
            "El origen físico X=0 debe quedar estrictamente a la derecha de la zona del eje Y"
        )
        assertTrue(
            conversor.ejeYPixelX() <= conversor.margenEjeY,
            "La línea del eje Y debe dibujarse dentro del margen reservado"
        )
    }
}
