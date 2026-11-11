package org.example.model

/**
 * Clase que almacena los resultados calculados de un tiro oblicuo.
 * Contiene todos los valores que se obtienen después de aplicar las fórmulas físicas.
 */
data class ResultadoTiroOblicuo(
    val alturaMaxima: Double,        // Altura máxima alcanzada en metros
    val distanciaHorizontal: Double,  // Alcance horizontal total en metros
    val tiempoVuelo: Double,          // Tiempo total de vuelo en segundos
    val velocidadX: Double = 0.0,    // Componente horizontal de la velocidad en m/s
    val velocidadY: Double = 0.0     // Componente vertical inicial de la velocidad en m/s
)

/**
 * Representa la posición y velocidad instantánea del proyectil en un momento específico de la trayectoria.
 */
data class PosicionTrayectoria(
    val x: Double,
    val y: Double,
    val tiempo: Double,
    val velocidadX: Double = 0.0,
    val velocidadY: Double = 0.0
)
