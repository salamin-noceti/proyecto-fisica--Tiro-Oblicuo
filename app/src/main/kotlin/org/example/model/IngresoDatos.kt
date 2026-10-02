package org.example.model

/**
 * Clase que representa los datos de entrada para un ejercicio de tiro oblicuo.
 * Contiene la velocidad inicial, el ángulo de lanzamiento y la altura desde
 * la que se dispara (0.0 m cuando el proyectil se lanza desde el suelo).
 */
data class IngresoDatos(
    val velocidadInicial: Double,  // Velocidad inicial en m/s
    val angulo: Double,            // Ángulo de lanzamiento en grados
    val alturaInicial: Double = 0.0  // Altura inicial en metros
)
