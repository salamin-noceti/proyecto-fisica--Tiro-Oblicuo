package org.example.ui.simulacion

/**
 * Temáticas visuales soportadas para la ambientación gráfica del tiro oblicuo.
 */
enum class TematicaSimulacion(val nombreVisible: String, val icono: String) {
    FUTBOL("Fútbol", "⚽"),
    BASQUET("Básquet", "🏀"),
    TENIS("Tenis", "🎾"),
    CANON("Cañón", "💣"),
    EDIFICIO("Edificio", "🏢"),
    GOLF("Golf", "⛳"),
    ATLETISMO("Atletismo", "🏃");

    fun etiquetaVisible(): String {
        return "$icono $nombreVisible"
    }
}
