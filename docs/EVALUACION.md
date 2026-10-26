<!-- evaluacion:inicio -->
# Evaluación — salamin-noceti/proyecto-fisica--Tiro-Oblicuo

> Documento **generado automáticamente** por la cátedra de Programación II (RepoSense + análisis de calidad POO/SOLID). **No editar a mano**: se regenera en cada evaluación.
> Rama evaluada: `main`. Análisis **heurístico** (regex + métricas): es una criba para orientar la revisión, **no una nota**.
> Generado: 2026-10-26.

## 1. Veredicto

| Aspecto | Resultado |
|---|---|
| Índice de calidad POO/SOLID | **1/100** |
| Código (LOC) | 7,177 |
| Archivos de código | 71 |
| Clases (bien usadas) | 80 (7 OK) |
| Código dentro de clases (%POO) | 95% |
| Funciones fuera de clases | 5 |
| Duplicación lógica | 4.7% |
| Señales S/O/L/I/D | 55/2/0/0/47 |
| Tope duro aplicado | `SUELTAS>=3` → índice ≤ 40 |

> Este informe **describe** lo que el análisis midió con los criterios ya establecidos de la materia. No es una nota.

### Semáforo de aprobación

Criterios de `CRITERIOS-CALIDAD.md`. **Aprobar** se juega con el perfil `estandar`; **destacar**, con el `exigente` (meta de excelencia).

| Semáforo | Resultado |
|---|---|
| Aprobar (`estandar`) | **no cumple** — 2/4 |
| Destacar (`exigente`) | **no cumple** — 1/5 |

**Criterios para aprobar**

| Criterio | Requisito | Tu resultado | Cumple |
|---|---|---|---|
| Código dentro de clases (%POO) | ≥ 85% | 95% | sí |
| Duplicación lógica | ≤ 10% | 4.7% | sí |
| Funciones fuera de clases | ≤ 1 | 5 | no |
| Señales POO/SOLID críticas | ninguna | Clase demasiado grande (SRP), Baja cohesión (LCOM), Acoplamiento alto (CBO), Switch sobre tipos (OCP) | no |

**Criterios para destacar**

| Criterio | Requisito | Tu resultado | Cumple |
|---|---|---|---|
| Código dentro de clases (%POO) | ≥ 95% | 95% | no |
| Duplicación lógica | ≤ 5% | 4.7% | sí |
| Funciones fuera de clases | ≤ 1 | 5 | no |
| Señales POO/SOLID críticas | ninguna | Clase demasiado grande (SRP), Baja cohesión (LCOM), Complejidad alta, Acoplamiento alto (CBO), Switch sobre tipos (OCP) | no |
| Índice de calidad POO/SOLID | ≥ 60 | 1/100 | no |

## 2. Autoría (RepoSense)

| Autor | Commits | Código (LOC) | Tests (LOC) | Docs (LOC) | Días activos | % del código |
|---|---:|---:|---:|---:|---:|---:|
| avsalomon | 12 | 6,439 | 1,908 | 0 | 9 | 100% |
| Romano-22 | 6 | 0 | 19 | 2 | 5 | 0% |

> _El LOC de autoría lo calcula RepoSense sobre el historial y puede diferir del LOC de código del análisis de calidad (que mira solo el estado de `main`)._

> **Reparto del trabajo:** avsalomon concentra el 100% de las líneas de código. Un reparto muy desigual suele indicar que la integración no fue realmente en equipo; vale la pena reconstruir cómo se coordinó.

## 3. Aspectos evaluados

Cada aspecto indica **qué mide**, **por qué importa** y **tu resultado**. Cuando el análisis reportó una señal, se agregan preguntas para **investigar la causa** (no hay recetas: entender el porqué es parte de la evaluación).

### 1. Índice de calidad POO/SOLID (indice)

