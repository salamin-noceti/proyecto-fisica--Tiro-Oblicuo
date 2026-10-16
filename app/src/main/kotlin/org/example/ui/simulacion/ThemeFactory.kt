package org.example.ui.simulacion

import org.example.model.Ejercicio

/**
 * Factoría para resolver y proveer las estrategias [ThemeRenderer]
 * desacoplando la creación de los renderizadores temáticos.
 *
 * @param detector Detector de temática para análisis de ejercicios.
 */
class ThemeFactory(
    val detector: ThemeDetector = ThemeDetector()
) {
    private val renderizadores: Map<TematicaSimulacion, ThemeRenderer> = mapOf(
        TematicaSimulacion.CANON to CanonThemeRenderer(),
        TematicaSimulacion.FUTBOL to FutbolThemeRenderer(),
        TematicaSimulacion.BASQUET to BasquetThemeRenderer(),
        TematicaSimulacion.GOLF to GolfThemeRenderer(),
        TematicaSimulacion.EDIFICIO to EdificioThemeRenderer(),
        TematicaSimulacion.ATLETISMO to AtletismoThemeRenderer()
    )

    /**
     * Retorna el [ThemeRenderer] correspondiente a la temática especificada.
     */
    fun obtenerPorTematica(tematica: TematicaSimulacion): ThemeRenderer {
        return renderizadores[tematica] ?: CanonThemeRenderer()
    }

    /**
     * Determina la temática de un [Ejercicio] y retorna su correspondiente [ThemeRenderer].
     */
    fun resolverParaEjercicio(ejercicio: Ejercicio?): ThemeRenderer {
        val tematica = detector.detectar(ejercicio)
        return obtenerPorTematica(tematica)
    }
}
