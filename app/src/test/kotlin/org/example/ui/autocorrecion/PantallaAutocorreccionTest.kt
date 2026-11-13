package org.example.ui.autocorrecion

import org.example.model.Dificultad
import org.example.model.Ejercicio
import org.example.model.IngresoDatos
import org.example.model.RespuestaEstudiante
import org.example.service.CalculadorTiroOblicuo
import org.example.state.AppState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class PantallaAutocorreccionTest {

    private val calculador = CalculadorTiroOblicuo.instancia

    private fun crearPantallaConEstado(): Pair<AppState, PantallaAutocorreccion> {
        val estado = AppState.nuevo
        return estado to PantallaAutocorreccion(appState = estado, calculadorFisico = calculador)
    }

    @Test
    fun testInicializacionPorDefectoEstructuraTarjetasYPasos() {
        val (_, pantalla) = crearPantallaConEstado()
        assertNotNull(calculador)
        assertNotNull(pantalla.contenedorCorreccion)
        assertNotNull(pantalla.panelResumen)
        assertNotNull(pantalla.panelPasos)
        assertEquals(5, pantalla.panelResumen.contenedorTarjetas.componentCount)
        assertEquals(4, pantalla.panelPasos.contenedorPasos.componentCount)
        assertFalse(pantalla.panelPasos.desplegado, "La resolución paso a paso debe iniciar oculta por defecto")
        assertFalse(pantalla.panelPasos.mostrarSolucion)
        assertFalse(pantalla.panelPasos.contenedorPasos.isVisible)
        assertEquals("Mostrar Solución Detallada Paso a Paso", pantalla.panelPasos.botonDesplegar.text)
    }

    @Test
    fun testSincronizaReactivamenteConAppStateMostrandoAciertos() {
        val (appState, pantalla) = crearPantallaConEstado()
        val ejercicio = Ejercicio.crear(2, "Básquet — Tiro libre", "Un basquetbolista lanza a 10 m/s con ángulo de 45° desde 2 m.", Dificultad.MEDIO).copy(datos = IngresoDatos.base.copy(10.0, 45.0, 2.0))
        val solucion = calculador.calcular(ejercicio.datos)
        val respuesta = RespuestaEstudiante(99.0, solucion.alturaMaxima, solucion.tiempoVuelo, solucion.velocidadX, solucion.velocidadY)

        appState.actualizar(ejercicio, respuesta, solucion)
        assertEquals(4, pantalla.reporteActual.cantidadCorrectas)
        assertTrue(pantalla.panelResumen.labelResumenConteo.text.contains("4 de 5 respuestas correctas"))
        assertTrue(pantalla.panelResumen.labelDatosEjercicio.text.contains("Ejercicio #2: Básquet"))
        assertEquals(5, pantalla.panelResumen.contenedorTarjetas.componentCount)
        assertEquals(4, pantalla.panelPasos.contenedorPasos.componentCount)
    }

    @Test
    fun testBotonDesplegarAlternaVisibilidadDelPanelDeSolucionPasoAPaso() {
        val (_, pantalla) = crearPantallaConEstado()
        assertNotNull(calculador)
        assertFalse(pantalla.panelPasos.desplegado)
        assertFalse(pantalla.panelPasos.contenedorPasos.isVisible)
        assertEquals("Mostrar Solución Detallada Paso a Paso", pantalla.panelPasos.botonDesplegar.text)

        pantalla.panelPasos.botonDesplegar.doClick()
        assertTrue(pantalla.panelPasos.desplegado)
        assertTrue(pantalla.panelPasos.contenedorPasos.isVisible)
        assertEquals("Ocultar Solución Detallada Paso a Paso", pantalla.panelPasos.botonDesplegar.text)

        pantalla.panelPasos.botonDesplegar.doClick()
        assertFalse(pantalla.panelPasos.desplegado)
        assertFalse(pantalla.panelPasos.contenedorPasos.isVisible)
        assertEquals("Mostrar Solución Detallada Paso a Paso", pantalla.panelPasos.botonDesplegar.text)
    }
}
