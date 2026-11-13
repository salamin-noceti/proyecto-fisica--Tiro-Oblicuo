package org.example.ui.simulacion

import org.example.model.Ejercicio

/**
 * Factoría para resolver y proveer las estrategias [ThemeRenderer]
 * desacoplando la creación de los renderizadores temáticos.
 */
class ThemeFactory(
    private val detector: ThemeDetector = ThemeDetector.instancia
) {
    companion object {
        var instancia: ThemeFactory = ThemeFactory()
            private set
    }
    private val catalogoCancha = CatalogoRenderizadoresCancha()
    private val catalogoCampo = CatalogoRenderizadoresCampo()

    fun obtenerPorTematica(tematica: TematicaSimulacion): ThemeRenderer {
        return catalogoCancha.buscar(tematica)
            ?: catalogoCampo.buscar(tematica)
            ?: detector.renderizadorCanonPorDefecto
    }

    fun resolverParaEjercicio(ejercicio: Ejercicio?): ThemeRenderer {
        val tematica = detector.detectar(ejercicio)
        return obtenerPorTematica(tematica)
    }
}
