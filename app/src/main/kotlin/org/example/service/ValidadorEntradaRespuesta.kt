package org.example.service

import org.example.model.RespuestaEstudiante

/**
 * Resultado de la validación individual de un campo de texto numérico.
 */
sealed class ResultadoValidacion {
    /**
     * Indica que el campo contiene un valor numérico válido y no negativo.
     * @property valor Número decimal parseado.
     */
    data class Valido(val valor: Double) : ResultadoValidacion()

    /**
     * Indica que ocurrió un error de validación (campo vacío, texto no numérico, etc.).
     * @property mensajeError Mensaje explicativo para el usuario.
     */
    data class Invalido(val mensajeError: String) : ResultadoValidacion()
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

        val resAlcance = validarCampo(textoAlcance, "Alcance horizontal")
        if (resAlcance is ResultadoValidacion.Invalido) {
            errores.add(resAlcance.mensajeError)
        }

        val resAltura = validarCampo(textoAltura, "Altura máxima")
        if (resAltura is ResultadoValidacion.Invalido) {
            errores.add(resAltura.mensajeError)
        }

        val resTiempo = validarCampo(textoTiempo, "Tiempo de vuelo")
        if (resTiempo is ResultadoValidacion.Invalido) {
            errores.add(resTiempo.mensajeError)
        }

        var vx = 0.0
        if (!textoVelocidadX.isNullOrBlank()) {
            when (val resVx = validarCampo(textoVelocidadX, "Velocidad horizontal (Vx)")) {
                is ResultadoValidacion.Valido -> vx = resVx.valor
                is ResultadoValidacion.Invalido -> errores.add(resVx.mensajeError)
            }
        }

        var vy = 0.0
        if (!textoVelocidadY.isNullOrBlank()) {
            when (val resVy = validarCampo(textoVelocidadY, "Velocidad vertical (Vy)")) {
                is ResultadoValidacion.Valido -> vy = resVy.valor
                is ResultadoValidacion.Invalido -> errores.add(resVy.mensajeError)
            }
        }

        return if (errores.isEmpty()) {
            val alcance = (resAlcance as ResultadoValidacion.Valido).valor
            val altura = (resAltura as ResultadoValidacion.Valido).valor
            val tiempo = (resTiempo as ResultadoValidacion.Valido).valor
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
