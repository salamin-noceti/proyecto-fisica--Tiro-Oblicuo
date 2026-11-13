package org.example.navigation

/**
 * Representa las tres secciones principales de navegación dentro de la aplicación.
 */
enum class SeccionNavegacion(
    titulo: String,
    descripcion: String,
    icono: String
) {
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

    var titulo: String
        private set

    var descripcion: String
        private set

    var icono: String
        private set

    init {
        this.titulo = titulo
        this.descripcion = descripcion
        this.icono = icono
    }

    fun etiquetaBoton(): String {
        return titulo
    }
}
