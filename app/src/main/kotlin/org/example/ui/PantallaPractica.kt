package org.example.ui

import org.example.model.Dificultad
import org.example.model.Ejercicio
import org.example.model.RespuestaEstudiante
import org.example.navigation.GestorNavegacion
import org.example.navigation.SeccionNavegacion
import org.example.service.BancoEjerciciosService
import org.example.service.CalculadorFisico
import org.example.service.CalculadorTiroOblicuo
import org.example.service.GeneradorEjerciciosBancoAdapter
import org.example.service.GeneradorEjerciciosService
import org.example.service.NavegadorSimulacionService
import org.example.service.ValidadorEntradaRespuesta
import org.example.state.AppState
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Cursor
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.Font
import java.awt.GraphicsEnvironment
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.GridLayout
import java.awt.Insets
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
import javax.swing.SwingConstants
import javax.swing.border.CompoundBorder
import javax.swing.border.EmptyBorder

/**
 * Pantalla de Práctica y Ejercicios del estudiante para el proyecto desktop "Simulación Tiro".
 *
 * Implementa los requerimientos funcionales y técnicos de la Issue 6:
 * 1. **Selector de Dificultad (Dropdown):** Permite elegir entre `Fácil`, `Medio` y `Difícil`.
 *    Al seleccionar o cambiar la opción, consulta al servicio del banco de ejercicios ([BancoEjerciciosService])
 *    a través de la abstracción [GeneradorEjerciciosService] (Inversión de Dependencias - DIP).
 * 2. **Visualización del Enunciado:** Recuadro visual claro que muestra el enunciado real y los
 *    parámetros iniciales del problema ($v_0$, $\theta$, $y_0$, $g$).
 * 3. **Formulario de Respuestas:** Campos de entrada numéricos etiquetados para las magnitudes
 *    físicas solicitadas (Alcance horizontal, Altura máxima, Tiempo de vuelo, Velocidad X, Velocidad Y).
 *    Valida entradas evitando campos vacíos o valores no numéricos, mostrando alertas visuales en pantalla.
 * 4. **Botón de Acción "Enviar a Simulación":** Valida las respuestas, carga en el estado compartido
 *    ([AppState]) el ejercicio actual, las respuestas del estudiante y la solución física exacta
 *    ([org.example.model.ResultadoTiroOblicuo]), y conmuta automáticamente la navegación a la sección de Simulación.
 *
 * @param gestorNavegacion Gestor de navegación para transiciones entre módulos.
 * @param appState Estado global compartido de la aplicación.
 * @param generadorService Abstracción del servicio que provee ejercicios (DIP).
 * @param navegadorSimulacion Adaptador opcional para delegar el control de simulación.
 * @param calculadorFisico Motor de cálculo analítico para resolver el tiro oblicuo.
 * @param validador Validador de respuestas numéricas ingresadas por el estudiante.
 */
