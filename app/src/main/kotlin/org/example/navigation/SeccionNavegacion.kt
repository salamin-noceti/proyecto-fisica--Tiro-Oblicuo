package org.example.navigation

/**
 * Representa las secciones o módulos principales disponibles en el menú de navegación
 * de la aplicación desktop "Simulación Tiro: Laboratorio de Tiro Oblicuo".
 *
 * @property titulo Texto exacto que se muestra en el botón o pestaña de la interfaz.
 * @property descripcion Breve descripción del propósito de la sección.
 * @property icono Emoji o símbolo que acompaña visualmente al título en la barra.
 */
enum class SeccionNavegacion(
    val titulo: String,
    val descripcion: String,
    val icono: String
) {
    /**
     * Módulo de práctica y ejercicios para el estudiante (Issue 6).
     */
    PRACTICA(
        titulo = "Práctica",
        descripcion = "Banco de ejercicios, selección de dificultad y carga de respuestas.",
        icono = "🎯"
    ),

    /**
     * Módulo de animación interactiva y gráficos físicos cinemáticos (Issues 7 y 8).
     */
    SIMULACION(
        titulo = "Simulación",
        descripcion = "Animación en tiempo real del proyectil y gráficos cinemáticos.",
        icono = "🚀"
    ),

    /**
     * Módulo de autocorrección, análisis paso a paso y feedback didáctico (Issue 9).
     */
    AUTOCORRECCION(
        titulo = "Autocorrección",
        descripcion = "Revisión paso a paso de fórmulas y retroalimentación pedagógica.",
        icono = "✅"
    );

    override fun toString(): String = titulo
}
