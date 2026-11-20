package org.example.ui

import org.example.navigation.GestorNavegacion
import org.example.navigation.SeccionNavegacion
import javax.swing.JPanel

/**
 * Abstracción del ensamblador de vistas y secciones para [VentanaPrincipal] (DIP).
 */
interface EnsambladorVistasService {
    companion object {
        val porDefecto: EnsambladorVistasService
            get() = EnsambladorVistasPrincipal.nuevo
    }

    fun obtenerGestorNavegacion(): GestorNavegacion
    fun obtenerBarraNavegacion(): BarraNavegacion
    fun construirPanelCentral(): JPanel
    fun mostrarSeccion(seccion: SeccionNavegacion): String
}
