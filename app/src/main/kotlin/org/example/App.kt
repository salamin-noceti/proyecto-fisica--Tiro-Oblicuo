package org.example

import org.example.service.MenuConsoleRunner
import org.example.ui.VentanaPrincipal
import javax.swing.SwingUtilities

/**
 * Punto de entrada principal para la aplicación "Simulación Tiro: Laboratorio de Tiro Oblicuo".
 *
 * Simplifica el arranque a una única función de entrada delegando la inicialización
 * en el objeto encapsulado [LanzadorAplicacion].
 *
 * @param args Argumentos pasados por línea de comandos.
 */
fun main(args: Array<String> = emptyArray()) = LanzadorAplicacion.ejecutar(args)

/**
 * Objeto responsable de orquestar el arranque de la aplicación en modo gráfico (EDT)
 * o en modo consola (`--console`).
 */
private object LanzadorAplicacion {
    fun ejecutar(args: Array<String>) {
        if (args.contains("--console")) {
            MenuConsoleRunner().iniciar()
        } else {
            SwingUtilities.invokeLater {
                VentanaPrincipal.iniciar()
            }
        }
    }
}
