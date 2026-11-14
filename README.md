<!-- calidad:inicio -->
![Calidad](https://img.shields.io/badge/Calidad-84%2F100-green) ![Aprobar](https://img.shields.io/badge/Aprobar-no-red) ![Destacar](https://img.shields.io/badge/Destacar-no-lightgrey) ![Duplicación lógica](https://img.shields.io/badge/Duplicaci%C3%B3n%20l%C3%B3gica-1.8%25-green) ![%POO](https://img.shields.io/badge/%25POO-99%25-green) ![Sueltas](https://img.shields.io/badge/Sueltas-0-green) ![Clases OK](https://img.shields.io/badge/Clases%20OK-67%2F106-green) ![S](https://img.shields.io/badge/S-0-green) ![O](https://img.shields.io/badge/O-0-green) ![L](https://img.shields.io/badge/L-0-green) ![I](https://img.shields.io/badge/I-0-green) ![D](https://img.shields.io/badge/D-0-green)

**Calidad POO/SOLID (heurístico):** índice **84/100** · %POO **99%** · funciones sueltas **0** · clases OK **67** · aprobar **no (3/4)** · destacar **no**
Detalle completo en [`docs/EVALUACION.md`](docs/EVALUACION.md).
<!-- calidad:fin -->

# Simulación Tiro: Laboratorio de Tiro Oblicuo (`proyecto-fisica--Tiro-Oblicuo`)

Proyecto de Programación 2 para calcular parámetros de tiro oblicuo en Kotlin — Noceti y Salomón.

Aplicación interactiva de escritorio desarrollada en **Kotlin (JVM 21)** con **Swing + FlatLaf** para el estudio, práctica, simulación en tiempo real y autocorrección pedagógica de cinemática en dos dimensiones (**Tiro Oblicuo / Movimiento Parabólico**).

---

## Arquitectura y Patrones de Diseño (POO / SOLID)

El proyecto está diseñado aplicando **Programación Orientada a Objetos (POO)** y los principios **SOLID**:

1. **Patrón Strategy (`ThemeRenderer` / `RenderizadorGraficoCinematico`):**
   - **Escenarios 2D (`org.example.ui.simulacion`):** `LienzoSimulacion2D` delega el renderizado temático (fondo, suelo, plataforma inicial, lanzador y proyectil) en la abstracción `ThemeRenderer`, resuelta dinámicamente por `ThemeFactory` y `ThemeDetector` (`CanonThemeRenderer`, `FutbolThemeRenderer`, `BasquetThemeRenderer`, `GolfThemeRenderer`, `TenisThemeRenderer`, `EdificioThemeRenderer`, `AtletismoThemeRenderer`).
   - **Gráficos Cinemáticos (`org.example.ui.simulacion.graficos`):** `PanelGraficosCinematicos` coordina las estrategias `RenderizadorPosicion`, `RenderizadorVelocidad` y `RenderizadorAceleracion` reutilizando el lienzo base `CartesianChartCanvas` (**DRY / DUP = 0%**).

2. **Patrón Observer (`AppStateListener`, `NavegacionListener`, `SimulacionListener`, `AdvertenciaFormatoListener`):**
   - `AppState` centraliza de manera reactiva el ejercicio activo, las respuestas del estudiante y la solución analítica exacta, sincronizando automáticamente las pestañas **Práctica**, **Simulación** y **Autocorrección**.
   - `ControladorSimulacion` emite actualizaciones a ~60 FPS hacia el lienzo 2D y los gráficos cartesianos en tiempo real.

3. **Principio Abierto/Cerrado — OCP (`ResultadoValidacion`):**
   - La jerarquía sellada `ResultadoValidacion` (`Valido`, `Invalido`) define el contrato polimórfico `.procesar(onValido, onInvalido)`, eliminando condicionales de tipo (`when` / `is`) en `ValidadorEntradaRespuesta` y `EntradaUsuarioService`.

4. **Principio de Inversión de Dependencias — DIP (`CalculadorFisico`, `GeneradorEjerciciosService`, `EvaluadorRespuestasService`):**
   - Los controladores y vistas dependen de interfaces (`CalculadorFisico`, `GeneradorEjerciciosService`, `EvaluadorRespuestasService`, `NavegadorSimulacionService`), facilitando la inyección de dependencias y las pruebas unitarias aisladas.

---

## Estructura de Paquetes

- `org.example.model`: Entidades de dominio inmutables y modelos con comportamiento (`IngresoDatos`, `ResultadoTiroOblicuo`, `PosicionTrayectoria`, `Ejercicio`, `Dificultad`, `RespuestaEstudiante`, `ReporteAutocorreccion`).
- `org.example.service`: Motor físico y servicios de dominio (`CalculadorFisico`, `CalculadorTiroOblicuo`, `BancoEjerciciosService`, `ValidadorEntradaRespuesta`, `EvaluadorRespuestas`).
- `org.example.state` y `org.example.navigation`: Gestión de estado compartido (`AppState`) y controlador de pestañas (`GestorNavegacion`, `SeccionNavegacion`).
- `org.example.ui`: Interfaz gráfica de escritorio (`VentanaPrincipal`, `BarraNavegacion`, `PantallaPractica`, `PantallaSimulacion`, `PantallaAutocorreccion`).

---

## Instrucciones de Ejecución y Pruebas

### Ejecutar la aplicación gráfica (Desktop GUI)
```bash
./gradlew run
```

### Ejecutar en modo consola interactiva
```bash
./gradlew run --args="--console"
```

### Ejecutar la suite completa de pruebas unitarias
```bash
./gradlew clean test
```
