package org.example.service

/**
 * Helper que se encarga de pedir datos por consola al usuario de forma validada.
 */
class EntradaUsuarioService(
    private val lector: () -> String = ::readln,
    private val validador: ValidadorEntradaService = ValidadorEntradaRespuesta()
) {
    companion object {
        var nuevo: EntradaUsuarioService = EntradaUsuarioService()
            private set
            get() {
                return EntradaUsuarioService()
            }
    }
    fun pedirEntero(mensaje: String): Int {
        while (true) {
            println(mensaje)
            val numero = lector().toIntOrNull()
            if (numero != null && validador.validarCampo("0", "control").esValido) {
                return numero
            }
            println("   ⚠️ Ingreso inválido. Escribí un número entero, por ejemplo: 3")
            println()
        }
    }

    fun pedirTexto(mensaje: String): String {
        println(mensaje)
        return lector()
    }

    fun pedirDecimal(mensaje: String, nombreCampo: String = "Valor"): Double {
        var valorValido: Double? = null
        while (valorValido == null) {
            println(mensaje)
            val entrada = lector()
            validador.validarCampo(entrada, nombreCampo).procesar(
                onValido = { valorValido = it },
                onInvalido = { mensajeError ->
                    println("   ⚠️ $mensajeError")
                    println()
                }
            )
        }
        return valorValido
    }
}