- **Qué mide:** Un resumen numérico (0-100) que combina la riqueza de clases bien usadas con la penalización por señales POO/SOLID y duplicación, y luego aplica topes duros.
- **Por qué importa:** Ordena y prioriza la revisión: un índice bajo indica varias señales que conviene mirar juntas.
- **Tu resultado:** índice **1/100**

### 2. Duplicación lógica (DRY) (duplicacion) — con señales

- **Qué mide:** Porcentaje de bloques de LÓGICA repetidos. Quedan fuera imports/includes, anotaciones, declaraciones de campos y comentarios/Javadoc/docstrings: esos bloques son boilerplate, no lógica.
- **Por qué importa:** Cada copia de lógica se mantiene por separado: un arreglo hay que repetirlo y los defectos se multiplican.
- **Tu resultado:** **4.7%** de duplicación lógica
- **Para investigar la causa:**
    - ¿El trabajo se repartió por pantallas o archivos y se integró copiando y pegando bloques?
    - ¿Qué zonas del sistema cambiaron juntas más de una vez (señal de lógica compartida)?
    - ¿Hubo un lugar común acordado para la lógica repetida antes de empezar, o cada integrante resolvió lo suyo?

### 3. Adopción de POO (% de código en clases) (poo) — sin señales

- **Qué mide:** Qué porción del código vive dentro de clases (no de funciones sueltas).
- **Por qué importa:** El diseño orientado a objetos es el eje de la materia: el comportamiento debería vivir como método de una clase.
- **Tu resultado:** **94.9%** del código dentro de clases

### 4. Funciones fuera de clases (sueltas) — con señales

- **Qué mide:** Funciones de nivel de módulo que no pertenecen a ninguna clase (en esta materia el comportamiento debe ser método de una clase).
- **Por qué importa:** Son la forma más directa de código procedural en un proyecto que debería ser orientado a objetos.
- **Tu resultado:** **5** función(es) fuera de clases
- **Para investigar la causa:**
    - ¿Esa función se dejó suelta por urgencia o por desconocer dónde ubicarla?
    - ¿Qué clase debería ser responsable de ese comportamiento?
    - ¿Había una convención sobre dónde poner cada cosa?

### 5. Clase demasiado grande (SRP) (god) — con señales

- **Qué mide:** Clases con muchas líneas, métodos o campos a la vez.
- **Por qué importa:** Una clase que hace de todo cambia por muchos motivos y cuesta entenderla y probarla.
- **Tu resultado:** **38** clase(s) demasiado grande(s)
- **Para investigar la causa:**
    - ¿La clase creció por acumulación de responsabilidades?
    - ¿Qué partes de la clase se usan siempre juntas y cuáles no?
    - ¿Se podría describir su responsabilidad en una sola frase?

### 6. Baja cohesión (LCOM) (cohesion) — con señales

- **Qué mide:** Métodos y campos de una clase que casi no se relacionan entre sí.
- **Por qué importa:** Una unidad que cambia por motivos distintos y comparte poca información suele esconder más de una responsabilidad.
- **Tu resultado:** **9** clase(s) con baja cohesión
- **Para investigar la causa:**
    - ¿Qué métodos de la clase no comparten estado con el resto?
    - ¿Cambian por motivos distintos según la tarea?
    - ¿Se pensó su responsabilidad antes de escribirla, o se fue agregando?

### 7. Complejidad alta (complejidad) — con señales

- **Qué mide:** Complejidad ciclomática: cantidad de caminos posibles dentro de un método o función (condicionales, bucles, casos).
- **Por qué importa:** Muchos caminos = difícil de seguir y de probar; suelen esconderse casos sin cubrir.
- **Tu resultado:** **1** método(s)/función(es) con complejidad alta
- **Para investigar la causa:**
    - ¿Se agregaron condiciones a medida que aparecían casos nuevos?
    - ¿Se probaron todos los caminos o solo el caso principal?
    - ¿Hay reglas de negocio mezcladas con detalles técnicos?

