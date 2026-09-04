package org.example

import org.example.model.IngresoDatos
import org.example.service.CalculadorFisico
import org.example.service.CalculadorTiroOblicuo

fun main() {
    val calculador: CalculadorFisico = CalculadorTiroOblicuo() // Linea 1
    val resultado = calculador.calcular(IngresoDatos(velocidadInicial = 20.0, anguloGrados = 45.0)) // Linea 2
    println(resultado) // Linea 3
}
