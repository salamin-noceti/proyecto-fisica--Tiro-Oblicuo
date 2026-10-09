package org.example
data class DatosEntrada(
    val velocidadInicial: Double,
    val anguloGrados: Double
) {
    init {
        require(velocidadInicial >= 0.0) {
            "La velocidad inicial no puede ser negativa: $velocidadInicial m/s"
        }
        require(anguloGrados in 0.0..90.0) {
            "El ángulo de disparo debe estar entre 0° y 90°: $anguloGrados°"
        }
    }
}
