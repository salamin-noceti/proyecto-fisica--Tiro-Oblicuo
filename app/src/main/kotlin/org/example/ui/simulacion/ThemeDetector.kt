package org.example.ui.simulacion

import org.example.model.Ejercicio

/**
 * Detector inteligente de la temática visual correspondiente a un ejercicio.
 */
class ThemeDetector {

    companion object {
        var instancia: ThemeDetector = ThemeDetector()
            private set
    }

    var renderizadorCanonPorDefecto: ThemeRenderer = CanonThemeRenderer.instancia
        private set

    private val reglas: List<Pair<Regex, TematicaSimulacion>> = listOf(
        Regex("""\b(fútbol|futbol|futbolista|futbolistas|balón|balon|balones|arquero|arqueros|patea|pateó|pateo|patear|penal|penales|gol|goles)\b""", RegexOption.IGNORE_CASE) to TematicaSimulacion.FUTBOL,
        Regex("""\b(básquet|basquet|basket|basketball|aro|aros|tablero|tableros|triple|triples|encesta|encestar)\b""", RegexOption.IGNORE_CASE) to TematicaSimulacion.BASQUET,
        Regex("""\b(golf|golfista|golfistas|hoyo|hoyos|tee|putter|fairway|green)\b""", RegexOption.IGNORE_CASE) to TematicaSimulacion.GOLF,
        Regex("""\b(tenis|tenista|tenistas|raqueta|raquetas|wimbledon|pelota de tenis|saque de tenis)\b""", RegexOption.IGNORE_CASE) to TematicaSimulacion.TENIS,
        Regex("""\b(edificio|edificios|terraza|terrazas|azotea|azoteas|rascacielos|torre|torres|balcón|balcon|ventana|ventanas|tejado)\b""", RegexOption.IGNORE_CASE) to TematicaSimulacion.EDIFICIO,
        Regex("""\b(atletismo|atleta|atletas|jabalina|jabalinas|disco|discos|pista|pistas|tartán|tartan|salto|saltos|despegue|corredor|corredores|velocista|velocistas)\b""", RegexOption.IGNORE_CASE) to TematicaSimulacion.ATLETISMO,
        Regex("""\b(cañón|canon|cañones|canones|mortero|morteros|bala|balas|disparo|disparos|artillería|artilleria|proyectil|proyectiles)\b""", RegexOption.IGNORE_CASE) to TematicaSimulacion.CANON
    )

    fun detectar(ejercicio: Ejercicio?): TematicaSimulacion {
        if (ejercicio == null) return TematicaSimulacion.CANON
        val contenido = "${ejercicio.titulo} ${ejercicio.enunciado}"
        val coincidencia = reglas.firstOrNull { it.first.containsMatchIn(contenido) }?.second
        if (coincidencia != null) return coincidencia
        return if (ejercicio.datos.alturaInicial > 0.0) TematicaSimulacion.EDIFICIO else TematicaSimulacion.CANON
    }
}
