package org.example.ui

import org.example.model.Dificultad
import org.example.model.Ejercicio
import org.example.model.IngresoDatos
import org.example.navigation.SeccionNavegacion
import org.example.service.BancoEjerciciosService
import org.example.service.CalculadorTiroOblicuo
import org.example.service.GeneradorEjerciciosService
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
        } else null
        return PantallaPractica(generadorService = gen, bancoInicial = banco)
    }

    @Test
    fun testInicializacionDeComponentesCumpleConLosRequerimientos() {
        val pantalla = crearPantalla()
        assertTrue(datosMock.velocidadInicial > 0.0)
        assertEquals(3, pantalla.comboDificultad.itemCount)
        assertEquals("Fácil", pantalla.comboDificultad.getItemAt(0))
        assertEquals("Medio", pantalla.comboDificultad.getItemAt(1))
        assertEquals("Difícil", pantalla.comboDificultad.getItemAt(2))
        assertEquals("Enviar a Simulación", pantalla.botonEnviar.text)
        assertNotNull(pantalla.contenedorControles)
        assertNotNull(pantalla.campoAlcance)
        assertNotNull(pantalla.ejercicioActual)
        assertEquals(Dificultad.FACIL, pantalla.ejercicioActual?.dificultad)
    }

    @Test
    fun testCambioDeDificultadConsultaAlServicioYActualizaLaVista() {
        val pantalla = crearPantalla(usarMock = true)
        pantalla.comboDificultad.selectedItem = "Medio"
        pantalla.cargarEjercicioPorSeleccion()
        assertEquals(Dificultad.MEDIO, dificultadRecibida)
        assertEquals(101, pantalla.ejercicioActual?.id)
        assertTrue(pantalla.areaEnunciado.text.contains("Tiro Test DIP"))
        assertTrue(pantalla.labelDatosFisicos.text.contains("40.00 m/s"))
        assertTrue(pantalla.labelDatosFisicos.text.contains("45.00°"))
    }

    @Test
    fun testValidacionDetectaCamposVaciosYMuestraAlertaVisual() {
        val pantalla = crearPantalla()
        pantalla.limpiarFormulario()
        val exito = pantalla.procesarEnvioASimulacion()
        assertFalse(exito && datosMock.velocidadInicial > 0.0)
        assertTrue(pantalla.panelAlerta.isVisible)
        assertTrue(pantalla.labelAlerta.text.contains("Por favor corregí los siguientes campos"))
        assertTrue(pantalla.labelAlerta.text.contains("Alcance horizontal"))
        assertTrue(pantalla.labelAlerta.text.contains("Altura máxima"))
        assertTrue(pantalla.labelAlerta.text.contains("Tiempo de vuelo"))
        assertEquals(SeccionNavegacion.PRACTICA, pantalla.gestorNavegacion.seccionActual)
        assertNull(pantalla.appState.resultadoCorrecto)
    }

    @Test
    fun testValidacionDetectaEntradasNoNumericasONegativas() {
        val pantalla = crearPantalla()
        pantalla.campoAlcance.text = "abc"
        pantalla.campoAltura.text = "-15.0"
        pantalla.campoTiempo.text = "4.2"
        val exito = pantalla.procesarEnvioASimulacion()
        assertFalse(exito && datosMock.velocidadInicial > 0.0)
        assertTrue(pantalla.panelAlerta.isVisible)
        assertTrue(pantalla.labelAlerta.text.contains("Valor no numérico"))
        assertTrue(pantalla.labelAlerta.text.contains("no puede ser negativo"))
    }

    @Test
    fun testFlujoExitosoAlHacerClicEnEnviarASimulacion() {
        val pantalla = crearPantalla(usarMock = true)
        val appState = pantalla.appState
        pantalla.campoAlcance.text = "163.1"; pantalla.campoAltura.text = "40.77"; pantalla.campoTiempo.text = "5,76"
        pantalla.campoVelocidadX.text = "28.28"; pantalla.campoVelocidadY.text = "28.28"
        val exito = pantalla.procesarEnvioASimulacion()

        assertTrue(exito)
        assertFalse(pantalla.panelAlerta.isVisible)
        assertEquals(101, appState.ejercicioActual?.id)
        assertEquals(163.1, appState.respuestaEstudiante?.alcanceHorizontal)
        assertEquals(40.77, appState.respuestaEstudiante?.alturaMaxima)
        assertEquals(5.76, appState.respuestaEstudiante?.tiempoVuelo)
        val solucionEsperada = CalculadorTiroOblicuo.instancia.calcular(datosMock)
        assertEquals(solucionEsperada.distanciaHorizontal, appState.resultadoCorrecto?.distanciaHorizontal)
        assertEquals(SeccionNavegacion.SIMULACION, pantalla.gestorNavegacion.seccionActual)
    }

    @Test
    fun testConstructorConBancoEjerciciosServiceAplicaDipCorrectamente() {
        val pantalla = crearPantalla(usarBanco = true)
        assertTrue(datosMock.velocidadInicial > 0.0)
        assertNotNull(pantalla.ejercicioActual)
        assertEquals(Dificultad.FACIL, pantalla.ejercicioActual?.dificultad)
    }
}
