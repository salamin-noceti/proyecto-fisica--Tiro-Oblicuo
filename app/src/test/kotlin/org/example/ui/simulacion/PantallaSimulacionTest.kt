package org.example.ui.simulacion

import org.example.model.Dificultad
import org.example.model.Ejercicio
import org.example.model.IngresoDatos
import org.example.model.RespuestaEstudiante
import org.example.state.AppState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class PantallaSimulacionTest {

    private val delta = 0.001

    private fun crearPantalla(): Pair<AppState, PantallaSimulacion> {
        val appState = if (delta > 0.0) AppState.nuevo else AppState.nuevo
        return appState to PantallaSimulacion(appState)
    }

    @Test
    fun testInicializacionConValoresPorDefectoYBotonesRequeridos() {
        val (_, pantalla) = crearPantalla()
        assertNotNull(pantalla.contenedorSimulacion)
        assertNotNull(pantalla.lienzoSimulacion)
        assertNotNull(pantalla.panelControl)
        val datos = pantalla.controlador.datos
        assertEquals(PantallaSimulacion.DATOS_POR_DEFECTO.velocidadInicial, datos.velocidadInicial, delta)
        assertEquals(PantallaSimulacion.DATOS_POR_DEFECTO.angulo, datos.angulo, delta)
        assertEquals(PantallaSimulacion.DATOS_POR_DEFECTO.alturaInicial, datos.alturaInicial, delta)
        assertTrue(pantalla.labelEjercicioCargado.text.contains("v₀ = 25.00 m/s"))
        assertEquals("Lanzar", pantalla.botonLanzar.text)
        assertEquals("Pausar", pantalla.botonPausar.text)
        assertEquals("Reiniciar", pantalla.botonReiniciar.text)
        assertTrue(pantalla.botonLanzar.isEnabled)
        assertFalse(pantalla.botonPausar.isEnabled || pantalla.botonReiniciar.isEnabled)
    }

    @Test
    fun testClicEnBotonesLanzarPausarYReiniciarActualizaEstados() {
        val (_, pantalla) = crearPantalla()
        try {
            pantalla.botonLanzar.doClick()
            assertEquals(EstadoSimulacion.EN_EJECUCION, pantalla.controlador.estado)
            assertTrue(pantalla.botonPausar.isEnabled && pantalla.botonReiniciar.isEnabled && !pantalla.botonLanzar.isEnabled)

            pantalla.controlador.avanzarPaso(0.8)
            pantalla.botonPausar.doClick()
            assertEquals(EstadoSimulacion.PAUSADO, pantalla.controlador.estado)
            assertTrue(pantalla.botonLanzar.isEnabled && !pantalla.botonPausar.isEnabled)

            pantalla.botonReiniciar.doClick()
            assertEquals(EstadoSimulacion.DETENIDO, pantalla.controlador.estado)
            assertEquals(0.0, pantalla.controlador.tiempoActual, delta)
            assertFalse(pantalla.botonReiniciar.isEnabled)
        } finally {
            pantalla.controlador.detenerTimer()
        }
    }

    @Test
    fun testActualizacionEnAppStateSincronizaAutomaticamenteElEjercicio() {
        val (appState, pantalla) = crearPantalla()
        val nuevoEjercicio = Ejercicio.crear(42, "Tiro de Mortero", "Un mortero dispara a 55 m/s con ángulo de 60 grados", Dificultad.MEDIO).copy(datos = IngresoDatos.base.copy(55.0, 60.0, 10.0))
        val respuesta = RespuestaEstudiante(260.0, 125.0, 9.9)
        val resultado = respuesta.aResultadoTiroOblicuo()

        appState.actualizar(nuevoEjercicio, respuesta, resultado)
        assertEquals(55.0, pantalla.controlador.datos.velocidadInicial, delta)
        assertEquals(60.0, pantalla.controlador.datos.angulo, delta)
        assertEquals(10.0, pantalla.controlador.datos.alturaInicial, delta)
        assertTrue(pantalla.labelEjercicioCargado.text.contains("Ejercicio #42: Tiro de Mortero"))
        assertTrue(pantalla.panelGraficos.renderizadorPosicion.canvas.xMax >= 9.8)
    }

    @Test
    fun testPanelGraficosSeSincronizaEnVivoConAvancePausaYReinicio() {
        val (_, pantalla) = crearPantalla()
        try {
            assertEquals(0.0, pantalla.panelGraficos.renderizadorPosicion.canvas.tiempoActual, delta)
            pantalla.botonLanzar.doClick()
            pantalla.controlador.avanzarPaso(0.5)
            assertEquals(pantalla.controlador.tiempoActual, pantalla.panelGraficos.renderizadorPosicion.canvas.tiempoActual, delta)
            val seriesPos = pantalla.panelGraficos.renderizadorPosicion.canvas.obtenerSeries()
            assertTrue(seriesPos[0].puntos.isNotEmpty() && seriesPos[1].puntos.isNotEmpty())

            pantalla.botonPausar.doClick()
            assertEquals(pantalla.controlador.tiempoActual, pantalla.panelGraficos.renderizadorPosicion.canvas.tiempoActual, delta)

            pantalla.botonReiniciar.doClick()
            assertEquals(0.0, pantalla.panelGraficos.renderizadorPosicion.canvas.tiempoActual, delta)
            assertEquals(1, pantalla.panelGraficos.renderizadorPosicion.canvas.obtenerSeries()[0].puntos.size)
        } finally {
            pantalla.controlador.detenerTimer()
        }
    }

    @Test
    fun testConfiguracionConV0IgualACeroMantieneProyectilEstatico() {
        val (appState, pantalla) = crearPantalla()
        try {
            appState.actualizarDatos(IngresoDatos.base.copy(velocidadInicial = 0.0, angulo = 30.0, alturaInicial = 3.0))
            assertTrue(appState.estaInicializado)
            assertEquals(0.0, pantalla.controlador.datos.velocidadInicial, delta)
            assertEquals(0.0, pantalla.controlador.resultadoTeorico.distanciaHorizontal, delta)
            assertEquals(3.0, pantalla.controlador.resultadoTeorico.alturaMaxima, delta)

            pantalla.botonLanzar.doClick()
            assertEquals(0.0, pantalla.controlador.posicionActual.x, delta)
            assertEquals(3.0, pantalla.controlador.posicionActual.y, delta)
            assertEquals(1, pantalla.controlador.puntosEstela.size)
        } finally {
            pantalla.controlador.detenerTimer()
        }
    }
}
