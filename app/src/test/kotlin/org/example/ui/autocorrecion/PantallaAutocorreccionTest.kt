package org.example.ui.autocorrecion

import org.example.model.Dificultad
import org.example.model.Ejercicio
import org.example.model.IngresoDatos
import org.example.model.RespuestaEstudiante
import org.example.service.CalculadorTiroOblicuo
import org.example.state.AppState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class PantallaAutocorreccionTest {

    @Test
    fun `inicializacion por defecto estructura las 5 tarjetas comparativas y los 4 pasos de solucion`() {
        val appState = AppState()
        val pantalla = PantallaAutocorreccion(appState = appState)

        assertNotNull(pantalla.contenedorCorreccion)
        assertNotNull(pantalla.panelResumen)
        assertNotNull(pantalla.panelPasos)

        assertEquals(5, pantalla.panelResumen.contenedorTarjetas.componentCount)
        assertEquals(4, pantalla.panelPasos.contenedorPasos.componentCount)
        assertFalse(pantalla.panelPasos.desplegado, "La resolución paso a paso debe iniciar oculta por defecto")
        assertFalse(pantalla.panelPasos.mostrarSolucion)
        assertFalse(pantalla.panelPasos.contenedorPasos.isVisible)
        assertEquals("Mostrar Solución Detallada Paso a Paso", pantalla.panelPasos.botonDesplegar.text)
    }

    @Test
    fun `sincroniza reactivamente con AppState mostrando aciertos, colores e indicadores`() {
        val appState = AppState()
        val pantalla = PantallaAutocorreccion(appState = appState)
        val calculador = CalculadorTiroOblicuo()

        val ejercicio = Ejercicio(
            id = 2,
            titulo = "Básquet — Tiro libre",
            enunciado = "Un basquetbolista lanza a 10 m/s con ángulo de 45° desde 2 m.",
            dificultad = Dificultad.MEDIO,
            datos = IngresoDatos(velocidadInicial = 10.0, angulo = 45.0, alturaInicial = 2.0)
        )
        val solucion = calculador.calcular(ejercicio.datos)

        // 4 respuestas correctas y 1 incorrecta (alcanceHorizontal)
        val respuesta = RespuestaEstudiante(
            alcanceHorizontal = 99.0,
            alturaMaxima = solucion.alturaMaxima,
            tiempoVuelo = solucion.tiempoVuelo,
            velocidadX = solucion.velocidadX,
            velocidadY = solucion.velocidadY
        )

        appState.actualizar(ejercicio, respuesta, solucion)

        assertEquals(4, pantalla.reporteActual.cantidadCorrectas)
        assertTrue(pantalla.panelResumen.labelResumenConteo.text.contains("4 de 5 respuestas correctas"))
        assertTrue(pantalla.panelResumen.labelDatosEjercicio.text.contains("Ejercicio #2: Básquet"))
        assertEquals(5, pantalla.panelResumen.contenedorTarjetas.componentCount)
        assertEquals(4, pantalla.panelPasos.contenedorPasos.componentCount)
    }

    @Test
    fun `boton desplegar alterna la visibilidad del panel de solucion paso a paso`() {
        val pantalla = PantallaAutocorreccion()

        // 1. Inicia oculto por defecto con texto "Mostrar Solución Detallada Paso a Paso"
        assertFalse(pantalla.panelPasos.desplegado)
        assertFalse(pantalla.panelPasos.contenedorPasos.isVisible)
        assertEquals("Mostrar Solución Detallada Paso a Paso", pantalla.panelPasos.botonDesplegar.text)

        // 2. Al presionar se despliega y cambia a "Ocultar Solución Detallada Paso a Paso"
        pantalla.panelPasos.botonDesplegar.doClick()
        assertTrue(pantalla.panelPasos.desplegado)
        assertTrue(pantalla.panelPasos.contenedorPasos.isVisible)
        assertEquals("Ocultar Solución Detallada Paso a Paso", pantalla.panelPasos.botonDesplegar.text)

        // 3. Al volver a presionar se oculta nuevamente
        pantalla.panelPasos.botonDesplegar.doClick()
        assertFalse(pantalla.panelPasos.desplegado)
        assertFalse(pantalla.panelPasos.contenedorPasos.isVisible)
        assertEquals("Mostrar Solución Detallada Paso a Paso", pantalla.panelPasos.botonDesplegar.text)
    }
}