### 8. Métodos/funciones largos o con muchos parámetros (tamano) — con señales

- **Qué mide:** Tamaño de los métodos/funciones y cantidad de parámetros.
- **Por qué importa:** Una función que hace varias cosas cuesta leerla, nombrarla y reutilizarla.
- **Tu resultado:** **31** método(s)/función(es) largos o con muchos parámetros
- **Para investigar la causa:**
    - ¿La función creció por acumulación de pasos?
    - ¿Agrupa operaciones que podrían describirse por separado?
    - ¿Los parámetros representan una misma idea (un objeto) o cosas sueltas?

### 9. Acoplamiento alto (CBO) (acoplamiento) — con señales

- **Qué mide:** Cuánto depende una clase de otros tipos del proyecto.
- **Por qué importa:** Piezas muy enlazadas hacen que un cambio se propague y que sea difícil reemplazar partes.
- **Tu resultado:** **54** clase(s) muy acoplada(s)
- **Para investigar la causa:**
    - ¿La clase conoce detalles internos de muchas otras?
    - ¿Se puede cambiar una parte sin tocar las demás?
    - ¿Los colaboradores llegan como parámetros o se crean por dentro?

### 10. Herencia profunda (DIT) (herencia) — sin señales

- **Qué mide:** Profundidad de la cadena de herencia de una clase.
- **Por qué importa:** Jerarquías muy profundas vuelven difícil saber de dónde viene cada comportamiento.
- **Tu resultado:** **0** clase(s) con herencia profunda

### 11. Switch sobre tipos (OCP) (ocp) — con señales

- **Qué mide:** Chequeos repetidos de tipo (`instanceof`/`is`/`typeof`) que equivalen a un switch sobre el tipo.
- **Por qué importa:** Cada tipo nuevo obliga a tocar varios lugares, en vez de extender con una implementación nueva.
- **Tu resultado:** **2** clase(s) con switch sobre tipos
- **Para investigar la causa:**
    - ¿Qué operación se resuelve preguntando el tipo?
    - ¿Qué se toca cada vez que aparece un tipo o caso nuevo?
    - ¿El comportamiento podría vivir en la propia jerarquía de tipos?

### 12. Interfaz con demasiados métodos (ISP) (isp) — sin señales

- **Qué mide:** Interfaces o clases abstractas con muchos métodos declarados.
- **Por qué importa:** Obliga a implementar operaciones que no se usan y acopla a los clientes a métodos que no necesitan.
- **Tu resultado:** **0** interfaz(es) con demasiados métodos

### 13. Override vacío o 'no implementado' (LSP) (lsp) — sin señales

- **Qué mide:** Métodos sobrescritos que quedan vacíos o lanzan 'no implementado'.
- **Por qué importa:** Suele indicar que la jerarquía no representa bien el problema y rompe la sustitución entre clases.
- **Tu resultado:** **0** override(s) vacío(s) o 'no implementado'

### 14. Dependencia de concretos y falta de abstracciones (DIP) (dip) — con señales

- **Qué mide:** Instanciación directa de clases concretas y ausencia de interfaces o clases abstractas pese al acoplamiento.
- **Por qué importa:** Depender de implementaciones concretas dificulta cambiar o probar una parte sin arrastrar las demás.
- **Tu resultado:** **47** dependencia(s) de concretos sin abstracciones
- **Para investigar la causa:**
    - ¿La clase crea por dentro a sus colaboradores en vez de recibirlos?
    - ¿Hay puntos de variación que convendría expresar como abstracción?
    - ¿Cómo se probaría esa clase cambiando una implementación?

### 15. Clase sin métodos (solo datos) (data) — con señales

