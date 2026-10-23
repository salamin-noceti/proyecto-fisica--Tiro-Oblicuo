package org.example.ui.simulacion.graficos

import org.example.model.IngresoDatos
import org.example.service.CalculadorTiroOblicuo
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class PanelGraficosCinematicosTest {

    private val datosEjemplo = IngresoDatos(velocidadInicial = 25.0, angulo = 45.0, alturaInicial = 0.0)
    private val calculador = CalculadorTiroOblicuo()
    private val resultadoTeorico = calculador.calcular(datosEjemplo)

    @Test
    fun `inicializacion correcta con 3 pestanas cartesianas y componentes instanciados`() {
        val panel = PanelGraficosCinematicos()

        assertNotNull(panel.renderizadorPosicion)
        assertNotNull(panel.renderizadorVelocidad)
        assertNotNull(panel.renderizadorAceleracion)

        assertEquals(3, panel.tabbedPane.tabCount)
        assertTrue(panel.tabbedPane.getTitleAt(0).contains("Posición"))
        assertTrue(panel.tabbedPane.getTitleAt(1).contains("Velocidad"))
        assertTrue(panel.tabbedPane.getTitleAt(2).contains("Aceleración"))
    }

    @Test
    fun `conmutacion fluida entre modo Pestanas y modo Grilla`() {
        val panel = PanelGraficosCinematicos()

        assertEquals(3, panel.tabbedPane.tabCount)
        assertEquals(0, panel.panelGrilla.componentCount)

        panel.mostrarModoGrilla()
        assertEquals(0, panel.tabbedPane.tabCount)
        assertEquals(3, panel.panelGrilla.componentCount)

        panel.mostrarModoPestanas()
        assertEquals(3, panel.tabbedPane.tabCount)
        assertEquals(0, panel.panelGrilla.componentCount)
    }

    @Test
    fun `actualizar propaga puntos y tiempos a los tres renderizadores`() {
        val panel = PanelGraficosCinematicos()
        panel.configurarEjercicio(datosEjemplo, resultadoTeorico)

        val puntosEstela = calculador.calcularTrayectoria(datosEjemplo, pasos = 5)
        panel.actualizar(
            tiempoActual = resultadoTeorico.tiempoVuelo,
            puntosEstela = puntosEstela,
            resultadoTeorico = resultadoTeorico,
            datos = datosEjemplo
        )

        assertEquals(resultadoTeorico.tiempoVuelo, panel.renderizadorPosicion.canvas.tiempoActual)
        assertEquals(resultadoTeorico.tiempoVuelo, panel.renderizadorVelocidad.canvas.tiempoActual)
        assertEquals(resultadoTeorico.tiempoVuelo, panel.renderizadorAceleracion.canvas.tiempoActual)

        assertEquals(2, panel.renderizadorPosicion.canvas.obtenerSeries().size)
        assertEquals(2, panel.renderizadorVelocidad.canvas.obtenerSeries().size)
        assertEquals(2, panel.renderizadorAceleracion.canvas.obtenerSeries().size)
    }

    @Test
    fun `reiniciar limpia las series en los tres renderizadores`() {
        val panel = PanelGraficosCinematicos()
        panel.configurarEjercicio(datosEjemplo, resultadoTeorico)

        val puntos = listOf(calculador.calcularPosicion(datosEjemplo, 0.5))
        panel.actualizar(0.5, puntos, resultadoTeorico, datosEjemplo)

        panel.reiniciar()

        assertEquals(0.0, panel.renderizadorPosicion.canvas.tiempoActual)
        assertEquals(0.0, panel.renderizadorVelocidad.canvas.tiempoActual)
        assertEquals(0.0, panel.renderizadorAceleracion.canvas.tiempoActual)
    }
}
