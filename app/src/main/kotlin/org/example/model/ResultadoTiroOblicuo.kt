package org.example.model

/**
 * Clase que almacena los resultados calculados de un tiro oblicuo.
 * Contiene todos los valores que se obtienen después de aplicar las fórmulas físicas.
 */
data class ResultadoTiroOblicuo(
    val alturaMaxima: Double,       // Altura máxima alcanzada en metros
    val distanciaHorizontal: Double, // Alcance horizontal total en metros
    val tiempoVuelo: Double         // Tiempo total de vuelo en segundos
)