- **Qué mide:** Clases que solo acumulan campos y no tienen comportamiento.
- **Por qué importa:** Separar datos de comportamiento reparte por el proyecto la lógica que debería estar cerca de esos datos.
- **Tu resultado:** **15** clase(s) sin métodos (solo datos)
- **Para investigar la causa:**
    - ¿Dónde vive el comportamiento que opera sobre esos datos?
    - ¿La clase representa un concepto del dominio o un contenedor técnico?
    - ¿Se evita la lógica de negocio dentro de la clase por comodidad?

### 16. Campos públicos (encapsulación) (encapsulacion) — con señales

- **Qué mide:** Clases que exponen campos sin control de acceso.
- **Por qué importa:** Expone el estado interno y hace que cualquier cambio dependa de quién lo modifica desde afuera.
- **Tu resultado:** **45** clase(s) con campos públicos
- **Para investigar la causa:**
    - ¿Se accede al campo desde otras clases para evitar un método?
    - ¿Qué reglas deberían respetarse al modificar ese estado?
    - ¿Los campos públicos se usan más como configuración que como estado?

### 17. Clase solo con métodos estáticos (utilitaria) (static) — sin señales

- **Qué mide:** Clases donde todos los métodos son estáticos.
- **Por qué importa:** Es comportamiento procedural disfrazado de clase: no se instancia ni participa del modelo de objetos.
- **Tu resultado:** **0** clase(s) solo con métodos estáticos

## 4. Anexo: dónde mirar

Archivos donde el análisis encontró cada señal (sin número de línea, a propósito):

**Clase demasiado grande (SRP) (god)** — 36 archivo(s):
- `app/src/main/kotlin/org/example/service/MenuConsoleRunner.kt`
- `app/src/main/kotlin/org/example/service/ValidadorEntradaRespuesta.kt`
- `app/src/main/kotlin/org/example/ui/BarraNavegacion.kt`
- `app/src/main/kotlin/org/example/ui/PantallaEjercicios.kt`
- `app/src/main/kotlin/org/example/ui/PantallaPractica.kt`
- `app/src/main/kotlin/org/example/ui/VentanaPrincipal.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/AtletismoThemeRenderer.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/BasquetThemeRenderer.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/CanonThemeRenderer.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/ControladorSimulacion.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/ConversorCoordenadas.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/EdificioThemeRenderer.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/FutbolThemeRenderer.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/GolfThemeRenderer.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/LienzoSimulacion2D.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/PantallaSimulacion.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/TenisThemeRenderer.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/graficos/CartesianChartCanvas.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/graficos/PanelGraficosCinematicos.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/graficos/RenderizadorAceleracion.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/graficos/RenderizadorPosicion.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/graficos/RenderizadorVelocidad.kt`
- `app/src/test/kotlin/org/example/GeneradorEjerciciosBancoAdapterTest.kt`
- `app/src/test/kotlin/org/example/PantallaEjerciciosSwingTest.kt`
- `app/src/test/kotlin/org/example/PantallaEjerciciosTest.kt`
- `app/src/test/kotlin/org/example/ValidadorEntradaRespuestaTest.kt`
- `app/src/test/kotlin/org/example/ui/PantallaPracticaTest.kt`
- `app/src/test/kotlin/org/example/ui/VentanaPrincipalTest.kt`
- `app/src/test/kotlin/org/example/ui/VerificacionVisualVentanaTest.kt`
- `app/src/test/kotlin/org/example/ui/simulacion/CalculadorPosicionTest.kt`
- `app/src/test/kotlin/org/example/ui/simulacion/ControladorSimulacionTest.kt`
- `app/src/test/kotlin/org/example/ui/simulacion/ConversorCoordenadasTest.kt`
- `app/src/test/kotlin/org/example/ui/simulacion/PantallaSimulacionTest.kt`
- `app/src/test/kotlin/org/example/ui/simulacion/ThemeRendererTest.kt`
- `app/src/test/kotlin/org/example/ui/simulacion/graficos/CartesianChartCanvasTest.kt`
- `app/src/test/kotlin/org/example/ui/simulacion/graficos/RenderizadorCinematicoTest.kt`

