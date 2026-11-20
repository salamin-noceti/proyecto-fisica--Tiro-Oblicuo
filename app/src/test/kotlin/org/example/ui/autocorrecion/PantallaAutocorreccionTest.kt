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
        return estado to PantallaAutocorreccion(CoordinadorAutocorreccion(appState = estado, calculadorFisico = calculador))
    }

    @Test
    fun testInicializacionPorDefectoEstructuraTarjetasYPasos() {
        val (_, pantalla) = crearPantallaConEstado()
        val resumen = pantalla.obtenerPanelResumen().obtenerResumenVisual()
        val pasos = pantalla.obtenerPanelPasos().obtenerEstadoVisual()
        assertNotNull(calculador)
        assertNotNull(pantalla.obtenerContenedorCorreccion())
        assertNotNull(pantalla.obtenerPanelResumen())
        assertNotNull(pantalla.obtenerPanelPasos())
        assertEquals("5", resumen["tarjetas"])
        assertEquals("4", pasos["cantidadPasos"])
        assertFalse(pantalla.obtenerPanelPasos().desplegado, "La resolución paso a paso debe iniciar oculta por defecto")
        assertFalse(pantalla.obtenerPanelPasos().mostrarSolucion)
        assertEquals("false", pasos["pasosVisible"])
        assertEquals("Mostrar Solución Detallada Paso a Paso", pasos["textoBoton"])
    }

    @Test
    fun testSincronizaReactivamenteConAppStateMostrandoAciertos() {
        val (appState, pantalla) = crearPantallaConEstado()
        val ejercicio = Ejercicio.crear(2, "Básquet — Tiro libre", "Un basquetbolista lanza a 10 m/s con ángulo de 45° desde 2 m.", Dificultad.MEDIO).copy(datos = IngresoDatos.base.copy(10.0, 45.0, 2.0))
        val solucion = calculador.calcular(ejercicio.datos)
        val respuesta = RespuestaEstudiante(99.0, solucion.alturaMaxima, solucion.tiempoVuelo, solucion.velocidadX, solucion.velocidadY)

        appState.actualizar(ejercicio, respuesta, solucion)
        val resumen = pantalla.obtenerPanelResumen().obtenerResumenVisual()
        val pasos = pantalla.obtenerPanelPasos().obtenerEstadoVisual()
        assertEquals(4, pantalla.obtenerReporteActual().cantidadCorrectas)
        assertTrue(resumen["conteo"]?.contains("4 de 5 respuestas correctas") == true)
        assertTrue(resumen["datos"]?.contains("Ejercicio #2: Básquet") == true)
        assertEquals("5", resumen["tarjetas"])
        assertEquals("4", pasos["cantidadPasos"])
    }

    @Test
    fun testBotonDesplegarAlternaVisibilidadDelPanelDeSolucionPasoAPaso() {
        val (_, pantalla) = crearPantallaConEstado()
        val panelPasos = pantalla.obtenerPanelPasos()
        assertNotNull(calculador)
        assertFalse(panelPasos.desplegado)
        assertEquals("false", panelPasos.obtenerEstadoVisual()["pasosVisible"])
        assertEquals("Mostrar Solución Detallada Paso a Paso", panelPasos.obtenerEstadoVisual()["textoBoton"])

        panelPasos.presionarBotonDesplegar()
        assertTrue(panelPasos.desplegado)
        assertEquals("true", panelPasos.obtenerEstadoVisual()["pasosVisible"])
        assertEquals("Ocultar Solución Detallada Paso a Paso", panelPasos.obtenerEstadoVisual()["textoBoton"])

        panelPasos.presionarBotonDesplegar()
        assertFalse(panelPasos.desplegado)
        assertEquals("false", panelPasos.obtenerEstadoVisual()["pasosVisible"])
        assertEquals("Mostrar Solución Detallada Paso a Paso", panelPasos.obtenerEstadoVisual()["textoBoton"])
    }
}
