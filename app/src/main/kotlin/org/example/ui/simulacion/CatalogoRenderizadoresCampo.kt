package org.example.ui.simulacion

/**
 * Catálogo de renderizadores temáticos de campo abierto y edificaciones.
 */
class CatalogoRenderizadoresCampo {
    private val mapa: Map<TematicaSimulacion, ThemeRenderer> = mapOf(
        TematicaSimulacion.GOLF to GolfThemeRenderer.instancia,
        TematicaSimulacion.ATLETISMO to AtletismoThemeRenderer(),
        TematicaSimulacion.EDIFICIO to EdificioThemeRenderer()
    )

    fun buscar(tematica: TematicaSimulacion): ThemeRenderer? {
        return mapa[tematica]
    }
}