**Baja cohesión (LCOM) (cohesion)** — 9 archivo(s):
- `app/src/main/kotlin/org/example/service/MenuConsoleRunner.kt`
- `app/src/main/kotlin/org/example/state/AppState.kt`
- `app/src/main/kotlin/org/example/ui/PantallaEjercicios.kt`
- `app/src/main/kotlin/org/example/ui/PantallaPractica.kt`
- `app/src/main/kotlin/org/example/ui/VentanaPrincipal.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/ControladorSimulacion.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/LienzoSimulacion2D.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/PantallaSimulacion.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/graficos/PanelGraficosCinematicos.kt`

**Complejidad alta (complejidad)** — 1 archivo(s):
- `app/src/main/kotlin/org/example/ui/PantallaPractica.kt`

**Métodos/funciones largos o con muchos parámetros (tamano)** — 7 archivo(s):
- `app/src/main/kotlin/org/example/service/CalculadorTiroOblicuo.kt`
- `app/src/main/kotlin/org/example/service/SimulacionConsoleRunner.kt`
- `app/src/main/kotlin/org/example/ui/PantallaEjercicios.kt`
- `app/src/main/kotlin/org/example/ui/PantallaPractica.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/LienzoSimulacion2D.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/PanelControlSimulacion.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/graficos/CartesianChartCanvas.kt`

**Acoplamiento alto (CBO) (acoplamiento)** — 48 archivo(s):
- `app/src/main/kotlin/org/example/navigation/GestorNavegacion.kt`
- `app/src/main/kotlin/org/example/service/BancoEjerciciosService.kt`
- `app/src/main/kotlin/org/example/service/CalculadorTiroOblicuo.kt`
- `app/src/main/kotlin/org/example/service/MenuConsoleRunner.kt`
- `app/src/main/kotlin/org/example/service/SimulacionConsoleRunner.kt`
- `app/src/main/kotlin/org/example/service/ValidadorEntradaRespuesta.kt`
- `app/src/main/kotlin/org/example/state/AppState.kt`
- `app/src/main/kotlin/org/example/ui/BarraNavegacion.kt`
- `app/src/main/kotlin/org/example/ui/PantallaAutocorrecion.kt`
- `app/src/main/kotlin/org/example/ui/PantallaEjercicios.kt`
- `app/src/main/kotlin/org/example/ui/PantallaPractica.kt`
- `app/src/main/kotlin/org/example/ui/VentanaPrincipal.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/AtletismoThemeRenderer.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/BasquetThemeRenderer.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/CanonThemeRenderer.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/ControladorSimulacion.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/ConversorCoordenadas.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/EdificioThemeRenderer.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/FutbolThemeRenderer.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/GolfThemeRenderer.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/LienzoSimulacion2D.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/PanelControlSimulacion.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/PantallaSimulacion.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/TematicaSimulacion.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/TenisThemeRenderer.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/ThemeDetector.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/ThemeFactory.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/ThemeRenderer.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/graficos/CartesianChartCanvas.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/graficos/PanelGraficosCinematicos.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/graficos/RenderizadorAceleracion.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/graficos/RenderizadorGraficoCinematico.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/graficos/RenderizadorPosicion.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/graficos/RenderizadorVelocidad.kt`
- `app/src/test/kotlin/org/example/AppTest.kt`
- `app/src/test/kotlin/org/example/PantallaEjerciciosSwingTest.kt`
- `app/src/test/kotlin/org/example/PantallaEjerciciosTest.kt`
- `app/src/test/kotlin/org/example/ValidadorEntradaRespuestaTest.kt`
- `app/src/test/kotlin/org/example/ui/PantallaPracticaTest.kt`
- `app/src/test/kotlin/org/example/ui/VentanaPrincipalTest.kt`
- `app/src/test/kotlin/org/example/ui/VerificacionVisualVentanaTest.kt`
- `app/src/test/kotlin/org/example/ui/simulacion/CalculadorPosicionTest.kt`
- `app/src/test/kotlin/org/example/ui/simulacion/ControladorSimulacionTest.kt`
- `app/src/test/kotlin/org/example/ui/simulacion/PantallaSimulacionTest.kt`
- `app/src/test/kotlin/org/example/ui/simulacion/ThemeRendererTest.kt`
- `app/src/test/kotlin/org/example/ui/simulacion/graficos/CartesianChartCanvasTest.kt`
- `app/src/test/kotlin/org/example/ui/simulacion/graficos/PanelGraficosCinematicosTest.kt`
- `app/src/test/kotlin/org/example/ui/simulacion/graficos/RenderizadorCinematicoTest.kt`

