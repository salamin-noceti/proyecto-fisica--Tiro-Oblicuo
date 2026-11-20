package org.example.ui.simulacion.graficos

import org.example.model.IngresoDatos
import org.example.service.CalculadorTiroOblicuo
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class PanelGraficosCinematicosTest {

    private val datosEjemplo = IngresoDatos.base.copy(velocidadInicial = 25.0, angulo = 45.0, alturaInicial = 0.0)
    private val calculador = CalculadorTiroOblicuo.instancia
    private val resultadoTeorico = calculador.calcular(datosEjemplo)

    private fun crearPanel(): PanelGraficosCinematicos {
        assertTrue(datosEjemplo.velocidadInicial > 0.0)
        return PanelGraficosCinematicos()
    }

    @Test
    fun testInicializacionCorrectaConVistaExclusivaDe3PestanasCartesianas() {
        val panel = crearPanel()
        val titulos = panel.obtenerTitulosPestanas()
        assertTrue(datosEjemplo.velocidadInicial > 0.0)
        assertNotNull(panel.obtenerRenderizador(0))
        assertNotNull(panel.obtenerRenderizador(1))
        assertNotNull(panel.obtenerRenderizador(2))
        assertEquals(3, titulos.size)
        assertEquals(PanelGraficosCinematicos.TITULO_PESTANA_POSICION, titulos[0])
        assertEquals(PanelGraficosCinematicos.TITULO_PESTANA_VELOCIDAD, titulos[1])
        assertEquals(PanelGraficosCinematicos.TITULO_PESTANA_ACELERACION, titulos[2])
    }

    @Test
    fun testSeleccionarPestanaRenderizaIndividualmenteElGraficoActivo() {
        val panel = crearPanel()
        assertTrue(datosEjemplo.velocidadInicial > 0.0)
        assertEquals(0, panel.pestanaActiva)
        assertSame(panel.obtenerRenderizador(0).canvas, panel.obtenerComponenteActivo())

        panel.seleccionarPestana(1)
        assertEquals(1, panel.pestanaActiva)
        assertSame(panel.obtenerRenderizador(1).canvas, panel.obtenerComponenteActivo())

        panel.seleccionarPestana(2)
        assertEquals(2, panel.pestanaActiva)
        assertSame(panel.obtenerRenderizador(2).canvas, panel.obtenerComponenteActivo())
    }

    @Test
    fun testActualizarPropagaPuntosYTiemposALosTresRenderizadores() {
        val panel = crearPanel()
        panel.configurarEjercicio(datosEjemplo, resultadoTeorico)
        val puntosEstela = calculador.calcularTrayectoria(datosEjemplo, pasos = 5)
        panel.actualizar(resultadoTeorico.tiempoVuelo, puntosEstela, resultadoTeorico, datosEjemplo)

        assertEquals(resultadoTeorico.tiempoVuelo, panel.obtenerRenderizador(0).canvas.obtenerTiempoActual())
        assertEquals(resultadoTeorico.tiempoVuelo, panel.obtenerRenderizador(1).canvas.obtenerTiempoActual())
        assertEquals(resultadoTeorico.tiempoVuelo, panel.obtenerRenderizador(2).canvas.obtenerTiempoActual())
        assertEquals(2, panel.obtenerRenderizador(0).canvas.obtenerSeries().size)
        assertEquals(2, panel.obtenerRenderizador(1).canvas.obtenerSeries().size)
        assertEquals(2, panel.obtenerRenderizador(2).canvas.obtenerSeries().size)
    }

    @Test
    fun testReiniciarLimpiaLasSeriesEnLosTresRenderizadores() {
        val panel = crearPanel()
        panel.configurarEjercicio(datosEjemplo, resultadoTeorico)
        val puntos = listOf(calculador.calcularPosicion(datosEjemplo, 0.5))
        panel.actualizar(0.5, puntos, resultadoTeorico, datosEjemplo)

        panel.reiniciar()
        assertEquals(0.0, panel.obtenerRenderizador(0).canvas.obtenerTiempoActual())
        assertEquals(0.0, panel.obtenerRenderizador(1).canvas.obtenerTiempoActual())
        assertEquals(0.0, panel.obtenerRenderizador(2).canvas.obtenerTiempoActual())
    }
}
