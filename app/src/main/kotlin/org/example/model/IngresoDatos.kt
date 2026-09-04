// Guarda los datos que el usuario ingresa para simular un tiro oblicuo.
// "data class" es una clase especial de kotlin crada para almacenar info y datos,
// nos genera automáticamente toString, equals, hashCode, etc.
package org.example.model

data class IngresoDatos(
    val velocidadInicial: Double,  // Velocidad con la que se lanza el proyectil (m/s)
    val anguloGrados: Double,      // Ángulo de lanzamiento en grados
    val alturaInicial: Double = 0.0 // Altura desde donde se lanza (m), default 0 = nivel del suelo
)

