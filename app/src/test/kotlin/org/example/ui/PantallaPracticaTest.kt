package org.example.ui

import org.example.model.Dificultad
import org.example.model.Ejercicio
import org.example.model.IngresoDatos
import org.example.navigation.GestorNavegacion
import org.example.navigation.SeccionNavegacion
import org.example.service.BancoEjerciciosService
import org.example.service.CalculadorTiroOblicuo
import org.example.service.GeneradorEjerciciosService
import org.example.state.AppState
import java.awt.GraphicsEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PantallaPracticaTest {

    private class FakeGeneradorService : GeneradorEjerciciosService {
        var dificultadRecibida: Dificultad? = null
        val ejercicioMock = Ejercicio(
            id = 101,
            titulo = "Tiro Test DIP",
            enunciado = "Un proyectil lanzado a 40 m/s con un ángulo de 45°.",
            dificultad = Dificultad.MEDIO,
            datos = IngresoDatos(velocidadInicial = 40.0, angulo = 45.0, alturaInicial = 0.0)
        )

        override fun obtenerEjercicioPorDificultad(dificultad: Dificultad): Ejercicio {
            dificultadRecibida = dificultad
            return ejercicioMock
        }
    }

    @Test
    fun `inicializacion de componentes cumple con los requerimientos`() {
        val pantalla = PantallaPractica()

        // 1. Selector de dificultad con 3 opciones
        assertEquals(3, pantalla.comboDificultad.itemCount)
        assertEquals("Fácil", pantalla.comboDificultad.getItemAt(0))
        assertEquals("Medio", pantalla.comboDificultad.getItemAt(1))
        assertEquals("Difícil", pantalla.comboDificultad.getItemAt(2))

        // 2. Botón visible con el texto exacto requerido
        assertEquals("Enviar a Simulación", pantalla.botonEnviar.text)

        // 3. Contenedores y campos instanciados
        assertNotNull(pantalla.contenedorControles)
        assertNotNull(pantalla.campoAlcance)
        assertNotNull(pantalla.campoAltura)
        assertNotNull(pantalla.campoTiempo)
        assertNotNull(pantalla.campoVelocidadX)
        assertNotNull(pantalla.campoVelocidadY)
        assertNotNull(pantalla.areaEnunciado)
        assertNotNull(pantalla.labelDatosFisicos)

        // 4. Ejercicio inicial cargado (Fácil por defecto)
        assertNotNull(pantalla.ejercicioActual)
        assertEquals(Dificultad.FACIL, pantalla.ejercicioActual?.dificultad)
    }

    @Test
    fun `cambio de dificultad consulta al servicio y actualiza la vista`() {
        val fakeService = FakeGeneradorService()
        val pantalla = PantallaPractica(generadorService = fakeService)

        pantalla.comboDificultad.selectedItem = "Medio"
        pantalla.cargarEjercicioPorSeleccion()

        assertEquals(Dificultad.MEDIO, fakeService.dificultadRecibida)
        assertEquals(101, pantalla.ejercicioActual?.id)
        assertTrue(pantalla.areaEnunciado.text.contains("Tiro Test DIP"))
        assertTrue(pantalla.labelDatosFisicos.text.contains("40.00 m/s"))
        assertTrue(pantalla.labelDatosFisicos.text.contains("45.00°"))
    }

    @Test
    fun `validacion detecta campos vacios y muestra alerta visual en pantalla`() {
        val pantalla = PantallaPractica()
        pantalla.limpiarFormulario()

        // Intentar enviar formulario vacío
        val exito = pantalla.procesarEnvioASimulacion()

        assertFalse(exito)
        assertTrue(pantalla.panelAlerta.isVisible)
        assertTrue(pantalla.labelAlerta.text.contains("Por favor corregí los siguientes campos"))
        assertTrue(pantalla.labelAlerta.text.contains("Alcance horizontal"))
        assertTrue(pantalla.labelAlerta.text.contains("Altura máxima"))
        assertTrue(pantalla.labelAlerta.text.contains("Tiempo de vuelo"))

        // La navegación NO debe haber cambiado
        assertEquals(SeccionNavegacion.PRACTICA, pantalla.gestorNavegacion.seccionActual)
        assertNull(pantalla.appState.resultadoCorrecto)
    }

    @Test
    fun `validacion detecta entradas no numericas o negativas`() {
        val pantalla = PantallaPractica()

        pantalla.campoAlcance.text = "abc"
        pantalla.campoAltura.text = "-15.0"
        pantalla.campoTiempo.text = "4.2"

        val exito = pantalla.procesarEnvioASimulacion()

        assertFalse(exito)
        assertTrue(pantalla.panelAlerta.isVisible)
        assertTrue(pantalla.labelAlerta.text.contains("Valor no numérico"))
        assertTrue(pantalla.labelAlerta.text.contains("no puede ser negativo"))
    }

    @Test
    fun `flujo exitoso al hacer clic en Enviar a Simulacion`() {
        val gestor = GestorNavegacion()
        val appState = gestor.appState
        val fakeService = FakeGeneradorService()
        val pantalla = PantallaPractica(
            gestorNavegacion = gestor,
            appState = appState,
            generadorService = fakeService
        )

        // Cargar datos válidos en las casillas (soporta punto y coma decimal)
        pantalla.campoAlcance.text = "163.1"
        pantalla.campoAltura.text = "40.77"
        pantalla.campoTiempo.text = "5,76"
        pantalla.campoVelocidadX.text = "28.28"
        pantalla.campoVelocidadY.text = "28.28"

        val exito = pantalla.procesarEnvioASimulacion()

        // 1. Envío exitoso y alerta visual oculta
        assertTrue(exito)
        assertFalse(pantalla.panelAlerta.isVisible)

        // 2. Cargar en el estado compartido (AppState) el ejercicio actual
        assertNotNull(appState.ejercicioActual)
        assertEquals(101, appState.ejercicioActual?.id)

        // 3. Cargar en el estado compartido las respuestas calculadas por el alumno
        assertNotNull(appState.respuestaEstudiante)
        assertEquals(163.1, appState.respuestaEstudiante?.alcanceHorizontal)
        assertEquals(40.77, appState.respuestaEstudiante?.alturaMaxima)
        assertEquals(5.76, appState.respuestaEstudiante?.tiempoVuelo)
        assertEquals(28.28, appState.respuestaEstudiante?.velocidadX)
        assertEquals(28.28, appState.respuestaEstudiante?.velocidadY)

        // 4. Cargar en el estado compartido los datos teóricos correctos (ResultadoTiroOblicuo)
        assertNotNull(appState.resultadoCorrecto)
        val solucionEsperada = CalculadorTiroOblicuo().calcular(fakeService.ejercicioMock.datos)
        assertEquals(solucionEsperada.distanciaHorizontal, appState.resultadoCorrecto?.distanciaHorizontal)
        assertEquals(solucionEsperada.alturaMaxima, appState.resultadoCorrecto?.alturaMaxima)
        assertEquals(solucionEsperada.tiempoVuelo, appState.resultadoCorrecto?.tiempoVuelo)

        // 5. Cambiar automáticamente la pestaña activa de navegación a Simulación
        assertEquals(SeccionNavegacion.SIMULACION, gestor.seccionActual)
    }

    @Test
    fun `integracion completa de navegacion dentro de VentanaPrincipal`() {
        if (GraphicsEnvironment.isHeadless()) {
            return
        }

        val ventana = VentanaPrincipal()
        try {
            // Inicialmente en Práctica
            assertEquals(SeccionNavegacion.PRACTICA, ventana.gestorNavegacion.seccionActual)

            // Cargar respuestas válidas en la pantalla de práctica de la ventana
            ventana.pantallaPractica.campoAlcance.text = "50.0"
            ventana.pantallaPractica.campoAltura.text = "12.5"
            ventana.pantallaPractica.campoTiempo.text = "3.2"

            // Simular clic en el botón "Enviar a Simulación"
            ventana.pantallaPractica.botonEnviar.doClick()

            // Debe haber cambiado automáticamente a Simulación
            assertEquals(SeccionNavegacion.SIMULACION, ventana.gestorNavegacion.seccionActual)
            assertNotNull(ventana.appState.ejercicioActual)
            assertNotNull(ventana.appState.respuestaEstudiante)
            assertNotNull(ventana.appState.resultadoCorrecto)
        } finally {
            ventana.dispose()
        }
    }

    @Test
    fun `constructor con BancoEjerciciosService aplica Inversion de Dependencias correctamente`() {
        val banco = BancoEjerciciosService()
        val pantalla = PantallaPractica(bancoEjerciciosService = banco)

        assertNotNull(pantalla.ejercicioActual)
        assertEquals(Dificultad.FACIL, pantalla.ejercicioActual?.dificultad)
    }
}
