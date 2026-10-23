package org.example.ui.simulacion

import org.example.model.Dificultad
import org.example.model.Ejercicio
import org.example.model.IngresoDatos
import org.example.model.RespuestaEstudiante
import org.example.model.ResultadoTiroOblicuo
import org.example.state.AppState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class PantallaSimulacionTest {

    @Test
    fun `inicializacion con valores por defecto seguros cuando AppState esta vacio`() {
        val appState = AppState()
        val pantalla = PantallaSimulacion(appState)

        assertNotNull(pantalla.contenedorSimulacion)
        assertNotNull(pantalla.lienzoSimulacion)
        assertNotNull(pantalla.panelControl)

        val datos = pantalla.controlador.datos
        assertEquals(PantallaSimulacion.DATOS_POR_DEFECTO.velocidadInicial, datos.velocidadInicial)
        assertEquals(PantallaSimulacion.DATOS_POR_DEFECTO.angulo, datos.angulo)
        assertEquals(PantallaSimulacion.DATOS_POR_DEFECTO.alturaInicial, datos.alturaInicial)

        assertTrue(pantalla.labelEjercicioCargado.text.contains("v₀ = 25.00 m/s"))
        assertTrue(pantalla.labelEjercicioCargado.text.contains("θ = 45.00°"))
    }

    @Test
    fun `los tres botones de control poseen exactamente los nombres requeridos`() {
        val pantalla = PantallaSimulacion()

        assertEquals("Lanzar", pantalla.botonLanzar.text)
        assertEquals("Pausar", pantalla.botonPausar.text)
        assertEquals("Reiniciar", pantalla.botonReiniciar.text)
    }

    @Test
    fun `estados de habilitacion inicial de los botones corresponden a simulacion detenida`() {
        val pantalla = PantallaSimulacion()

        assertTrue(pantalla.botonLanzar.isEnabled, "Lanzar debe estar habilitado al inicio")
        assertFalse(pantalla.botonPausar.isEnabled, "Pausar debe estar deshabilitado al inicio")
        assertFalse(pantalla.botonReiniciar.isEnabled, "Reiniciar debe estar deshabilitado al inicio")
    }

    @Test
    fun `clic en boton Lanzar inicia la simulacion y actualiza estados de botones`() {
        val pantalla = PantallaSimulacion()

        try {
            pantalla.botonLanzar.doClick()

            assertEquals(EstadoSimulacion.EN_EJECUCION, pantalla.controlador.estado)
            assertFalse(pantalla.botonLanzar.isEnabled)
            assertTrue(pantalla.botonPausar.isEnabled)
            assertTrue(pantalla.botonReiniciar.isEnabled)
        } finally {
            pantalla.controlador.detenerTimer()
        }
    }

    @Test
    fun `clic en boton Pausar congela la simulacion y permite reanudar con Lanzar`() {
        val pantalla = PantallaSimulacion()

        try {
            pantalla.botonLanzar.doClick()
            pantalla.botonPausar.doClick()

            assertEquals(EstadoSimulacion.PAUSADO, pantalla.controlador.estado)
            assertTrue(pantalla.botonLanzar.isEnabled, "Lanzar debe habilitarse para permitir reanudar")
            assertFalse(pantalla.botonPausar.isEnabled)
            assertTrue(pantalla.botonReiniciar.isEnabled)
        } finally {
            pantalla.controlador.detenerTimer()
        }
    }

    @Test
    fun `clic en boton Reiniciar regresa al instante inicial t=0`() {
        val pantalla = PantallaSimulacion()

        try {
            pantalla.botonLanzar.doClick()
            pantalla.controlador.avanzarPaso(0.8)
            assertTrue(pantalla.controlador.tiempoActual > 0.0)

            pantalla.botonReiniciar.doClick()

            assertEquals(EstadoSimulacion.DETENIDO, pantalla.controlador.estado)
            assertEquals(0.0, pantalla.controlador.tiempoActual)
            assertTrue(pantalla.botonLanzar.isEnabled)
            assertFalse(pantalla.botonPausar.isEnabled)
            assertFalse(pantalla.botonReiniciar.isEnabled)
        } finally {
            pantalla.controlador.detenerTimer()
        }
    }

    @Test
    fun `actualizacion en AppState sincroniza automaticamente el ejercicio y sus condiciones`() {
        val appState = AppState()
        val pantalla = PantallaSimulacion(appState)

        val nuevoEjercicio = Ejercicio(
            id = 42,
            titulo = "Tiro de Mortero",
            enunciado = "Un mortero dispara a 55 m/s con ángulo de 60 grados",
            dificultad = Dificultad.MEDIO,
            datos = IngresoDatos(velocidadInicial = 55.0, angulo = 60.0, alturaInicial = 10.0)
        )
        val respuesta = RespuestaEstudiante(alcanceHorizontal = 260.0, alturaMaxima = 125.0, tiempoVuelo = 9.9)
        val resultado = ResultadoTiroOblicuo(alturaMaxima = 125.6, distanciaHorizontal = 265.4, tiempoVuelo = 9.88)

        // Simular transferencia desde Issue 6
        appState.actualizar(nuevoEjercicio, respuesta, resultado)

        // Verificar que la pantalla se actualizó
        assertEquals(55.0, pantalla.controlador.datos.velocidadInicial)
        assertEquals(60.0, pantalla.controlador.datos.angulo)
        assertEquals(10.0, pantalla.controlador.datos.alturaInicial)
        assertTrue(pantalla.labelEjercicioCargado.text.contains("Ejercicio #42: Tiro de Mortero"))
        assertTrue(pantalla.labelEjercicioCargado.text.contains("v₀ = 55.00 m/s"))

        // Verificar que el panel de gráficos cinemáticos actualizó sus escalas
        assertTrue(pantalla.panelGraficos.renderizadorPosicion.canvas.xMax >= 9.8)
    }

    @Test
    fun `panelGraficos se sincroniza en vivo con el avance, pausa y reinicio de la simulacion`() {
        val pantalla = PantallaSimulacion()

        try {
            assertNotNull(pantalla.panelGraficos)
            assertNotNull(pantalla.splitSimulacion)

            // Estado inicial: tiempo 0
            assertEquals(0.0, pantalla.panelGraficos.renderizadorPosicion.canvas.tiempoActual)

            // 1. Lanzar simulación y avanzar un paso
            pantalla.botonLanzar.doClick()
            pantalla.controlador.avanzarPaso(0.5)

            assertTrue(pantalla.controlador.tiempoActual > 0.0)
            assertEquals(
                pantalla.controlador.tiempoActual,
                pantalla.panelGraficos.renderizadorPosicion.canvas.tiempoActual
            )
            val seriesPos = pantalla.panelGraficos.renderizadorPosicion.canvas.obtenerSeries()
            assertTrue(seriesPos[0].puntos.isNotEmpty(), "La serie x(t) debe contener puntos trazados")
            assertTrue(seriesPos[1].puntos.isNotEmpty(), "La serie y(t) debe contener puntos trazados")

            // 2. Pausar: congela las curvas en el tiempo actual alcanzado
            pantalla.botonPausar.doClick()
            assertEquals(EstadoSimulacion.PAUSADO, pantalla.controlador.estado)
            val tiempoCongelado = pantalla.panelGraficos.renderizadorPosicion.canvas.tiempoActual
            assertEquals(pantalla.controlador.tiempoActual, tiempoCongelado)

            // 3. Reiniciar: limpia las trayectorias de los gráficos y vuelve a t=0
            pantalla.botonReiniciar.doClick()
            assertEquals(EstadoSimulacion.DETENIDO, pantalla.controlador.estado)
            assertEquals(0.0, pantalla.panelGraficos.renderizadorPosicion.canvas.tiempoActual)
            val seriesReiniciadas = pantalla.panelGraficos.renderizadorPosicion.canvas.obtenerSeries()
            assertEquals(1, seriesReiniciadas[0].puntos.size, "Al reiniciar solo queda el punto t=0")
        } finally {
            pantalla.controlador.detenerTimer()
        }
    }

    @Test
    fun `configuracion con v0 igual a 0 en AppState no aplica fallback a 35_3m y mantiene proyectil estatico`() {
        val appState = AppState()
        val pantalla = PantallaSimulacion(appState)

        try {
            appState.actualizarDatos(IngresoDatos(velocidadInicial = 0.0, angulo = 30.0, alturaInicial = 3.0))

            assertTrue(appState.estaInicializado)
            assertEquals(0.0, pantalla.controlador.datos.velocidadInicial)
            assertEquals(0.0, pantalla.controlador.resultadoTeorico.distanciaHorizontal)
            assertEquals(3.0, pantalla.controlador.resultadoTeorico.alturaMaxima)
            assertEquals(0.0, pantalla.controlador.resultadoTeorico.tiempoVuelo)

            pantalla.botonLanzar.doClick()
            assertEquals(0.0, pantalla.controlador.posicionActual.x)
            assertEquals(3.0, pantalla.controlador.posicionActual.y)
            assertEquals(1, pantalla.controlador.puntosEstela.size)
        } finally {
            pantalla.controlador.detenerTimer()
        }
    }
}
