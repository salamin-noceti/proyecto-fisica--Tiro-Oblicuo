package org.example
/**
* Datos de entrada para el tiro oblicuo.
* Solo contiene los valores que el usuario debe proporcionar.
*/
data class DatosEntrada(
    val velocidadInicial: Double, // V₀ en m/s
    val anguloGrados: Double // θ en grados (se convierte a radianes internamente)
)
/**
* Resultados del cálculo del tiro oblicuo.
* Agrupa todos los valores calculados en una sola estructura inmutable.
*/

data class ResultadosTiro(
    val vx: Double, // Componente horizontal de la velocidad (m/s)
    val vy: Double, // Componente vertical de la velocidad (m/s)
    val tiempoSubida: Double, // Tiempo hasta alcanzar la altura máxima (s)
    val tiempoTotal: Double, // Tiempo total de vuelo (s)
    val alturaMaxima: Double, // Altura máxima alcanzada (m)
    val alcanceHorizontal: Double // Distancia horizontal total recorrida (m)
)

/**
* Clase responsable ÚNICAMENTE de los cálculos del tiro oblicuo.
* Sigue el Principio de Responsabilidad Única (SRP): solo hace física.
*/

class TiroOblicuo {
// Constante de gravedad: valor fijo e inmutable (no puede cambiar)
// Unidad: metros por segundo al cuadrado (m/s²)
    private val gravedad = 9.81

/**
* Método principal: recibe los datos de entrada y devuelve todos los resultados.
* Descompone la velocidad inicial en componentes y aplica las fórmulas cinemáticas.
*/
fun calcular(entrada: DatosEntrada): ResultadosTiro {
// Convertimos el ángulo de grados a radianes, porque las funciones
// trigonométricas de Kotlin (sin, cos) trabajan con radianes
    val anguloRadianes = Math.toRadians(entrada.anguloGrados)

// Descomponemos la velocidad inicial en sus componentes:
// Vx = V₀ · cos(θ) → se mantiene constante (no hay aceleración horizontal)
// Vy = V₀ · sin(θ) → cambia debido a la gravedad
    val vx = entrada.velocidadInicial * Math.cos(anguloRadianes)
    val vy = entrada.velocidadInicial * Math.sin(anguloRadianes)

// Tiempo de subida: el tiempo que tarda Vy en llegar a cero
// Fórmula: t_subida = Vy / g
// En el punto más alto, la velocidad vertical es exactamente 0
    val tiempoSubida = vy / gravedad

// Tiempo total de vuelo: el proyectil sube y baja la misma distancia
// Fórmula: t_total = 2 · t_subida
    val tiempoTotal = 2.0 * tiempoSubida

// Altura máxima: se obtiene con la ecuación de movimiento vertical
// Fórmula: h_max = Vy² / (2 · g)
// Viene de: v² = v₀² - 2·g·h, cuando v = 0 (en el punto más alto)
    val alturaMaxima = (vy * vy) / (2.0 * gravedad)

// Alcance horizontal: distancia que recorre en el eje X durante todo el vuelo
// fórmula: R = Vx · t_total
// Como Vx es constante (sin rozamiento), simplemente multiplicamos
    val alcanceHorizontal = vx * tiempoTotal

return ResultadosTiro(
    vx = redondear(vx),
    vy = redondear(vy),
    tiempoSubida = redondear(tiempoSubida),
    tiempoTotal = redondear(tiempoTotal),
    alturaMaxima = redondear(alturaMaxima),
    alcanceHorizontal = redondear(alcanceHorizontal)
)
}
/**
* Redondea un valor a 2 decimales para mejor legibilidad.
*/
private fun redondear(valor: Double): Double =
Math.round(valor * 100.0) / 100.0

} 

