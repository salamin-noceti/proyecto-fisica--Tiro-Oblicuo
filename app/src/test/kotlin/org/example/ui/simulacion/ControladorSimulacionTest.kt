package org.example.ui.simulacion

import org.example.model.IngresoDatos
import org.example.service.CalculadorTiroOblicuo
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ControladorSimulacionTest {

    private val margen = 0.05

    @Test
    fun `inicializacion correcta con estado detenido y tiempo en cero`() {
        val datos = IngresoDatos(velocidadInicial = 20.0, angulo = 45.0, alturaInicial = 0.0)
        val controlador = ControladorSimulacion(datosIniciales = datos)

        assertEquals(EstadoSimulacion.DETENIDO, controlador.estado)
        assertEquals(0.0, controlador.tiempoActual)
        assertEquals(1, controlador.puntosEstela.size)
        assertEquals(0.0, controlador.posicionActual.x)
        assertEquals(0.0, controlador.posicionActual.y)
    }

    @Test
    fun `lanzar cambia estado a en ejecucion`() {
        val controlador = ControladorSimulacion()
        controlador.lanzar()

        try {
            assertEquals(EstadoSimulacion.EN_EJECUCION, controlador.estado)
        } finally {
            controlador.detenerTimer()
        }
    }

    @Test
    fun `pausar congela momentaneamente la posicion y el tiempo`() {
        val controlador = ControladorSimulacion()
        controlador.lanzar()

        // Avanzar un delta temporal
        controlador.avanzarPaso(0.5)
        val tiempoAntesDePausa = controlador.tiempoActual
        assertTrue(tiempoAntesDePausa > 0.0)

        controlador.pausar()
        assertEquals(EstadoSimulacion.PAUSADO, controlador.estado)
        assertEquals(tiempoAntesDePausa, controlador.tiempoActual)

        // Mientras está pausado, no avanza
        controlador.avanzarPaso(0.5)
        assertEquals(tiempoAntesDePausa, controlador.tiempoActual)
    }

    @Test
    fun `reiniciar restaura proyectil y tiempo a cero y limpia la estela`() {
        val controlador = ControladorSimulacion()
        controlador.lanzar()
        controlador.avanzarPaso(1.0)
        assertTrue(controlador.puntosEstela.size > 1)

        controlador.reiniciar()
        assertEquals(EstadoSimulacion.DETENIDO, controlador.estado)
        assertEquals(0.0, controlador.tiempoActual)
        assertEquals(1, controlador.puntosEstela.size)
        assertEquals(0.0, controlador.posicionActual.x)
        assertEquals(0.0, controlador.posicionActual.y)
    }

    @Test
    fun `detiene automaticamente la simulacion al tocar el suelo`() {
        val datos = IngresoDatos(velocidadInicial = 20.0, angulo = 45.0, alturaInicial = 0.0)
        val calculador = CalculadorTiroOblicuo()
        val resultado = calculador.calcular(datos)
        val tiempoVuelo = resultado.tiempoVuelo

        val controlador = ControladorSimulacion(calculadorFisico = calculador, datosIniciales = datos)
        controlador.lanzar()

        // Avanzar más allá del tiempo total de vuelo
        controlador.avanzarPaso(tiempoVuelo + 2.0)

        assertEquals(EstadoSimulacion.FINALIZADO, controlador.estado)
        assertEquals(tiempoVuelo, controlador.tiempoActual)
        assertEquals(0.0, controlador.posicionActual.y)
        assertTrue(abs(controlador.posicionActual.x - resultado.distanciaHorizontal) < margen)
    }

    @Test
    fun `reconfigurar ejercicio actualiza datos y reinicia el estado`() {
        val controlador = ControladorSimulacion()
        controlador.lanzar()
        controlador.avanzarPaso(0.5)

        val nuevosDatos = IngresoDatos(velocidadInicial = 30.0, angulo = 60.0, alturaInicial = 5.0)
        controlador.configurarEjercicio(nuevosDatos)

        assertEquals(EstadoSimulacion.DETENIDO, controlador.estado)
        assertEquals(0.0, controlador.tiempoActual)
        assertEquals(nuevosDatos, controlador.datos)
        assertEquals(5.0, controlador.posicionActual.y)
    }

    @Test
    fun `listener recibe notificaciones de cambio de estado y avance`() {
        val controlador = ControladorSimulacion()
        var notificado = false
        var estadoRecibido: EstadoSimulacion? = null

        controlador.agregarListener { estado, _, _ ->
            notificado = true
            estadoRecibido = estado
        }

        controlador.lanzar()
        assertTrue(notificado)
        assertEquals(EstadoSimulacion.EN_EJECUCION, estadoRecibido)

        controlador.detenerTimer()
    }

    @Test
    fun `lanzar con v0 igual a cero mantiene el proyectil estatico en (0, y0)`() {
        val datosCero = IngresoDatos(velocidadInicial = 0.0, angulo = 30.0, alturaInicial = 4.0)
        val controlador = ControladorSimulacion(datosIniciales = datosCero)

        try {
            controlador.lanzar()

            assertEquals(EstadoSimulacion.FINALIZADO, controlador.estado)
            assertEquals(0.0, controlador.tiempoActual)
            assertEquals(0.0, controlador.posicionActual.x)
            assertEquals(4.0, controlador.posicionActual.y)
            assertEquals(1, controlador.puntosEstela.size)
        } finally {
            controlador.detenerTimer()
        }
    }
}
