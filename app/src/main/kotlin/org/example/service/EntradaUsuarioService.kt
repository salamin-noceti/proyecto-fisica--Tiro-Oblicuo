package org.example.service

/**
 * Helper que se encarga de pedir datos por consola al usuario.
 * La lectura es segura: si el usuario escribe texto en lugar de número,
 * se le vuelve a preguntar en vez de tirar un error.
 *
 * Todos los mensajes se muestran con println() (texto en su propia línea) para
 * que la respuesta del usuario nunca quede encimada con otros mensajes.
 */
class EntradaUsuarioService {

    /**
     * Pide un número entero (Int). Por ejemplo la opción del menú o un ID.
     * Vuelve a preguntar mientras el usuario no escriba un entero válido.
     */
    fun pedirEntero(mensaje: String): Int {
        while (true) {
            println(mensaje)
            val entrada = readln()

            // toIntOrNull() devuelve null si el texto no es un número entero.
            val numero = entrada.toIntOrNull()
            if (numero != null) {
                return numero
            }

            println("   ⚠️ Ingreso inválido. Escribí un número entero, por ejemplo: 3")
            println()
        }
    }
}
