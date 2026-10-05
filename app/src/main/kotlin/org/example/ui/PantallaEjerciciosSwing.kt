package org.example.ui

import org.example.model.Dificultad
import org.example.model.Ejercicio
import org.example.model.RespuestaEstudiante
import org.example.service.CalculadorTiroOblicuo
import org.example.service.GeneradorEjerciciosBancoAdapter
import org.example.service.GeneradorEjerciciosService
import org.example.service.NavegadorSimulacionService
import org.example.service.SimulacionConsoleRunner
import org.example.service.ValidadorEntradaRespuesta
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Font
import java.awt.GridLayout
import java.util.Locale
import javax.swing.BorderFactory
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextArea
import javax.swing.JTextField
import javax.swing.border.EmptyBorder

/**
 * Implementación gráfica en Swing para la Pantalla de Ejercicios (Issue 6).
 *
 * Cumple con todos los requerimientos funcionales y criterios arquitectónicos:
 * 1. Menú desplegable ([JComboBox]) con las opciones: "Fácil", "Medio" y "Difícil".
 * 2. Recuadro claramente delimitado para mostrar el enunciado y datos físicos del problema.
 * 3. Campos de texto numéricos ([JTextField]) para ingresar alcance horizontal, altura máxima y tiempo de vuelo.
 * 4. Validación de datos ante campos vacíos o valores no numéricos.
 * 5. Botón visible con el texto exacto "Enviar a Simulación" que transfiere los datos a [NavegadorSimulacionService].
 * 6. Cumplimiento de Inversión de Dependencias (DIP) mediante interfaces de servicio.
 */
