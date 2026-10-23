package org.example.service

/**
 * Helper que se encarga de pedir datos por consola al usuario.
 * La lectura es segura: si el usuario escribe texto en lugar de número,
 * se le vuelve a preguntar en vez de tirar un error.
 *
 * Todos los mensajes se muestran con println() (texto en su propia línea) para
 * que la respuesta del usuario nunca quede encimada con otros mensajes.
 *
 * @property lector Función proveedora de cadenas desde la consola (por defecto invoca [readln]).
 */
class EntradaUsuarioService(
    private val lector: () -> String = { readln() },
    private val validador: ValidadorEntradaRespuesta = ValidadorEntradaRespuesta()
) {

    /**
     * Pide un número entero (Int). Por ejemplo la opción del menú o un ID.
     * Vuelve a preguntar mientras el usuario no escriba un entero válido.
     */
    fun pedirEntero(mensaje: String): Int {
        while (true) {
            println(mensaje)
            val entrada = lector()

            val numero = entrada.toIntOrNull()
            if (numero != null) {
                return numero
            }

            println("   ⚠️ Ingreso inválido. Escribí un número entero, por ejemplo: 3")
            println()
        }
    }

    /**
     * Pide un texto o línea por consola.
     */
    fun pedirTexto(mensaje: String): String {
        println(mensaje)
        return lector()
    }

    /**
     * Pide un valor numérico decimal (Double) validando que no esté vacío,
     * que sea un número válido y que no sea negativo para magnitudes físicas.
     *
     * @param mensaje Mensaje o indicación mostrado al usuario.
     * @param nombreCampo Nombre de la magnitud física a reportar en caso de error.
     * @return Valor [Double] válido ingresado por el usuario.
     */
    fun pedirDecimal(mensaje: String, nombreCampo: String = "Valor"): Double {
        while (true) {
            println(mensaje)
            val entrada = lector()

            when (val resultado = validador.validarCampo(entrada, nombreCampo)) {
                is ResultadoValidacion.Valido -> return resultado.valor
                is ResultadoValidacion.Invalido -> {
                    println("   ⚠️ ${resultado.mensajeError}")
                    println()
                }
            }
        }
    }
}
