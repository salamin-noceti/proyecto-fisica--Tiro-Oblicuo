package org.example.ui.simulacion

/**
 * Temáticas visuales soportadas para la ambientación gráfica del tiro oblicuo.
 *
 * @param nombreVisible Nombre legible de la temática.
 * @param icono Emoticono o símbolo representativo.
 */
enum class TematicaSimulacion(
    val nombreVisible: String,
    val icono: String
) {
    FUTBOL("Fútbol", "⚽"),
    BASQUET("Básquet", "🏀"),
    TENIS("Tenis", "🎾"),
    CANON("Cañón", "💣"),
    EDIFICIO("Edificio", "🏢"),
    GOLF("Golf", "⛳"),
    ATLETISMO("Atletismo", "🏃")
}
