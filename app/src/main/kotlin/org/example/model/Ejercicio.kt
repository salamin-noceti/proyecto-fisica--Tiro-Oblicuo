package org.example.model

/**
 * Representa un ejercicio del banco de ejercicios.
 * Un ejercicio guarda su enunciado, su dificultad, los datos de ingreso
 * y (opcionalmente) el resultado ya calculado.
 */
data class Ejercicio(
    val id: Int,                          // Identificador único del ejercicio
    val titulo: String,                   // Nombre corto del ejercicio
    val enunciado: String,                // Texto de la consigna a mostrar
    val dificultad: Dificultad,           // Nivel de dificultad
    val datos: IngresoDatos,              // Datos de entrada (v0, ángulo, altura)
    var resultado: ResultadoTiroOblicuo? = null  // Resultado, nulo hasta que se resuelva
)
