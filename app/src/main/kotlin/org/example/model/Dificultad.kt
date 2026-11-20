package org.example.model

import java.util.Locale

/**
 * Nivel de dificultad de un ejercicio del banco.
 * El menú usa este enum para agrupar y para pedir la dificultad al usuario.
 */
enum class Dificultad(
    private val nombreVisible: String
) {
    FACIL("Fácil"),
    MEDIO("Medio"),
    DIFICIL("Difícil");

    companion object {
        fun desdeTexto(seleccion: String?): Dificultad {
            return when (seleccion?.lowercase(Locale.ROOT)?.trim()) {
                "medio" -> MEDIO
                "difícil", "dificil" -> DIFICIL
                else -> FACIL
            }
        }

        fun desdeOpcionConsola(opcion: Int): Dificultad? {
            return when (opcion) {
                1 -> FACIL
                2 -> MEDIO
                3 -> DIFICIL
                else -> null
            }
        }
    }

    fun etiqueta(): String {
        return nombreVisible
    }
}
