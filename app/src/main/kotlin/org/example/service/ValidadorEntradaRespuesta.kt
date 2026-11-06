package org.example.service

import org.example.model.RespuestaEstudiante

/**
 * Resultado de la validación individual de un campo de texto numérico.
 */
sealed class ResultadoValidacion {
    /**
     * Procesa polimórficamente el resultado de validación invocando el callback correspondiente,
     * cumpliendo con el Principio Abierto/Cerrado (OCP) sin requerir condicionales `when` o `is`.
     *
     * @param onValido Acción a ejecutar cuando el valor numérico es válido.
     * @param onInvalido Acción a ejecutar cuando existe un mensaje de error de validación.
     */
    abstract fun procesar(
        onValido: (Double) -> Unit,
        onInvalido: (String) -> Unit
    )

    /**
     * Indica que el campo contiene un valor numérico válido y no negativo.
     * @property valor Número decimal parseado.
     */
    data class Valido(val valor: Double) : ResultadoValidacion() {
        override fun procesar(onValido: (Double) -> Unit, onInvalido: (String) -> Unit) {
            onValido(valor)
        }
    }

    /**
     * Indica que ocurrió un error de validación (campo vacío, texto no numérico, etc.).
     * @property mensajeError Mensaje explicativo para el usuario.
     */
    data class Invalido(val mensajeError: String) : ResultadoValidacion() {
        override fun procesar(onValido: (Double) -> Unit, onInvalido: (String) -> Unit) {
            onInvalido(mensajeError)
        }
    }
}

/**
 * Servicio encargado de validar las respuestas de texto ingresadas por el usuario
 * para cada una de las magnitudes físicas del ejercicio.
 *
 * Aplica reglas estrictas:
 * 1. El campo no puede estar vacío ni contener solo espacios en blanco.
 * 2. Debe ser convertible a número decimal (admite coma y punto decimal).
 * 3. Las magnitudes físicas (alcance, altura, tiempo) deben ser mayores o iguales a cero.
 */
class ValidadorEntradaRespuesta {

    /**
     * Valida individualmente el valor textual ingresado para un campo determinado.
     *
     * @param entrada Texto ingresado por el usuario.
     * @param nombreCampo Nombre amigable de la magnitud (ej: "Alcance horizontal").
     * @return [ResultadoValidacion.Valido] o [ResultadoValidacion.Invalido].
     */
    fun validarCampo(entrada: String?, nombreCampo: String): ResultadoValidacion {
        if (entrada.isNullOrBlank()) {
            return ResultadoValidacion.Invalido(
                "El campo '$nombreCampo' no puede estar vacío. Por favor, ingresá un número."
            )
        }

        val entradaLimpia = entrada.trim().replace(',', '.')
        val numero = entradaLimpia.toDoubleOrNull()
            ?: return ResultadoValidacion.Invalido(
                "Valor no numérico en '$nombreCampo': '$entrada'. Por favor, ingresá un número válido (ej: 15.5)."
            )

        if (numero < 0.0) {
            return ResultadoValidacion.Invalido(
                "El valor de '$nombreCampo' no puede ser negativo: $numero"
            )
        }

        return ResultadoValidacion.Valido(numero)
    }

    /**
     * Valida en conjunto las respuestas numéricas ingresadas por el estudiante.
     *
     * Valida las magnitudes principales de trayectoria (alcance, altura y tiempo)
     * y opcionalmente las componentes de velocidad (Vx, Vy) si fueron ingresadas.
     *
     * @param textoAlcance Cadena ingresada para el alcance horizontal.
     * @param textoAltura Cadena ingresada para la altura máxima.
     * @param textoTiempo Cadena ingresada para el tiempo de vuelo.
     * @param textoVelocidadX Cadena opcional para la velocidad horizontal Vx.
     * @param textoVelocidadY Cadena opcional para la velocidad vertical Vy.
     * @return Un par que contiene:
     *         - [RespuestaEstudiante] instanciada si los campos requeridos son válidos (o null si hay errores).
     *         - Lista de cadenas con los mensajes de error detectados.
     */
    fun validarRespuestas(
        textoAlcance: String?,
        textoAltura: String?,
        textoTiempo: String?,
        textoVelocidadX: String? = null,
        textoVelocidadY: String? = null
    ): Pair<RespuestaEstudiante?, List<String>> {
        val errores = mutableListOf<String>()
        var alcance = 0.0
        var altura = 0.0
        var tiempo = 0.0
        var vx = 0.0
        var vy = 0.0

        validarCampo(textoAlcance, "Alcance horizontal").procesar(
            onValido = { alcance = it },
            onInvalido = { errores.add(it) }
        )

        validarCampo(textoAltura, "Altura máxima").procesar(
            onValido = { altura = it },
            onInvalido = { errores.add(it) }
        )

        validarCampo(textoTiempo, "Tiempo de vuelo").procesar(
            onValido = { tiempo = it },
            onInvalido = { errores.add(it) }
        )

        if (!textoVelocidadX.isNullOrBlank()) {
            validarCampo(textoVelocidadX, "Velocidad horizontal (Vx)").procesar(
                onValido = { vx = it },
                onInvalido = { errores.add(it) }
            )
        }

        if (!textoVelocidadY.isNullOrBlank()) {
            validarCampo(textoVelocidadY, "Velocidad vertical (Vy)").procesar(
                onValido = { vy = it },
                onInvalido = { errores.add(it) }
            )
        }

        return if (errores.isEmpty()) {
            Pair(
                RespuestaEstudiante(
                    alcanceHorizontal = alcance,
                    alturaMaxima = altura,
                    tiempoVuelo = tiempo,
                    velocidadX = vx,
                    velocidadY = vy
                ),
                emptyList()
            )
        } else {
            Pair(null, errores)
        }
    }
}
