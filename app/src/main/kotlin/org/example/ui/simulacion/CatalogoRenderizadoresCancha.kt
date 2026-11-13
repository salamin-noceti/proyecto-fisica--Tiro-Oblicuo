package org.example.ui.simulacion

/**
 * Catálogo de renderizadores temáticos de deportes de cancha cerrada/abierta.
 */
class CatalogoRenderizadoresCancha {
    private val mapa: Map<TematicaSimulacion, ThemeRenderer> = mapOf(
        TematicaSimulacion.FUTBOL to FutbolThemeRenderer.instancia,
        TematicaSimulacion.BASQUET to BasquetThemeRenderer(),
        TematicaSimulacion.TENIS to TenisThemeRenderer()
    )

    fun buscar(tematica: TematicaSimulacion): ThemeRenderer? {
        return mapa[tematica]
    }
}
