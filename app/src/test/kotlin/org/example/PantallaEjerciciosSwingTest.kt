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
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class PantallaEjerciciosSwingTest {

    private class FakeGenerador : GeneradorEjerciciosService {
        var dificultadRecibida: Dificultad? = null
        val ejercicio = Ejercicio(
            id = 50,
            titulo = "Lanzamiento GUI Test",
            enunciado = "Un proyectil lanzado a 30 m/s y 60°.",
            dificultad = Dificultad.MEDIO,
            datos = IngresoDatos(velocidadInicial = 30.0, angulo = 60.0, alturaInicial = 0.0)
        )

        override fun obtenerEjercicioPorDificultad(dificultad: Dificultad): Ejercicio {
            dificultadRecibida = dificultad
            return ejercicio
        }
    }

    private class FakeNavegador : NavegadorSimulacionService {
        var llamado = false
        var respuestaCapturada: RespuestaEstudiante? = null

        override fun transferirASimulacion(ejercicio: Ejercicio, respuesta: RespuestaEstudiante) {
            llamado = true
            respuestaCapturada = respuesta
        }
    }

    @Test
    fun `inicializacion de componentes y cambio de dificultad en Swing`() {
        val fakeGen = FakeGenerador()
        val fakeNav = FakeNavegador()

        val pantallaSwing = PantallaEjerciciosSwing(
            generadorService = fakeGen,
            navegadorSimulacion = fakeNav
        )

        // Verificamos que el botón contenga el texto exacto requerido
        assertEquals("Enviar a Simulación", pantallaSwing.botonEnviar.text)

        // Cambiamos a Medio
        pantallaSwing.comboDificultad.selectedItem = "Medio"
        pantallaSwing.cargarEjercicioPorSeleccion()

        assertEquals(Dificultad.MEDIO, fakeGen.dificultadRecibida)
        assertTrue(pantallaSwing.areaEnunciado.text.contains("Lanzamiento GUI Test"))
    }

    @Test
    fun `envio a simulacion exitoso con campos validos en Swing`() {
        val fakeGen = FakeGenerador()
        val fakeNav = FakeNavegador()

        val pantallaSwing = PantallaEjerciciosSwing(
            generadorService = fakeGen,
            navegadorSimulacion = fakeNav
        )

        pantallaSwing.campoAlcance.text = "79.5"
        pantallaSwing.campoAltura.text = "34.4"
        pantallaSwing.campoTiempo.text = "5.3"

        val exito = pantallaSwing.procesarEnvioASimulacion()

        assertTrue(exito)
        assertTrue(fakeNav.llamado)
        assertNotNull(fakeNav.respuestaCapturada)
        assertEquals(79.5, fakeNav.respuestaCapturada?.alcanceHorizontal)
        assertEquals(34.4, fakeNav.respuestaCapturada?.alturaMaxima)
        assertEquals(5.3, fakeNav.respuestaCapturada?.tiempoVuelo)
    }
}
