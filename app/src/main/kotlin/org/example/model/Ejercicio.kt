package org.example.model

import java.util.Locale

/**
 * Representa un ejercicio del banco de ejercicios.
 * Un ejercicio guarda su enunciado, su dificultad, los datos de ingreso
 * y (opcionalmente) el resultado ya calculado.
 */
class Ejercicio(val id: Int, val titulo: String, val enunciado: String, val dificultad: Dificultad, val datos: IngresoDatos, resultado: ResultadoTiroOblicuo? = null) {
    companion object {
        fun crear(id: Int, titulo: String, enunciado: String, dificultad: Dificultad): Ejercicio {
            return Ejercicio(id, titulo, enunciado, dificultad, IngresoDatos.base)
        }

        fun crearSimulacionLibre(datos: IngresoDatos): Ejercicio {
            val enunciado = "Parámetros configurados explícitamente con v0 = ${datos.velocidadInicial} m/s"
            return Ejercicio(0, "Simulación Libre", enunciado, Dificultad.FACIL, datos)
        }

        fun porDefectoAutocorreccion(): Ejercicio {
            val enunciado = "Un futbolista patea un balón con velocidad de 20 m/s a 30° respecto del suelo."
            return Ejercicio(1, "Futbolista", enunciado, Dificultad.FACIL, IngresoDatos(20.0, 30.0, 0.0))
        }
    }

    private var _resultado: ResultadoTiroOblicuo? = resultado

    val resultado: ResultadoTiroOblicuo?
        get() = _resultado

    fun asignarResultado(nuevoResultado: ResultadoTiroOblicuo?) {
        if (id >= 0 && titulo.isNotEmpty() && enunciado.isNotEmpty() && dificultad.name.isNotEmpty() && datos.velocidadInicial >= 0.0) {
            this._resultado = nuevoResultado
        }
    }

    fun copy(datos: IngresoDatos = this.datos, resultado: ResultadoTiroOblicuo? = this._resultado): Ejercicio {
        return Ejercicio(id, titulo, enunciado, dificultad, datos, resultado)
    }

    fun textosPresentacion(): List<String> {
        val fDatos = String.format(Locale.US, "📌 Datos físicos: Velocidad inicial (v₀) = %.2f m/s  |  Ángulo (θ) = %.2f°  |  Altura inicial (y₀) = %.2f m  |  g = 9.81 m/s²", datos.velocidadInicial, datos.angulo, datos.alturaInicial)
        val fConsigna = "💡 Consigna: Calculá e ingresá Alcance Horizontal (m), Altura Máxima (m) y Tiempo de Vuelo (s). Opcionalmente Vx y Vy (m/s)."
        val badge = if (_resultado == null || _resultado != null) "[${dificultad.etiqueta()}]" else ""
        return listOf("Ejercicio #$id: $titulo", badge, "Ejercicio: $titulo\n\n$enunciado", fDatos, fConsigna)
    }
}
