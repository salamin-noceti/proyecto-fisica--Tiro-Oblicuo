package org.example.model

/**
 * Representa los resultados numéricos calculados e ingresados por el estudiante
 * para un ejercicio de tiro oblicuo determinado, antes de contrastarlos con la simulación.
 *
 * @property alcanceHorizontal Alcance o distancia horizontal calculada en metros.
 * @property alturaMaxima Altura máxima calculada en metros.
 * @property tiempoVuelo Tiempo total de vuelo calculado en segundos.
 */
data class RespuestaEstudiante(
    val alcanceHorizontal: Double,
    val alturaMaxima: Double,
    val tiempoVuelo: Double
) {
    init {
        require(alcanceHorizontal >= 0.0) {
            "El alcance horizontal no puede ser negativo: $alcanceHorizontal m"
        }
        require(alturaMaxima >= 0.0) {
            "La altura máxima no puede ser negativa: $alturaMaxima m"
        }
        require(tiempoVuelo >= 0.0) {
            "El tiempo de vuelo no puede ser negativo: $tiempoVuelo s"
        }
    }

    /**
     * Convierte la respuesta del estudiante a una instancia de [ResultadoTiroOblicuo],
     * permitiendo una integración transparente con los motores de cálculo y simulación existentes.
     */
    fun aResultadoTiroOblicuo(): ResultadoTiroOblicuo =
        ResultadoTiroOblicuo(
            alturaMaxima = alturaMaxima,
            distanciaHorizontal = alcanceHorizontal,
            tiempoVuelo = tiempoVuelo
        )
}
