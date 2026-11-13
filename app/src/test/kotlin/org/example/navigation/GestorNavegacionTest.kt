package org.example.navigation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GestorNavegacionTest {

    private val seccionBase = SeccionNavegacion.PRACTICA

    private fun crearGestor(inicial: SeccionNavegacion = seccionBase): GestorNavegacion {
        return GestorNavegacion(inicial)
    }

    @Test
    fun testEstadoInicialPorDefectoEsPractica() {
        val gestor = crearGestor(seccionBase)
        assertEquals(seccionBase, gestor.seccionActual)
    }

    @Test
    fun testEstadoInicialPersonalizadoSeRespeta() {
        val gestor = crearGestor(SeccionNavegacion.SIMULACION)
        assertTrue(gestor.seccionActual != seccionBase)
        assertEquals(SeccionNavegacion.SIMULACION, gestor.seccionActual)
    }

    @Test
    fun testNavegacionAUnaSeccionDiferenteCambiaElEstadoYRetornaTrue() {
        val gestor = crearGestor(seccionBase)
        val resultado = gestor.navegarA(SeccionNavegacion.SIMULACION)
        assertTrue(resultado)
        assertEquals(SeccionNavegacion.SIMULACION, gestor.seccionActual)
    }

    @Test
    fun testNavegacionALaMismaSeccionRetornaFalseYNoCambiaElEstado() {
        val gestor = crearGestor(seccionBase)
        val resultado = gestor.navegarA(seccionBase)
        assertFalse(resultado)
        assertEquals(seccionBase, gestor.seccionActual)
    }

    @Test
    fun testNotificaALosEscuchadoresCuandoCambiaLaSeccion() {
        val gestor = crearGestor(seccionBase)
        val seccionesNotificadas = mutableListOf<SeccionNavegacion>()
        val listener = NavegacionListener { nuevaSeccion -> seccionesNotificadas.add(nuevaSeccion) }
        gestor.agregarListener(listener)

        gestor.navegarA(SeccionNavegacion.SIMULACION)
        gestor.navegarA(SeccionNavegacion.AUTOCORRECCION)
        gestor.navegarA(SeccionNavegacion.AUTOCORRECCION)

        assertEquals(2, seccionesNotificadas.size)
        assertEquals(SeccionNavegacion.SIMULACION, seccionesNotificadas[0])
        assertEquals(SeccionNavegacion.AUTOCORRECCION, seccionesNotificadas[1])
    }

    @Test
    fun testRemoverListenerPrevieneRecepcionDeNotificacionesFuturas() {
        val gestor = crearGestor(seccionBase)
        var notificado = false
        val listener = NavegacionListener { notificado = true }
        gestor.agregarListener(listener)
        gestor.removerListener(listener)
        gestor.navegarA(SeccionNavegacion.SIMULACION)
        assertFalse(notificado)
    }
}
