package org.example

fun main() {
    // Datos de ejemplo: velocidad inicial 50 m/s, ángulo 45°
    val entrada = DatosEntrada(velocidadInicial = 50.0, anguloGrados = 45.0)
    val calculadora = TiroOblicuo()
    val resultados = calculadora.calcular(entrada)

    // Mostramos los resultados formateados
    println("=== Tiro Oblicuo ===")
    println("Velocidad inicial: ${entrada.velocidadInicial} m/s")
    println("Ángulo: ${entrada.anguloGrados}°")
    println()
    println("Vx (componente horizontal): ${resultados.vx} m/s")
    println("Vy (componente vertical):   ${resultados.vy} m/s")
    println()
    println("Tiempo de subida:   ${resultados.tiempoSubida} s")
    println("Tiempo total vuelo: ${resultados.tiempoTotal} s")
    println("Altura máxima:     ${resultados.alturaMaxima} m")
    println("Alcance horizontal:${resultados.alcanceHorizontal} m")
}
