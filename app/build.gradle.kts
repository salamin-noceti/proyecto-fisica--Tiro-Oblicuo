plugins {
    id("org.jetbrains.kotlin.jvm")
    application
}

application {
    mainClass.set("org.example.AppKt")
}

// Gradle no reenvía el teclado al proceso java por defecto.
// Con esto, la tarea "run" conecta la consola del usuario con el programa.
tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}

repositories {
    mavenCentral()
}