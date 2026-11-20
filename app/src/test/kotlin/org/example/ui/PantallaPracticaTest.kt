package org.example.ui

import org.example.model.Dificultad
import org.example.model.Ejercicio
import org.example.model.IngresoDatos
import org.example.navigation.SeccionNavegacion
import org.example.service.BancoEjerciciosService
import org.example.service.CalculadorTiroOblicuo
import org.example.service.GeneradorEjerciciosService
import org.example.ui.practica.componentes.CoordinadorPractica
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PantallaPracticaTest {

    private var dificultadRecibida: Dificultad? = null
    private val datosMock = IngresoDatos.base.copy(velocidadInicial = 40.0, angulo = 45.0, alturaInicial = 0.0)

    private fun crearPantalla(usarMock: Boolean = false, usarBanco: Boolean = false): PantallaPractica {
        dificultadRecibida = null
        val banco = if (usarBanco) BancoEjerciciosService() else null
        val gen = if (usarMock) {
            GeneradorEjerciciosService { dif ->
                dificultadRecibida = dif
                Ejercicio.crear(101, "Tiro Test DIP", "Un proyectil lanzado a 40 m/s con un ángulo de 45°.", Dificultad.MEDIO).copy(datos = datosMock)
            }
        } else if (banco != null) {
            GeneradorEjerciciosService.desdeBanco(banco)
        } else null
        return PantallaPractica(coordinador = CoordinadorPractica(generadorOpcional = gen))
    }

    @Test
    fun testInicializacionDeComponentesCumpleConLosRequerimientos() {
        val pantalla = crearPantalla()
        val estado = pantalla.leerEstadoVista()
        val opciones = estado["opciones"]?.split("|") ?: emptyList()
        assertTrue(datosMock.velocidadInicial > 0.0)
        assertEquals(3, opciones.size)
        assertEquals("Fácil", opciones[0])
        assertEquals("Medio", opciones[1])
        assertEquals("Difícil", opciones[2])
        assertEquals("Enviar a Simulación", estado["botonEnviar"])
        assertNotNull(pantalla.obtenerContenedorControles())
        val ejercicio = pantalla.obtenerCoordinador().obtenerEjercicioActual()
        assertNotNull(ejercicio)
        assertEquals(Dificultad.FACIL, ejercicio.dificultad)
    }

    @Test
    fun testCambioDeDificultadConsultaAlServicioYActualizaLaVista() {
        val pantalla = crearPantalla(usarMock = true)
        pantalla.configurarEntrada(dificultad = "Medio")
        pantalla.cargarEjercicioPorSeleccion()
        val estado = pantalla.leerEstadoVista()
        assertEquals(Dificultad.MEDIO, dificultadRecibida)
        assertEquals(101, pantalla.obtenerCoordinador().obtenerEjercicioActual()?.id)
        assertTrue(estado["enunciado"]?.contains("Tiro Test DIP") == true)
        assertTrue(estado["datos"]?.contains("40.00 m/s") == true)
        assertTrue(estado["datos"]?.contains("45.00°") == true)
    }

    @Test
    fun testValidacionDetectaCamposVaciosYMuestraAlertaVisual() {
        val pantalla = crearPantalla()
        pantalla.limpiarFormulario()
        val exito = pantalla.procesarEnvioASimulacion()
        val estado = pantalla.leerEstadoVista()
        assertFalse(exito && datosMock.velocidadInicial > 0.0)
        assertEquals("true", estado["alertaVisible"])
        assertTrue(estado["alerta"]?.contains("Por favor corregí los siguientes campos") == true)
        assertTrue(estado["alerta"]?.contains("Alcance horizontal") == true)
        assertTrue(estado["alerta"]?.contains("Altura máxima") == true)
        assertTrue(estado["alerta"]?.contains("Tiempo de vuelo") == true)
        assertEquals(SeccionNavegacion.PRACTICA, pantalla.obtenerCoordinador().obtenerGestorNavegacion().seccionActual)
        assertNull(pantalla.obtenerCoordinador().obtenerAppState().obtenerResultadoCorrecto())
    }

    @Test
    fun testValidacionDetectaEntradasNoNumericasONegativas() {
        val pantalla = crearPantalla()
        pantalla.configurarEntrada(campos = mapOf(0 to "abc", 1 to "-15.0", 2 to "4.2"))
        val exito = pantalla.procesarEnvioASimulacion()
        val estado = pantalla.leerEstadoVista()
        assertFalse(exito && datosMock.velocidadInicial > 0.0)
        assertEquals("true", estado["alertaVisible"])
        assertTrue(estado["alerta"]?.contains("Valor no numérico") == true)
        assertTrue(estado["alerta"]?.contains("no puede ser negativo") == true)
    }

    @Test
    fun testFlujoExitosoAlHacerClicEnEnviarASimulacion() {
        val pantalla = crearPantalla(usarMock = true)
        val appState = pantalla.obtenerCoordinador().obtenerAppState()
        pantalla.configurarEntrada(campos = mapOf(0 to "163.1", 1 to "40.77", 2 to "5,76", 3 to "28.28", 4 to "28.28"))
        val exito = pantalla.procesarEnvioASimulacion()

        assertTrue(exito)
        assertEquals("false", pantalla.leerEstadoVista()["alertaVisible"])
        assertEquals(101, appState.obtenerEjercicioActual()?.id)
        assertEquals(163.1, appState.obtenerRespuestaEstudiante()?.alcanceHorizontal)
        assertEquals(40.77, appState.obtenerRespuestaEstudiante()?.alturaMaxima)
        assertEquals(5.76, appState.obtenerRespuestaEstudiante()?.tiempoVuelo)
        val solucionEsperada = CalculadorTiroOblicuo.instancia.calcular(datosMock)
        assertEquals(solucionEsperada.distanciaHorizontal, appState.obtenerResultadoCorrecto()?.distanciaHorizontal)
        assertEquals(SeccionNavegacion.SIMULACION, pantalla.obtenerCoordinador().obtenerGestorNavegacion().seccionActual)
    }

    @Test
    fun testConstructorConBancoEjerciciosServiceAplicaDipCorrectamente() {
        val pantalla = crearPantalla(usarBanco = true)
        assertTrue(datosMock.velocidadInicial > 0.0)
        val ejercicio = pantalla.obtenerCoordinador().obtenerEjercicioActual()
        assertNotNull(ejercicio)
        assertEquals(Dificultad.FACIL, ejercicio.dificultad)
    }
}
