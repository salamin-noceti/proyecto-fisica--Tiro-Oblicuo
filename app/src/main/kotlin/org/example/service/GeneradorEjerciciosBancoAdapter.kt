package org.example.service

import org.example.model.Dificultad
import org.example.model.Ejercicio

/**
 * Adaptador que implementa [GeneradorEjerciciosService] utilizando los ejercicios
 * provistos por [BancoEjerciciosService].
 *
 * Permite que la pantalla de ejercicios funcione y se pueda probar de inmediato
 * mientras se completa la implementación final de la Issue 5. Rota cíclicamente
 * entre los ejercicios disponibles para la dificultad indicada.
 */
class GeneradorEjerciciosBancoAdapter(
    private val banco: BancoEjerciciosService = BancoEjerciciosService()
) : GeneradorEjerciciosService {

    private val indicesPorDificultad = mutableMapOf<Dificultad, Int>()

    override fun obtenerEjercicioPorDificultad(dificultad: Dificultad): Ejercicio {
        val ejercicios = banco.obtenerPorDificultad(dificultad)
        check(ejercicios.isNotEmpty()) {
            "No se encontraron ejercicios en el banco para la dificultad $dificultad."
        }

        val siguienteIndice = indicesPorDificultad.getOrDefault(dificultad, 0)
        val ejercicioBase = ejercicios[siguienteIndice % ejercicios.size]
        indicesPorDificultad[dificultad] = siguienteIndice + 1

        // Retornamos una copia para garantizar que el estado del ejercicio
        // (por ejemplo si ya fue resuelto previamente) no interfiera con una nueva práctica.
        return ejercicioBase.copy(resultado = null)
    }
}
