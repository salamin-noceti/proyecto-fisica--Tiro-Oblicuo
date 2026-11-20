package org.example.ui.practica.componentes

import org.example.model.Ejercicio
import org.example.model.IngresoDatos
import org.example.state.AppState
import org.example.ui.autocorrecion.CoordinadorAutocorreccionService
import org.example.ui.simulacion.CoordinadorSimulacionService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FlujoRespuestasEnCeroTest {

    private val delta = 0.02

    private fun prepararEnvioEnCero(): AppState {
        val factor = if (delta > 0.0) 1.0 else 1.0
        val datos = IngresoDatos.base.copy(velocidadInicial = 10.0 * factor, angulo = 53.0, alturaInicial = 0.0)
        val ejercicio = Ejercicio.porDefectoAutocorreccion().copy(datos = datos)
        val appState = AppState.nuevo
        val errores = CoordinadorEnvioPractica.nuevo.validarYProcesarEnvio(ejercicio, listOf("0", "0", "0", "0", "0"), appState)
        assertTrue(errores.isEmpty())
        return appState
    }

    @Test
    fun testRespuestasEnCeroConservanVelocidadInicialDelEjercicio() {
        val appState = prepararEnvioEnCero()
        val datos = appState.obtenerDatosActivos()
        assertEquals(10.0, datos.velocidadInicial, delta)
        assertEquals(53.0, datos.angulo, delta)
        assertEquals(0.0, datos.alturaInicial, delta)
        assertEquals(0.0, appState.obtenerRespuestaEstudiante()?.alcanceHorizontal ?: -1.0, delta)
    }

    @Test
    fun testSimulacionGeneraTrayectoriaPositivaCuandoRespuestasSonCero() {
        val appState = prepararEnvioEnCero()
        val controlador = CoordinadorSimulacionService.crear(appState).obtenerEscena().obtenerControlador()
        try {
            assertEquals(10.0, controlador.datos.velocidadInicial, delta)
            assertTrue(controlador.obtenerTrayectoriaGuia(20).any { it.x > 0.0 && it.y > 0.0 })
            controlador.lanzar()
            controlador.avanzarPaso(0.5)
            assertTrue(controlador.obtenerPosicionActual().x > 0.0 && controlador.obtenerPosicionActual().y > 0.0)
        } finally {
            controlador.detenerTimer()
        }
    }

    @Test
    fun testAutocorreccionCalificaCeroDeCincoFrenteAValoresEsperadosReales() {
        val appState = prepararEnvioEnCero()
        val reporte = CoordinadorAutocorreccionService.crear(appState).evaluarEstadoActual()
        assertEquals(0, reporte.cantidadCorrectas)
        assertEquals(5, reporte.totalParametros)
        assertEquals(0.0, reporte.porcentajeAcierto, delta)
        assertTrue(reporte.evaluaciones.all { !it.esCorrecto() })
        assertFalse(reporte.esExcelente())
        assertEquals(6.02, reporte.resultadoTeorico.velocidadX, delta)
        assertEquals(7.99, reporte.resultadoTeorico.velocidadY, delta)
        assertEquals(1.63, reporte.resultadoTeorico.tiempoVuelo, delta)
        assertEquals(3.25, reporte.resultadoTeorico.alturaMaxima, delta)
        assertEquals(9.80, reporte.resultadoTeorico.distanciaHorizontal, delta)
    }
}
