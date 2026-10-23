package org.example.service

import org.example.model.Dificultad
import org.example.model.Ejercicio
import org.example.model.IngresoDatos

/**
 * Servicio que expone y gestiona el banco de ejercicios predefinidos de la guía oficial.
 */
class BancoEjerciciosService {

    private val ejercicios: List<Ejercicio> = listOf(
        // ── FÁCIL ────────────────────────────────────────────────────────────
        Ejercicio(
            id = 1,
            titulo = "Futbolista",
            enunciado = "Un futbolista patea un balón con velocidad de 20 m/s a 30° respecto del suelo.",
            dificultad = Dificultad.FACIL,
            datos = IngresoDatos(velocidadInicial = 20.0, angulo = 30.0, alturaInicial = 0.0)
        ),
        Ejercicio(
            id = 2,
            titulo = "Básquet",
            enunciado = "Un jugador lanza la pelota hacia el aro a 10 m/s con un ángulo de 53°.",
            dificultad = Dificultad.FACIL,
            datos = IngresoDatos(velocidadInicial = 10.0, angulo = 53.0, alturaInicial = 0.0)
        ),
        Ejercicio(
            id = 3,
            titulo = "Proyectil de juguete",
            enunciado = "Se dispara un proyectil de juguete a 14.14 m/s con un ángulo de 45°.",
            dificultad = Dificultad.FACIL,
            datos = IngresoDatos(velocidadInicial = 14.14, angulo = 45.0, alturaInicial = 0.0)
        ),

        // ── MEDIO ────────────────────────────────────────────────────────────
        Ejercicio(
            id = 4,
            titulo = "Saque de arquero",
            enunciado = "Un arquero saca desde su área imprimiendo al balón una velocidad de 25 m/s a 37°.",
            dificultad = Dificultad.MEDIO,
            datos = IngresoDatos(velocidadInicial = 25.0, angulo = 37.0, alturaInicial = 0.0)
        ),
        Ejercicio(
            id = 5,
            titulo = "Disparo de cañón",
            enunciado = "Un cañón dispara una bala a 50 m/s con un ángulo de elevación de 30°.",
            dificultad = Dificultad.MEDIO,
            datos = IngresoDatos(velocidadInicial = 50.0, angulo = 30.0, alturaInicial = 0.0)
        ),
        Ejercicio(
            id = 6,
            titulo = "Tiro con elevación",
            enunciado = "Lanzamiento con ángulo pronunciado de 60° a 30 m/s desde el suelo.",
            dificultad = Dificultad.MEDIO,
            datos = IngresoDatos(velocidadInicial = 30.0, angulo = 60.0, alturaInicial = 0.0)
        ),

        // ── DIFÍCIL ──────────────────────────────────────────────────────────
        Ejercicio(
            id = 7,
            titulo = "Lanzamiento desde edificio",
            enunciado = "Desde la terraza de un edificio de 15 m se lanza una piedra a 20 m/s a 30°.",
            dificultad = Dificultad.DIFICIL,
            datos = IngresoDatos(velocidadInicial = 20.0, angulo = 30.0, alturaInicial = 15.0)
        ),
        Ejercicio(
            id = 8,
            titulo = "Golpe de tenista",
            enunciado = "Un tenista golpea una pelota a 1.25 m de altura a 20 m/s y 37°.",
            dificultad = Dificultad.DIFICIL,
            datos = IngresoDatos(velocidadInicial = 20.0, angulo = 37.0, alturaInicial = 1.25)
        ),
        Ejercicio(
            id = 9,
            titulo = "Atleta de salto en largo",
            enunciado = "Un atleta despega del suelo a 45° con velocidad de 8.94 m/s.",
            dificultad = Dificultad.DIFICIL,
            datos = IngresoDatos(velocidadInicial = 8.94, angulo = 45.0, alturaInicial = 0.0)
        )
    )

    /**
     * Devuelve todos los ejercicios del banco.
     */
    fun obtenerTodos(): List<Ejercicio> = ejercicios

    /**
     * Devuelve únicamente los ejercicios correspondientes a la dificultad solicitada.
     */
    fun obtenerPorDificultad(dificultad: Dificultad): List<Ejercicio> =
        ejercicios.filter { it.dificultad == dificultad }

    /**
     * Busca un ejercicio por su ID. Devuelve null si no existe.
     */
    fun obtenerPorId(id: Int): Ejercicio? = ejercicios.find { it.id == id }

    /**
     * Resuelve un ejercicio por su ID calculando los resultados físicos y
     * almacenándolos en su propiedad [Ejercicio.resultado].
     * Devuelve el ejercicio actualizado, o null si el ID no existe.
     */
    fun resolverEjercicio(id: Int, calculador: CalculadorFisico): Ejercicio? {
        val ejercicio = obtenerPorId(id) ?: return null
        return resolverEjercicio(ejercicio, calculador)
    }

    /**
     * Resuelve una instancia de ejercicio calculando los resultados físicos y
     * almacenándolos en su propiedad [Ejercicio.resultado].
     * Devuelve el ejercicio actualizado.
     */
    fun resolverEjercicio(ejercicio: Ejercicio, calculador: CalculadorFisico): Ejercicio {
        ejercicio.resultado = calculador.calcular(ejercicio.datos)
        return ejercicio
    }
}
