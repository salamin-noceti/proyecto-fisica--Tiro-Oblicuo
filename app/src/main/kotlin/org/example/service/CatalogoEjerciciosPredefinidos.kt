package org.example.service

import org.example.model.Dificultad
import org.example.model.Ejercicio
import org.example.model.IngresoDatos

/**
 * Proveedor cohesivo del catálogo inicial de ejercicios predefinidos de la guía oficial.
 */
class CatalogoEjerciciosPredefinidos {
    private val alturaSuelo = 0.0

    fun construirTodos(): List<Ejercicio> {
        return listOf(
            ficha(1, Dificultad.FACIL, listOf("Futbolista", "Un futbolista patea un balón con velocidad de 20 m/s a 30° respecto del suelo."), datos(20.0, 30.0, alturaSuelo)),
            ficha(2, Dificultad.FACIL, listOf("Básquet", "Un jugador lanza la pelota hacia el aro a 10 m/s con un ángulo de 53°."), datos(10.0, 53.0, alturaSuelo)),
            ficha(3, Dificultad.FACIL, listOf("Proyectil de juguete", "Se dispara un proyectil de juguete a 14.14 m/s con un ángulo de 45°."), datos(14.14, 45.0, alturaSuelo)),
            ficha(4, Dificultad.MEDIO, listOf("Saque de arquero", "Un arquero saca desde su área imprimiendo al balón una velocidad de 25 m/s a 37°."), datos(25.0, 37.0, alturaSuelo)),
            ficha(5, Dificultad.MEDIO, listOf("Disparo de cañón", "Un cañón dispara una bala a 50 m/s con un ángulo de elevación de 30°."), datos(50.0, 30.0, alturaSuelo)),
            ficha(6, Dificultad.MEDIO, listOf("Tiro con elevación", "Lanzamiento con ángulo pronunciado de 60° a 30 m/s desde el suelo."), datos(30.0, 60.0, alturaSuelo)),
            ficha(7, Dificultad.DIFICIL, listOf("Lanzamiento desde edificio", "Desde la terraza de un edificio de 15 m se lanza una piedra a 20 m/s a 30°."), datos(20.0, 30.0, 15.0)),
            ficha(8, Dificultad.DIFICIL, listOf("Golpe de tenista", "Un tenista golpea una pelota a 1.25 m de altura a 20 m/s y 37°."), datos(20.0, 37.0, 1.25)),
            ficha(9, Dificultad.DIFICIL, listOf("Atleta de salto en largo", "Un atleta despega del suelo a 45° con velocidad de 8.94 m/s."), datos(8.94, 45.0, alturaSuelo))
        )
    }

    private fun ficha(id: Int, dificultad: Dificultad, textos: List<String>, datos: IngresoDatos): Ejercicio {
        return Ejercicio(id = id, titulo = textos[0], enunciado = textos[1], dificultad = dificultad, datos = datos)
    }

    private fun datos(v0: Double, angulo: Double, y0: Double): IngresoDatos {
        return IngresoDatos(velocidadInicial = v0, angulo = angulo, alturaInicial = y0.coerceAtLeast(alturaSuelo))
    }
}