class PantallaEjerciciosSwing(
    private val generadorService: GeneradorEjerciciosService = GeneradorEjerciciosBancoAdapter(),
    private val navegadorSimulacion: NavegadorSimulacionService = SimulacionConsoleRunner(),
    private val validador: ValidadorEntradaRespuesta = ValidadorEntradaRespuesta()
) : JPanel(BorderLayout(12, 12)) {

    // Componentes de la interfaz
    val comboDificultad: JComboBox<String>
    val areaEnunciado: JTextArea
    val labelDatosFisicos: JLabel
    val campoAlcance: JTextField
    val campoAltura: JTextField
    val campoTiempo: JTextField
    val botonEnviar: JButton

    var ejercicioActual: Ejercicio? = null
        private set

    init {
        border = EmptyBorder(16, 16, 16, 16)

        // ── 1. SELECTOR / MENÚ DESPLEGABLE DE DIFICULTAD ────────────
        val panelSuperior = JPanel(BorderLayout(8, 8))
        panelSuperior.border = BorderFactory.createTitledBorder("1. Nivel de Dificultad")

        val panelCombo = JPanel()
        panelCombo.layout = BoxLayout(panelCombo, BoxLayout.X_AXIS)
        panelCombo.add(JLabel("Seleccioná la dificultad: "))

        val opciones = arrayOf("Fácil", "Medio", "Difícil")
        comboDificultad = JComboBox(opciones)
        panelCombo.add(comboDificultad)
        panelCombo.add(Box.createHorizontalGlue())

        panelSuperior.add(panelCombo, BorderLayout.CENTER)
        add(panelSuperior, BorderLayout.NORTH)

        // ── 2. SECCIÓN DELIMITADA: ENUNCIADO Y DATOS FÍSICOS ───────
        val panelCentral = JPanel(BorderLayout(8, 8))

        val panelEnunciado = JPanel(BorderLayout(6, 6))
        panelEnunciado.border = BorderFactory.createTitledBorder("2. Enunciado del Problema")

        areaEnunciado = JTextArea(5, 40).apply {
            isEditable = false
            lineWrap = true
            wrapStyleWord = true
            font = Font(Font.SANS_SERIF, Font.PLAIN, 14)
            background = Color(248, 249, 250)
            border = EmptyBorder(8, 8, 8, 8)
        }
        val scrollEnunciado = JScrollPane(areaEnunciado)
        panelEnunciado.add(scrollEnunciado, BorderLayout.CENTER)

        labelDatosFisicos = JLabel("Datos físicos: -").apply {
            font = Font(Font.SANS_SERIF, Font.BOLD, 12)
            border = EmptyBorder(4, 4, 4, 4)
        }
        panelEnunciado.add(labelDatosFisicos, BorderLayout.SOUTH)
        panelCentral.add(panelEnunciado, BorderLayout.NORTH)

        // ── 3. ENTRADA DE RESPUESTAS DEL ESTUDIANTE ────────────────
        val panelRespuestas = JPanel(GridLayout(3, 2, 8, 8))
        panelRespuestas.border = BorderFactory.createTitledBorder("3. Respuestas Calculadas por el Estudiante")

        campoAlcance = JTextField()
        campoAltura = JTextField()
        campoTiempo = JTextField()

        panelRespuestas.add(JLabel("Alcance horizontal (m):"))
        panelRespuestas.add(campoAlcance)
        panelRespuestas.add(JLabel("Altura máxima (m):"))
        panelRespuestas.add(campoAltura)
        panelRespuestas.add(JLabel("Tiempo total de vuelo (s):"))
        panelRespuestas.add(campoTiempo)

        panelCentral.add(panelRespuestas, BorderLayout.CENTER)
        add(panelCentral, BorderLayout.CENTER)

        // ── 4. ACCIÓN DE ENVÍO: BOTÓN 'Enviar a Simulación' ─────────
        val panelInferior = JPanel(BorderLayout(8, 8))
        botonEnviar = JButton("Enviar a Simulación").apply {
            font = Font(Font.SANS_SERIF, Font.BOLD, 14)
        }
        panelInferior.add(botonEnviar, BorderLayout.EAST)
        add(panelInferior, BorderLayout.SOUTH)

        // Escuchadores de eventos
        configurarEventos()

        // Carga inicial del primer ejercicio (Fácil por defecto)
        cargarEjercicioPorSeleccion()
    }

    private fun configurarEventos() {
        // Al cambiar de dificultad, solicitar un nuevo ejercicio al servicio
        comboDificultad.addActionListener {
            cargarEjercicioPorSeleccion()
        }

        // Al presionar el botón "Enviar a Simulación"
        botonEnviar.addActionListener {
            procesarEnvioASimulacion()
        }
    }

    /**
     * Mapea la opción seleccionada en el combo al enum [Dificultad]
     * y obtiene un nuevo ejercicio del [GeneradorEjerciciosService].
     */
    fun cargarEjercicioPorSeleccion() {
        val dificultad = when (comboDificultad.selectedItem as? String) {
            "Medio" -> Dificultad.MEDIO
            "Difícil" -> Dificultad.DIFICIL
            else -> Dificultad.FACIL
        }

        val nuevoEjercicio = generadorService.obtenerEjercicioPorDificultad(dificultad)
        ejercicioActual = nuevoEjercicio

        areaEnunciado.text = "Ejercicio: ${nuevoEjercicio.titulo}\n\n${nuevoEjercicio.enunciado}"
        labelDatosFisicos.text = "<html><b>Datos iniciales:</b> " +
            "v₀ = ${formatear(nuevoEjercicio.datos.velocidadInicial)} m/s &nbsp;|&nbsp; " +
            "θ = ${formatear(nuevoEjercicio.datos.angulo)}° &nbsp;|&nbsp; " +
            "y₀ = ${formatear(nuevoEjercicio.datos.alturaInicial)} m &nbsp;|&nbsp; " +
            "g = ${formatear(CalculadorTiroOblicuo.GRAVEDAD)} m/s²</html>"

        // Limpia los casilleros de respuestas para el nuevo ejercicio
        campoAlcance.text = ""
        campoAltura.text = ""
        campoTiempo.text = ""
    }

    /**
     * Valida los campos ingresados y transfiere el flujo a simulación si todo es correcto.
     */
    fun procesarEnvioASimulacion(): Boolean {
        val ejercicio = ejercicioActual ?: return false

        val (respuesta, errores) = validador.validarRespuestas(
            textoAlcance = campoAlcance.text,
            textoAltura = campoAltura.text,
            textoTiempo = campoTiempo.text
        )

        if (errores.isNotEmpty()) {
            val mensajeAlerta = errores.joinToString("\n• ", prefix = "Por favor corregí los siguientes campos:\n• ")
            JOptionPane.showMessageDialog(
                this,
                mensajeAlerta,
                "Aviso de Validación de Datos",
                JOptionPane.WARNING_MESSAGE
            )
            return false
        }

        if (respuesta != null) {
            navegadorSimulacion.transferirASimulacion(ejercicio, respuesta)
            return true
        }

        return false
    }

    /**
     * Crea y muestra una ventana [JFrame] independiente para esta pantalla.
     */
    fun mostrarEnVentana(titulo: String = "SimuTiro · Pantalla de Ejercicios"): JFrame {
        val frame = JFrame(titulo)
        frame.defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
        frame.contentPane = this
        frame.pack()
        frame.setLocationRelativeTo(null)
        frame.isVisible = true
        return frame
    }

    private fun formatear(v: Double): String = String.format(Locale.US, "%.2f", v)
}
