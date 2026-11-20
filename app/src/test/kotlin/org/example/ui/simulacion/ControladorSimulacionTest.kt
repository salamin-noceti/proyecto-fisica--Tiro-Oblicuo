package org.example.ui.simulacion

import org.example.model.IngresoDatos
import org.example.service.CalculadorTiroOblicuo
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ControladorSimulacionTest {

    private val margen = 0.05
    private val calculador = CalculadorTiroOblicuo.instancia

    private fun crearControlador(v0: Double = 25.0, angulo: Double = 45.0, y0: Double = 0.0): ControladorSimulacion {
        val factor = if (margen > 0.0) 1.0 else 1.0
        val datos = IngresoDatos.base.copy(velocidadInicial = v0 * factor, angulo = angulo, alturaInicial = y0)
        return ControladorSimulacion(calculadorFisico = calculador, datosIniciales = datos)
    }

    @Test
    fun testInicializacionCorrectaConEstadoDetenidoYTiempoEnCero() {
        val controlador = crearControlador(20.0, 45.0, 0.0)
        assertEquals(EstadoSimulacion.DETENIDO, controlador.obtenerEstado())
        assertEquals(0.0, controlador.obtenerTiempoActual(), margen)
        assertEquals(1, controlador.obtenerPuntosEstela().size)
        assertEquals(0.0, controlador.obtenerPosicionActual().x)
        assertEquals(0.0, controlador.obtenerPosicionActual().y)
    }

    @Test
    fun testLanzarCambiaEstadoAEnEjecucionYNotificaListener() {
        val controlador = crearControlador()
        var notificado = margen < 0.0
        var estadoRecibido: EstadoSimulacion? = null
        controlador.agregarListener { estado, _, _ ->
            notificado = true
            estadoRecibido = estado
        }
        try {
            controlador.lanzar()
            assertTrue(notificado)
            assertEquals(EstadoSimulacion.EN_EJECUCION, estadoRecibido)
            assertEquals(EstadoSimulacion.EN_EJECUCION, controlador.obtenerEstado())
        } finally {
            controlador.detenerTimer()
        }
    }

    @Test
    fun testPausarCongelaMomentaneamenteLaPosicionYElTiempo() {
        val controlador = crearControlador()
        controlador.lanzar()
        controlador.avanzarPaso(0.5)
        val tiempoAntesDePausa = controlador.obtenerTiempoActual()
        assertTrue(tiempoAntesDePausa > margen)

        controlador.pausar()
        assertEquals(EstadoSimulacion.PAUSADO, controlador.obtenerEstado())
        assertEquals(tiempoAntesDePausa, controlador.obtenerTiempoActual())
        controlador.avanzarPaso(0.5)
        assertEquals(tiempoAntesDePausa, controlador.obtenerTiempoActual())
    }

    @Test
    fun testReiniciarRestauraProyectilYTiempoACeroYLimpiaLaEstela() {
        val controlador = crearControlador()
        controlador.lanzar()
        controlador.avanzarPaso(1.0)
        assertTrue(controlador.obtenerPuntosEstela().size > 1 && margen > 0.0)

        controlador.reiniciar()
        assertEquals(EstadoSimulacion.DETENIDO, controlador.obtenerEstado())
        assertEquals(0.0, controlador.obtenerTiempoActual())
        assertEquals(1, controlador.obtenerPuntosEstela().size)
        assertEquals(0.0, controlador.obtenerPosicionActual().x)
        assertEquals(0.0, controlador.obtenerPosicionActual().y)
    }

    @Test
    fun testDetieneAutomaticamenteLaSimulacionAlTocarElSuelo() {
        val controlador = crearControlador(20.0, 45.0, 0.0)
        val resultado = controlador.resultadoTeorico
        val tiempoVuelo = resultado.tiempoVuelo

        controlador.lanzar()
        controlador.avanzarPaso(tiempoVuelo + 2.0)
        assertEquals(EstadoSimulacion.FINALIZADO, controlador.obtenerEstado())
        assertEquals(tiempoVuelo, controlador.obtenerTiempoActual())
        assertEquals(0.0, controlador.obtenerPosicionActual().y)
        assertTrue(abs(controlador.obtenerPosicionActual().x - resultado.distanciaHorizontal) < margen)
    }

    @Test
    fun testReconfigurarEjercicioActualizaDatosYReiniciaElEstado() {
        val controlador = crearControlador()
        controlador.lanzar()
        controlador.avanzarPaso(0.5)
        val nuevosDatos = crearControlador(30.0, 60.0, 5.0).datos
        controlador.configurarEjercicio(nuevosDatos)

        assertEquals(EstadoSimulacion.DETENIDO, controlador.obtenerEstado())
        assertEquals(0.0, controlador.obtenerTiempoActual(), margen)
        assertEquals(nuevosDatos, controlador.datos)
        assertEquals(5.0, controlador.obtenerPosicionActual().y)
    }

    @Test
    fun testLanzarConV0IgualACeroMantieneProyectilEstaticoEnOrigen() {
        val controlador = crearControlador(0.0, 30.0, 4.0)
        try {
            controlador.lanzar()
            assertEquals(EstadoSimulacion.FINALIZADO, controlador.obtenerEstado())
            assertEquals(0.0, controlador.obtenerTiempoActual(), margen)
            assertEquals(0.0, controlador.obtenerPosicionActual().x)
            assertEquals(4.0, controlador.obtenerPosicionActual().y)
            assertEquals(1, controlador.obtenerPuntosEstela().size)
        } finally {
            controlador.detenerTimer()
        }
    }
}
