package org.example.ui.simulacion

/**
 * Temáticas visuales soportadas para la ambientación gráfica del tiro oblicuo.
 */
enum class TematicaSimulacion(
    nombreVisible: String,
    icono: String
) {
    FUTBOL("Fútbol", "⚽"),
    BASQUET("Básquet", "🏀"),
    TENIS("Tenis", "🎾"),
    CANON("Cañón", "💣"),
    EDIFICIO("Edificio", "🏢"),
    GOLF("Golf", "⛳"),
    ATLETISMO("Atletismo", "🏃");

    var nombreVisible: String
        private set

    var icono: String
        private set

    init {
        this.nombreVisible = nombreVisible
        this.icono = icono
    }

    fun etiquetaVisible(): String {
        return "$icono $nombreVisible"
    }
}
