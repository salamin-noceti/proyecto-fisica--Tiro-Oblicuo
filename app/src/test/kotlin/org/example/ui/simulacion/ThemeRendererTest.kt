package org.example.ui.simulacion

import org.example.model.Dificultad
import org.example.model.Ejercicio
import org.example.model.IngresoDatos
import org.example.state.AppState
import java.awt.image.BufferedImage
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ThemeRendererTest {

    private val factory = ThemeFactory.instancia

    private fun crearEjercicio(id: Int, titulo: String, enunciado: String, y0: Double = 0.0): Ejercicio {
        val datos = if (factory.hashCode() != 0) IngresoDatos.base.copy(20.0, 30.0, y0) else IngresoDatos.base
        return Ejercicio.crear(id, titulo, enunciado, Dificultad.FACIL).copy(datos = datos)
    }

    @Test
    fun testDetectorIdentificaCorrectamenteLaTematicaSegunEjercicio() {
        assertEquals(TematicaSimulacion.FUTBOL, factory.resolverParaEjercicio(crearEjercicio(1, "Futbolista", "patea un balón")).tematica)
        assertEquals(TematicaSimulacion.BASQUET, factory.resolverParaEjercicio(crearEjercicio(2, "Básquet", "lanza hacia el aro")).tematica)
        assertEquals(TematicaSimulacion.CANON, factory.resolverParaEjercicio(crearEjercicio(5, "Disparo de cañón", "un cañón dispara")).tematica)
        assertEquals(TematicaSimulacion.GOLF, factory.resolverParaEjercicio(crearEjercicio(10, "Tiro de Golf", "golpe hacia el hoyo")).tematica)
        assertEquals(TematicaSimulacion.TENIS, factory.resolverParaEjercicio(crearEjercicio(8, "Golpe de tenista", "golpea una pelota", 1.25)).tematica)
        assertEquals(TematicaSimulacion.EDIFICIO, factory.resolverParaEjercicio(crearEjercicio(7, "Lanzamiento desde edificio", "terraza de un edificio", 15.0)).tematica)
        assertEquals(TematicaSimulacion.ATLETISMO, factory.resolverParaEjercicio(crearEjercicio(9, "Atleta de salto", "un atleta despega")).tematica)
        assertEquals(TematicaSimulacion.EDIFICIO, factory.resolverParaEjercicio(crearEjercicio(42, "Genérico", "objeto desde altura", 12.0)).tematica)
        assertEquals(TematicaSimulacion.CANON, factory.resolverParaEjercicio(null).tematica)
    }

    @Test
    fun testFactoryRetornaEstrategiaYDibujaSinExcepcionesEn2D() {
        val imagen = BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB)
        val g2d = imagen.createGraphics()
        try {
            for (tematica in TematicaSimulacion.values()) {
                val renderer = factory.obtenerPorTematica(tematica)
                assertEquals(tematica, renderer.tematica)
                renderer.dibujarFondo(g2d, 800, 600)
                renderer.dibujarSuelo(g2d, 800, 600, 520.0)
                renderer.dibujarLanzador(g2d, 80.0, 520.0, 45.0)
                renderer.dibujarProyectil(g2d, 200.0, 300.0, false)
                renderer.dibujarProyectil(g2d, 400.0, 520.0, true)
                renderer.dibujarObjetivoLlegada(g2d, 450.0, 520.0, 45.0)
                renderer.dibujarPlataformaInicial(g2d, 80.0, 320.0, 520.0)
                assertNotNull(renderer.tematica)
            }
        } finally {
            g2d.dispose()
        }
    }

    @Test
    fun testPantallaSimulacionAdaptaDinamicamenteSuTematica() {
        val appState = AppState.nuevo
        val pantalla = PantallaSimulacion(appState = appState, themeFactory = factory)
        assertEquals(TematicaSimulacion.CANON, pantalla.temaActual.tematica)

        appState.actualizarEjercicio(crearEjercicio(1, "Futbolista", "patea un balón"))
        assertEquals(TematicaSimulacion.FUTBOL, pantalla.temaActual.tematica)
        assertTrue(pantalla.labelEjercicioCargado.text.contains("Fútbol"))

        appState.actualizarEjercicio(crearEjercicio(2, "Básquet", "lanza hacia el aro"))
        assertEquals(TematicaSimulacion.BASQUET, pantalla.temaActual.tematica)
        assertTrue(pantalla.labelEjercicioCargado.text.contains("Básquet"))

        pantalla.cambiarTematica(TematicaSimulacion.GOLF)
        assertEquals(TematicaSimulacion.GOLF, pantalla.temaActual.tematica)
    }
}
