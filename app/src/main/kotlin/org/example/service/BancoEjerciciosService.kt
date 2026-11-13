package org.example.service

import org.example.model.Dificultad
import org.example.model.Ejercicio

/**
 * Servicio que expone y gestiona el banco de ejercicios predefinidos de la guía oficial.
 */
class BancoEjerciciosService(
    catalogoInicial: List<Ejercicio> = CatalogoEjerciciosPredefinidos().construirTodos()
) : RepositorioEjerciciosService {

    private val ejercicios: List<Ejercicio> = catalogoInicial.toList()

    override fun obtenerTodos(): List<Ejercicio> {
        return ejercicios.toList()
    }

    override fun obtenerPorDificultad(dificultad: Dificultad): List<Ejercicio> {
        return ejercicios.filter { it.dificultad == dificultad }
    }

    override fun obtenerPorId(id: Int): Ejercicio? {
        return ejercicios.find { it.id == id }
    }

    override fun resolverEjercicio(id: Int, calculador: CalculadorFisico): Ejercicio? {
        val encontrado = ejercicios.find { it.id == id } ?: return null
        return resolverEjercicio(encontrado, calculador)
    }

    override fun resolverEjercicio(ejercicio: Ejercicio, calculador: CalculadorFisico): Ejercicio {
        val resultado = calculador.calcular(ejercicio.datos)
        ejercicio.asignarResultado(resultado)
        ejercicios.find { it.id == ejercicio.id }?.asignarResultado(resultado)
        return ejercicio
    }
}
