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
import kotlin.test.assertTrue

class PantallaEjerciciosTest {

    private class FakeGeneradorService : GeneradorEjerciciosService {
        var dificultadConsultada: Dificultad? = null
        val ejercicioMock = Ejercicio(
            id = 99,
            titulo = "Ejercicio Test",
            enunciado = "Un cañón dispara un proyectil a 20 m/s y 45°.",
            dificultad = Dificultad.FACIL,
            datos = IngresoDatos(velocidadInicial = 20.0, angulo = 45.0, alturaInicial = 0.0)
        )

        override fun obtenerEjercicioPorDificultad(dificultad: Dificultad): Ejercicio {
            dificultadConsultada = dificultad
            return ejercicioMock
        }
    }

    private class FakeNavegadorSimulacion : NavegadorSimulacionService {
        var ejercicioTransferido: Ejercicio? = null
        var respuestaTransferida: RespuestaEstudiante? = null
        var fueLlamado: Boolean = false

        override fun transferirASimulacion(ejercicio: Ejercicio, respuesta: RespuestaEstudiante) {
            fueLlamado = true
            ejercicioTransferido = ejercicio
            respuestaTransferida = respuesta
        }
    }

    @Test
    fun `seleccionarDificultad lee y mapea correctamente la opcion`() {
        val entradas = mutableListOf("1")
        val entradaService = EntradaUsuarioService(lector = { entradas.removeAt(0) })
        val pantalla = PantallaEjercicios(entrada = entradaService)

        val dificultad = pantalla.seleccionarDificultad()
        assertEquals(Dificultad.FACIL, dificultad)
    }

    @Test
    fun `capturarRespuestasEstudiante valida y reintenta ante entradas vacias o no numericas`() {
        val entradas = mutableListOf(
            "",          // Intento inválido: vacío en alcance
            "abc",       // Intento inválido: no numérico en alcance
            "40.5",      // Válido alcance: 40.5
            "-2",        // Intento inválido: negativo en altura
            "10.2",      // Válido altura: 10.2
            "2.88"       // Válido tiempo: 2.88
        )
        val entradaService = EntradaUsuarioService(lector = { entradas.removeAt(0) })
        val pantalla = PantallaEjercicios(entrada = entradaService)

        val respuesta = pantalla.capturarRespuestasEstudiante()

        assertEquals(40.5, respuesta.alcanceHorizontal)
        assertEquals(10.2, respuesta.alturaMaxima)
        assertEquals(2.88, respuesta.tiempoVuelo)
    }

    @Test
    fun `flujo completo de resolucion transfiere datos al presionar Enviar a Simulacion`() {
        val fakeGenerador = FakeGeneradorService()
        val fakeNavegador = FakeNavegadorSimulacion()

        val entradas = mutableListOf(
            "1",        // Selección de dificultad: 1 (Fácil)
            "40.77",    // Entrada: Alcance horizontal
            "10.19",    // Entrada: Altura máxima
            "2.88",     // Entrada: Tiempo de vuelo
            "1",        // Acción: 1 (Enviar a Simulación)
            "2"         // ¿Deseas practicar con otro ejercicio?: 2 (No, salir)
        )
        val entradaService = EntradaUsuarioService(lector = { entradas.removeAt(0) })
        val pantalla = PantallaEjercicios(
            generadorService = fakeGenerador,
            navegadorSimulacion = fakeNavegador,
            entrada = entradaService
        )

        pantalla.mostrar()

        assertEquals(Dificultad.FACIL, fakeGenerador.dificultadConsultada)
        assertTrue(fakeNavegador.fueLlamado)
        assertNotNull(fakeNavegador.ejercicioTransferido)
        assertEquals(99, fakeNavegador.ejercicioTransferido?.id)

        assertNotNull(fakeNavegador.respuestaTransferida)
        assertEquals(40.77, fakeNavegador.respuestaTransferida?.alcanceHorizontal)
        assertEquals(10.19, fakeNavegador.respuestaTransferida?.alturaMaxima)
        assertEquals(2.88, fakeNavegador.respuestaTransferida?.tiempoVuelo)
    }
}
