package org.example.navigation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GestorNavegacionTest {

    @Test
    fun `estado inicial por defecto es PRACTICA`() {
        val gestor = GestorNavegacion()
        assertEquals(SeccionNavegacion.PRACTICA, gestor.seccionActual)
    }

    @Test
    fun `estado inicial personalizado se respeta`() {
        val gestor = GestorNavegacion(SeccionNavegacion.SIMULACION)
        assertEquals(SeccionNavegacion.SIMULACION, gestor.seccionActual)
    }

    @Test
    fun `navegacion a una seccion diferente cambia el estado y retorna true`() {
        val gestor = GestorNavegacion()
        val resultado = gestor.navegarA(SeccionNavegacion.SIMULACION)

        assertTrue(resultado)
        assertEquals(SeccionNavegacion.SIMULACION, gestor.seccionActual)
    }

    @Test
    fun `navegacion a la misma seccion retorna false y no cambia el estado`() {
        val gestor = GestorNavegacion(SeccionNavegacion.PRACTICA)
        val resultado = gestor.navegarA(SeccionNavegacion.PRACTICA)

        assertFalse(resultado)
        assertEquals(SeccionNavegacion.PRACTICA, gestor.seccionActual)
    }

    @Test
    fun `notifica a los escuchadores cuando cambia la seccion`() {
        val gestor = GestorNavegacion()
        val seccionesNotificadas = mutableListOf<SeccionNavegacion>()

        val listener = NavegacionListener { nuevaSeccion ->
            seccionesNotificadas.add(nuevaSeccion)
        }
        gestor.agregarListener(listener)

        gestor.navegarA(SeccionNavegacion.SIMULACION)
        gestor.navegarA(SeccionNavegacion.AUTOCORRECCION)
        gestor.navegarA(SeccionNavegacion.AUTOCORRECCION) // redundante

        assertEquals(2, seccionesNotificadas.size)
        assertEquals(SeccionNavegacion.SIMULACION, seccionesNotificadas[0])
        assertEquals(SeccionNavegacion.AUTOCORRECCION, seccionesNotificadas[1])
    }

    @Test
    fun `remover listener previene recepcion de notificaciones futuras`() {
        val gestor = GestorNavegacion()
        var notificado = false

        val listener = NavegacionListener {
            notificado = true
        }
        gestor.agregarListener(listener)
        gestor.removerListener(listener)

        gestor.navegarA(SeccionNavegacion.SIMULACION)

        assertFalse(notificado)
    }
}
