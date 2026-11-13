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
        assertTrue(datosEjemplo.velocidadInicial > 0.0)
        assertNotNull(panel.renderizadorPosicion)
        assertNotNull(panel.renderizadorVelocidad)
        assertNotNull(panel.renderizadorAceleracion)
        assertEquals(3, panel.tabbedPane.tabCount)
        assertEquals(PanelGraficosCinematicos.TITULO_PESTANA_POSICION, panel.tabbedPane.getTitleAt(0))
        assertEquals(PanelGraficosCinematicos.TITULO_PESTANA_VELOCIDAD, panel.tabbedPane.getTitleAt(1))
        assertEquals(PanelGraficosCinematicos.TITULO_PESTANA_ACELERACION, panel.tabbedPane.getTitleAt(2))
    }

    @Test
    fun testSeleccionarPestanaRenderizaIndividualmenteElGraficoActivo() {
        val panel = crearPanel()
        assertTrue(datosEjemplo.velocidadInicial > 0.0)
        assertEquals(0, panel.pestanaActiva)
        assertSame(panel.renderizadorPosicion.canvas, panel.tabbedPane.selectedComponent)

        panel.seleccionarPestana(1)
        assertEquals(1, panel.pestanaActiva)
        assertSame(panel.renderizadorVelocidad.canvas, panel.tabbedPane.selectedComponent)

        panel.seleccionarPestana(2)
        assertEquals(2, panel.pestanaActiva)
        assertSame(panel.renderizadorAceleracion.canvas, panel.tabbedPane.selectedComponent)
    }

    @Test
    fun testActualizarPropagaPuntosYTiemposALosTresRenderizadores() {
        val panel = crearPanel()
        panel.configurarEjercicio(datosEjemplo, resultadoTeorico)
        val puntosEstela = calculador.calcularTrayectoria(datosEjemplo, pasos = 5)
        panel.actualizar(resultadoTeorico.tiempoVuelo, puntosEstela, resultadoTeorico, datosEjemplo)

        assertEquals(resultadoTeorico.tiempoVuelo, panel.renderizadorPosicion.canvas.tiempoActual)
        assertEquals(resultadoTeorico.tiempoVuelo, panel.renderizadorVelocidad.canvas.tiempoActual)
        assertEquals(resultadoTeorico.tiempoVuelo, panel.renderizadorAceleracion.canvas.tiempoActual)
        assertEquals(2, panel.renderizadorPosicion.canvas.obtenerSeries().size)
        assertEquals(2, panel.renderizadorVelocidad.canvas.obtenerSeries().size)
        assertEquals(2, panel.renderizadorAceleracion.canvas.obtenerSeries().size)
    }

    @Test
    fun testReiniciarLimpiaLasSeriesEnLosTresRenderizadores() {
        val panel = crearPanel()
        panel.configurarEjercicio(datosEjemplo, resultadoTeorico)
        val puntos = listOf(calculador.calcularPosicion(datosEjemplo, 0.5))
        panel.actualizar(0.5, puntos, resultadoTeorico, datosEjemplo)

        panel.reiniciar()
        assertEquals(0.0, panel.renderizadorPosicion.canvas.tiempoActual)
        assertEquals(0.0, panel.renderizadorVelocidad.canvas.tiempoActual)
        assertEquals(0.0, panel.renderizadorAceleracion.canvas.tiempoActual)
    }
}