open class PantallaPractica(
    open val gestorNavegacion: GestorNavegacion = GestorNavegacion(),
    open val appState: AppState = gestorNavegacion.appState,
    open val generadorService: GeneradorEjerciciosService = GeneradorEjerciciosBancoAdapter(),
    open val navegadorSimulacion: NavegadorSimulacionService? = null,
    open val calculadorFisico: CalculadorFisico = CalculadorTiroOblicuo(),
    open val validador: ValidadorEntradaRespuesta = ValidadorEntradaRespuesta()
) : JPanel(BorderLayout()) {

    /**
     * Constructor secundario que acepta directamente [BancoEjerciciosService] inyectándolo
     * a través del adaptador [GeneradorEjerciciosBancoAdapter] cumpliendo con DIP.
     */
    constructor(
        gestorNavegacion: GestorNavegacion,
        appState: AppState,
        bancoEjerciciosService: BancoEjerciciosService,
        calculadorFisico: CalculadorFisico = CalculadorTiroOblicuo(),
        validador: ValidadorEntradaRespuesta = ValidadorEntradaRespuesta()
    ) : this(
        gestorNavegacion = gestorNavegacion,
        appState = appState,
        generadorService = GeneradorEjerciciosBancoAdapter(bancoEjerciciosService),
        calculadorFisico = calculadorFisico,
        validador = validador
    )

    /**
     * Constructor secundario conveniente para instanciar directamente con [BancoEjerciciosService].
     */
    constructor(bancoEjerciciosService: BancoEjerciciosService) : this(
        generadorService = GeneradorEjerciciosBancoAdapter(bancoEjerciciosService)
    )

    // Paleta de colores consistente con la aplicación
    companion object {
        val COLOR_FONDO: Color = Color(245, 247, 250)
        val COLOR_TARJETA: Color = Color.WHITE
        val COLOR_BORDE: Color = Color(226, 232, 240)
        val COLOR_AZUL_PRIMARIO: Color = Color(37, 99, 235)
        val COLOR_TEXTO_TITULO: Color = Color(15, 23, 42)
        val COLOR_TEXTO_SECUNDARIO: Color = Color(100, 116, 139)
        val COLOR_ALERTA_FONDO: Color = Color(254, 242, 242)
        val COLOR_ALERTA_BORDE: Color = Color(248, 113, 113)
        val COLOR_ALERTA_TEXTO: Color = Color(185, 28, 28)
    }

    // ── COMPONENTES DE INTERFAZ GRÁFICA ──────────────────────────────
    /** Contenedor reservado que aloja los controles interactivos de la Issue 6. */
    val contenedorControles: JPanel = JPanel(BorderLayout())

    /** Selector desplegable con los niveles de dificultad disponibles. */
    val comboDificultad: JComboBox<String>

    /** Botón auxiliar para cargar otro ejercicio de la misma dificultad. */
    val botonOtroEjercicio: JButton

    /** Título identificador del ejercicio actual. */
    val labelTituloEjercicio: JLabel

    /** Etiqueta que muestra el nivel de dificultad actual. */
    val labelBadgeDificultad: JLabel

    /** Área de texto con el enunciado completo del problema. */
    val areaEnunciado: JTextArea

    /** Etiqueta formateada con los datos físicos iniciales provistos. */
    val labelDatosFisicos: JLabel

    /** Etiqueta con la orientación didáctica según la dificultad. */
    val labelGuiaConsigna: JLabel

    /** Campo de texto para ingresar el alcance horizontal en metros. */
    val campoAlcance: JTextField

    /** Campo de texto para ingresar la altura máxima en metros. */
    val campoAltura: JTextField

    /** Campo de texto para ingresar el tiempo de vuelo en segundos. */
    val campoTiempo: JTextField

    /** Campo de texto para ingresar la velocidad horizontal Vx en m/s. */
    val campoVelocidadX: JTextField

    /** Campo de texto para ingresar la velocidad vertical Vy en m/s. */
    val campoVelocidadY: JTextField

    /** Panel de alerta visual para mensajes de validación en pantalla. */
    val panelAlerta: JPanel

    /** Mensaje de alerta visual en pantalla ante errores de validación. */
    val labelAlerta: JLabel

    /** Botón principal visible con el texto exacto requerido. */
    val botonEnviar: JButton

    /** Ejercicio seleccionado y visualizado actualmente. */
    var ejercicioActual: Ejercicio? = null
        private set

    init {
        border = EmptyBorder(16, 20, 16, 20)
        background = COLOR_FONDO

        // 1. Encabezado principal del módulo
        val panelEncabezado = crearEncabezado(
            titulo = "🎯 Módulo de Práctica",
            subtitulo = "Resolución interactiva de ejercicios de Tiro Oblicuo según nivel de dificultad"
        )
        add(panelEncabezado, BorderLayout.NORTH)

        // 2. Construcción de componentes interactivos
        val panelTarjetaPrincipal = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            background = COLOR_TARJETA
            border = CompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE, 1),
                EmptyBorder(18, 20, 18, 20)
            )
        }

        // ── 2.1. Selector de Dificultad (Dropdown) ───────────────────
        val opcionesDificultad = arrayOf("Fácil", "Medio", "Difícil")
        comboDificultad = JComboBox(opcionesDificultad).apply {
            font = Font(Font.SANS_SERIF, Font.PLAIN, 14)
            cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        }

        botonOtroEjercicio = JButton("🎲 Otro Ejercicio").apply {
            font = Font(Font.SANS_SERIF, Font.PLAIN, 13)
            cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            toolTipText = "Cargar otro ejercicio disponible para el mismo nivel de dificultad"
        }

        val panelSelectorDificultad = crearPanelSelectorDificultad()
        panelTarjetaPrincipal.add(panelSelectorDificultad)
        panelTarjetaPrincipal.add(Box.createRigidArea(Dimension(0, 12)))

        // ── 2.2. Visualización del Enunciado del Ejercicio ────────────
        labelTituloEjercicio = JLabel("Cargando ejercicio...").apply {
            font = Font(Font.SANS_SERIF, Font.BOLD, 16)
            foreground = COLOR_TEXTO_TITULO
        }

        labelBadgeDificultad = JLabel("[Fácil]").apply {
            font = Font(Font.SANS_SERIF, Font.BOLD, 12)
            foreground = COLOR_AZUL_PRIMARIO
        }

        areaEnunciado = JTextArea(3, 40).apply {
            isEditable = false
            lineWrap = true
            wrapStyleWord = true
            font = Font(Font.SANS_SERIF, Font.PLAIN, 14)
            background = Color(248, 250, 252)
            border = CompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE, 1),
                EmptyBorder(10, 12, 10, 12)
            )
        }

        labelDatosFisicos = JLabel("Datos iniciales: -").apply {
            font = Font(Font.SANS_SERIF, Font.BOLD, 13)
            foreground = Color(30, 41, 59)
            border = EmptyBorder(6, 4, 4, 4)
        }

        labelGuiaConsigna = JLabel("Consigna: -").apply {
            font = Font(Font.SANS_SERIF, Font.ITALIC, 12)
            foreground = COLOR_TEXTO_SECUNDARIO
            border = EmptyBorder(2, 4, 6, 4)
        }

        val panelEnunciado = crearPanelEnunciado()
        panelTarjetaPrincipal.add(panelEnunciado)
        panelTarjetaPrincipal.add(Box.createRigidArea(Dimension(0, 12)))

        // ── 2.3. Formulario de Respuestas del Estudiante ──────────────
        campoAlcance = JTextField().apply { font = Font(Font.SANS_SERIF, Font.PLAIN, 14) }
        campoAltura = JTextField().apply { font = Font(Font.SANS_SERIF, Font.PLAIN, 14) }
        campoTiempo = JTextField().apply { font = Font(Font.SANS_SERIF, Font.PLAIN, 14) }
        campoVelocidadX = JTextField().apply { font = Font(Font.SANS_SERIF, Font.PLAIN, 14) }
        campoVelocidadY = JTextField().apply { font = Font(Font.SANS_SERIF, Font.PLAIN, 14) }

        val panelRespuestas = crearPanelFormularioRespuestas()
        panelTarjetaPrincipal.add(panelRespuestas)
        panelTarjetaPrincipal.add(Box.createRigidArea(Dimension(0, 10)))

        // ── 2.4. Banner de Alerta Visual en Pantalla ──────────────────
        labelAlerta = JLabel().apply {
            font = Font(Font.SANS_SERIF, Font.BOLD, 12)
            foreground = COLOR_ALERTA_TEXTO
        }

        panelAlerta = JPanel(FlowLayout(FlowLayout.LEFT, 10, 8)).apply {
            background = COLOR_ALERTA_FONDO
            border = BorderFactory.createLineBorder(COLOR_ALERTA_BORDE, 1)
            isVisible = false
            add(labelAlerta)
        }
        panelTarjetaPrincipal.add(panelAlerta)
        panelTarjetaPrincipal.add(Box.createRigidArea(Dimension(0, 12)))

        // ── 2.5. Botón de Acción Exacto: "Enviar a Simulación" ─────────
        botonEnviar = JButton("Enviar a Simulación").apply {
            font = Font(Font.SANS_SERIF, Font.BOLD, 14)
            background = COLOR_AZUL_PRIMARIO
            foreground = Color.WHITE
            isFocusPainted = false
            cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            border = EmptyBorder(10, 24, 10, 24)
        }

        val panelAccion = JPanel(FlowLayout(FlowLayout.RIGHT, 0, 0)).apply {
            isOpaque = false
            add(botonEnviar)
        }
        panelTarjetaPrincipal.add(panelAccion)

        // Asignación al contenedor de controles y agregado al centro
        val scrollPanel = JScrollPane(panelTarjetaPrincipal).apply {
            border = BorderFactory.createEmptyBorder()
            viewport.background = COLOR_FONDO
        }
        contenedorControles.add(scrollPanel, BorderLayout.CENTER)
        add(contenedorControles, BorderLayout.CENTER)

        // Configuración de eventos y carga inicial
        configurarEventos()
        cargarEjercicioPorSeleccion()
    }

    /**
     * Vincula los escuchadores a los componentes interactivos.
     */
    private fun configurarEventos() {
        // Al cambiar de dificultad, solicitar ejercicio al servicio
        comboDificultad.addActionListener {
            cargarEjercicioPorSeleccion()
        }

        // Al presionar el botón de otro ejercicio
        botonOtroEjercicio.addActionListener {
            cargarEjercicioPorSeleccion()
        }

        // Al presionar el botón principal "Enviar a Simulación"
        botonEnviar.addActionListener {
            procesarEnvioASimulacion()
        }
    }

    /**
     * Mapea la opción seleccionada en el combo al enum [Dificultad]
     * y obtiene un nuevo ejercicio del servicio [GeneradorEjerciciosService].
     */
    fun cargarEjercicioPorSeleccion() {
        val dificultad = when (comboDificultad.selectedItem as? String) {
            "Medio" -> Dificultad.MEDIO
            "Difícil" -> Dificultad.DIFICIL
            else -> Dificultad.FACIL
        }

        val nuevoEjercicio = generadorService.obtenerEjercicioPorDificultad(dificultad)
        ejercicioActual = nuevoEjercicio

        // Actualizar visualizador del enunciado
        labelTituloEjercicio.text = "Ejercicio #${nuevoEjercicio.id}: ${nuevoEjercicio.titulo}"
        labelBadgeDificultad.text = "[${etiquetaDificultad(nuevoEjercicio.dificultad)}]"
        areaEnunciado.text = "Ejercicio: ${nuevoEjercicio.titulo}\n\n${nuevoEjercicio.enunciado}"

        // Actualizar datos físicos iniciales
        val v0 = formatear(nuevoEjercicio.datos.velocidadInicial)
        val angulo = formatear(nuevoEjercicio.datos.angulo)
        val y0 = formatear(nuevoEjercicio.datos.alturaInicial)
        val g = formatear(CalculadorTiroOblicuo.GRAVEDAD)

        labelDatosFisicos.text = "<html><b>📊 Datos provistos:</b> " +
            "v₀ = <b>$v0 m/s</b> &nbsp;|&nbsp; " +
            "θ = <b>$angulo°</b> &nbsp;|&nbsp; " +
            "y₀ = <b>$y0 m</b> &nbsp;|&nbsp; " +
            "g = <b>$g m/s²</b></html>"

        // Actualizar guía de la consigna pedagógica según dificultad
        labelGuiaConsigna.text = when (dificultad) {
            Dificultad.FACIL -> "💡 Guía didáctica (Fácil): Calculá las velocidades iniciales (Vx, Vy) y parámetros de la parábola."
            Dificultad.MEDIO -> "💡 Guía didáctica (Medio): Problema centrado en calcular el tiempo de vuelo y el alcance horizontal total."
            Dificultad.DIFICIL -> "💡 Guía didáctica (Difícil): Considerá la altura inicial de lanzamiento y calculá todos los parámetros."
        }

        // Limpiar formulario y alertas
        limpiarFormulario()
        ocultarAlerta()
    }

    /**
     * Limpia los campos de texto del formulario de respuestas.
     */
    fun limpiarFormulario() {
        campoAlcance.text = ""
        campoAltura.text = ""
        campoTiempo.text = ""
        campoVelocidadX.text = ""
        campoVelocidadY.text = ""
    }

    /**
     * Valida los campos ingresados por el estudiante y, si son válidos:
     * 1. Calcula la solución exacta teórica ([org.example.model.ResultadoTiroOblicuo]).
     * 2. Actualiza el estado compartido ([AppState]).
     * 3. Conmuta la sección activa a [SeccionNavegacion.SIMULACION].
     *
     * @return `true` si el envío y la transición fueron exitosos, `false` en caso de error de validación.
     */
    fun procesarEnvioASimulacion(): Boolean {
        val ejercicio = ejercicioActual ?: return false

        // 1. Validar las respuestas ingresadas
        val (respuesta, errores) = validador.validarRespuestas(
            textoAlcance = campoAlcance.text,
            textoAltura = campoAltura.text,
            textoTiempo = campoTiempo.text,
            textoVelocidadX = campoVelocidadX.text,
            textoVelocidadY = campoVelocidadY.text
        )

        if (errores.isNotEmpty()) {
            mostrarAlerta(errores)
            return false
        }

        if (respuesta != null) {
            ocultarAlerta()

            // Distinguir si se ingresaron explícitamente valores en 0.0 (caso límite v0 = 0):
            // evita ejecutar el tiro parabólico por defecto de 35.3 m (v0 = 20 m/s, θ = 30°).
            val esEntradaCero = respuesta.alcanceHorizontal == 0.0 &&
                respuesta.alturaMaxima == 0.0 &&
                respuesta.tiempoVuelo == 0.0 &&
                respuesta.velocidadX == 0.0 &&
                respuesta.velocidadY == 0.0

            val ejercicioConfigurado = if (esEntradaCero) {
                ejercicio.copy(datos = ejercicio.datos.copy(velocidadInicial = 0.0))
            } else {
                ejercicio
            }

            // 2. Calcular los datos correctos del sistema
            val solucionSistema = calculadorFisico.calcular(ejercicioConfigurado.datos)
            ejercicioConfigurado.resultado = solucionSistema

            // 3. Cargar en el estado compartido (AppState)
            appState.actualizar(
                ejercicio = ejercicioConfigurado,
                respuesta = respuesta,
                resultadoCorrecto = solucionSistema
            )

            // Notificación compatible al navegador de simulación si existe
            navegadorSimulacion?.transferirASimulacion(ejercicioConfigurado, respuesta)

            // 4. Cambiar automáticamente la pestaña activa de la ventana a Simulación
            gestorNavegacion.navegarA(SeccionNavegacion.SIMULACION)

            return true
        }

        return false
    }

    /**
     * Muestra el mensaje de alerta en la pantalla ante errores de validación
     * y presenta un cuadro de diálogo si el entorno no es headless.
     *
     * @param errores Lista de mensajes de error generados por el validador.
     */
    fun mostrarAlerta(errores: List<String>) {
        val mensajeHtml = "<html><b>⚠️ Por favor corregí los siguientes campos:</b><br>• " +
            errores.joinToString("<br>• ") + "</html>"
        labelAlerta.text = mensajeHtml
        panelAlerta.isVisible = true

        if (!GraphicsEnvironment.isHeadless()) {
            val mensajeDialogo = errores.joinToString("\n• ", prefix = "Por favor corregí los siguientes campos:\n• ")
            JOptionPane.showMessageDialog(
                this,
                mensajeDialogo,
                "Aviso de Validación de Datos",
                JOptionPane.WARNING_MESSAGE
            )
        }

        revalidate()
        repaint()
    }

    /**
     * Oculta el panel de alerta visual en pantalla.
     */
    fun ocultarAlerta() {
        panelAlerta.isVisible = false
        labelAlerta.text = ""
        revalidate()
        repaint()
    }

    /**
     * Construye una ventana independiente [JFrame] útil para pruebas y previsualizaciones aisladas.
     */
    fun mostrarEnVentana(titulo: String = "SimuTiro · Pantalla de Práctica"): JFrame {
        val frame = JFrame(titulo)
        frame.defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
        frame.contentPane = this
        frame.pack()
        frame.setSize(900, 680)
        frame.setLocationRelativeTo(null)
        frame.isVisible = true
        return frame
    }

    // ── MÉTODOS DE CONSTRUCCIÓN VISUAL ───────────────────────────────

    private fun crearEncabezado(titulo: String, subtitulo: String): JPanel {
        return JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            isOpaque = false
            border = EmptyBorder(0, 0, 14, 0)

            val labelTitulo = JLabel(titulo).apply {
                font = Font(Font.SANS_SERIF, Font.BOLD, 22)
                foreground = COLOR_TEXTO_TITULO
            }
            val labelSubtitulo = JLabel(subtitulo).apply {
                font = Font(Font.SANS_SERIF, Font.PLAIN, 14)
                foreground = COLOR_TEXTO_SECUNDARIO
            }

            add(labelTitulo)
            add(Box.createRigidArea(Dimension(0, 4)))
            add(labelSubtitulo)
        }
    }

    private fun crearPanelSelectorDificultad(): JPanel {
        return JPanel(BorderLayout(10, 0)).apply {
            isOpaque = false
            border = CompoundBorder(
                BorderFactory.createTitledBorder("1. Nivel de Dificultad"),
                EmptyBorder(6, 10, 6, 10)
            )

            val panelIzquierdo = JPanel(FlowLayout(FlowLayout.LEFT, 8, 2)).apply {
                isOpaque = false
                add(JLabel("Seleccionar nivel:").apply {
                    font = Font(Font.SANS_SERIF, Font.BOLD, 13)
                    foreground = COLOR_TEXTO_TITULO
                })
                add(comboDificultad)
                add(Box.createRigidArea(Dimension(12, 0)))
                add(botonOtroEjercicio)
            }

            add(panelIzquierdo, BorderLayout.CENTER)
        }
    }

    private fun crearPanelEnunciado(): JPanel {
        return JPanel(BorderLayout(6, 6)).apply {
            isOpaque = false
            border = CompoundBorder(
                BorderFactory.createTitledBorder("2. Enunciado del Ejercicio (Banco de Ejercicios)"),
                EmptyBorder(6, 10, 8, 10)
            )

            val panelEncabezadoEnunciado = JPanel(FlowLayout(FlowLayout.LEFT, 6, 2)).apply {
                isOpaque = false
                add(labelTituloEjercicio)
                add(labelBadgeDificultad)
            }

            val panelInferiorEnunciado = JPanel().apply {
                layout = BoxLayout(this, BoxLayout.Y_AXIS)
                isOpaque = false
                add(labelDatosFisicos)
                add(labelGuiaConsigna)
            }

            add(panelEncabezadoEnunciado, BorderLayout.NORTH)
            add(areaEnunciado, BorderLayout.CENTER)
            add(panelInferiorEnunciado, BorderLayout.SOUTH)
        }
    }

    private fun crearPanelFormularioRespuestas(): JPanel {
        return JPanel(GridBagLayout()).apply {
            isOpaque = false
            border = CompoundBorder(
                BorderFactory.createTitledBorder("3. Formulario de Respuestas del Estudiante"),
                EmptyBorder(8, 12, 10, 12)
            )

            val gbc = GridBagConstraints().apply {
                fill = GridBagConstraints.HORIZONTAL
                insets = Insets(4, 6, 4, 6)
            }

            // Fila 0: Alcance horizontal y Altura máxima
            gbc.gridx = 0
            gbc.gridy = 0
            gbc.weightx = 0.2
            add(JLabel("Alcance horizontal (m):*"), gbc)

            gbc.gridx = 1
            gbc.gridy = 0
            gbc.weightx = 0.3
            add(campoAlcance, gbc)

            gbc.gridx = 2
            gbc.gridy = 0
            gbc.weightx = 0.2
            add(JLabel("Altura máxima (m):*"), gbc)

            gbc.gridx = 3
            gbc.gridy = 0
            gbc.weightx = 0.3
            add(campoAltura, gbc)

            // Fila 1: Tiempo de vuelo y Velocidad X
            gbc.gridx = 0
            gbc.gridy = 1
            gbc.weightx = 0.2
            add(JLabel("Tiempo de vuelo (s):*"), gbc)

            gbc.gridx = 1
            gbc.gridy = 1
            gbc.weightx = 0.3
            add(campoTiempo, gbc)

            gbc.gridx = 2
            gbc.gridy = 1
            gbc.weightx = 0.2
            add(JLabel("Velocidad horizontal Vx (m/s):"), gbc)

            gbc.gridx = 3
            gbc.gridy = 1
            gbc.weightx = 0.3
            add(campoVelocidadX, gbc)

            // Fila 2: Velocidad Y
            gbc.gridx = 0
            gbc.gridy = 2
            gbc.weightx = 0.2
            add(JLabel("Velocidad vertical Vy (m/s):"), gbc)

            gbc.gridx = 1
            gbc.gridy = 2
            gbc.weightx = 0.3
            add(campoVelocidadY, gbc)

            gbc.gridx = 2
            gbc.gridy = 2
            gbc.gridwidth = 2
            gbc.weightx = 0.5
            val labelAclaracion = JLabel("<html><small style='color: #64748b;'>* Campos obligatorios para verificación de trayectoria</small></html>")
            add(labelAclaracion, gbc)
        }
    }

    private fun etiquetaDificultad(dificultad: Dificultad): String = when (dificultad) {
        Dificultad.FACIL -> "Fácil"
        Dificultad.MEDIO -> "Medio"
        Dificultad.DIFICIL -> "Difícil"
    }

    private fun formatear(v: Double): String = String.format(Locale.US, "%.2f", v)
}
