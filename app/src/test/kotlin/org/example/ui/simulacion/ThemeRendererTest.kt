package org.example.ui.simulacion

import org.example.model.Dificultad
import org.example.model.Ejercicio
import org.example.model.IngresoDatos
import org.example.model.RespuestaEstudiante
import org.example.model.ResultadoTiroOblicuo
import org.example.state.AppState
import java.awt.image.BufferedImage
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ThemeRendererTest {

    private val detector = ThemeDetector()
    private val factory = ThemeFactory(detector)

    @Test
    fun `detector identifica correctamente la tematica segun el titulo y enunciado del ejercicio`() {
        val ejercicioFutbol = Ejercicio(
            id = 1,
            titulo = "Futbolista",
            enunciado = "Un futbolista patea un balón con velocidad de 20 m/s a 30° respecto del suelo.",
            dificultad = Dificultad.FACIL,
            datos = IngresoDatos(velocidadInicial = 20.0, angulo = 30.0)
        )
        assertEquals(TematicaSimulacion.FUTBOL, detector.detectar(ejercicioFutbol))

        val ejercicioBasquet = Ejercicio(
            id = 2,
            titulo = "Básquet",
            enunciado = "Un jugador lanza la pelota hacia el aro a 10 m/s con un ángulo de 53°.",
            dificultad = Dificultad.FACIL,
            datos = IngresoDatos(velocidadInicial = 10.0, angulo = 53.0)
        )
        assertEquals(TematicaSimulacion.BASQUET, detector.detectar(ejercicioBasquet))

        val ejercicioCanon = Ejercicio(
            id = 5,
            titulo = "Disparo de cañón",
            enunciado = "Un cañón dispara una bala a 50 m/s con un ángulo de elevación de 30°.",
            dificultad = Dificultad.MEDIO,
            datos = IngresoDatos(velocidadInicial = 50.0, angulo = 30.0)
        )
        assertEquals(TematicaSimulacion.CANON, detector.detectar(ejercicioCanon))

        val ejercicioGolf = Ejercicio(
            id = 10,
            titulo = "Tiro de Golf",
            enunciado = "Un golfista realiza un golpe desde el tee hacia el hoyo en el green.",
            dificultad = Dificultad.FACIL,
            datos = IngresoDatos(velocidadInicial = 35.0, angulo = 25.0)
        )
        assertEquals(TematicaSimulacion.GOLF, detector.detectar(ejercicioGolf))

        val ejercicioEdificio = Ejercicio(
            id = 7,
            titulo = "Lanzamiento desde edificio",
            enunciado = "Desde la terraza de un edificio de 15 m se lanza una piedra a 20 m/s a 30°.",
            dificultad = Dificultad.DIFICIL,
            datos = IngresoDatos(velocidadInicial = 20.0, angulo = 30.0, alturaInicial = 15.0)
        )
        assertEquals(TematicaSimulacion.EDIFICIO, detector.detectar(ejercicioEdificio))

        val ejercicioAtletismo = Ejercicio(
            id = 9,
            titulo = "Atleta de salto en largo",
            enunciado = "Un atleta despega del suelo a 45° con velocidad de 8.94 m/s.",
            dificultad = Dificultad.DIFICIL,
            datos = IngresoDatos(velocidadInicial = 8.94, angulo = 45.0)
        )
        assertEquals(TematicaSimulacion.ATLETISMO, detector.detectar(ejercicioAtletismo))

        // Ejercicio sin deporte explícito pero con altura inicial cae en Edificio
        val ejercicioConAltura = Ejercicio(
            id = 42,
            titulo = "Ejercicio genérico",
            enunciado = "Se lanza un objeto a 20 m/s a 30° desde 12 m de altura.",
            dificultad = Dificultad.MEDIO,
            datos = IngresoDatos(velocidadInicial = 20.0, angulo = 30.0, alturaInicial = 12.0)
        )
        assertEquals(TematicaSimulacion.EDIFICIO, detector.detectar(ejercicioConAltura))

        // Ejercicio nulo o genérico cae en Cañón por defecto
        assertEquals(TematicaSimulacion.CANON, detector.detectar(null))
    }

    @Test
    fun `factory retorna la estrategia adecuada segun el patron Strategy`() {
        assertIs<CanonThemeRenderer>(factory.obtenerPorTematica(TematicaSimulacion.CANON))
        assertIs<FutbolThemeRenderer>(factory.obtenerPorTematica(TematicaSimulacion.FUTBOL))
        assertIs<BasquetThemeRenderer>(factory.obtenerPorTematica(TematicaSimulacion.BASQUET))
        assertIs<GolfThemeRenderer>(factory.obtenerPorTematica(TematicaSimulacion.GOLF))
        assertIs<EdificioThemeRenderer>(factory.obtenerPorTematica(TematicaSimulacion.EDIFICIO))
        assertIs<AtletismoThemeRenderer>(factory.obtenerPorTematica(TematicaSimulacion.ATLETISMO))
    }

    @Test
    fun `todas las estrategias dibujan sin excepciones sobre un contexto grafico 2D`() {
        val imagen = BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB)
        val g2d = imagen.createGraphics()

        val renderizadores: List<ThemeRenderer> = listOf(
            CanonThemeRenderer(),
            FutbolThemeRenderer(),
            BasquetThemeRenderer(),
            GolfThemeRenderer(),
            EdificioThemeRenderer(),
            AtletismoThemeRenderer()
        )

        try {
            for (renderer in renderizadores) {
                // 1. Fondo
                renderer.dibujarFondo(g2d, 800, 600)
                // 2. Suelo
                renderer.dibujarSuelo(g2d, 800, 600, 520.0)
                // 3. Lanzador
                renderer.dibujarLanzador(g2d, 80.0, 520.0, 45.0)
                // 4. Proyectil en vuelo
                renderer.dibujarProyectil(g2d, 200.0, 300.0, false)
                // 5. Proyectil en impacto
                renderer.dibujarProyectil(g2d, 400.0, 520.0, true)
                // 6. Objetivo de llegada
                renderer.dibujarObjetivoLlegada(g2d, 450.0, 520.0, 45.0)

                assertNotNull(renderer.tematica)
            }
        } finally {
            g2d.dispose()
        }
    }

    @Test
    fun `PantallaSimulacion adapta dinamicamente su ThemeRenderer cuando cambia el ejercicio en AppState`() {
        val appState = AppState()
        val pantalla = PantallaSimulacion(appState)

        // Estado inicial por defecto: Cañón
        assertEquals(TematicaSimulacion.CANON, pantalla.temaActual.tematica)
        assertIs<CanonThemeRenderer>(pantalla.temaActual)

        // Transferir ejercicio de Fútbol
        val ejercicioFutbol = Ejercicio(
            id = 1,
            titulo = "Futbolista",
            enunciado = "Un futbolista patea un balón con velocidad de 20 m/s a 30° respecto del suelo.",
            dificultad = Dificultad.FACIL,
            datos = IngresoDatos(velocidadInicial = 20.0, angulo = 30.0)
        )
        val resp = RespuestaEstudiante(35.0, 5.0, 2.0)
        val res = ResultadoTiroOblicuo(5.1, 35.3, 2.04)

        appState.actualizar(ejercicioFutbol, resp, res)

        // Debe haber conmutado automáticamente a Fútbol
        assertEquals(TematicaSimulacion.FUTBOL, pantalla.temaActual.tematica)
        assertIs<FutbolThemeRenderer>(pantalla.temaActual)
        assertTrue(pantalla.labelEjercicioCargado.text.contains("Fútbol"))

        // Transferir ejercicio de Básquet
        val ejercicioBasquet = Ejercicio(
            id = 2,
            titulo = "Básquet",
            enunciado = "Un jugador lanza la pelota hacia el aro a 10 m/s con un ángulo de 53°.",
            dificultad = Dificultad.FACIL,
            datos = IngresoDatos(velocidadInicial = 10.0, angulo = 53.0)
        )
        appState.actualizar(ejercicioBasquet, resp, res)

        // Debe haber conmutado automáticamente a Básquet
        assertEquals(TematicaSimulacion.BASQUET, pantalla.temaActual.tematica)
        assertIs<BasquetThemeRenderer>(pantalla.temaActual)
        assertTrue(pantalla.labelEjercicioCargado.text.contains("Básquet"))
    }

    @Test
    fun `cambiarTematica manual actualiza correctamente el renderizador del lienzo`() {
        val pantalla = PantallaSimulacion()

        pantalla.cambiarTematica(TematicaSimulacion.GOLF)
        assertEquals(TematicaSimulacion.GOLF, pantalla.temaActual.tematica)
        assertIs<GolfThemeRenderer>(pantalla.temaActual)

        pantalla.cambiarTematica(TematicaSimulacion.FUTBOL)
        assertEquals(TematicaSimulacion.FUTBOL, pantalla.temaActual.tematica)
        assertIs<FutbolThemeRenderer>(pantalla.temaActual)
    }
}