**Switch sobre tipos (OCP) (ocp)** — 2 archivo(s):
- `app/src/main/kotlin/org/example/service/ValidadorEntradaRespuesta.kt`
- `app/src/test/kotlin/org/example/ValidadorEntradaRespuestaTest.kt`

**Dependencia de concretos y falta de abstracciones (DIP) (dip)** — 43 archivo(s):
- `app/src/main/kotlin/org/example/service/BancoEjerciciosService.kt`
- `app/src/main/kotlin/org/example/service/CalculadorTiroOblicuo.kt`
- `app/src/main/kotlin/org/example/service/MenuConsoleRunner.kt`
- `app/src/main/kotlin/org/example/service/ValidadorEntradaRespuesta.kt`
- `app/src/main/kotlin/org/example/state/AppState.kt`
- `app/src/main/kotlin/org/example/ui/BarraNavegacion.kt`
- `app/src/main/kotlin/org/example/ui/PantallaAutocorrecion.kt`
- `app/src/main/kotlin/org/example/ui/PantallaEjercicios.kt`
- `app/src/main/kotlin/org/example/ui/PantallaPractica.kt`
- `app/src/main/kotlin/org/example/ui/VentanaPrincipal.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/AtletismoThemeRenderer.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/BasquetThemeRenderer.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/CanonThemeRenderer.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/ControladorSimulacion.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/EdificioThemeRenderer.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/FutbolThemeRenderer.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/GolfThemeRenderer.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/LienzoSimulacion2D.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/PanelControlSimulacion.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/PantallaSimulacion.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/TematicaSimulacion.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/TenisThemeRenderer.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/ThemeDetector.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/ThemeFactory.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/ThemeRenderer.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/graficos/CartesianChartCanvas.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/graficos/PanelGraficosCinematicos.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/graficos/RenderizadorAceleracion.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/graficos/RenderizadorPosicion.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/graficos/RenderizadorVelocidad.kt`
- `app/src/test/kotlin/org/example/AppTest.kt`
- `app/src/test/kotlin/org/example/PantallaEjerciciosSwingTest.kt`
- `app/src/test/kotlin/org/example/PantallaEjerciciosTest.kt`
- `app/src/test/kotlin/org/example/ui/PantallaPracticaTest.kt`
- `app/src/test/kotlin/org/example/ui/VentanaPrincipalTest.kt`
- `app/src/test/kotlin/org/example/ui/VerificacionVisualVentanaTest.kt`
- `app/src/test/kotlin/org/example/ui/simulacion/CalculadorPosicionTest.kt`
- `app/src/test/kotlin/org/example/ui/simulacion/ControladorSimulacionTest.kt`
- `app/src/test/kotlin/org/example/ui/simulacion/PantallaSimulacionTest.kt`
- `app/src/test/kotlin/org/example/ui/simulacion/ThemeRendererTest.kt`
- `app/src/test/kotlin/org/example/ui/simulacion/graficos/CartesianChartCanvasTest.kt`
- `app/src/test/kotlin/org/example/ui/simulacion/graficos/PanelGraficosCinematicosTest.kt`
- `app/src/test/kotlin/org/example/ui/simulacion/graficos/RenderizadorCinematicoTest.kt`

