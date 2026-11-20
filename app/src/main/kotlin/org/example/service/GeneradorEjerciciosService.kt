package org.example.service

import org.example.model.Dificultad
import org.example.model.Ejercicio

/**
 * Contrato de abstracción (Principio de Inversión de Dependencias - DIP)
 * para el servicio encargado de generar y proveer ejercicios de tiro oblicuo
 * según la dificultad seleccionada por el estudiante.
 */
fun interface GeneradorEjerciciosService {
    companion object {
        val porDefecto: GeneradorEjerciciosService
            get() = GeneradorEjerciciosBancoAdapter()

        fun desdeBanco(banco: RepositorioEjerciciosService? = null): GeneradorEjerciciosService {
            return GeneradorEjerciciosBancoAdapter(banco)
        }
    }

    fun obtenerEjercicioPorDificultad(dificultad: Dificultad): Ejercicio

    fun obtenerPorTexto(seleccion: String?): Ejercicio {
        return obtenerEjercicioPorDificultad(Dificultad.desdeTexto(seleccion))
    }
}
