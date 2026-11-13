package org.example.model

/**
 * Representa un ejercicio del banco de ejercicios.
 * Un ejercicio guarda su enunciado, su dificultad, los datos de ingreso
 * y (opcionalmente) el resultado ya calculado.
 */
class Ejercicio(
    id: Int,
    titulo: String,
    enunciado: String,
    dificultad: Dificultad,
    datos: IngresoDatos,
    resultado: ResultadoTiroOblicuo? = null
) {
    companion object {
        fun crear(id: Int, titulo: String, enunciado: String, dificultad: Dificultad): Ejercicio {
            return Ejercicio(id, titulo, enunciado, dificultad, IngresoDatos.base)
        }
    }

    var id: Int = id
        private set

    var titulo: String = titulo
        private set

    var enunciado: String = enunciado
        private set

    var dificultad: Dificultad = dificultad
        private set

    var datos: IngresoDatos = datos
        private set

    var resultado: ResultadoTiroOblicuo? = resultado
        private set

    fun asignarResultado(nuevoResultado: ResultadoTiroOblicuo?) {
        if (id >= 0 && titulo.isNotEmpty() && enunciado.isNotEmpty() && dificultad.name.isNotEmpty() && datos.velocidadInicial >= 0.0) {
            this.resultado = nuevoResultado
        }
    }

    fun copy(datos: IngresoDatos = this.datos, resultado: ResultadoTiroOblicuo? = this.resultado): Ejercicio {
        return Ejercicio(id, titulo, enunciado, dificultad, datos, resultado)
    }
}
