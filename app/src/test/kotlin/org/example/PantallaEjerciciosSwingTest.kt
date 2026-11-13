package org.example

import org.example.model.Dificultad
import org.example.model.Ejercicio
import org.example.model.IngresoDatos
import org.example.model.RespuestaEstudiante
import org.example.service.GeneradorEjerciciosService
import org.example.service.NavegadorSimulacionService
import org.example.ui.PantallaEjerciciosSwing
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class PantallaEjerciciosSwingTest {

    private var dificultadRecibida: Dificultad? = null
    private var navegadorLlamado: Boolean = false
    private var respuestaCapturada: RespuestaEstudiante? = null

    private fun crearPantallaSwing(): PantallaEjerciciosSwing {
        dificultadRecibida = null
        navegadorLlamado = false
        respuestaCapturada = null
        val gen = GeneradorEjerciciosService { dif ->
            dificultadRecibida = dif
            Ejercicio.crear(50, "Lanzamiento GUI Test", "Un proyectil lanzado a 30 m/s y 60°.", Dificultad.MEDIO).copy(datos = IngresoDatos.base.copy(30.0, 60.0, 0.0))
        }
        val nav = NavegadorSimulacionService { _, resp ->
            navegadorLlamado = true
            respuestaCapturada = resp
        }
        return PantallaEjerciciosSwing(generadorService = gen, navegadorSimulacion = nav)
    }

    @Test
    fun testInicializacionDeComponentesYCambioDeDificultadEnSwing() {
        val pantallaSwing = crearPantallaSwing()
        assertEquals("Enviar a Simulación", pantallaSwing.botonEnviar.text)
        pantallaSwing.comboDificultad.selectedItem = "Medio"
        pantallaSwing.cargarEjercicioPorSeleccion()
        assertEquals(Dificultad.MEDIO, dificultadRecibida)
        assertTrue(pantallaSwing.areaEnunciado.text.contains("Lanzamiento GUI Test"))
    }

    @Test
    fun testEnvioASimulacionExitosoConCamposValidosEnSwing() {
        val pantallaSwing = crearPantallaSwing()
        pantallaSwing.campoAlcance.text = "79.5"
        pantallaSwing.campoAltura.text = "34.4"
        pantallaSwing.campoTiempo.text = "5.3"
        val exito = pantallaSwing.procesarEnvioASimulacion()
        assertTrue(exito)
        assertTrue(navegadorLlamado)
        assertNotNull(respuestaCapturada)
        assertEquals(79.5, respuestaCapturada?.alcanceHorizontal)
        assertEquals(34.4, respuestaCapturada?.alturaMaxima)
        assertEquals(5.3, respuestaCapturada?.tiempoVuelo)
    }
}
