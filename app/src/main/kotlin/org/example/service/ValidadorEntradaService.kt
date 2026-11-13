package org.example.service

import org.example.model.RespuestaEstudiante

/**
 * Contrato de validación de entradas de respuestas del estudiante (DIP).
 */
interface ValidadorEntradaService {
    fun validarCampo(entrada: String?, nombreCampo: String): ResultadoValidacion
    fun validarRespuestas(textoAlcance: String?, textoAltura: String?, textoTiempo: String?, velocidadesOpcionales: List<String?> = emptyList()): Pair<RespuestaEstudiante?, List<String>>
}
