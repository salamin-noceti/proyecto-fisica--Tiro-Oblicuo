package org.example.ui.simulacion

import org.example.model.Dificultad
import org.example.model.Ejercicio
import org.example.model.IngresoDatos
import org.example.model.RespuestaEstudiante
import org.example.state.AppState
import kotlin.test.Test
import kotlin.test.assertEquals
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
        val escena = pantalla.obtenerEscena()
        val botones = escena.obtenerPanelControl().leerEstadoBotones()
        assertNotNull(pantalla.obtenerContenedorSimulacion())
        assertNotNull(escena.obtenerLienzo())
        assertNotNull(escena.obtenerPanelControl())
        val datos = escena.obtenerControlador().datos
        assertEquals(PantallaSimulacion.DATOS_POR_DEFECTO.velocidadInicial, datos.velocidadInicial, delta)
        assertEquals(PantallaSimulacion.DATOS_POR_DEFECTO.angulo, datos.angulo, delta)
        assertEquals(PantallaSimulacion.DATOS_POR_DEFECTO.alturaInicial, datos.alturaInicial, delta)
        assertTrue(pantalla.obtenerTextoEjercicioCargado().contains("v₀ = 25.00 m/s"))
        assertEquals("Lanzar", botones["lanzarTexto"])
        assertEquals("Pausar", botones["pausarTexto"])
        assertEquals("Reiniciar", botones["reiniciarTexto"])
        assertEquals("true", botones["lanzarHabilitado"])
        assertEquals("false", botones["pausarHabilitado"])
        assertEquals("false", botones["reiniciarHabilitado"])
    }

    @Test
    fun testClicEnBotonesLanzarPausarYReiniciarActualizaEstados() {
        val (_, pantalla) = crearPantalla()
        val escena = pantalla.obtenerEscena()
        val controlador = escena.obtenerControlador()
        val panelControl = escena.obtenerPanelControl()
        try {
            panelControl.presionarBoton(0)
            assertEquals(EstadoSimulacion.EN_EJECUCION, controlador.obtenerEstado())
            val bEjec = panelControl.leerEstadoBotones()
            assertEquals("true", bEjec["pausarHabilitado"])
            assertEquals("true", bEjec["reiniciarHabilitado"])
            assertEquals("false", bEjec["lanzarHabilitado"])

            controlador.avanzarPaso(0.8)
            panelControl.presionarBoton(1)
            assertEquals(EstadoSimulacion.PAUSADO, controlador.obtenerEstado())
            val bPausa = panelControl.leerEstadoBotones()
            assertEquals("true", bPausa["lanzarHabilitado"])
            assertEquals("false", bPausa["pausarHabilitado"])

            panelControl.presionarBoton(2)
            assertEquals(EstadoSimulacion.DETENIDO, controlador.obtenerEstado())
            assertEquals(0.0, controlador.obtenerTiempoActual(), delta)
            assertEquals("false", panelControl.leerEstadoBotones()["reiniciarHabilitado"])
        } finally {
            controlador.detenerTimer()
        }
    }

    @Test
    fun testActualizacionEnAppStateSincronizaAutomaticamenteElEjercicio() {
        val (appState, pantalla) = crearPantalla()
        val escena = pantalla.obtenerEscena()
        val nuevoEjercicio = Ejercicio.crear(42, "Tiro de Mortero", "Un mortero dispara a 55 m/s con ángulo de 60 grados", Dificultad.MEDIO).copy(datos = IngresoDatos.base.copy(55.0, 60.0, 10.0))
        val respuesta = RespuestaEstudiante(260.0, 125.0, 9.9)
        val resultado = respuesta.aResultadoTiroOblicuo()

        appState.actualizar(nuevoEjercicio, respuesta, resultado)
        val datos = escena.obtenerControlador().datos
        assertEquals(55.0, datos.velocidadInicial, delta)
        assertEquals(60.0, datos.angulo, delta)
        assertEquals(10.0, datos.alturaInicial, delta)
        assertTrue(pantalla.obtenerTextoEjercicioCargado().contains("Ejercicio #42: Tiro de Mortero"))
        assertTrue(escena.obtenerPanelGraficos().obtenerRenderizador(0).canvas.obtenerLimites()[1] >= 9.8)
    }

    @Test
    fun testPanelGraficosSeSincronizaEnVivoConAvancePausaYReinicio() {
        val (_, pantalla) = crearPantalla()
        val escena = pantalla.obtenerEscena()
        val controlador = escena.obtenerControlador()
        val panelControl = escena.obtenerPanelControl()
        val canvasPos = escena.obtenerPanelGraficos().obtenerRenderizador(0).canvas
        try {
            assertEquals(0.0, canvasPos.obtenerTiempoActual(), delta)
            panelControl.presionarBoton(0)
            controlador.avanzarPaso(0.5)
            assertEquals(controlador.obtenerTiempoActual(), canvasPos.obtenerTiempoActual(), delta)
            val seriesPos = canvasPos.obtenerSeries()
            assertTrue(seriesPos[0].puntos.isNotEmpty() && seriesPos[1].puntos.isNotEmpty())

            panelControl.presionarBoton(1)
            assertEquals(controlador.obtenerTiempoActual(), canvasPos.obtenerTiempoActual(), delta)

            panelControl.presionarBoton(2)
            assertEquals(0.0, canvasPos.obtenerTiempoActual(), delta)
            assertEquals(1, canvasPos.obtenerSeries()[0].puntos.size)
        } finally {
            controlador.detenerTimer()
        }
    }

    @Test
    fun testConfiguracionConV0IgualACeroMantieneProyectilEstatico() {
        val (appState, pantalla) = crearPantalla()
        val escena = pantalla.obtenerEscena()
        val controlador = escena.obtenerControlador()
        try {
            appState.actualizarDatos(IngresoDatos.base.copy(velocidadInicial = 0.0, angulo = 30.0, alturaInicial = 3.0))
            assertTrue(appState.estaInicializado())
            assertEquals(0.0, controlador.datos.velocidadInicial, delta)
            assertEquals(0.0, controlador.resultadoTeorico.distanciaHorizontal, delta)
            assertEquals(3.0, controlador.resultadoTeorico.alturaMaxima, delta)

            escena.obtenerPanelControl().presionarBoton(0)
            assertEquals(0.0, controlador.obtenerPosicionActual().x, delta)
            assertEquals(3.0, controlador.obtenerPosicionActual().y, delta)
            assertEquals(1, controlador.obtenerPuntosEstela().size)
        } finally {
            controlador.detenerTimer()
        }
    }
}
