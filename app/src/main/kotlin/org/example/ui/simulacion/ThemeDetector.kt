package org.example.ui.simulacion

import org.example.model.Ejercicio

/**
 * Detector inteligente de la temática visual correspondiente a un ejercicio.
 * Analiza el título y la consigna del ejercicio para deducir el contexto físico y deportivo
 * utilizando delimitadores de palabras completas para evitar falsos positivos por subcadenas.
 */
class ThemeDetector {

    private val patronFutbol = Regex(
        """\b(fútbol|futbol|futbolista|futbolistas|balón|balon|balones|arquero|arqueros|patea|pateó|pateo|patear|penal|penales|gol|goles)\b""",
        RegexOption.IGNORE_CASE
    )

    private val patronBasquet = Regex(
        """\b(básquet|basquet|basket|basketball|aro|aros|tablero|tableros|triple|triples|encesta|encestar)\b""",
        RegexOption.IGNORE_CASE
    )

    private val patronGolf = Regex(
        """\b(golf|golfista|golfistas|hoyo|hoyos|tee|putter|fairway|green)\b""",
        RegexOption.IGNORE_CASE
    )

    private val patronCanon = Regex(
        """\b(cañón|canon|cañones|canones|mortero|morteros|bala|balas|disparo|disparos|artillería|artilleria|proyectil|proyectiles)\b""",
        RegexOption.IGNORE_CASE
    )

    private val patronTenis = Regex(
        """\b(tenis|tenista|tenistas|raqueta|raquetas|wimbledon|pelota de tenis|saque de tenis)\b""",
        RegexOption.IGNORE_CASE
    )

    private val patronAtletismo = Regex(
        """\b(atletismo|atleta|atletas|jabalina|jabalinas|disco|discos|pista|pistas|tartán|tartan|salto|saltos|despegue|corredor|corredores|velocista|velocistas)\b""",
        RegexOption.IGNORE_CASE
    )

    private val patronEdificio = Regex(
        """\b(edificio|edificios|terraza|terrazas|azotea|azoteas|rascacielos|torre|torres|balcón|balcon|ventana|ventanas|tejado)\b""",
        RegexOption.IGNORE_CASE
    )

    /**
     * Identifica la temática más apropiada para el ejercicio dado, priorizando el contexto deportivo
     * explícito del título/enunciado. Si no hay coincidencia deportiva pero el lanzamiento parte de una
     * altura inicial ($y_0 > 0$), se asume la temática de [TematicaSimulacion.EDIFICIO].
     * En cualquier otro caso cae en [TematicaSimulacion.CANON] como física clásica de artillería.
     *
     * @param ejercicio Ejercicio obtenido desde [org.example.state.AppState].
     * @return Temática detectada ([TematicaSimulacion]).
     */
    fun detectar(ejercicio: Ejercicio?): TematicaSimulacion {
        if (ejercicio == null) {
            return TematicaSimulacion.CANON
        }

        val contenido = "${ejercicio.titulo} ${ejercicio.enunciado}"

        return when {
            patronFutbol.containsMatchIn(contenido) -> TematicaSimulacion.FUTBOL
            patronBasquet.containsMatchIn(contenido) -> TematicaSimulacion.BASQUET
            patronGolf.containsMatchIn(contenido) -> TematicaSimulacion.GOLF
            patronTenis.containsMatchIn(contenido) -> TematicaSimulacion.TENIS
            patronEdificio.containsMatchIn(contenido) -> TematicaSimulacion.EDIFICIO
            patronAtletismo.containsMatchIn(contenido) -> TematicaSimulacion.ATLETISMO
            patronCanon.containsMatchIn(contenido) -> TematicaSimulacion.CANON
            ejercicio.datos.alturaInicial > 0.0 -> TematicaSimulacion.EDIFICIO
            else -> TematicaSimulacion.CANON
        }
    }
}
