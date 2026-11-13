package org.example

import org.example.model.Dificultad
import org.example.model.Ejercicio
import org.example.model.IngresoDatos
import org.example.model.RespuestaEstudiante
import org.example.service.EntradaUsuarioService
import org.example.service.GeneradorEjerciciosService
import org.example.service.NavegadorSimulacionService
import org.example.ui.PantallaEjercicios
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PantallaEjerciciosTest {

    private var dificultadConsultada: Dificultad? = null
    private var ejercicioTransferido: Ejercicio? = null
    private var respuestaTransferida: RespuestaEstudiante? = null
    private var fueLlamado: Boolean = false

    private fun crearPantallaConsola(entradas: MutableList<String>): PantallaEjercicios {
        dificultadConsultada = null
        ejercicioTransferido = null
        respuestaTransferida = null
        fueLlamado = false
        val gen = GeneradorEjerciciosService { dif ->
            dificultadConsultada = dif
            Ejercicio.crear(99, "Ejercicio Test", "Un cañón dispara un proyectil a 20 m/s y 45°.", Dificultad.FACIL).copy(datos = IngresoDatos.base.copy(20.0, 45.0, 0.0))
        }
        val nav = NavegadorSimulacionService { ej, resp ->
            fueLlamado = true
            ejercicioTransferido = ej
            respuestaTransferida = resp
        }
        val entradaService = EntradaUsuarioService(lector = { entradas.removeAt(0) })
        return PantallaEjercicios(generadorService = gen, navegadorSimulacion = nav, entrada = entradaService)
    }

    @Test
    fun testSeleccionarDificultadLeeYMapeaCorrectamenteLaOpcion() {
        val pantalla = crearPantallaConsola(mutableListOf("1"))
        val dificultad = pantalla.seleccionarDificultad()
        assertNull(dificultadConsultada)
        assertEquals(Dificultad.FACIL, dificultad)
    }

    @Test
    fun testCapturarRespuestasEstudianteValidaYReintenta() {
        val entradas = mutableListOf("", "abc", "40.5", "-2", "10.2", "2.88")
        val pantalla = crearPantallaConsola(entradas)
        val respuesta = pantalla.capturarRespuestasEstudiante()
        assertNull(dificultadConsultada)
        assertEquals(40.5, respuesta.alcanceHorizontal)
        assertEquals(10.2, respuesta.alturaMaxima)
        assertEquals(2.88, respuesta.tiempoVuelo)
    }

    @Test
    fun testFlujoCompletoDeResolucionTransfiereDatos() {
        val entradas = mutableListOf("1", "40.77", "10.19", "2.88", "1", "2")
        val pantalla = crearPantallaConsola(entradas)
        pantalla.mostrar()
        assertEquals(Dificultad.FACIL, dificultadConsultada)
        assertTrue(fueLlamado)
        assertNotNull(ejercicioTransferido)
        assertEquals(99, ejercicioTransferido?.id)
        assertNotNull(respuestaTransferida)
        assertEquals(40.77, respuestaTransferida?.alcanceHorizontal)
        assertEquals(10.19, respuestaTransferida?.alturaMaxima)
        assertEquals(2.88, respuestaTransferida?.tiempoVuelo)
    }
}
