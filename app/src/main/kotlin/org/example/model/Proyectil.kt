// Representa el objeto que se va a lanzar en el tiro oblicuo.
// Por ahora es simple: tiene nombre y masa. Más adelante le podemos agregar más datos.
package org.example.model

class Proyectil(
    val nombre: String,    // Nombre del proyectil (ej: "Pelota de fútbol")
    val masa: Double = 1.0 // Masa en kilogramos, 默认 1 kg
) {
    // Retorna una descripción legible del proyectil
    override fun toString(): String = "$nombre (${masa} kg)"
}
