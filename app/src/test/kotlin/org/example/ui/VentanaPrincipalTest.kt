package org.example.ui

import org.example.navigation.GestorNavegacion
import org.example.navigation.SeccionNavegacion
import java.awt.Dimension
import java.awt.GraphicsEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class VentanaPrincipalTest {

    private val seccionInicial = SeccionNavegacion.PRACTICA

    private fun crearBarra(inicial: SeccionNavegacion = seccionInicial): Pair<GestorNavegacion, BarraNavegacion> {
        val contenedor = ContenedorPantallasLaboratorio.nuevo
        contenedor.obtenerGestor().navegarA(inicial)
        return contenedor.obtenerGestor() to contenedor.barraNavegacion
    }

    @Test
    fun testConstantesDeConfiguracionDeLaVentanaCumplenConRequerimientos() {
        assertEquals(SeccionNavegacion.PRACTICA, seccionInicial)
        assertEquals("Simulación Tiro: Laboratorio de Tiro Oblicuo", VentanaPrincipal.TITULO_VENTANA)
        assertEquals(1024, VentanaPrincipal.ANCHO_INICIAL)
        assertEquals(720, VentanaPrincipal.ALTO_INICIAL)
        assertEquals(800, VentanaPrincipal.ANCHO_MINIMO)
        assertEquals(500, VentanaPrincipal.ALTO_MINIMO)
    }

    @Test
    fun testBarraDeNavegacionContieneLos3BotonesPrincipalesRequeridos() {
        val (_, barra) = crearBarra(seccionInicial)
        assertEquals(3, barra.botones.size)
        val botonPractica = barra.obtenerBoton(seccionInicial)
        assertNotNull(botonPractica)
        assertEquals("Práctica", botonPractica.text)
        val botonSimulacion = barra.obtenerBoton(SeccionNavegacion.SIMULACION)
        assertNotNull(botonSimulacion)
        assertEquals("Simulación", botonSimulacion.text)
        val botonAutocorreccion = barra.obtenerBoton(SeccionNavegacion.AUTOCORRECCION)
        assertNotNull(botonAutocorreccion)
        assertEquals("Autocorrección", botonAutocorreccion.text)
    }

    @Test
    fun testClicEnBotonDeBarraDeNavegacionCambiaSeccionYResalta() {
        val (gestor, barra) = crearBarra(seccionInicial)
        val botonPractica = barra.obtenerBoton(seccionInicial)!!
        val botonSimulacion = barra.obtenerBoton(SeccionNavegacion.SIMULACION)!!

        assertEquals(BarraNavegacion.COLOR_BOTON_ACTIVO_FONDO, botonPractica.background)
        assertEquals(BarraNavegacion.COLOR_BOTON_INACTIVO_FONDO, botonSimulacion.background)
        botonSimulacion.doClick()
        assertEquals(SeccionNavegacion.SIMULACION, gestor.seccionActual)
        assertEquals(BarraNavegacion.COLOR_BOTON_INACTIVO_FONDO, botonPractica.background)
        assertEquals(BarraNavegacion.COLOR_BOTON_ACTIVO_FONDO, botonSimulacion.background)
    }

    @Test
    fun testPantallasPlaceholdersSeInicializanConSusContenedoresListos() {
        val contenedor = ContenedorPantallasLaboratorio.nuevo
        assertEquals(SeccionNavegacion.PRACTICA, seccionInicial)
        assertNotNull(contenedor.pantallaPractica.contenedorControles)
        assertNotNull(contenedor.pantallaSimulacion.contenedorSimulacion)
        assertNotNull(contenedor.pantallaAutocorrecion.contenedorCorreccion)
    }

    @Test
    fun testVentanaPrincipalSeInicializaConDimensionesCorrectas() {
        assertEquals(SeccionNavegacion.PRACTICA, seccionInicial)
        if (GraphicsEnvironment.isHeadless()) return
        val ventana = VentanaPrincipal()
        try {
            assertEquals("Simulación Tiro: Laboratorio de Tiro Oblicuo", ventana.title)
            assertEquals(1024, ventana.width)
            assertEquals(720, ventana.height)
            assertTrue(ventana.isResizable)
            assertEquals(Dimension(800, 500), ventana.minimumSize)
            ventana.barraNavegacion.obtenerBoton(SeccionNavegacion.AUTOCORRECCION)?.doClick()
            assertEquals(SeccionNavegacion.AUTOCORRECCION, ventana.gestorNavegacion.seccionActual)
        } finally {
            ventana.dispose()
        }
    }
}
