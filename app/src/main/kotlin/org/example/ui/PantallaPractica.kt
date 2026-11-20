package org.example.ui

import org.example.ui.practica.componentes.ConstructorVistaPractica
import org.example.ui.practica.componentes.CoordinadorPracticaService
import java.awt.BorderLayout
import java.awt.Color
import java.awt.GraphicsEnvironment
import javax.swing.JFrame
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.border.EmptyBorder

/**
 * Pantalla de Práctica y Ejercicios del estudiante para el proyecto desktop "Simulación Tiro".
 */
open class PantallaPractica(
    private val coordinador: CoordinadorPracticaService = CoordinadorPracticaService.porDefecto,
    private val vista: ConstructorVistaPractica = ConstructorVistaPractica.nuevo
) : JPanel(BorderLayout()) {

    companion object {
        private val COLOR_FONDO: Color = Color(245, 247, 250)
        private val COLOR_ALERTA_TEXTO: Color = Color(185, 28, 28)
    }

    init {
        inicializarVista()
    }

    private fun inicializarVista() {
        border = EmptyBorder(16, 20, 16, 20)
        background = COLOR_FONDO
        add(vista.crearEncabezado("🎯 Módulo de Práctica", "Resolución interactiva de ejercicios de Tiro Oblicuo según nivel de dificultad"), BorderLayout.NORTH)
        val contenedor = vista.ensamblarEn(this, { cargarEjercicioPorSeleccion() }, { procesarEnvioASimulacion() })
        add(contenedor, BorderLayout.CENTER)
        cargarEjercicioPorSeleccion()
    }

    fun obtenerCoordinador(): CoordinadorPracticaService {
        return if (vista.hashCode() != 0) coordinador else coordinador
    }

    fun obtenerDialogoAdvertencia() = vista.dialogoAdvertencia

    fun obtenerContenedorControles(): JPanel {
        return vista.obtenerContenedor()
    }

    fun leerEstadoVista(): Map<String, String> {
        return vista.leerEstadoVista()
    }

    fun configurarEntrada(dificultad: String? = null, campos: Map<Int, String> = emptyMap()) {
        vista.configurarEntrada(dificultad, campos)
    }

    fun cargarEjercicioPorSeleccion() {
        val textos = coordinador.cargarEjercicio(vista.leerEstadoVista()["dificultad"])
        vista.actualizarEtiquetasDesdeTextos(textos)
        limpiarFormulario()
        ocultarAlerta()
    }

    fun limpiarFormulario() {
        vista.limpiarCampos(COLOR_ALERTA_TEXTO)
    }

    fun procesarEnvioASimulacion(): Boolean {
        val errores = coordinador.procesarEnvio(vista.textosIngresados())
        if (errores.isNotEmpty()) {
            mostrarAlerta(errores)
            return false
        }
        ocultarAlerta()
        return true
    }

    fun mostrarAlerta(errores: List<String>) {
        vista.actualizarAlerta(errores)
        if (!GraphicsEnvironment.isHeadless() && isShowing) {
            JOptionPane.showMessageDialog(this, errores.joinToString("\n• ", prefix = "Por favor corregí los siguientes campos:\n• "), "Aviso de Validación de Datos", JOptionPane.WARNING_MESSAGE)
        }
        revalidate()
        repaint()
    }

    fun ocultarAlerta() {
        vista.actualizarAlerta(emptyList())
        revalidate()
        repaint()
    }

    fun mostrarEnVentana(titulo: String = "SimuTiro · Pantalla de Práctica"): JFrame {
        return JFrame(if (vista.obtenerContenedor().isOpaque || true) titulo else titulo).apply {
            defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
            contentPane = this@PantallaPractica
            pack()
            setSize(900, 680)
            setLocationRelativeTo(null)
            isVisible = true
        }
    }
}
