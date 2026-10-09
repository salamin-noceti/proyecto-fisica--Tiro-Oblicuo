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

    @Test
    fun `constantes de configuracion de la ventana cumplen con los requerimientos`() {
        assertEquals("Simulación Tiro: Laboratorio de Tiro Oblicuo", VentanaPrincipal.TITULO_VENTANA)
        assertEquals(1024, VentanaPrincipal.ANCHO_INICIAL)
        assertEquals(720, VentanaPrincipal.ALTO_INICIAL)
        assertEquals(800, VentanaPrincipal.ANCHO_MINIMO)
        assertEquals(500, VentanaPrincipal.ALTO_MINIMO)
    }

    @Test
    fun `barra de navegacion contiene los 3 botones principales requeridos`() {
        val gestor = GestorNavegacion()
        val barra = BarraNavegacion(gestor)

        assertEquals(3, barra.botones.size)

        val botonPractica = barra.obtenerBoton(SeccionNavegacion.PRACTICA)
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
    fun `clic en boton de barra de navegacion cambia seccion en gestor y resalta visualmente`() {
        val gestor = GestorNavegacion(SeccionNavegacion.PRACTICA)
        val barra = BarraNavegacion(gestor)

        val botonPractica = barra.obtenerBoton(SeccionNavegacion.PRACTICA)!!
        val botonSimulacion = barra.obtenerBoton(SeccionNavegacion.SIMULACION)!!

        // Inicialmente, Práctica debe estar resaltada con el color activo
        assertEquals(BarraNavegacion.COLOR_BOTON_ACTIVO_FONDO, botonPractica.background)
        assertEquals(BarraNavegacion.COLOR_BOTON_INACTIVO_FONDO, botonSimulacion.background)

        // Simular clic en botón Simulación
        botonSimulacion.doClick()

        // El gestor debe haber actualizado la sección
        assertEquals(SeccionNavegacion.SIMULACION, gestor.seccionActual)

        // Los estilos deben haberse invertido
        assertEquals(BarraNavegacion.COLOR_BOTON_INACTIVO_FONDO, botonPractica.background)
        assertEquals(BarraNavegacion.COLOR_BOTON_ACTIVO_FONDO, botonSimulacion.background)
    }

    @Test
    fun `pantallas placeholders se inicializan con sus contenedores listos`() {
        val pantallaPractica = PantallaPractica()
        assertNotNull(pantallaPractica.contenedorControles)

        val pantallaSimulacion = PantallaSimulacion()
        assertNotNull(pantallaSimulacion.contenedorSimulacion)

        val pantallaAutocorrecion = PantallaAutocorrecion()
        assertNotNull(pantallaAutocorrecion.contenedorCorreccion)
    }

    @Test
    fun `ventana principal se inicializa con dimensiones correctas y redimensionable`() {
        if (GraphicsEnvironment.isHeadless()) {
            // Entorno sin soporte gráfico (headless)
            return
        }

        val gestor = GestorNavegacion()
        val ventana = VentanaPrincipal(gestor)

        try {
            assertEquals("Simulación Tiro: Laboratorio de Tiro Oblicuo", ventana.title)
            assertEquals(1024, ventana.width)
            assertEquals(720, ventana.height)
            assertTrue(ventana.isResizable)
            assertEquals(Dimension(800, 500), ventana.minimumSize)

            assertNotNull(ventana.pantallaPractica)
            assertNotNull(ventana.pantallaSimulacion)
            assertNotNull(ventana.pantallaAutocorrecion)
            assertNotNull(ventana.barraNavegacion)

            // Validar navegación interactiva reflejada en la ventana
            ventana.barraNavegacion.obtenerBoton(SeccionNavegacion.AUTOCORRECCION)?.doClick()
            assertEquals(SeccionNavegacion.AUTOCORRECCION, gestor.seccionActual)
        } finally {
            ventana.dispose()
        }
    }
}
