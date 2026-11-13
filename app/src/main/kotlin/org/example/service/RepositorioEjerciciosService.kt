package org.example.service

import org.example.model.Dificultad
import org.example.model.Ejercicio

/**
 * Abstracción para el repositorio de consulta y resolución de ejercicios del banco (DIP).
 */
interface RepositorioEjerciciosService {
    fun obtenerTodos(): List<Ejercicio>
    fun obtenerPorDificultad(dificultad: Dificultad): List<Ejercicio>
    fun obtenerPorId(id: Int): Ejercicio?
    fun resolverEjercicio(id: Int, calculador: CalculadorFisico): Ejercicio?
    fun resolverEjercicio(ejercicio: Ejercicio, calculador: CalculadorFisico): Ejercicio
}
