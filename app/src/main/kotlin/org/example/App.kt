package org.example

import org.example.service.MenuConsoleRunner
import org.example.ui.VentanaPrincipal
import javax.swing.SwingUtilities

/**
 * Punto de entrada principal para la aplicación "Simulación Tiro: Laboratorio de Tiro Oblicuo".
 *
 * Conecta e inicia de manera limpia la ventana gráfica principal de escritorio en el
 * hilo de despacho de eventos de Swing (Event Dispatch Thread - EDT).
 * Admite el argumento opcional `--console` para ejecutar el menú por consola si se requiere.
 *
 * @param args Argumentos pasados por línea de comandos.
 */
fun main(args: Array<String>) {
    if (args.contains("--console")) {
        MenuConsoleRunner().iniciar()
    } else {
        SwingUtilities.invokeLater {
            VentanaPrincipal.iniciar()
        }
    }
}

/**
 * Sobrecarga sin argumentos para ejecuciones directas desde el entorno de ejecución o IDE.
 */
fun main() {
    main(emptyArray())
}
