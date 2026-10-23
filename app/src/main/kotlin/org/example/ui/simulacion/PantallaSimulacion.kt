package org.example.ui.simulacion

import org.example.model.IngresoDatos
import org.example.service.CalculadorFisico
import org.example.service.CalculadorTiroOblicuo
import org.example.state.AppState
import org.example.ui.simulacion.graficos.PanelGraficosCinematicos
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Component
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.Font
import java.awt.GridLayout
import java.util.Locale
import javax.swing.BorderFactory
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSplitPane
import javax.swing.border.CompoundBorder
import javax.swing.border.EmptyBorder

/**
 * Pantalla principal del módulo de Simulación Gráfica 2D del proyecto desktop "Simulación Tiro".
 *
 * Implementa los requerimientos funcionales y técnicos de la Issue 7 con tematización dinámica:
 * 1. **Lienzo de Animación 2D y Tematización Dinámica (Patrón Strategy):**
 *    Detecta el contexto del ejercicio en [AppState] (`FUTBOL`, `CANON`, `BASQUET`, `GOLF`)
 *    y delega el renderizado en la estrategia [ThemeRenderer] adecuada:
 *    - **Fútbol:** Cancha con franjas de césped, silueta de futbolista en remate y pelota con gajos.
 *    - **Cañón:** Campo abierto, cañón de artillería orientable en $\theta$ y bala metálica.
 *    - **Básquet:** Parqué de madera lustrado, jugador en suspensión y pelota reglamentaria con aro.
 *    - **Golf:** Green/fairway con búnker de arena, golfista en swing y pelota con hoyuelos.
 * 2. **Detalles Visuales y Escala:**
 *    Preserva la escala métrica automática, cotas métricas del eje Y (con marcas de 2m, 4m),
 *    el asta de bandera en el alcance total ($d$) y la telemetría flotante.
 * 3. **Botones de Control Interactivos:**
 *    "Lanzar", "Pausar" y "Reiniciar" con sus nombres y comportamientos exactos.
 *
 * @param appState Estado global compartido de la aplicación.
 * @param calculadorFisico Motor físico inyectado para los cálculos analíticos.
 * @param themeFactory Factoría para la resolución de estrategias visuales.
 */
