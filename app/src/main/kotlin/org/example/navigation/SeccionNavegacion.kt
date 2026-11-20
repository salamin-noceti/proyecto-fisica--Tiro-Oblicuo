package org.example.navigation

/**
 * Representa las tres secciones principales de navegación dentro de la aplicación.
 */
enum class SeccionNavegacion(val titulo: String, val descripcion: String, val icono: String) {
    PRACTICA(
        titulo = "Práctica",
        descripcion = "Resolución de ejercicios y carga de parámetros del estudiante",
        icono = "🎯"
    ),
    SIMULACION(
        titulo = "Simulación",
        descripcion = "Visualización gráfica interactiva de la trayectoria parabólica",
        icono = "🚀"
    ),
    AUTOCORRECCION(
        titulo = "Autocorrección",
        descripcion = "Evaluación automática de resultados y desglose de soluciones",
        icono = "✅"
    );

    fun etiquetaBoton(): String {
        return titulo
    }

    fun textoBarraEstado(): String {
        return "Sección activa: $titulo  •  $descripcion"
    }
}
