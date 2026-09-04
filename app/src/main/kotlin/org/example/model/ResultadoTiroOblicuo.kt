// Guarda todos los resultados calculados de un tiro oblicuo.
// Es "data class" e inmutable (val) para que los datos no cambien por accidente.
package org.example.model

data class ResultadoTiroOblicuo(
    val alcanceHorizontal: Double, // Distancia horizontal que recorre el proyectil (m)
    val alturaMaxima: Double,      // Altura más alta que alcanza (m)
    val tiempoVuelo: Double,       // Tiempo total que el proyectil está en el aire (s)
    val vx: Double,                // Componente horizontal de la velocidad (m/s)
    val vy: Double                 // Componente vertical de la velocidad inicial (m/s)
) {
    // Formato legible para mostrar en pantalla
    override fun toString(): String = """------ Resultado Tiro Oblicuo ------
(Alcance Horizontal = $alcanceHorizontal,
 Altura Máxima = $alturaMaxima,
 Tiempo de Vuelo = $tiempoVuelo,
 Velocidad Horizontal = $vx,
 Velocidad Vertical = $vy)"""
}