**Clase sin métodos (solo datos) (data)** — 15 archivo(s):
- `app/src/main/kotlin/org/example/model/RespuestaEstudiante.kt`
- `app/src/main/kotlin/org/example/navigation/SeccionNavegacion.kt`
- `app/src/test/kotlin/org/example/GeneradorEjerciciosBancoAdapterTest.kt`
- `app/src/test/kotlin/org/example/ValidadorEntradaRespuestaTest.kt`
- `app/src/test/kotlin/org/example/navigation/GestorNavegacionTest.kt`
- `app/src/test/kotlin/org/example/ui/VentanaPrincipalTest.kt`
- `app/src/test/kotlin/org/example/ui/VerificacionVisualVentanaTest.kt`
- `app/src/test/kotlin/org/example/ui/simulacion/CalculadorPosicionTest.kt`
- `app/src/test/kotlin/org/example/ui/simulacion/ControladorSimulacionTest.kt`
- `app/src/test/kotlin/org/example/ui/simulacion/ConversorCoordenadasTest.kt`
- `app/src/test/kotlin/org/example/ui/simulacion/PantallaSimulacionTest.kt`
- `app/src/test/kotlin/org/example/ui/simulacion/ThemeRendererTest.kt`
- `app/src/test/kotlin/org/example/ui/simulacion/graficos/CartesianChartCanvasTest.kt`
- `app/src/test/kotlin/org/example/ui/simulacion/graficos/PanelGraficosCinematicosTest.kt`
- `app/src/test/kotlin/org/example/ui/simulacion/graficos/RenderizadorCinematicoTest.kt`

**Campos públicos (encapsulación) (encapsulacion)** — 43 archivo(s):
- `app/src/main/kotlin/org/example/model/RespuestaEstudiante.kt`
- `app/src/main/kotlin/org/example/service/MenuConsoleRunner.kt`
- `app/src/main/kotlin/org/example/service/ValidadorEntradaRespuesta.kt`
- `app/src/main/kotlin/org/example/state/AppState.kt`
- `app/src/main/kotlin/org/example/ui/BarraNavegacion.kt`
- `app/src/main/kotlin/org/example/ui/PantallaAutocorrecion.kt`
- `app/src/main/kotlin/org/example/ui/PantallaEjercicios.kt`
- `app/src/main/kotlin/org/example/ui/PantallaPractica.kt`
- `app/src/main/kotlin/org/example/ui/VentanaPrincipal.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/AtletismoThemeRenderer.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/BasquetThemeRenderer.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/CanonThemeRenderer.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/ControladorSimulacion.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/ConversorCoordenadas.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/EdificioThemeRenderer.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/FutbolThemeRenderer.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/GolfThemeRenderer.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/LienzoSimulacion2D.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/PanelControlSimulacion.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/PantallaSimulacion.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/TenisThemeRenderer.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/ThemeRenderer.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/graficos/CartesianChartCanvas.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/graficos/PanelGraficosCinematicos.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/graficos/RenderizadorAceleracion.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/graficos/RenderizadorPosicion.kt`
- `app/src/main/kotlin/org/example/ui/simulacion/graficos/RenderizadorVelocidad.kt`
- `app/src/test/kotlin/org/example/GeneradorEjerciciosBancoAdapterTest.kt`
- `app/src/test/kotlin/org/example/PantallaEjerciciosSwingTest.kt`
- `app/src/test/kotlin/org/example/PantallaEjerciciosTest.kt`
- `app/src/test/kotlin/org/example/ValidadorEntradaRespuestaTest.kt`
- `app/src/test/kotlin/org/example/navigation/GestorNavegacionTest.kt`
- `app/src/test/kotlin/org/example/ui/PantallaPracticaTest.kt`
- `app/src/test/kotlin/org/example/ui/VentanaPrincipalTest.kt`
- `app/src/test/kotlin/org/example/ui/VerificacionVisualVentanaTest.kt`
- `app/src/test/kotlin/org/example/ui/simulacion/CalculadorPosicionTest.kt`
- `app/src/test/kotlin/org/example/ui/simulacion/ControladorSimulacionTest.kt`
- `app/src/test/kotlin/org/example/ui/simulacion/ConversorCoordenadasTest.kt`
- `app/src/test/kotlin/org/example/ui/simulacion/PantallaSimulacionTest.kt`
- `app/src/test/kotlin/org/example/ui/simulacion/ThemeRendererTest.kt`
- `app/src/test/kotlin/org/example/ui/simulacion/graficos/CartesianChartCanvasTest.kt`
- `app/src/test/kotlin/org/example/ui/simulacion/graficos/PanelGraficosCinematicosTest.kt`
- `app/src/test/kotlin/org/example/ui/simulacion/graficos/RenderizadorCinematicoTest.kt`

