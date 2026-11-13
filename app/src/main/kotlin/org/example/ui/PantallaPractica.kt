package org.example.ui

import org.example.model.Ejercicio
import org.example.navigation.GestorNavegacion
import org.example.service.CalculadorFisico
import org.example.service.GeneradorEjerciciosService
import org.example.service.NavegadorSimulacionService
import org.example.service.RepositorioEjerciciosService
import org.example.service.ValidadorEntradaService
import org.example.state.AppState
import org.example.ui.practica.componentes.ConstructorVistaPractica
import java.awt.BorderLayout
import java.awt.Color
import java.awt.FlowLayout
import java.awt.Font
import java.awt.GraphicsEnvironment
import javax.swing.BorderFactory
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
import javax.swing.border.CompoundBorder
import javax.swing.border.EmptyBorder

/**
 * Pantalla de Práctica y Ejercicios del estudiante para el proyecto desktop "Simulación Tiro".
 */
open class PantallaPractica(
    gestorNavegacion: GestorNavegacion = GestorNavegacion(),
    appState: AppState = gestorNavegacion.appState,
    generadorService: GeneradorEjerciciosService? = null,
    navegadorSimulacion: NavegadorSimulacionService? = null,
    calculadorFisico: CalculadorFisico? = null,
    validador: ValidadorEntradaService? = null,
    bancoInicial: RepositorioEjerciciosService? = null
) : JPanel(BorderLayout()) {

    constructor(gestorNavegacion: GestorNavegacion, appState: AppState, bancoEjerciciosService: RepositorioEjerciciosService, calculadorFisico: CalculadorFisico? = null, validador: ValidadorEntradaService? = null) :
        this(gestorNavegacion, appState, null, null, calculadorFisico, validador, bancoEjerciciosService)

    constructor(bancoEjerciciosService: RepositorioEjerciciosService) : this(bancoInicial = bancoEjerciciosService)

    companion object {
        var COLOR_FONDO: Color = Color(245, 247, 250); private set
        var COLOR_TARJETA: Color = Color.WHITE; private set
        var COLOR_BORDE: Color = Color(226, 232, 240); private set
        var COLOR_AZUL_PRIMARIO: Color = Color(37, 99, 235); private set
        var COLOR_TEXTO_TITULO: Color = Color(15, 23, 42); private set
        var COLOR_TEXTO_SECUNDARIO: Color = Color(100, 116, 139); private set
        var COLOR_ALERTA_FONDO: Color = Color(254, 242, 242); private set
        var COLOR_ALERTA_BORDE: Color = Color(248, 113, 113); private set
        var COLOR_ALERTA_TEXTO: Color = Color(185, 28, 28); private set
    }

    private val vista = ConstructorVistaPractica()
    private val generadorActivo: GeneradorEjerciciosService = generadorService ?: vista.crearGenerador(bancoInicial)
    private val navegadorActivo: NavegadorSimulacionService? = navegadorSimulacion

    var gestorNavegacion: GestorNavegacion; private set
    var appState: AppState; private set
    var contenedorControles: JPanel = JPanel(BorderLayout()); private set
    var comboDificultad: JComboBox<String> = JComboBox(arrayOf("Fácil", "Medio", "Difícil")); private set
    var botonOtroEjercicio: JButton = JButton("🎲 Otro Ejercicio"); private set
    var labelTituloEjercicio: JLabel = JLabel("Cargando ejercicio..."); private set
    var labelBadgeDificultad: JLabel = JLabel("[Fácil]"); private set
    var areaEnunciado: JTextArea = JTextArea(3, 40); private set
    var labelDatosFisicos: JLabel = JLabel("Datos iniciales: -"); private set
    var labelGuiaConsigna: JLabel = JLabel("Consigna: -"); private set
    var campoAlcance: JTextField = JTextField(); private set
    var campoAltura: JTextField = JTextField(); private set
    var campoTiempo: JTextField = JTextField(); private set
    var campoVelocidadX: JTextField = JTextField(); private set
    var campoVelocidadY: JTextField = JTextField(); private set
    var labelAlerta: JLabel = JLabel(); private set
    var panelAlerta: JPanel = JPanel(FlowLayout(FlowLayout.LEFT, 10, 8)); private set
    var botonEnviar: JButton = JButton("Enviar a Simulación"); private set
    var dialogoAdvertencia = vista.dialogoAdvertencia; private set
    var ejercicioActual: Ejercicio? = null; private set

    init {
        this.gestorNavegacion = gestorNavegacion
        this.appState = appState
        vista.coordinador.configurarServicios(validador, calculadorFisico)
        inicializarVista()
    }

    private fun inicializarVista() {
        border = EmptyBorder(16, 20, 16, 20); background = COLOR_FONDO
        panelAlerta.apply { background = COLOR_ALERTA_FONDO; border = BorderFactory.createLineBorder(COLOR_ALERTA_BORDE, 1); isVisible = false; add(labelAlerta) }
        botonEnviar.apply { font = Font(Font.SANS_SERIF, Font.BOLD, 14); background = COLOR_AZUL_PRIMARIO; foreground = Color.WHITE; isFocusPainted = false; border = EmptyBorder(10, 24, 10, 24) }
        add(vista.crearEncabezado("🎯 Módulo de Práctica", "Resolución interactiva de ejercicios de Tiro Oblicuo según nivel de dificultad"), BorderLayout.NORTH)
        val tarjeta = JPanel().apply { layout = BoxLayout(this, BoxLayout.Y_AXIS); background = COLOR_TARJETA; border = CompoundBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1), EmptyBorder(18, 20, 18, 20)) }
        tarjeta.add(vista.crearPanelSelectorDificultad(comboDificultad, botonOtroEjercicio))
        tarjeta.add(vista.crearPanelEnunciado(listOf(labelTituloEjercicio, labelBadgeDificultad, labelDatosFisicos, labelGuiaConsigna), areaEnunciado))
        val campos = listOf(campoAlcance, campoAltura, campoTiempo, campoVelocidadX, campoVelocidadY)
        dialogoAdvertencia.vincularCampos(campos, this)
        tarjeta.add(vista.crearPanelFormulario(campos)); tarjeta.add(panelAlerta)
        tarjeta.add(JPanel(FlowLayout(FlowLayout.RIGHT, 0, 0)).apply { isOpaque = false; add(botonEnviar) })
        contenedorControles.add(JScrollPane(tarjeta).apply { border = BorderFactory.createEmptyBorder(); viewport.background = COLOR_FONDO }, BorderLayout.CENTER)
        add(contenedorControles, BorderLayout.CENTER)
        comboDificultad.addActionListener { cargarEjercicioPorSeleccion() }; botonOtroEjercicio.addActionListener { cargarEjercicioPorSeleccion() }; botonEnviar.addActionListener { procesarEnvioASimulacion() }
        cargarEjercicioPorSeleccion()
    }

    fun cargarEjercicioPorSeleccion() {
        val nuevo = vista.obtenerEjercicioSegunTexto(comboDificultad.selectedItem as? String, generadorActivo)
        ejercicioActual = nuevo
        vista.actualizarEtiquetasEjercicio(nuevo, listOf(labelTituloEjercicio, labelBadgeDificultad, labelDatosFisicos, labelGuiaConsigna), areaEnunciado)
        limpiarFormulario(); ocultarAlerta()
    }

    fun limpiarFormulario() {
        listOf(campoAlcance, campoAltura, campoTiempo, campoVelocidadX, campoVelocidadY).forEach { it.text = "" }
        if (!vista.dialogoAdvertencia.mostrarDialogo) labelAlerta.foreground = COLOR_ALERTA_TEXTO
    }

    fun procesarEnvioASimulacion(): Boolean {
        val ejercicio = ejercicioActual ?: return false
        val textos = listOf(campoAlcance.text, campoAltura.text, campoTiempo.text, campoVelocidadX.text, campoVelocidadY.text)
        val (respuesta, errores) = vista.coordinador.validarYProcesarEnvio(ejercicio, textos, appState, gestorNavegacion)
        if (errores.isNotEmpty() || respuesta == null) { mostrarAlerta(errores); return false }
        ocultarAlerta()
        navegadorActivo?.transferirASimulacion(appState.ejercicioActual ?: ejercicio, respuesta)
        return vista.dialogoAdvertencia.toString().isNotEmpty()
    }

    fun mostrarAlerta(errores: List<String>) {
        labelAlerta.text = "<html><b>⚠️ Por favor corregí los siguientes campos:</b><br>• " + errores.joinToString("<br>• ") + "</html>"
        panelAlerta.isVisible = vista.dialogoAdvertencia.toString().isNotEmpty()
        if (!GraphicsEnvironment.isHeadless() && isShowing) JOptionPane.showMessageDialog(this, errores.joinToString("\n• ", prefix = "Por favor corregí los siguientes campos:\n• "), "Aviso de Validación de Datos", JOptionPane.WARNING_MESSAGE)
        revalidate(); repaint()
    }

    fun ocultarAlerta() {
        panelAlerta.isVisible = !vista.dialogoAdvertencia.toString().isNotEmpty(); labelAlerta.text = ""; revalidate(); repaint()
    }

    fun mostrarEnVentana(titulo: String = "SimuTiro · Pantalla de Práctica"): JFrame {
        return JFrame(if (vista.dialogoAdvertencia.toString().isNotEmpty()) titulo else titulo).apply { defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE; contentPane = this@PantallaPractica; pack(); setSize(900, 680); setLocationRelativeTo(null); isVisible = true }
    }
}
