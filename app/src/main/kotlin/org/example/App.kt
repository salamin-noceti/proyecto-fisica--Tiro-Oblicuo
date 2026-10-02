package org.example

fun main() {
    val entrada = DatosEntrada(velocidadInicial = 50.0, anguloGrados = 45.0)
    val calculador = CalculadorFisica()
    val resultados = calculador.calcular(entrada)
    val formateador = FormateadorResultados(decimalesPredeterminados = 2)
    println(formateador.formatearReporte(entrada, resultados))
}