## 5. Evolución por commit

Métricas calculadas sobre el estado de `main` en **cada commit** (13 commits con trabajo), del primero al último. La calidad usa el perfil `exigente`; el aporte de código lo mide RepoSense. El índice puede **bajar** al crecer el proyecto: se acumulan señales.

```mermaid
xychart-beta
    title "Índice POO/SOLID a lo largo de los commits"
    x-axis ["f5b4533", "0407e5e", "dd3780e", "d36b80c", "34979f5", "5b92c65", "2e89df6", "39d9b06", "0a667a3", "76a2cc8", "4b1ecf0", "27cb813", "5bef311"]
    y-axis "Índice" 0 --> 100
    line [0, 0, 0, 50, 25, 10, 17, 15, 4, 3, 2, 2, 1]
```

| # | Fecha | Commit | Autor | LOC | Índice | %POO | Duplic. | Sueltas |
|---|---|---|---|---:|---:|---:|---:|---:|
| 1 | 2026-08-14 | `f5b4533` | Romano-22 | 0 | 0 | 0% | 0.0% | 0 |
| 2 | 2026-08-14 | `0407e5e` | Romano-22 | 0 | 0 | 0% | 0.0% | 0 |
| 3 | 2026-08-28 | `dd3780e` | dqmdz-etec | 0 | 0 | 0% | 0.0% | 0 |
| 4 | 2026-08-28 | `d36b80c` | avsalomon | 65 | 50 | 9% | 0.0% | 0 |
| 5 | 2026-09-04 | `34979f5` | avsalomon | 133 | 25 | 46% | 0.0% | 1 |
| 6 | 2026-09-07 | `5b92c65` | Romano-22 | 215 | 10 | 109% | 0.0% | 1 |
| 7 | 2026-10-02 | `2e89df6` | Romano-22 | 277 | 17 | 68% | 0.0% | 1 |
| 8 | 2026-10-05 | `39d9b06` | avsalomon | 638 | 15 | 77% | 0.0% | 0 |
| 9 | 2026-10-09 | `0a667a3` | avsalomon | 1,443 | 4 | 77% | 12.0% | 2 |
| 10 | 2026-10-16 | `76a2cc8` | avsalomon | 3,076 | 3 | 84% | 5.5% | 5 |
| 11 | 2026-10-16 | `4b1ecf0` | avsalomon | 5,634 | 2 | 86% | 5.1% | 5 |
| 12 | 2026-10-19 | `27cb813` | avsalomon | 5,927 | 2 | 86% | 5.0% | 5 |
| 13 | 2026-10-26 | `5bef311` | avsalomon | 7,177 | 1 | 95% | 4.7% | 5 |

---
_Informe generado por la cátedra. Los criterios y umbrales de medición corresponden al perfil `exigente` de `calidad.py`; el semáforo de aprobación, a `CRITERIOS-CALIDAD.md` (ver `README.md`). Ante dudas, revisá el archivo señalado._
<!-- evaluacion:fin -->