class PantallaSimulacion(
    val appState: AppState = AppState(),
    val calculadorFisico: CalculadorFisico = CalculadorTiroOblicuo(),
    val themeFactory: ThemeFactory = ThemeFactory()
) : JPanel(BorderLayout(0, 10)) {

    companion object {
        /** Datos seguros por defecto si aún no se transfirió ningún ejercicio de práctica. */
        val DATOS_POR_DEFECTO = IngresoDatos(
            velocidadInicial = 25.0,
            angulo = 45.0,
            alturaInicial = 0.0
        )
    }

    /** Conversor geométrico y matemático de coordenadas (Metros -> Píxeles). */
    val conversor: ConversorCoordenadas = ConversorCoordenadas()

    /** Controlador del bucle de simulación, cronómetro y estados de animación. */
    val controlador: ControladorSimulacion

    /** Lienzo gráfico 2D interactivo con renderizado temático desacoplado. */
    val lienzoSimulacion: LienzoSimulacion2D

    /** Panel con la botonera de control (Lanzar, Pausar, Reiniciar). */
    val panelControl: PanelControlSimulacion

    /** Panel de Gráficos Cinemáticos Cartesianos en Tiempo Real (Issue 8). */
    val panelGraficos: PanelGraficosCinematicos = PanelGraficosCinematicos()

    /** Divisor responsivo entre el lienzo de animación 2D y el panel de gráficos cinemáticos. */
    val splitSimulacion: JSplitPane

    /**
     * Contenedor central reservado para alojar el lienzo y garantizar compatibilidad
     * con la infraestructura de navegación y pruebas de la Issue 5.
     */
    val contenedorSimulacion: JPanel = JPanel(BorderLayout())

    /** Etiqueta informativa con los parámetros del ejercicio activo y la temática detectada. */
    val labelEjercicioCargado: JLabel = JLabel().apply {
        alignmentX = Component.LEFT_ALIGNMENT
        font = Font(Font.SANS_SERIF, Font.BOLD, 13)
        foreground = Color(37, 99, 235)
        isVisible = true
    }

    /** Estrategia de renderizado temático actualmente activa en el lienzo. */
    val temaActual: ThemeRenderer
        get() = lienzoSimulacion.themeRenderer

    // Accesos directos a los tres botones para interacción externa y tests
    val botonLanzar: JButton get() = panelControl.botonLanzar
    val botonPausar: JButton get() = panelControl.botonPausar
    val botonReiniciar: JButton get() = panelControl.botonReiniciar

    // Etiquetas del panel inferior con los resultados analíticos teóricos
    private val labelAlcanceTeorico: JLabel = JLabel()
    private val labelAlturaTeorica: JLabel = JLabel()
    private val labelTiempoTeorico: JLabel = JLabel()
    private val labelVelocidadesTeoricas: JLabel = JLabel()

    init {
        border = EmptyBorder(16, 20, 16, 20)
        background = Color(245, 247, 250)

        // 1. Resolver datos iniciales y temática inicial (desde AppState o valores por defecto)
        val ejercicioInicial = appState.ejercicioActual
        val datosIniciales = if (appState.estaInicializado && ejercicioInicial != null) {
            ejercicioInicial.datos
        } else {
            DATOS_POR_DEFECTO
        }
        val temaInicial = themeFactory.resolverParaEjercicio(ejercicioInicial)

        // 2. Inicializar controlador, lienzo y botonera
        controlador = ControladorSimulacion(
            calculadorFisico = calculadorFisico,
            datosIniciales = datosIniciales
        )
        lienzoSimulacion = LienzoSimulacion2D(
            controlador = controlador,
            conversor = conversor,
            themeRenderer = temaInicial
        )
        panelControl = PanelControlSimulacion(controlador = controlador)

        // 3. Montar panel norte (Encabezado + Badge de ejercicio)
        val panelNorte = crearPanelNorte()
        add(panelNorte, BorderLayout.NORTH)

        // 4. Montar contenedor central con divisor responsivo (Animación 2D + Gráficos Cartesianos)
        panelGraficos.configurarEjercicio(datosIniciales, controlador.resultadoTeorico)
        panelGraficos.actualizar(
            tiempoActual = controlador.tiempoActual,
            puntosEstela = controlador.puntosEstela,
            resultadoTeorico = controlador.resultadoTeorico,
            datos = controlador.datos
        )

        contenedorSimulacion.background = Color.WHITE
        contenedorSimulacion.border = CompoundBorder(
            BorderFactory.createLineBorder(Color(226, 232, 240), 1),
            EmptyBorder(2, 2, 2, 2)
        )

        splitSimulacion = JSplitPane(JSplitPane.HORIZONTAL_SPLIT, lienzoSimulacion, panelGraficos).apply {
            isContinuousLayout = true
            dividerSize = 6
            border = null
            resizeWeight = 0.52
        }
        contenedorSimulacion.add(splitSimulacion, BorderLayout.CENTER)
        add(contenedorSimulacion, BorderLayout.CENTER)

        // 5. Montar panel sur (Botonera de control + Resumen analítico)
        val panelSur = crearPanelSur()
        add(panelSur, BorderLayout.SOUTH)

        // 6. Actualizar textos con las condiciones físicas actuales
        actualizarInfoEjercicio()

        // 7. Sincronizar bucle de tiempo y telemetría en vivo con los gráficos cinemáticos
        controlador.agregarListener { _, _, puntosEstela ->
            panelGraficos.actualizar(
                tiempoActual = controlador.tiempoActual,
                puntosEstela = puntosEstela,
                resultadoTeorico = controlador.resultadoTeorico,
                datos = controlador.datos
            )
        }

        // 8. Suscribir a cambios en el estado compartido AppState para actualización reactiva
        appState.agregarListener {
            sincronizarConAppState()
        }
    }

    /**
     * Sincroniza el lienzo, el controlador y la temática gráfica con los datos de [AppState].
     */
    fun sincronizarConAppState() {
        val ejercicio = appState.ejercicioActual
        val nuevosDatos = if (appState.estaInicializado && ejercicio != null) {
            ejercicio.datos
        } else {
            DATOS_POR_DEFECTO
        }

        // Actualizar escalas y límites de los tres gráficos cinemáticos cartesianos y reiniciar controlador
        panelGraficos.configurarEjercicio(nuevosDatos, calculadorFisico.calcular(nuevosDatos))
        controlador.configurarEjercicio(nuevosDatos)

        // Detectar y aplicar la nueva temática dinámica (Patrón Strategy)
        val nuevoTema = themeFactory.resolverParaEjercicio(ejercicio)
        lienzoSimulacion.themeRenderer = nuevoTema

        actualizarInfoEjercicio()
        revalidate()
        repaint()
    }

    /**
     * Permite conmutar manualmente la temática visual (por ejemplo para pruebas o preferencias del usuario).
     */
    fun cambiarTematica(tematica: TematicaSimulacion) {
        lienzoSimulacion.themeRenderer = themeFactory.obtenerPorTematica(tematica)
        actualizarInfoEjercicio()
        lienzoSimulacion.repaint()
    }

    private fun actualizarInfoEjercicio() {
        val ejercicio = appState.ejercicioActual
        val tema = lienzoSimulacion.themeRenderer.tematica

        if (ejercicio != null) {
            val d = ejercicio.datos
            labelEjercicioCargado.text = "🎯 [${tema.icono} ${tema.nombreVisible}] Ejercicio #${ejercicio.id}: ${ejercicio.titulo} " +
                "— Condiciones: v₀ = ${formatear(d.velocidadInicial)} m/s, θ = ${formatear(d.angulo)}°, y₀ = ${formatear(d.alturaInicial)} m"
        } else {
            val d = DATOS_POR_DEFECTO
            labelEjercicioCargado.text = "🎯 [${tema.icono} ${tema.nombreVisible}] Modo Simulación Libre — Condiciones: " +
                "v₀ = ${formatear(d.velocidadInicial)} m/s, θ = ${formatear(d.angulo)}°, y₀ = ${formatear(d.alturaInicial)} m"
        }

        // Actualizar métricas teóricas esperadas
        val sol = controlador.resultadoTeorico
        labelAlcanceTeorico.text = "Alcance: ${formatear(sol.distanciaHorizontal)} m"
        labelAlturaTeorica.text = "Altura Máx: ${formatear(sol.alturaMaxima)} m"
        labelTiempoTeorico.text = "Tiempo Vuelo: ${formatear(sol.tiempoVuelo)} s"
        labelVelocidadesTeoricas.text = "Vx = ${formatear(sol.velocidadX)} m/s | Vy₀ = ${formatear(sol.velocidadY)} m/s"
    }

    private fun crearPanelNorte(): JPanel {
        return JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            isOpaque = false
            border = EmptyBorder(0, 0, 10, 0)

            val labelTitulo = JLabel("🚀 Módulo de Simulación: Animación en Vivo").apply {
                font = Font(Font.SANS_SERIF, Font.BOLD, 20)
                foreground = Color(15, 23, 42)
            }
            val labelSubtitulo = JLabel("Lanzamiento parabólico en tiempo real con ambientación deportiva dinámica y controles interactivos").apply {
                font = Font(Font.SANS_SERIF, Font.PLAIN, 13)
                foreground = Color(100, 116, 139)
            }

            add(labelTitulo)
            add(Box.createRigidArea(Dimension(0, 3)))
            add(labelSubtitulo)
            add(Box.createRigidArea(Dimension(0, 8)))
            add(labelEjercicioCargado)
        }
    }

    private fun crearPanelSur(): JPanel {
        return JPanel(BorderLayout(0, 6)).apply {
            isOpaque = false

            // Botonera de control (Lanzar, Pausar, Reiniciar)
            add(panelControl, BorderLayout.NORTH)

            // Fila de tarjetas con los datos teóricos
            val panelValoresTeoricos = JPanel(GridLayout(1, 4, 10, 0)).apply {
                isOpaque = false
                border = EmptyBorder(4, 0, 0, 0)

                add(crearTarjetaMetrica("📍 Alcance Horizontal", labelAlcanceTeorico, Color(37, 99, 235)))
                add(crearTarjetaMetrica("🏔 Altura Máxima", labelAlturaTeorica, Color(16, 185, 129)))
                add(crearTarjetaMetrica("⏱ Tiempo de Vuelo", labelTiempoTeorico, Color(245, 158, 11)))
                add(crearTarjetaMetrica("💨 Componentes Iniciales", labelVelocidadesTeoricas, Color(100, 116, 139)))
            }
            add(panelValoresTeoricos, BorderLayout.SOUTH)
        }
    }

    private fun crearTarjetaMetrica(titulo: String, labelValor: JLabel, colorBorde: Color): JPanel {
        return JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            background = Color.WHITE
            border = CompoundBorder(
                BorderFactory.createMatteBorder(0, 3, 0, 0, colorBorde),
                CompoundBorder(
                    BorderFactory.createLineBorder(Color(226, 232, 240), 1),
                    EmptyBorder(6, 10, 6, 10)
                )
            )

            val lblTitulo = JLabel(titulo).apply {
                font = Font(Font.SANS_SERIF, Font.PLAIN, 11)
                foreground = Color(100, 116, 139)
            }
            labelValor.apply {
                font = Font(Font.SANS_SERIF, Font.BOLD, 12)
                foreground = Color(30, 41, 59)
            }

            add(lblTitulo)
            add(Box.createRigidArea(Dimension(0, 2)))
            add(labelValor)
        }
    }

    private fun formatear(valor: Double): String = String.format(Locale.US, "%.2f", valor)
}
